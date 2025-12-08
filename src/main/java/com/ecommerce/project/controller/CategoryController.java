package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {

    CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/echo")
    public ResponseEntity<String> echoMessage(@RequestParam( name = "message", required = false) String message){
        return new ResponseEntity<>("Echoed message "+ message, HttpStatus.OK);
    };

//  @RequestMapping(value = "/public/categories", method = RequestMethod.POST)
    @Tag(name= "Categories API", description= "APIs for managing Category") // Group APIs
    @Operation(summary = "Create category", description = "API to create new category") // Describe API
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category is created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Input", content = @Content()),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER , required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORY_BY , required = false ) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER , required = false ) String sortOrder
    ){
        CategoryResponse categoryResponse =  categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @Tag(name= "Categories API", description= "APIs for managing Category")
    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO categoryDTO1 = categoryService.createcategory(categoryDTO);
        return new ResponseEntity<>( categoryDTO1, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(
            @Parameter(description = "ID of the Category that you wish to delete")
            @PathVariable Long categoryId){
        CategoryDTO  categoryDTO  = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId){
        CategoryDTO categoryDTO1 = categoryService.updateCategory(categoryDTO, categoryId);
        return new ResponseEntity<>(categoryDTO1, HttpStatus.OK);
    }

}
