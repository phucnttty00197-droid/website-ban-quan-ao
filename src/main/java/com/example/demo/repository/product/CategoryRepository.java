package com.example.demo.repository.product;

import com.example.demo.Model.product.Categories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Categories, String> {
    List<Categories> findByDeletedFalse();
    Optional<Categories> findByIdAndDeletedFalse(String id);
}
