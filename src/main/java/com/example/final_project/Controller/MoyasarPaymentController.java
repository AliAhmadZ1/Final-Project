package com.example.final_project.Controller;

import com.example.final_project.Api.ApiResponse;
import com.example.final_project.Model.MoyasarPayment;
import com.example.final_project.Model.TaxReports;
import com.example.final_project.Model.MyUser;
import com.example.final_project.Service.MoyasarPaymentService;
import com.example.final_project.Service.PaymentService;
import com.example.final_project.Service.TaxReportsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/moyasar-payment")
@RequiredArgsConstructor
public class MoyasarPaymentController {

    private final MoyasarPaymentService moyasarPaymentService;

    @Value("${moyasar.api.key}")
    private String apiKey;

    @PostMapping("/pay-tax/tax-report/{taxReportId}")
    public ResponseEntity<?> processPayment(@PathVariable Integer taxReportId, @RequestBody MoyasarPayment moyasarPayment) throws IOException {
        return moyasarPaymentService.processPayment(taxReportId, moyasarPayment);
    }

    @RequestMapping(value = "/callback", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> callbackHandler(HttpServletRequest request) {
        String paymentId = request.getParameter("id");
        String taxReportIdStr = request.getParameter("taxReportId");

        if (paymentId == null || taxReportIdStr == null) {
            return ResponseEntity.status(400).body(new ApiResponse("Missing payment ID or tax report ID"));
        }

        try {
            moyasarPaymentService.callback(paymentId,taxReportIdStr); // استدعاء callback
            return ResponseEntity.ok("Tax Report payment process done!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse("Payment failed: " + e.getMessage()));
        }
    }

}
