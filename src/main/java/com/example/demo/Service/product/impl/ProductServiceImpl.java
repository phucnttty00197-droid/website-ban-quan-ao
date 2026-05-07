package com.example.demo.Service.product.impl;

import com.example.demo.Model.product.Products;
import com.example.demo.repository.product.ProductsRepository;
import com.example.demo.Service.product.ProductSizeService;
import com.example.demo.Service.product.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductsService {
        private final ProductSizeService productSizeService;
        private final ProductsRepository productRepository;

        @Override
    public List<Products> findAll() {return productRepository.findAll();
        }
        @Override
    public List<Products> findAllPage(int page,int size){
            Page<Products> products = productRepository.findAll(PageRequest.of(page,size,Sort.by("id").descending() ));
                    attachSizes(products.getContent());
                    return products;
        }
}
