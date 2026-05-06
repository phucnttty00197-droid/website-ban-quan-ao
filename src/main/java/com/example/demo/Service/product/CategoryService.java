package com.example.demo.Service.product;

import com.example.demo.Model.product.Categories;
import jdk.jfr.Category;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface CategoryService  {
    List<Categories> findAll();
    Optional<Categories> findById(String id);
    Categories create(Categories category);
    Categories update(Categories category);
    void deleteById(String id);
}
