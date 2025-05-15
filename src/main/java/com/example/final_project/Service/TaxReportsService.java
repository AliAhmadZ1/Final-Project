package com.example.final_project.Service;

import com.example.final_project.Api.ApiException;
import com.example.final_project.DTOOUT.TaxReportStatusDTO;
import com.example.final_project.Model.*;
import com.example.final_project.Notification.NotificationService;
import com.example.final_project.Repository.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaxReportsService {

    private final TaxReportsRepository taxReportsRepository;
    private final BusinessRepository businessRepository;
    private final AuditorRepository auditorRepository;
    private final SalesRepository salesRepository;
    private final NotificationService notificationService;
    private final MyUserRepository myUserRepository;
    private final TaxPayerRepository taxPayerRepository;



    public List<TaxReports> getAllTaxReports(Integer id) {
        Auditor auditor = auditorRepository.findAuditorsById(id);
        if (auditor == null) {
            throw new ApiException("auditor not found");
        }
        return taxReportsRepository.findAll();
    }



    public void addTaxReports(Integer auditor_id, Integer business_id, TaxReports taxReports) {
        Business business = businessRepository.findBusinessById(business_id);
        Auditor auditor = auditorRepository.findAuditorsById(auditor_id);

        if (auditor == null || business == null) {
            throw new ApiException("auditor or business not found");
        }
        taxReports.setStatus("Pending");
        taxReportsRepository.save(taxReports);
    }

    public void updateTaxReports(Integer id, Integer taxReportId, TaxReports taxReports) {
        Auditor auditor = auditorRepository.findAuditorsById(id);
        if (auditor == null)
            throw new ApiException("Auditor not found");
        TaxReports oldTaxReports = taxReportsRepository.findTaxReportsById(taxReportId);

        if (oldTaxReports == null) {
            throw new ApiException("TaxReports not found");
        }

        oldTaxReports.setTotalTax(taxReports.getTotalTax());
        oldTaxReports.setStart_date(taxReports.getStart_date());
        oldTaxReports.setEnd_date(taxReports.getEnd_date());
        oldTaxReports.setStatus(taxReports.getStatus());
        oldTaxReports.setPaymentDate(taxReports.getPaymentDate());

        taxReportsRepository.save(oldTaxReports);
    }


    public void deleteTaxReports(Integer id, Integer taxReportId) {
        MyUser admin = myUserRepository.findUserByIdAndRole(id, "ADMIN");
        if (admin == null)
            throw new ApiException("Auditor not found");
        TaxReports taxReports = taxReportsRepository.findTaxReportsById(taxReportId);
        if (taxReports == null) {
            throw new ApiException("Tax Reports not found");
        }
        taxReportsRepository.delete(taxReports);
    }

    // Khalid almutiri
    public void applyLatePaymentPenalty(Integer id, Integer taxReportId) {
        Auditor auditor = auditorRepository.findAuditorsById(id);
        if (auditor == null)
            throw new ApiException("Auditor not found");
        TaxReports taxReport = taxReportsRepository.findTaxReportsById(taxReportId);

        if (taxReport == null) {
            throw new ApiException("Tax report not found");
        }

        if (!taxReport.getStatus().equals("Approved")) {
            throw new ApiException("Penalty applies only to approved tax reports");
        }

        if (taxReport.getPaymentDate() == null) {
            throw new ApiException("Payment date is missing");
        }

        LocalDate approvalDate = taxReport.getEnd_date().toLocalDate();
        LocalDate dueDate = approvalDate.plusMonths(1);
        LocalDate today = LocalDate.now();

        if (today.isAfter(dueDate)){
            Double penalty = taxReport.getTotalTax() * 0.05;
            taxReport.setTotalTax(taxReport.getTotalTax() + penalty);
            taxReportsRepository.save(taxReport);
        } else {
            throw new ApiException("No penalty: payment is still within grace period");
        }
        String emailTo = taxReport.getBusiness().getTaxPayer().getMyUser().getEmail();
        String name = taxReport.getBusiness().getTaxPayer().getMyUser().getName();
        String message = "Dear"+name+"\n\n Due to your failure to " +
                "pay the value-added tax and your delay of one month, " +
                "a financial penalty has been imposed, " +
                "which is 5% of the total amount due.";
        notificationService.sendEmail(emailTo,"Due to VAT Non-Payment\n" +
                "\n",message);
    }


    // Khalid almutiri
    public void applyTwoMonthLatePenalty(Integer id, Integer taxReportId) {
        Auditor auditor = auditorRepository.findAuditorsById(id);
        if (auditor == null)
            throw new ApiException("Auditor not found");

        TaxReports taxReport = taxReportsRepository.findTaxReportsById(taxReportId);

        if (taxReport == null) {
            throw new ApiException("Tax report not found");
        }

        if (!taxReport.getStatus().equals("Approved")) {
            throw new ApiException("Penalty applies only to approved tax reports");
        }

        if (taxReport.getPaymentDate() == null || taxReport.getEnd_date() == null) {
            throw new ApiException("Missing payment or approval date");
        }

        LocalDate endDate = taxReport.getEnd_date().toLocalDate();
        LocalDate twoMonthDue = endDate.plusMonths(2);
        LocalDate today = LocalDate.now();

        if (today.isBefore(twoMonthDue)) {
            throw new ApiException("No penalty: Two months have not passed since end date");
        }

        double originalTax = taxReport.getTotalTax();
        double penalty = originalTax * 0.10;
        taxReport.setTotalTax(originalTax + penalty);


        String emailTo = taxReport.getBusiness().getTaxPayer().getMyUser().getEmail();
        String name = taxReport.getBusiness().getTaxPayer().getMyUser().getName();
        String message = "Dear"+name+"\n\n Due to your failure to " +
                "pay the value-added tax and your delay of two month, " +
                "a financial penalty has been imposed, " +
                "which is 10% of the total amount due.";
        notificationService.sendEmail(emailTo,"Due to VAT Non-Payment\n" +
                "\n",message);

        taxReportsRepository.save(taxReport);
    }


    // Khalid almutiri
    public void applyLegalActionForTaxEvasion(Integer id, Integer taxReportId) {
        Auditor auditor = auditorRepository.findAuditorsById(id);
        if (auditor == null)
            throw new ApiException("Auditor not found");

        TaxReports taxReport = taxReportsRepository.findTaxReportsById(taxReportId);
        if (taxReport == null) {
            throw new ApiException("Tax report not found");
        }

        if (!taxReport.getStatus().equals("Approved")) {
            throw new ApiException("Legal action applies only to approved tax reports");
        }

        if (taxReport.getEnd_date() == null) {
            throw new ApiException("Missing end date");
        }

        LocalDate endDate = taxReport.getEnd_date().toLocalDate();
        LocalDate legalDeadline = endDate.plusDays(90);
        LocalDate today = LocalDate.now();

        boolean unpaid = (taxReport.getPaymentDate() == null || taxReport.getPaymentDate().isAfter(legalDeadline));

        if (today.isAfter(legalDeadline) && unpaid) {
            taxReport.setStatus("Under Legal Action");
            taxReportsRepository.save(taxReport);
        } else {
            throw new ApiException("Conditions for legal action not met");
        }

        String emailTo = taxReport.getBusiness().getTaxPayer().getMyUser().getEmail();
        String name = taxReport.getBusiness().getTaxPayer().getMyUser().getName();
        String message = "Dear "+name+"\n\n  " +
                "We regret to inform you that, due to your failure to settle the due value-added tax (VAT) \n" +
                "and a delay exceeding three months, your case has been officially referred for legal action and the initiation of a lawsuit. \n" +
                "Please be advised that, in addition to the outstanding tax amount, you will also be held responsible for all associated legal costs.\n" +
                "\n" +
                "Sincerely,";
        notificationService.sendEmail(emailTo,"Due to VAT Non-Payment\n" +
                "\n",message);
    }



    // Khalid almutiri
    public void changeTaxReportStatus(Integer taxReportId, Integer auditorId, TaxReportStatusDTO taxReportStatusDTO) {
        TaxReports taxReport = taxReportsRepository.findTaxReportsById(taxReportId);

        if (taxReport == null) {
            throw new ApiException("Tax report not found");
        }
        Auditor auditor = auditorRepository.findAuditorsById(auditorId);
        if (auditor == null) {
            throw new ApiException("Auditor not found");
        }


        if (taxReport.getAuditor() == null || !taxReport.getAuditor().getId().equals(auditorId)) {
            throw new ApiException("This report does not belong to the specified auditor");
        }

        List<String> allowedStatuses = List.of("Pending", "Approved", "Paid", "Under Legal Action", "Rejected");
        if (!allowedStatuses.contains(taxReportStatusDTO.getNewStatus())) {
            throw new ApiException("Invalid status value");
        }

        taxReport.setStatus(taxReportStatusDTO.getNewStatus());
        taxReportsRepository.save(taxReport);
    }


    // Khalid almutiri
    public List<TaxReports> getUnpaidDueTaxReports(Integer id) {
        Auditor auditor = auditorRepository.findAuditorsById(id);
        if (auditor == null)
            throw new ApiException("Auditor not found");
        return taxReportsRepository.findAllByPaymentDateIsNotNullAndStatusNot("Paid");
    }



    // Khalid almutiri
    public List<TaxReports> getReportsByAuditor(Integer auditorId) {
        Auditor auditor = auditorRepository.findAuditorsById(auditorId);
        if (auditor == null) {
            throw new ApiException("Auditor not found");
        }
        return taxReportsRepository.findAllByAuditorId(auditorId);
    }


    // Khalid almutiri
    public Long getReportCountByStatus(Integer auditorId, String status) {
        Auditor auditor = auditorRepository.findAuditorsById(auditorId);
        if (auditor == null) {
            throw new ApiException("Auditor not found");
        }
        return taxReportsRepository.countByAuditorIdAndStatus(auditorId, status);
    }

    // Khalid almutiri
    public Double getApprovalRate(Integer auditorId) {
        Auditor auditor = auditorRepository.findAuditorsById(auditorId);
        if (auditor == null)
            throw new ApiException("auditor not found");
        Long total = taxReportsRepository.countByAuditorId(auditorId);
        Long approved = taxReportsRepository.countByAuditorIdAndStatus(auditorId, "Approved");

        if (total == 0) return 0.0;

        return (approved * 100.0) / total;
    }


    // Khalid almutiri
    public void bulkApproveReports(Integer auditorId, List<Integer> reportIds) {
        for (Integer reportId : reportIds) {
            TaxReports taxReport = taxReportsRepository.findTaxReportsById(reportId);
            if (taxReport != null && taxReport.getAuditor() != null &&
                    taxReport.getAuditor().getId().equals(auditorId)) {

                taxReport.setStatus("Approved");
                taxReportsRepository.save(taxReport);
            }
        }
    }

    // Khalid almutiri
    public TaxReports getLatestReportByAuditor(Integer auditorId) {
        Auditor auditor = auditorRepository.findAuditorsById(auditorId);
        if (auditor == null)
            throw new ApiException("Auditor not found");
        List<TaxReports> reports = taxReportsRepository.findTopByAuditorIdOrderByEnd_dateDesc(auditorId);
        if (reports.isEmpty()) {
            throw new ApiException("No reports found for auditor");
        }
        return reports.get(0);
    }



    public List<TaxReports> printTaxReportForEveryBusinesses(Integer taxPayerId) {
        TaxPayer taxPayer = taxPayerRepository.findTaxBuyerById(taxPayerId);
        if (taxPayer == null)
            throw new ApiException("Tax Payer not found");
        List<TaxReports> taxReports = taxReportsRepository.findTaxReportsByTaxPayer(taxPayerId);
        if (taxReports.isEmpty())
            throw new ApiException("you don't have any tax reports");
        return taxReports;
    }


    // Khalid almutiri
    public List<TaxReports> getUnapprovedTaxReports(Integer auditorId) {
        Auditor auditor = auditorRepository.findAuditorsById(auditorId);
        if (auditor == null)
            throw new ApiException("Auditor not found");
        return taxReportsRepository.findAllUnapproved();
    }



    public Map<String, Object> getPaymentStatusByReportId(Integer taxPayerId, Integer reportId) {
        TaxPayer taxPayer = taxPayerRepository.findTaxBuyerById(taxPayerId);
        if (taxPayer == null)
            throw new ApiException("Tax Payer not found");
        TaxReports report = taxReportsRepository.findTaxReportsById(reportId);
        if (report == null) {
            throw new ApiException("Tax report not found");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("reportId ", report.getId());
        result.put("paymentStatus ", report.getStatus());

        return result;
    }

    public void notifyUpcomingPayments() {
        List<TaxReports> reports = taxReportsRepository.findAll();
        LocalDate today = LocalDate.now();

        for (TaxReports report : reports) {
            if (report.getStatus().equalsIgnoreCase("Paid")) continue;

            LocalDate paymentDate = report.getPaymentDate();
            if (paymentDate != null) {
                long daysLeft = ChronoUnit.DAYS.between(today, paymentDate);

                if (daysLeft <= 3 && daysLeft >= 0) {
                    Business business = report.getBusiness();
                    if (business == null || business.getTaxPayer() == null) continue;

                    String email = business.getTaxPayer().getMyUser().getEmail();
                    String subject = " Payment Reminder - Tax Report #" + report.getId();
                    String message = "Dear Taxpayer,\n\nThis is a reminder that the tax report (ID: " + report.getId() +
                            ") is due for payment on " + paymentDate + ".\n\n" +
                            "Please make the payment on time to avoid any legal consequences.\n\n" +
                            "Regards,\nMohasil Team";

                    notificationService.sendEmail(email, subject, message);
                }
            }
        }
    }


    public byte[] getTaxReportAsPdf(Integer userId, Integer reportId) {
        MyUser myUser = myUserRepository.findUserByIdAndRole(userId, "TAXPAYER");
        if (myUser == null)
            throw new ApiException("User not found or doesn't have permission");
        TaxReports report = taxReportsRepository.findTaxReportsById(reportId);
        if (report == null) throw new ApiException("Tax report not found.");

        if (!report.getBusiness().getTaxPayer().getId().equals(userId)) {
            throw new ApiException("You don't have access to this tax report!");
        }


        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();


            try {
                InputStream is = getClass().getResourceAsStream("/logo.png");
                if (is != null) {
                    Image logo = Image.getInstance(is.readAllBytes());
                    logo.scaleToFit(120, 120);
                    logo.setAlignment(Element.ALIGN_CENTER);
                    document.add(logo);
                    document.add(Chunk.NEWLINE);
                }
            } catch (Exception e) {
                System.out.println("Logo not found or failed to load.");
            }


            Paragraph title = new Paragraph("TAX REPORT SUMMARY", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);


            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

            document.add(new Paragraph("------------------------------------------------------------"));

            document.add(new Paragraph("Report ID: " + report.getId()));
            document.add(new Paragraph("Status: " + report.getStatus()));
            document.add(new Paragraph("Tax Amount: " + report.getTotalTax() + " SAR"));
            document.add(new Paragraph("Payment Due: " + report.getPaymentDate()));
            document.add(new Paragraph("Report Period:"));
            document.add(new Paragraph("   • From: " + report.getStart_date()));
            document.add(new Paragraph("   • To  : " + report.getEnd_date()));
            document.add(new Paragraph("Created At: " + LocalDateTime.now()));

            if (report.getBusiness() != null) {
                document.add(new Paragraph("Business Name: " + report.getBusiness().getBusinessName()));
            }

            document.add(new Paragraph("------------------------------------------------------------"));
            document.add(Chunk.NEWLINE);


            document.add(new Paragraph("This document summarizes your tax obligations as submitted."));
            document.add(new Paragraph("Please ensure payment is completed before the due date to avoid penalties."));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Signature: ___________________________"));
            document.add(new Paragraph("Mohasil Team", new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC)));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }


}
