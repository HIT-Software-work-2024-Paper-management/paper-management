package com.myproject.controller;

import com.myproject.dto.CategoryDTO;
import com.myproject.model.Category;
import com.myproject.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;




@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> saveCategory(@RequestParam("categoryName") String name,
                                                 @RequestParam(value = "categoryParent", required = false) String parentName,
                                                 HttpServletResponse response) {
        try {
            Category category = new Category();
            category.setName(name);

            System.out.println("arrived here");

            if (parentName != null && !parentName.isEmpty()) {
                Optional<Category> parentCategoryOpt = categoryService.getCategoryByName(parentName);
                if (parentCategoryOpt.isPresent()) {
                    category.setParent(parentCategoryOpt.get());
                } else {
                    return ResponseEntity.badRequest().build();
                }
            }

            Category savedCategory = categoryService.saveCategory(category);
            return ResponseEntity.ok(savedCategory);

        } catch (Exception e) {
            // 检查响应是否已提交
            if (!response.isCommitted()) {
                try {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } else {
                e.printStackTrace(); // 记录日志或其他处理方式
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        try {
            List<CategoryDTO> categories = categoryService.getAllCategories();
            return ResponseEntity.ok().body(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/searchid")
    public ResponseEntity<List<Category>> getAllCategoriesid() {

        try {

            List<Category> categories = categoryService.getAllCategoriesid();

            return ResponseEntity.ok().body(categories);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }

    }



    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestParam("name") String name,
                                                   @RequestParam(value = "parentName", required = false) String parentName) {
        Optional<Category> updatedCategoryOpt = categoryService.getCategoryById(id);
        if (updatedCategoryOpt.isPresent()) {
            Category updatedCategory = updatedCategoryOpt.get();
            updatedCategory.setName(name);

            if (parentName != null && !parentName.isEmpty()) {
                Optional<Category> parentCategoryOpt = categoryService.getCategoryByName(parentName);
                if (parentCategoryOpt.isPresent()) {
                    updatedCategory.setParent(parentCategoryOpt.get());
                } else {
                    return ResponseEntity.badRequest().build();
                }
            } else {
                updatedCategory.setParent(null);
            }

            Category savedCategory = categoryService.saveCategory(updatedCategory);
            return ResponseEntity.ok(savedCategory);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
