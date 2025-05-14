package com.example.final_project.Controller;

import com.example.final_project.Api.ApiResponse;
import com.example.final_project.DTO.ProductDTO;
import com.example.final_project.Model.MyUser;
import com.example.final_project.Model.Product;
import com.example.final_project.Service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @GetMapping("/get")
    public ResponseEntity getAllProductForBusiness(@AuthenticationPrincipal MyUser accountant){
        return ResponseEntity.status(200).body(productService.getAllProductForBusiness(accountant.getId()));
    }


    @PostMapping("/add-to-branch/{branchId}")
    public ResponseEntity addProductToBranch(@AuthenticationPrincipal MyUser accountant, @PathVariable Integer branchId, @RequestBody ProductDTO product) {
        productService.addProductToBranch(accountant.getId(), branchId, product);
        return ResponseEntity.status(200).body(new ApiResponse("Product added successfully"));
    }


    // authority -> Accountant
    /// ?????????????????????????????????
    @PutMapping("/update/{branchId}/{productId}")
    public ResponseEntity updateProduct(@AuthenticationPrincipal MyUser accountant ,@PathVariable Integer branchId,@PathVariable Integer productId, @RequestBody@Valid Product product){
        productService.updateProduct(accountant.getId(),branchId, productId, product);
        return ResponseEntity.status(200).body(new ApiResponse("product updated"));
    }

    // authority -> Accountant
    /// ?????????????????????????????????
    @DeleteMapping("/delete/{branchId}/{productId}")
    public ResponseEntity deleteProduct(@AuthenticationPrincipal MyUser accountant,@PathVariable Integer branchId,@PathVariable Integer productId){
        productService.deleteProduct(accountant.getId(),branchId, productId);
        return ResponseEntity.status(200).body(new ApiResponse("product deleted"));
    }

    /// ?????????????????????????????????
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<?> getProductsByBarcode(@AuthenticationPrincipal MyUser accountant,@PathVariable String barcode) {
        Product products = productService.getProductsByBarcode(accountant.getId(),barcode);
        return ResponseEntity.ok(products);
    }









}
