package com.example.demo.repository.product;

import com.example.demo.Model.product.Categories;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Categories, String> {
}
