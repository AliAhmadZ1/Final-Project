package com.example.final_project.Service;


import com.example.final_project.Api.ApiException;
import com.example.final_project.DTO.AccountantDTO;
import com.example.final_project.DTO.TaxPayerDTO;
import com.example.final_project.Model.Accountant;
import com.example.final_project.Model.Business;
import com.example.final_project.Model.TaxPayer;
import com.example.final_project.Model.MyUser;
import com.example.final_project.Model.*;
import com.example.final_project.Notification.NotificationService;
import com.example.final_project.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.support.JdbcAccessor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.*;
import java.time.Month;
import java.util.List;


@Service
@RequiredArgsConstructor


public class TaxPayerService {


    private final TaxPayerRepository taxPayerRepository;
    private final MyUserRepository myUserRepository;
    private final AccountantRepository accountantRepository;
    private final NotificationService notificationService;
    private final BusinessRepository businessRepository;
    private final SalesRepository salesRepository;

    private final JdbcAccessor jdbcAccessor;


    private final CounterBoxRepository counterBoxRepository;
    private final BranchRepository branchRepository;
//    private final WhatsAp-delete-pService whatsAppService;
    //  private final WhatsAp-delete-pService whatsAppService;


    /// run by admin
    public void activateTP(Integer adminId, Integer taxPayerId) {
        MyUser admin = myUserRepository.findUserByIdAndRole(adminId,"ADMIN");
        if (admin==null)
            throw new ApiException("you don't have permission");
        TaxPayer taxPayer = taxPayerRepository.findTaxBuyerById(taxPayerId);
        if (taxPayer == null) {
            throw new ApiException("Tax Payer is not found");
        }
        if (taxPayer.getIsActive()) {
            throw new ApiException("Tax Payer is already active");
        }
        taxPayer.setIsActive(true);
        taxPayerRepository.save(taxPayer);


        String subject = "Successful Activation of Your Account as a Taxpayer";
        String message = "Dear : " + taxPayer.getMyUser().getName() + " We are pleased to inform you that your account has been successfully activated you can now use our services :\n" +
                "Best regards,\n" +
                "[mohasil team]";


        notificationService.sendEmail(taxPayer.getMyUser().getEmail(), subject, message);

    }


    public List<TaxPayer> getAllTaxTaxPayers(Integer AuditId) {
        return taxPayerRepository.findAll();
    }


    public void register(TaxPayerDTO taxPayerDTO) {
        MyUser user = new MyUser();
        if (taxPayerRepository.findTaxPayerByCommercialRegistration(taxPayerDTO.getCommercialRegistration()) != null) {
            throw new ApiException("A Taxpayer With the same commercial registration number already exit");

        }
        user.setRole("TAXPAYER");
        user.setName(taxPayerDTO.getName());
        user.setUsername(taxPayerDTO.getUsername());
        String hashPassword = new BCryptPasswordEncoder().encode(taxPayerDTO.getPassword());
        user.setPassword(hashPassword);
        user.setEmail(taxPayerDTO.getEmail());


        TaxPayer taxPayer = new TaxPayer();
        taxPayer.setPhoneNumber(taxPayerDTO.getPhoneNumber());
        taxPayer.setCommercialRegistration(taxPayerDTO.getCommercialRegistration());


        taxPayer.setMyUser(user);
        taxPayer.setIsActive(false);
        taxPayer.setCommercialRegistration(taxPayerDTO.getCommercialRegistration());
        taxPayer.setPhoneNumber(taxPayerDTO.getPhoneNumber());

        taxPayer.setRegistrationDate(LocalDateTime.now());
        user.setTaxPayer(taxPayer);

        taxPayerRepository.save(taxPayer);
        myUserRepository.save(user);

    }


    public void updateTaxPayer(Integer taxPayerId, TaxPayerDTO taxPayerDTO) {

        TaxPayer taxPayer = taxPayerRepository.findTaxBuyerById(taxPayerId);

        if (taxPayer == null) {
            throw new ApiException("The Taxpayer is not found");
        }

        taxPayer.getMyUser().setEmail(taxPayerDTO.getEmail());
        taxPayer.getMyUser().setUsername(taxPayerDTO.getUsername());
        String hashPassword = new BCryptPasswordEncoder().encode(taxPayerDTO.getPassword());
        taxPayer.getMyUser().setPassword(hashPassword);


        taxPayer.setPhoneNumber(taxPayerDTO.getPhoneNumber());
        taxPayer.setCommercialRegistration(taxPayerDTO.getCommercialRegistration());

        taxPayerRepository.save(taxPayer);

    }


