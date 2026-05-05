package com.example.demo.Service.products;

import com.example.demo.Model.product.Products;
import jdk.jfr.Category;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService extends Repository<Category, Integer> {
    List<Category> findAll();

}
