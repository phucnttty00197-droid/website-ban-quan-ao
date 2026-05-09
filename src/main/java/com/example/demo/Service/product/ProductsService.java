package com.example.demo.Service.product;

import com.example.demo.Model.product.Products;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public interface ProductsService  {
    List<Products> findAll();
    Page<Products> findAllPage(int page, int size);
    Optional<Products> findById(Integer id);
    Optional<Products> findByIdWithSizes(Integer id);
    List<Products> findTop8ByOrderByCreateDateDesc();
    List<Products> findTop8ByDiscountGreaterThanOrderByDiscountDesc(double discount);
    List<Products> findTop8BestSeller();
    List<Products> findByCategoryId(String categoryId);
    List<Products> findTop4ByCategoryIdAndIdNot(String categoryId, Integer id);
    List<Products> findAllWithSizes();
    List<Products> searchWithFilters(String keyword,
                                     String categoryId,
                                     BigDecimal minPrice,
                                     BigDecimal maxPrice,
                                     String sort);

    Page<Products> searchWithFiltersPage(String keyword,
                                         String categoryID,
                                         BigDecimal minPrice,
                                         BigDecimal maxPrice,
                                         String sort,
                                         int page, int size);
    Products create(Products products);
    Products update(Products products);
    void deleteById(Integer id);

}
