package com.example.final_project.Service;

import com.example.final_project.Api.ApiException;
import com.example.final_project.Model.*;
import com.example.final_project.DTO.DTOAuditor;
import com.example.final_project.Repository.*;
import com.example.final_project.Model.Business;
import com.example.final_project.Model.TaxPayer;
import com.example.final_project.Model.MyUser;
import com.example.final_project.Repository.AuditorRepository;
import com.example.final_project.Repository.BusinessRepository;
import com.example.final_project.Repository.MyUserRepository;
import com.example.final_project.Repository.TaxPayerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditorService {

    private final AuditorRepository auditorRepository;
    private final MyUserRepository myUserRepository;
    private final BusinessRepository businessRepository;
    private final SalesRepository salesRepository;
    private final TaxReportsRepository taxReportsRepository;
    private final TaxPayerRepository taxPayerRepository;

    // authority -> ADMIN
    public List<Auditor> getAllAuditors(Integer id) {
        MyUser admin = myUserRepository.findUserByIdAndRole(id, "ADMIN");
        if (admin == null)
            throw new ApiException("user not found or doesn't have permission");
        return auditorRepository.findAll();
    }

    public void addAuditor(Integer adminId, DTOAuditor dtoAuditor) {
        MyUser admin = myUserRepository.findUserByIdAndRole(adminId, "ADMIN");
        if (admin == null)
            throw new ApiException("user not found or does have permission");
        MyUser myUser = new MyUser();
        myUser.setName(dtoAuditor.getName());
        myUser.setUsername(dtoAuditor.getUsername());

        String hashPassword = new BCryptPasswordEncoder().encode(dtoAuditor.getPassword());
        myUser.setPassword(hashPassword);

        myUser.setEmail(dtoAuditor.getEmail());
        myUser.setRole("AUDITOR");
        Auditor auditor = new Auditor(null, dtoAuditor.getSOCPA(), myUser, null);
        myUser.setAuditor(auditor);
        myUserRepository.save(myUser);
        auditorRepository.save(auditor);
    }

    public void updateAuditor(Integer auditorId, DTOAuditor dtoAuditor) {
        MyUser myUser = myUserRepository.findUserById(auditorId);
        if (myUser == null) {
            throw new ApiException("auditor not found");
        }
        myUser.setName(dtoAuditor.getName());
        myUser.setUsername(dtoAuditor.getUsername());
        myUser.setPassword(dtoAuditor.getPassword());
        myUser.setEmail(dtoAuditor.getEmail());
        myUser.getAuditor().setSOCPA(dtoAuditor.getSOCPA());

        myUserRepository.save(myUser);
    }


    public void deleteAuditor(Integer auditorId) {
        MyUser myUser = myUserRepository.findUserById(auditorId);
        if (myUser == null)
            throw new ApiException("auditor not found");
        myUserRepository.delete(myUser);
    }


    // Ali Ahmed Alshehri
    // Endpoint 27
    public void createTaxReport(Integer auditorId, Integer businessId) {
        Auditor auditor = auditorRepository.findAuditorsById(auditorId);
        if (auditor == null)
            throw new ApiException("auditor not found");
        Business business = businessRepository.findBusinessById(businessId);
        if (business == null)
            throw new ApiException("this business not found");
        List<Sales> sales = salesRepository.findSalesByBusinessId(businessId);
        if (sales.isEmpty())
            throw new ApiException("there are no sales for this business");
        Double totalTax = 0.0;
        TaxReports taxReports = new TaxReports();
        for (Sales s : sales) {
//            if (s.getTaxReports()!=null)
//                throw new ApiException("this invoice tax was paid");
            totalTax += s.getTax_amount();
            s.setTaxReports(taxReports);
        }
        taxReports.setStart_date(LocalDateTime.now().minusDays(90));
        taxReports.setBusiness(business);
        taxReports.setTotalTax(totalTax);
        taxReports.setEnd_date(LocalDateTime.now());
        taxReports.setStatus("Pending");

        businessRepository.save(business);
        salesRepository.saveAll(sales);
        taxReportsRepository.save(taxReports);
    }


    // Khalid almutiri
    public void approveTaxReportStatus(Integer taxReportId, Integer auditorId) {
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

        taxReport.setStatus("Approved");
        taxReportsRepository.save(taxReport);
    }

    // Khalid almutiri
    public void rejectTaxReportStatus(Integer taxReportId, Integer auditorId) {
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

        taxReport.setStatus("Rejected");
        taxReportsRepository.save(taxReport);
    }


    public void activateBusiness(Integer auditId, Integer taxPayerId, Integer businessId) {

        Auditor auditor = auditorRepository.findAuditorsById(auditId);
        if (auditor == null) {
            throw new ApiException("auditor not found");
        }

        TaxPayer taxPayer = taxPayerRepository.findTaxBuyerById(taxPayerId);
        if (taxPayer == null) {
            throw new ApiException("The Taxpayer is not found");
        }


        Business business = businessRepository.findBusinessByIdAndTaxPayer(businessId, taxPayer);
        if (business == null) {
            throw new ApiException("The business is not found or not related to the tax payer");
        }


        if (business.getIsActive()) {
            throw new ApiException("The business is already activated");

        }


        business.setIsActive(true);
        businessRepository.save(business);

    }


    public void blockBusiness(Integer auditId, Integer taxPayerId, Integer businessId) {

        Auditor auditor = auditorRepository.findAuditorsById(auditId);
        if (auditor == null) {
            throw new ApiException("auditor not found");
        }

        TaxPayer taxPayer = taxPayerRepository.findTaxBuyerById(taxPayerId);

        if (taxPayer == null) {
            throw new ApiException("The Taxpayer is not found");
        }


        Business business = businessRepository.findBusinessByIdAndTaxPayer(businessId, taxPayer);
        if (business == null) {
            throw new ApiException("The business is not found or not related to the tax payer");
        }


        if (!business.getIsActive()) {
            throw new ApiException("The business is already inactive");

        }

        business.setIsActive(false);
        businessRepository.save(business);

    }


    // Khalid almutiri
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void createAdminUser() {
        if (myUserRepository.findUserByUsername("admin") == null) {
            MyUser admin = new MyUser();
            admin.setName("Admin User");
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("Admin12345"));
            admin.setRole("ADMIN");

            myUserRepository.save(admin);
            System.out.println("Admin user created");
        } else {
            System.out.println("Admin user already exists");
        }
    }

}
