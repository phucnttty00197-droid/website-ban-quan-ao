package com.example.demo.Repository.products;

import com.example.demo.Model.product.Categories;
import jdk.jfr.Category;
import org.springframework.data.repository.Repository;

public interface CategoryRepository extends Repository<Categories, Integer> {
}
