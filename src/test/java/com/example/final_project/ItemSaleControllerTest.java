//package com.example.final_project;
//
//import com.example.final_project.Controller.ItemSaleController;
//import com.example.final_project.Model.ItemSale;
//import com.example.final_project.Model.MyUser;
//import com.example.final_project.Service.ItemSaleService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.Mockito.when;
//import static org.mockito.Mockito.mock;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(controllers = ItemSaleController.class)
//@AutoConfigureMockMvc
//@Import(ItemSaleControllerTest.MockConfig.class)
//public class ItemSaleControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ItemSaleService itemSaleService;
//
//    @Test
//    void testGetItemSalesBySalesId() throws Exception {
//        // Fake ItemSale
//        ItemSale item = new ItemSale();
//        item.setProductName("Test Product");
//
//        // Mock service behavior
//        when(itemSaleService.getItemSalesBySalesId(Mockito.eq(1), Mockito.eq(10)))
//                .thenReturn(List.of(item));
//
//        // Inject mock MyUser into Spring Security context
//        MyUser mockUser = new MyUser();
//        mockUser.setId(1);  // Important: used in controller
//
//        UsernamePasswordAuthenticationToken auth =
//                new UsernamePasswordAuthenticationToken(mockUser, null, List.of());
//
//        SecurityContextHolder.getContext().setAuthentication(auth);
//
//        // Perform request
//        mockMvc.perform(get("/api/v1/item-sales/sales/10")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].productName").value("Test Product"));
//
//        // Clean up
//        SecurityContextHolder.clearContext();
//    }
//
//    @TestConfiguration
//    static class MockConfig {
//        @Bean
//        public ItemSaleService itemSaleService() {
//            return mock(ItemSaleService.class);
//        }
//    }
//}
