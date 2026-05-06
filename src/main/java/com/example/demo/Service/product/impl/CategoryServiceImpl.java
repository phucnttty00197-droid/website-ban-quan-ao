package com.example.demo.Service.product.impl;

import com.example.demo.Model.product.Categories;
import com.example.demo.Repository.products.CategoryRepository;
import com.example.demo.Service.product.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
        @Override
    public List<Categories> findAll(){return categoryRepository.findAll();}
}