    public void deleteTaxPayer(Integer taxPayerId) {

        TaxPayer taxPayer = taxPayerRepository.findTaxBuyerById(taxPayerId);
        if (taxPayer == null) {
            throw new ApiException("The Taxpayer is not found");
        }
        myUserRepository.delete(taxPayer.getMyUser());
    }


    public void addAccountant(Integer taxPayerID, Integer businessId, AccountantDTO accountantDTO) {
        TaxPayer taxPayer = taxPayerRepository.findTaxBuyerById(taxPayerID);
        if (taxPayer == null) {
            throw new ApiException("The Taxpayer is not found");
        }
        Business business1 = businessRepository.findBusinessByIdAndTaxPayer(businessId, taxPayer);
        if (business1 == null) {
            throw new ApiException("Business is not found or does not belong to tax payer");
        }

        MyUser myUserACC = new MyUser();
        myUserACC.setRole("ACCOUNTANT");
        myUserACC.setName(accountantDTO.getName());
        myUserACC.setUsername(accountantDTO.getUsername());
        String hashPassword = new BCryptPasswordEncoder().encode(accountantDTO.getPassword());
        myUserACC.setPassword(hashPassword);
        myUserACC.setEmail(accountantDTO.getEmail());

        Accountant accountant = new Accountant();
        accountant.setEmployeeId(accountantDTO.getEmployeeId());
        accountant.setRegistrationDate(LocalDateTime.now());
        accountant.setPhoneNumber(accountantDTO.getPhoneNumber());
        accountant.setMyUser(myUserACC);
        accountant.setIsActive(true);
        accountant.setBusiness(business1);
        myUserACC.setAccountant(accountant);
        accountantRepository.save(accountant);
        myUserRepository.save(myUserACC);


        String subject = "Successful Activation of Your Account";
        String message = "We are pleased to inform you that your account has been successfully activated with the authority of an Accountant. Below are your login details:\n" +
                "\n" +
                "Username: \n" + accountant.getMyUser().getUsername() +
                "\n" +
                "Password:\n" + accountantDTO.getPassword() +
                "\n" +
                "Employee Code:\n" + accountant.getEmployeeId() +
                "\n" +
                "Please keep this information secure and do not share it with anyone." +
                "\n note: Inactive accounts will be deleted in 30 \n" +
                "\n" +
                "If you have any questions or need assistance, feel free to contact us.\n" +
                "\n" +
                "Best regards,\n" +
                "[mohasil team]";


        String phone = accountantDTO.getPhoneNumber();
        if (phone.startsWith("0")) {
            phone = phone.substring(1);
        }
        String fullPhoneNumber = "966" + phone;

//        whatsAppService.sendAccountantActivationMessage(
//                accountantDTO.getUsername(),
//                accountantDTO.getPassword(),
//                accountantDTO.getEmployeeId(),
//                fullPhoneNumber,
//                LocalDate.now()
//        );


        notificationService.sendEmail(accountant.getMyUser().getEmail(), subject, message);
    }





    public void notify20daysInactivity(Integer accountantId){
        Accountant accountant = accountantRepository.findAccountantByIsActiveAndId(true, accountantId);
        if (accountant == null) {
            throw new ApiException("Accountant is not found or not active");
        }
        String subject = "Warning ! Inactivity Detected ";
        String message = "Dear : " + accountant.getMyUser().getName() + " We have noticed that your account has been inactive for over 20 days since registration ." +
                "please ensure you resume activity within the next 10 days to avoid deactivation \n" +
                "Best regards,\n" +
                "[mohasil team]";

        notificationService.sendEmail(accountant.getMyUser().getEmail(), subject, message);
    }






