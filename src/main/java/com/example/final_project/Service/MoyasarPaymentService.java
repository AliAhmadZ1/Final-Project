package com.example.final_project.Service;

import com.example.final_project.Api.ApiException;
import com.example.final_project.Model.MoyasarPayment;
import com.example.final_project.Model.Payment;
import com.example.final_project.Model.TaxPayer;
import com.example.final_project.Model.TaxReports;
import com.example.final_project.Repository.TaxPayerRepository;
import com.example.final_project.Repository.TaxReportsRepository;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
//@RequiredArgsConstructor
public class MoyasarPaymentService {

    private final TaxReportsRepository taxReportsRepository;

    @Value("${moyasar.api.key}")
    private String apiKey;

    private static final String MOYASAR_API_URL = "https://api.moyasar.com/v1/payments/";

    public MoyasarPaymentService(TaxReportsRepository taxReportsRepository) {
        this.taxReportsRepository = taxReportsRepository;
    }

    // Ali Ahmed Alshehri
    public ResponseEntity<?> processPayment(Integer taxReportId, MoyasarPayment moyasarPayment) throws IOException {
        TaxReports taxReports = taxReportsRepository.findById(taxReportId)
                .orElseThrow(() -> new ApiException("Tax Report not found"));

        if ("Paid".equalsIgnoreCase(taxReports.getStatus())) {
            throw new ApiException("This TaxReport is already paid");
        }

        int amount = moyasarPayment.getAmount(); // amount already provided in the `MoyasarPayment`
        String stAmount = String.valueOf(amount);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(apiKey, "");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("amount", stAmount);
        params.add("currency", "SAR");
        params.add("description", moyasarPayment.getDescription());
        params.add("callback_url", "http://localhost:5000/api/v1/moyasar-payment/callback?taxReportId=" + taxReportId);
        params.add("source[type]", "card"); // يمكن تغيير النوع هنا
        params.add("source[name]", moyasarPayment.getName());
        params.add("source[number]", moyasarPayment.getNumber());
        params.add("source[month]", moyasarPayment.getMonth());
        params.add("source[year]", moyasarPayment.getYear());
        params.add("source[cvc]", moyasarPayment.getCvc());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(MOYASAR_API_URL, request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.getBody());

        String paymentId = json.get("id").asText();
        String transactionUrl = json.get("transaction_url").asText();

        taxReports.setStatus("Pending");
        taxReportsRepository.save(taxReports);

        return ResponseEntity.status(response.getStatusCode()).body(transactionUrl);
    }

    // Ali Ahmed Alshehri
    public TaxReports callback(String paymentId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(MOYASAR_API_URL + paymentId, HttpMethod.GET, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.getBody());

        String status = json.get("status").asText();
        String taxReportIdStr = json.get("metadata") != null && json.get("metadata").has("taxReportId")
                ? json.get("metadata").get("taxReportId").asText()
                : null;

        if (taxReportIdStr == null) {
            throw new ApiException("Metadata is missing taxReportId");
        }

        Integer taxReportId = Integer.parseInt(taxReportIdStr);
        TaxReports taxReport = taxReportsRepository.findById(taxReportId)
                .orElseThrow(() -> new ApiException("Tax Report not found"));

        if ("paid".equalsIgnoreCase(status)) {
            taxReport.setStatus("Paid");
            taxReport.setPaymentDate(LocalDate.now());
        } else {
            taxReport.setStatus("Rejected");
        }

        taxReportsRepository.save(taxReport);
        return taxReport;
    }
}
