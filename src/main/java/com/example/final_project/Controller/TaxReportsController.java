package com.example.final_project.Controller;

import com.example.final_project.Api.ApiResponse;
import com.example.final_project.Model.MyUser;
import com.example.final_project.Model.TaxReports;
import com.example.final_project.Service.TaxReportsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/taxReports")
@RequiredArgsConstructor
public class TaxReportsController {

    private final TaxReportsService taxReportsService;

    // authority -> Auditor
    @GetMapping("/get")
    public ResponseEntity getAllTaxReports(@AuthenticationPrincipal MyUser myUser){
        return ResponseEntity.status(HttpStatus.OK).body(taxReportsService.getAllTaxReports(myUser.getId()));
    }

    // authority -> Auditor
    @PostMapping("/add/{business_id}")
    public ResponseEntity addTaxReports(@AuthenticationPrincipal MyUser myUser, @PathVariable Integer business_id, @Valid @RequestBody TaxReports taxReports){
        taxReportsService.addTaxReports(myUser.getId(), business_id, taxReports);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Tax Reports is added!"));
    }

    // authority -> Auditor
    @PutMapping("/update/{id}")
    public ResponseEntity updateTaxReports(@AuthenticationPrincipal MyUser myUser, @PathVariable Integer id, @Valid @RequestBody TaxReports taxReports){
        taxReportsService.updateTaxReports(myUser.getId(),id, taxReports);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Tax Reports is updated"));
    }

    // authority -> Admin
    @DeleteMapping("delete/{id}")
    public ResponseEntity deleteTaxReports(@AuthenticationPrincipal MyUser myUser, @PathVariable Integer id){
        taxReportsService.deleteTaxReports(myUser.getId(),id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Tax Reports is deleted"));
    }

    //فرض غرامة تاخير سداد بعدها بشهر
    // authority -> Auditor
    @PutMapping("/apply-penalty/{taxReportId}")
    public ResponseEntity applyLatePaymentPenalty(@AuthenticationPrincipal MyUser myUser, @PathVariable Integer taxReportId) {
        taxReportsService.applyLatePaymentPenalty(myUser.getId(),taxReportId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Late payment penalty applied if applicable"));
    }

    //فرض غرامة تاخير سداد بعدها بشهرين
    // authority -> Auditor
    @PutMapping("/apply-2month-penalty/{taxReportId}")
    public ResponseEntity applyTwoMonthLatePenalty(@AuthenticationPrincipal MyUser myUser, @PathVariable Integer taxReportId) {
        taxReportsService.applyTwoMonthLatePenalty(myUser.getId(),taxReportId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("10% penalty applied for 2-month late payment"));
    }

    //في حال تجاوز 90 يوم يتم اتخاذ الإجراءات القانونية
    // authority -> Auditor
    @PutMapping("/apply-legal-action/{taxReportId}")
    public ResponseEntity applyLegalAction(@AuthenticationPrincipal MyUser myUser, @PathVariable Integer taxReportId) {
        taxReportsService.applyLegalActionForTaxEvasion(myUser.getId(),taxReportId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Legal action applied for tax evasion"));
    }



//################################
// التقارير المستحقه لدفع وغير مدفوعه
// authority -> Auditor
    @GetMapping("/due-payment")
    public ResponseEntity getUnpaidDueTaxReports(@AuthenticationPrincipal MyUser myUser) {
        return ResponseEntity.status(HttpStatus.OK).body(taxReportsService.getUnpaidDueTaxReports(myUser.getId()));
    }


    // authority -> Auditor
    @GetMapping("/reports/{auditorId}")
    public ResponseEntity getReportsByAuditor(@AuthenticationPrincipal MyUser myUser) {
        return ResponseEntity.status(HttpStatus.OK).body(taxReportsService.getReportsByAuditor(myUser.getId()));
    }

    // 2. Get report count by status for an auditor
    // authority -> Auditor
    @GetMapping("/report-count/{auditorId}/{status}")
    public ResponseEntity getReportCountByStatus(@AuthenticationPrincipal MyUser myUser, @PathVariable String status) {
        return ResponseEntity.status(HttpStatus.OK).body(taxReportsService.getReportCountByStatus(myUser.getId(), status));
    }

    // 4. Get approval rate for an auditor
    // authority -> Auditor
    @GetMapping("/approval-rate/{auditorId}")
    public ResponseEntity getApprovalRate(@AuthenticationPrincipal MyUser myUser) {
        return ResponseEntity.status(HttpStatus.OK).body(taxReportsService.getApprovalRate(myUser.getId()));
    }

    // 5. Bulk approve reports for an auditor
    // authority -> Auditor
    @PutMapping("/bulk-approve/{auditorId}")
    public ResponseEntity bulkApproveReports(@AuthenticationPrincipal MyUser myUser, @RequestBody List<Integer> reportIds) {
        taxReportsService.bulkApproveReports(myUser.getId(), reportIds);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Reports approved successfully"));
    }

    // 7. Get latest report reviewed by auditor
    // authority -> Auditor
    @GetMapping("/latest-report")
    public ResponseEntity getLatestReport(@AuthenticationPrincipal MyUser myUser) {
        return ResponseEntity.status(HttpStatus.OK).body(taxReportsService.getLatestReportByAuditor(myUser.getId()));
    }

    // authority -> Auditor
    @GetMapping("/unapproved")
    public ResponseEntity getUnapprovedTaxReports(@AuthenticationPrincipal MyUser myUser) {
        return ResponseEntity.status(200).body(taxReportsService.getUnapprovedTaxReports(myUser.getId()));
    }

    // authority -> Auditor
    @GetMapping("/{reportId}/payment-status")
    public ResponseEntity getPaymentStatus(@AuthenticationPrincipal MyUser myUser, @PathVariable Integer reportId) {
        return ResponseEntity.status(200).body(taxReportsService.getPaymentStatusByReportId(myUser.getId(),reportId));
    }

    // DUJA
    @GetMapping("/notify-upcoming-payment")
    public ResponseEntity<String> notifyUpcomingPayments() {
        taxReportsService.notifyUpcomingPayments();
        return ResponseEntity.status(200).body("Notifications sent to taxpayers with near-due reports.");
    }

    // Endpoint 37
    // authority -> TaxPayer
    @GetMapping("/get-tax-reports/tax-payer/{taxPayerId}")
    public ResponseEntity printTaxReportForEveryBusinesses(@AuthenticationPrincipal MyUser myUser){
        return ResponseEntity.status(200).body(taxReportsService.printTaxReportForEveryBusinesses(myUser.getId()));
    }

    ///Auth-> TaxPayer
    @GetMapping("/print/{reportId}")
    public ResponseEntity<byte[]> printTaxReportAsPdf(@AuthenticationPrincipal MyUser myUser, @PathVariable Integer reportId) {
        byte[] pdf = taxReportsService.getTaxReportAsPdf(myUser.getId(),reportId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tax-report-" + reportId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
