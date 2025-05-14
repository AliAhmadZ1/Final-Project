//package com.example.final_project;
//
//
//import com.example.final_project.Controller.BusinessController;
//import com.example.final_project.Service.BusinessService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeEach;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//@Import(BusinessControllerTest.BusinessServiceMockConfig.class)
//public class BusinessControllerTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    BusinessService businessService;
//
//    @BeforeEach
//    void setUp() {
//
//        when(businessService.businessRevenue(1, 2)).thenReturn(5000.0);
//    }
//
//    @Test
//    public void testBusinessRevenue() throws Exception {
//        mockMvc.perform(get("/api/v1/business/business-revenue/1/2"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("The total revenue of the business: 5000.0"));
//    }
//
//
//    @TestConfiguration
//    static class BusinessServiceMockConfig {
//        @Bean
//        public BusinessService businessService() {
//            return mock(BusinessService.class);
//        }
//    }
//}
