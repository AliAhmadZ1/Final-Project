//package com.example.final_project;
//
//import com.example.final_project.Api.ApiResponse;
//import com.example.final_project.Controller.ProductController;
//import com.example.final_project.Model.Accountant;
//import com.example.final_project.Model.Branch;
//import com.example.final_project.Model.Product;
//import com.example.final_project.Service.ProductService;
//import org.hamcrest.Matchers;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.ResultMatcher;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.springframework.http.ResponseEntity.status;
//
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(value = ProductController.class , excludeAutoConfiguration = {SecurityAutoConfiguration.class})
//public class BranchControllerTest {
//
//    @MockitoBean
//    ProductService productService;
//    @Autowired
//    MockMvc mockMvc;
//
//
//    ApiResponse apiResponse;
//    Accountant accountant;
//    Branch branch;
//    Product product;
//
//    @BeforeEach
//    void setUp() {
//
//        product = new Product(1,"water",12.0,24,"123456789",null,null);
//        accountant = new Accountant(2,"12457",false, LocalDateTime.now(),"0566779966",LocalDateTime.now(),null,null,null,null);
//        branch = new Branch(3,"1234","Riyadh","Riyadh",null,null,null,null,null);
//
//    }
//
////    @Test
////    public void addProductToBranchTest2() throws Exception {
////        // Arrange: Set up mock behavior
////        Product mockResponse = product; // Adjust according to your Product class
////        Mockito.when(productService.addProductToBranch(accountant.getId(), branch.getId(), product))
////                .thenReturn(mockResponse);
////
////        // Act & Assert: Perform the request and assert the result
////        mockMvc.perform(MockMvcRequestBuilders
////                        .post("/add-to-branch/{accountantId}/{branchId}", accountant.getId(), branch.getId())
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(new ObjectMapper().writeValueAsString(product)))
////                .andExpect(MockMvcResultMatchers.status().isOk())
////                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
////                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("added!!"));
////    }
////
////    @Test
////    public void addProductToBranchTest() throws Exception {
////        Mockito.doThrow()
////                .when(productService)
////                .addProductToBranch(accountant.getId(), branch.getId(), product);
////
////        Mockito.when(productService.addProductToBranch(accountant.getId(), branch.getId(), product));
////        mockMvc.perform(apiResponse("/add-to-branch/{accountantId}/{branchId}"))
////                .andExpect((ResultMatcher) status(200))
////                .andExpect(MockMvcResultMatchers.jsonPath("$",
////                        Matchers.hasSize(1)))
////                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("added!!"
////                ));
////    }
//
//    private RequestBuilder apiResponse(String s) {
//        return null;
//    }
//
//
//}
