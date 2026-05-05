package com.example.demo.Service.products;

import com.example.demo.Model.product.Sizes;

import java.util.List;
import java.util.Optional;

public interface SizeService {
    List<Sizes> findAll();

    Optional<Sizes> findById(Integer id);

}
