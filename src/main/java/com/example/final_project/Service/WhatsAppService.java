//
//package com.example.final_project.Service;
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//
//@RequiredArgsConstructor
//
//@Service
//public class WhatsAppService {
//
//
//    public void sendAccountantActivationMessage(String username, String password, String employeeId, String phoneNumber, LocalDate createdDate) {
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//
//        String formattedDate = createdDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
//
//        String message = " Your accountant account has been activated successfully!\n\n"
//                + "Date: " + formattedDate + "\n"
//                + "Username: " + username + "\n"
//                + "Password: " + password + "\n"
//                + "employee ID: " + employeeId + "\n\n"
//                + "Please keep this information safe.\n\n"
//                + "For support, contact the Muhassel Team.";
//
//        Message.creator(
//                new PhoneNumber("whatsapp:" + phoneNumber),
//                new PhoneNumber(FROM_WHATSAPP_NUMBER),
//                message
//        ).create();
//    }
//}
//
