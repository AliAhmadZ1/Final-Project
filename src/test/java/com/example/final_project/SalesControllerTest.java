//package com.example.final_project;
//
//import com.example.final_project.DTO.ProductDTO;
//import com.example.final_project.Service.SalesService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//@Import(SalesControllerTest.SalesServiceTestConfig.class)
//public class SalesControllerTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    SalesService salesService;
//
////    ProductDTO productDTO;
//
//    @BeforeEach
//    void setUp() {
//        productDTO = new ProductDTO("Nova water", 1.0, 5,"3456184658");
//    }
//
//    @Test
//    public void testAddProductInSale() throws Exception {
//        mockMvc.perform(put("/api/v1/sales/add-product-in-sale/{accountantId}/{saleId}", 1, 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(productDTO)))
//                .andExpect(status().isOk());
//
//    }
//
//
//    @TestConfiguration
//    static class SalesServiceTestConfig {
//        @Bean
//        public SalesService salesService() {
//            return mock(SalesService.class);
//        }
//    }
//}
