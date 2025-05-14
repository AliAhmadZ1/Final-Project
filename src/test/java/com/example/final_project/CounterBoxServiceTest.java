//package com.example.final_project;
//
//
//import com.example.final_project.Model.Accountant;
//import com.example.final_project.Model.CounterBox;
//import com.example.final_project.Repository.AccountantRepository;
//import com.example.final_project.Repository.CounterBoxRepository;
//import com.example.final_project.Service.CounterBoxService;
//import com.example.final_project.Api.ApiException;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class CounterBoxServiceTest {
//    @InjectMocks
//    CounterBoxService counterBoxService;
//
//    @Mock
//    CounterBoxRepository counterBoxRepository;
//
//    @Mock
//    AccountantRepository accountantRepository;
//
//    CounterBox counterBox;
//    Accountant accountant;
//
//    @BeforeEach
//    void setUp() {
//        accountant = new Accountant();
//        accountant.setId(1);
//
//        counterBox = new CounterBox();
//        counterBox.setId(1);
//        counterBox.setStatus("Closed");
//        counterBox.setOpenDatetime(null);
//    }
//
//    @Test
//    public void openCounterBox_success() {
//        when(counterBoxRepository.findCounterBoxById(1)).thenReturn(counterBox);
//        when(accountantRepository.getReferenceById(1)).thenReturn(accountant);
//
//        counterBoxService.openCounterBox(1, 1);
//
//        Assertions.assertEquals("Opened", counterBox.getStatus());
//        Assertions.assertEquals(accountant, counterBox.getAccountant());
//        Assertions.assertNotNull(counterBox.getOpenDatetime());
//
//        verify(counterBoxRepository, times(1)).save(counterBox);
//    }
//
//    @Test
//    public void openCounterBox_notFound() {
//        when(counterBoxRepository.findCounterBoxById(1)).thenReturn(null);
//
//        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
//            counterBoxService.openCounterBox(1, 1);
//        });
//
//        Assertions.assertEquals("CounterBox not found", exception.getMessage());
//    }
//
//    @Test
//    public void openCounterBox_alreadyOpened() {
//        counterBox.setOpenDatetime(LocalDateTime.now());
//        when(counterBoxRepository.findCounterBoxById(1)).thenReturn(counterBox);
//
//        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
//            counterBoxService.openCounterBox(1, 1);
//        });
//
//        Assertions.assertEquals("CounterBox is already opened", exception.getMessage());
//    }
//
//    @Test
//    public void openCounterBox_accountantNotFound() {
//        when(counterBoxRepository.findCounterBoxById(1)).thenReturn(counterBox);
//        when(accountantRepository.getReferenceById(1)).thenReturn(null);
//
//        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
//            counterBoxService.openCounterBox(1, 1);
//        });
//
//        Assertions.assertEquals("Accountant not found", exception.getMessage());
//    }
//}
