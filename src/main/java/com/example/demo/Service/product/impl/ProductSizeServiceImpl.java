package com.example.demo.Service.product.impl;

import com.example.demo.Model.product.Product_size;

import com.example.demo.Service.product.ProductSizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductSizeServiceImpl implements ProductSizeService {
    private final com.example.demo.repository.product.ProductSizeRepository productSizeRepository;



    @Override
    public List<Product_size> findByProductId(Integer productId) {
        return productSizeRepository.findByProductId(productId);
    }
    @Override
    public List<Product_size> findByProductIds(List<Integer> productIds) {
        return productSizeRepository.findByProductIdIn(productIds);
    }

    @Override
    public Optional<Product_size> findByProductIdAndSizeId(Integer productId, Integer sizeId) {
        return productSizeRepository.findByProductIdAndSizeId(productId, sizeId);
    }

    @Override
    public Product_size save(Product_size productSize) {
        return productSizeRepository.save(productSize);
    }

    @Override
    @Transactional
    public void deleteByProductId(Integer productId) {
        productSizeRepository.deleteByProductId(productId);
    }
}
