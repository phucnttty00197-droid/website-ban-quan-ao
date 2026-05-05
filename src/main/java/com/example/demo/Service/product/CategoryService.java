package com.example.demo.Service.product;

import jdk.jfr.Category;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CategoryService extends Repository<Category, Integer> {
    List<Category> findAll();
    Optional<Category> findById(String id);
    Category create(Category category);
    Category update(Category category);
    void delete(Category category);
}
