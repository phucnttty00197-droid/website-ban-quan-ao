package com.example.demo.Service.product;

import com.example.demo.Model.product.Product_size;

import java.util.List;
import java.util.Optional;

public interface ProductSizeService {
    List<Product_size> findByProductId(Integer productId);
    List<Product_size> findByProductIds( List<Integer> productIds);
    Optional<Product_size> findByProductIdAndSizeId(Integer productId,Integer sizeId);
    Product_size save(Product_size product_size);
    void deleteByProductId(Integer productId);
}
