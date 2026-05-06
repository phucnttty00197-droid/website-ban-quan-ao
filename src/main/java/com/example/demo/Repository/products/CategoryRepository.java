package com.example.demo.Repository.products;

import com.example.demo.Model.product.Categories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface CategoryRepository extends JpaRepository<Categories, String> {
}
