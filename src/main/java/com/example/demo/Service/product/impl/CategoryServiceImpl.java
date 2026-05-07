package com.example.demo.Service.product.impl;

import com.example.demo.Model.product.Categories;
import com.example.demo.repository.product.CategoryRepository;
import com.example.demo.Service.product.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Categories> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Categories> findById(String id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Categories create(Categories category) {
        return categoryRepository.save(category);
    }

    @Override
    public Categories update(Categories category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteById(String id) {
        categoryRepository.deleteById(id);
    }
}
