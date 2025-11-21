package com.ecommerce.project.controller;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

//  @RequestMapping(value = "/public/categories", method = RequestMethod.POST)
    @GetMapping("/public/categories")
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> categories =  categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<?> createCategory(@RequestBody Category category){
        categoryService.createcategory(category);
        return new ResponseEntity<>("Category added successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId){
        try {
            String status = categoryService.deleteCategory(categoryId);
//            return  ResponseEntity.ok(status);
//            return  ResponseEntity.status(HttpStatus.OK).body(status);
              return new ResponseEntity<>(status, HttpStatus.OK);

        } catch (ResponseStatusException e){
             return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<?> updateCategory(@RequestBody Category category, @PathVariable Long categoryId){
        try{
            Category savedCategory = categoryService.updateCategory(category, categoryId);
            return new ResponseEntity<>("Category with category id : " + savedCategory + " updated successfully", HttpStatus.OK);

        } catch (ResponseStatusException e){
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }

}
