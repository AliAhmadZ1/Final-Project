//package com.example.final_project;
//
//import com.example.final_project.DTO.DTOAuditor;
//import com.example.final_project.Model.Accountant;
//import com.example.final_project.Model.Auditor;
//import com.example.final_project.Model.MyUser;
//import com.example.final_project.Model.TaxPayer;
//import com.example.final_project.Repository.AccountantRepository;
//import com.example.final_project.Repository.AuditorRepository;
//import com.example.final_project.Repository.MyUserRepository;
//import com.example.final_project.Repository.TaxPayerRepository;
//import com.example.final_project.Service.AuditorService;
//import com.example.final_project.Service.TaxPayerService;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class AuditorServiceTest{
//
//
//
//    @InjectMocks
//    AuditorService auditorService;
//
//    @InjectMocks
//    TaxPayerService taxPayerService;
//
//    @Mock
//    AuditorRepository auditorRepository;
//    @Mock
//    MyUserRepository myUserRepository;
//    @Mock
//    AccountantRepository accountantRepository;
//    @Mock
//    TaxPayerRepository taxPayerRepository;
//
//    MyUser myUser;
//
//    Auditor auditor1,auditor2,auditor3;
//
//    DTOAuditor dtoAuditor;
//
//    List<Auditor> auditors;
//
//    TaxPayer taxPayer;
//
//    Accountant accountant;
//
//    @BeforeEach
//    void setUP(){
//        myUser =new MyUser(null,"khalid","khalid1","Kk1234","khalidss@gmail.com","Auditor",null,null,null);
//        auditor1 = new Auditor(null,"Aa", myUser,null);
//        auditor2=new Auditor(null,"Kk", myUser,null);
//        auditor3=new Auditor(null,"Bb", myUser,null);
//        auditors=new ArrayList<>();
//        auditors.add(auditor1);
//        auditors.add(auditor2);
//        auditors.add(auditor3);
//
//        dtoAuditor=new DTOAuditor("ali","ali12","ali12","ali123","Aaa");
//
//        taxPayer = new TaxPayer(1,"0546123496","1122333345", LocalDateTime.now(),true,null,null,null,null);
//        accountant = new Accountant(2,"12457",false,LocalDateTime.now(),"0566779966",LocalDateTime.now(),null,null,null,null);
//
//    }
//
//
//    // khalid
//    @Test
//    public void getAllAuditors_success(){
//        when(myUserRepository.findUserByIdAndRole(myUser.getId(), "ADMIN")).thenReturn(myUser);
//        when(auditorRepository.findAll()).thenReturn(auditors);
//
//        List<Auditor> result = auditorService.getAllAuditors(myUser.getId());
//
//        Assertions.assertEquals(3, result.size());
//        Assertions.assertEquals("Aa", result.get(0).getSOCPA());
//        verify(myUserRepository, times(1)).findUserByIdAndRole(myUser.getId(), "ADMIN");
//        verify(auditorRepository, times(1)).findAll();
//    }
//
//
//    // khalid
//    @Test
//    public void addAuditor(){
//        when(myUserRepository.findUserByIdAndRole(myUser.getId(), "ADMIN")).thenReturn(myUser);
//
//        auditorService.addAuditor(myUser.getId(), dtoAuditor);
//
//        verify(myUserRepository, times(1)).findUserByIdAndRole(myUser.getId(), "ADMIN");
//        verify(auditorRepository, times(1)).save(any(Auditor.class));
//    }
//
//    // Ali
//    @Test
//    public void activateAccountantTest(){
//        when(taxPayerRepository.findTaxBuyerById(taxPayer.getId())).thenReturn(taxPayer);
//        when(accountantRepository.findAccountantById(accountant.getId())).thenReturn(accountant);
//
//        taxPayerService.activateAccountant(taxPayer.getId(),accountant.getId());
//
//        verify(taxPayerRepository,times(1)).findTaxBuyerById(taxPayer.getId());
//        verify(accountantRepository,times(1)).findAccountantById(accountant.getId());
//        verify(accountantRepository,times(1)).save(accountant);
//    }
//
//
//}