    /// if accountant has not opened since the register date or not opened for a 30 days
    public void blockUnnActiveAccountant(Integer taxPayerId, Integer accountantId) {
        TaxPayer taxPayer = taxPayerRepository.findTaxBuyerById(taxPayerId);
        if (taxPayer == null) {
            throw new ApiException("The Taxpayer is not found");
        }

        Accountant accountant = accountantRepository.findAccountantByIsActiveAndId(true, accountantId);
        if (accountant == null) {
            throw new ApiException("Accountant is not found or not active");
        }

        Business business = businessRepository.findBusinessByIdAndTaxPayer(accountant.getBusiness().getId(), taxPayer);
        if (!business.getIsActive()) {
            throw new ApiException("Your business that is related to this branch is not active");
        }



        LocalDateTime activeDate=accountant.getLastActiveCounterBox();
        LocalDateTime thirtyDays=LocalDateTime.now().minusDays(30);
        LocalDateTime twentyDays=LocalDateTime.now().minusDays(20);


        /// 1 check if the accountant never logged since registration
        if (activeDate == null) {
            /// 30 days since registration and non activity
            if (accountant.getRegistrationDate().isBefore(thirtyDays)) {
                accountant.setIsActive(false);
                accountantRepository.save(accountant);
                throw new ApiException("The accountant has never been active!");

            }else if (accountant.getRegistrationDate().isBefore(twentyDays)){///  20 days non active
                notify20daysInactivity(accountantId);
                throw new ApiException("The accountant is been inactive for 20 days , accountant has been notified");
            }
        }


        /// / if the accountant is active but not for 20 daya

        if(activeDate!=null && activeDate.isBefore(thirtyDays)){
            accountant.setIsActive(false);
            accountantRepository.save(accountant);

        }else if(activeDate!=null && activeDate.isBefore(twentyDays)){
            notify20daysInactivity(accountantId);
            throw new ApiException("The accountant is been inactive for 20 days , accountant has been notified");

        }
    }

    // Displays all accountants associated with the TB across all branches affiliated with him
    public List<Map<String, Object>> getAccountantsByTaxPayerId (Integer taxPayerId){
        List<Object[]> rows = accountantRepository.findAccountantsByTaxPayerId(taxPayerId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("taxPayerId", row[0]);
            map.put("commercialRegistration", row[1]);
            map.put("employeeId", row[2]);
            map.put("isActive", row[3]);
            map.put("branchId", row[4]);
            result.add(map);
        }

        return result;
    }


    public Double getYearRevenue (Integer taxPayerId, Integer businessId,int year){

        TaxPayer taxPayer = taxPayerRepository.findTaxBuyerById(taxPayerId);
        if (taxPayer == null) {
            throw new ApiException("The Taxpayer is not found");
        }

        Business business = businessRepository.findBusinessById(businessId);
        if (!business.getIsActive()) {
            throw new ApiException("The business is not found");
        }

        LocalDateTime startDate = LocalDateTime.of(year, Month.JANUARY, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, Month.DECEMBER, 31, 23, 59);
        List<Sales> yearlyRevenue = salesRepository.findSalesByBranch_BusinessAndSaleDateBetween(business, startDate, endDate);

        if (yearlyRevenue.isEmpty()) {
            throw new ApiException("No sales found for this business");

        }
        Double totalR = 0.0;
        for (Sales s : yearlyRevenue) {
            totalR += s.getGrand_amount();

        }
        return totalR;

    }

    // Ali Ahmed Alshehri
    // Endpoint 40
    public void activateAccountant(Integer taxPayerId, Integer accountantId) {
        TaxPayer taxPayer = taxPayerRepository.findTaxBuyerById(taxPayerId);
        Accountant accountant = accountantRepository.findAccountantById(accountantId);
        if (taxPayer == null)
            throw new ApiException("tax payer not found");
        if (accountant == null)
            throw new ApiException("accountant not found");

        if (accountant.getIsActive())
            throw new ApiException("accountant is already active");
        accountant.setIsActive(true);
        accountantRepository.save(accountant);
    }

    // Ali Ahmed Alshehri
    // Endpoint 41
    public void deActivateAccountant(Integer taxPayerId, Integer accountantId) {
        TaxPayer taxPayer = taxPayerRepository.findTaxBuyerById(taxPayerId);
        Accountant accountant = accountantRepository.findAccountantById(accountantId);
        if (taxPayer == null)
            throw new ApiException("tax payer not found");
        if (accountant == null)
            throw new ApiException("accountant not found");

        if (!accountant.getIsActive())
            throw new ApiException("accountant is already non active");

        accountant.setIsActive(false);
        accountantRepository.save(accountant);
    }

}

