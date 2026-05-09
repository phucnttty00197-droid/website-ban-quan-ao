package com.example.demo.Service.product.impl;

import com.example.demo.Model.product.Product_size;
import com.example.demo.Model.product.Products;
import com.example.demo.repository.product.ProductsRepository;
import com.example.demo.Service.product.ProductSizeService;
import com.example.demo.Service.product.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductsService {

    private final ProductsRepository productRepository;
    private final ProductSizeService productSizeService;

    @Override
    public List<Products> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Page<Products> findAllPage(int page, int size) {
        Page<Products> products = productRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
        attachSizes(products.getContent());
        return products;
    }

    @Override
    public Optional<Products> findById(Integer id) {
        return productRepository.findById(id);
    }

    @Override
    public Optional<Products> findByIdWithSizes(Integer id) {
        Optional<Products> product = productRepository.findById(id);
        product.ifPresent(this::attachSizes);
        return product;
    }
    @Override
    public List<Products> findTop8ByOrderByCreateDateDesc() {
        List<Products> products = productRepository.findTop8ByOrderByCreateDateDesc();
        attachSizes(products);
        return products;
    }

    @Override
    public List<Products> findTop8ByDiscountGreaterThanOrderByDiscountDesc(double discount) {
        List<Products> products = productRepository.findTop8ByDiscountGreaterThanOrderByDiscountDesc(BigDecimal.valueOf(discount));
        attachSizes(products);
        return products;
    }

    @Override
    public List<Products> findTop8BestSeller() {
        List<Products> products = productRepository.findBestSellers(PageRequest.of(0, 8));
        attachSizes(products);
        return products;
    }

    @Override
    public List<Products> findByCategoryId(String categoryId) {
        List<Products> products = productRepository.findByCategoryId(categoryId);
        attachSizes(products);
        return products;
    }

    @Override
    public List<Products> findTop4ByCategoryIdAndIdNot(String categoryId, Integer id) {
        List<Products> products = productRepository.findTop4ByCategoryIdAndIdNot(categoryId, id);
        attachSizes(products);
        return products;
    }

    @Override
    public List<Products> findAllWithSizes() {
        List<Products> products = productRepository.findAll();
        attachSizes(products);
        return products;
    }

    @Override
    public List<Products> searchWithFilters(String keyword,
                                           String categoryId,
                                           BigDecimal minPrice,
                                           BigDecimal maxPrice,
                                           String sort) {
        String keywordValue = keyword != null ? keyword.trim() : null;
        String categoryValue = categoryId != null ? categoryId.trim() : null;
        List<Products> products;
        if ("price_asc".equalsIgnoreCase(sort)) {
            products = productRepository.searchOrderByPriceAsc(keywordValue, categoryValue, minPrice, maxPrice);
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            products = productRepository.searchOrderByPriceDesc(keywordValue, categoryValue, minPrice, maxPrice);
        } else {
            products = productRepository.search(keywordValue, categoryValue, minPrice, maxPrice);
        }
        attachSizes(products);
        return products;
    }
    @Override
    public Page<Products> searchWithFiltersPage(String keyword,
                                               String categoryId,
                                               BigDecimal minPrice,
                                               BigDecimal maxPrice,
                                               String sort,
                                               int page,
                                               int size) {
        String keywordValue = keyword != null ? keyword.trim() : null;
        String categoryValue = categoryId != null ? categoryId.trim() : null;
        PageRequest pageRequest;
        if ("price_asc".equalsIgnoreCase(sort)) {
            pageRequest = PageRequest.of(page, size, Sort.by("price").ascending());
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            pageRequest = PageRequest.of(page, size, Sort.by("price").descending());
        } else {
            pageRequest = PageRequest.of(page, size);
        }
        Page<Products> products = productRepository.searchPage(keywordValue, categoryValue, minPrice, maxPrice, pageRequest);
        attachSizes(products.getContent());
        return products;
    }

    @Override
    public Products create(Products product) {
        return productRepository.save(product);
    }

    @Override
    public Products update(Products product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteById(Integer id) {
        productRepository.deleteById(id);
    }

    private void attachSizes(Products product) {
        List<Product_size> sizes = productSizeService.findByProductId(product.getId());
        product.setProductSizes(sizes);
    }

    private void attachSizes(List<Products> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        List<Integer> ids = new ArrayList<>();
        for (Products product : products) {
            ids.add(product.getId());
        }
        List<Product_size> sizes = productSizeService.findByProductIds(ids);
        Map<Integer, List<Product_size>> map = new HashMap<>();
        for (Product_size size : sizes) {
            Integer productId = size.getProduct().getId();
            map.computeIfAbsent(productId, key -> new ArrayList<>()).add(size);
        }
        for (Products product : products) {
            product.setProductSizes(map.getOrDefault(product.getId(), new ArrayList<>()));
        }
    }
}
