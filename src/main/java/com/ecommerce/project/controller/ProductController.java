package com.ecommerce.project.controller;

import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {
     @Autowired
     ProductService productService;

     @PostMapping("/admin/categories/{categoryId}/product")
     public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO, @PathVariable Long categoryId){
         ProductDTO productDTO1 = productService.addProduct(productDTO, categoryId);
         return new ResponseEntity<>(productDTO1, HttpStatus.CREATED);
     }

     @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProduct(){
         ProductResponse productResponse = productService.getAllProducts();
         return new ResponseEntity<>(productResponse,HttpStatus.OK);
     }

     @PostMapping("/public/categories/{categoryId}/products")
     public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId){
         ProductResponse productResponse = productService.searchByCategory(categoryId);
         return new ResponseEntity<>(productResponse, HttpStatus.OK);
     }

     @PostMapping("/public/products/keyword/{keyword}")
     public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword){
         ProductResponse productResponse = productService.searchProductByKeyword(keyword);
         return new ResponseEntity<>(productResponse,HttpStatus.OK);
     }

     @PutMapping("/admin/products/{productId}")
     public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO, @PathVariable Long productId){
         ProductDTO productDTO1 = productService.updateProduct(productDTO, productId);
         return new ResponseEntity<>(productDTO1, HttpStatus.CREATED);
     }

     @DeleteMapping("/admin/products/{productId}")
     public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
          ProductDTO productDTO = productService.deleteProduct(productId);
          return new ResponseEntity<>(productDTO, HttpStatus.OK);
     }

     @PutMapping("/admin/products/{productId}/image")
     public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId, @RequestParam("image")MultipartFile image) throws IOException {
         ProductDTO productDTO = productService.updateProductImage(productId, image);
         return new ResponseEntity<>(productDTO, HttpStatus.OK);
     }
}
