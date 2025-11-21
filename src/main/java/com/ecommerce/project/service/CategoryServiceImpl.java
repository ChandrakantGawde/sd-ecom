package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

//    private List<Category> categories = new ArrayList<>();

    @Autowired
    private CategoryRepository categoryRepository;
//    private Long nextId = 1L;
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createcategory(Category category) {
//        category.setCategoryId(nextId++);
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        categoryRepository.delete(category);
        return "Category with categoryId: "+ categoryId + " deleted successfully";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Optional<Category> savedCategoryOptional = categoryRepository.findById(categoryId);
        Category savedCategory = savedCategoryOptional.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);

        return savedCategory;
    }

}
