package com.example.final_project.Service;

import com.example.final_project.Api.ApiException;
import com.example.final_project.Model.MoyasarPayment;
import com.example.final_project.Model.Payment;
import com.example.final_project.Model.TaxPayer;
import com.example.final_project.Model.TaxReports;
import com.example.final_project.Repository.PaymentRepository;
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
@RequiredArgsConstructor
public class MoyasarPaymentService {

    private final TaxReportsRepository taxReportsRepository;
    private final PaymentRepository paymentRepository;

    @Value("${moyasar.api.key}")
    private String apiKey;

    private static final String MOYASAR_API_URL = "https://api.moyasar.com/v1/payments/";


    // Ali Ahmed Alshehri
    public ResponseEntity<?> processPayment(Integer taxReportId, MoyasarPayment moyasarPayment) throws IOException {
//        TaxReports taxReports = taxReportsRepository.findById(taxReportId)
//                .orElseThrow(() -> new ApiException("Tax Report not found"));

        TaxReports taxReports = taxReportsRepository.findTaxReportsById(taxReportId);
        if(taxReports==null)
            throw new ApiException("Tax Report not found");

        if ("Paid".equalsIgnoreCase(taxReports.getStatus())) {
            throw new ApiException("This TaxReport is already paid");
        }

        double amountInt = taxReports.getTotalTax()*100;
        int amount = (int) amountInt;
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

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    // Ali Ahmed Alshehri
    public void callback(String paymentId,String taxReportId) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(MOYASAR_API_URL + paymentId, HttpMethod.GET, entity, String.class);

        int id = Integer.parseInt(taxReportId);
        TaxReports taxReports = taxReportsRepository.findTaxReportsById(id);
        Payment payment = new Payment();
        if (getPaymentStatus(paymentId).equalsIgnoreCase("Paid")) {
            taxReports.setStatus("Paid");
            payment.setStatus("Paid");
        }else {
            payment.setStatus("Unpaid");
        }

        payment.setPaymentId(paymentId);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setName(taxReports.getBusiness().getTaxPayer().getMyUser().getName());
        payment.setTaxReports(taxReports);
        payment.setTaxPayer(taxReports.getBusiness().getTaxPayer());

        paymentRepository.save(payment);
        taxReportsRepository.save(taxReports);

    }

    public String getPaymentStatus(String payment_id){

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey,"");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(MOYASAR_API_URL + payment_id,
                HttpMethod.GET,entity, String.class);
        String resp = response.getBody().toString();
        if (response.getBody().contains("paid"))
            return "Paid";
        else
            return "Unpaid";
    }


}
