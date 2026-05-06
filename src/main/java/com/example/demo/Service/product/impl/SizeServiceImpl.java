package com.example.demo.Service.product.impl;

import com.example.demo.Model.product.Sizes;
import com.example.demo.Repository.products.SizeRepository;
import com.example.demo.Service.product.SizeService;
import com.example.demo.Service.product.SizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {

    private final SizeRepository sizeRepository;

    @Override
    public List<Sizes> findAll() {
        return sizeRepository.findAll();
    }

    @Override
    public Optional<Sizes> findById(Integer id) {
        return sizeRepository.findById(id);
    }
}
