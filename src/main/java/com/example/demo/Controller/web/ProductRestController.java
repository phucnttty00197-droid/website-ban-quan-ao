package com.example.demo.Controller.web;

import com.example.demo.DTO.product.ProductDTO;
import com.example.demo.DTO.product.ProductRequestDTO;
import com.example.demo.Model.product.Categories;
import com.example.demo.Model.product.Products;
import com.example.demo.Service.product.CategoryService;
import com.example.demo.Service.product.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductRestController {
    private final ProductsService productsService;
    private final CategoryService categoryService;

    @GetMapping
    public List<ProductDTO> findAll() {
        return productsService.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Integer id) {
        return productsService.findById(id)
                .map(products -> ResponseEntity.ok(toDTO(products)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProductRequestDTO request) {
        Optional<Categories> category = resolveCategory(request.getCategoryId());
        if (request.getCategoryId() != null && category.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy Category");
        }
        Products product = new Products();
        applyRequest(product, request);
        category.ifPresent(product::setCategory);
        Products saved = productsService.create(product);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody ProductRequestDTO request) {
        Optional<Products> existing = productsService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Optional<Categories> category = resolveCategory(request.getCategoryId());
        if (request.getCategoryId() != null && category.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy Category");
        }
        Products product = existing.get();
        applyRequest(product, request);
        if (request.getCategoryId() != null) {
            product.setCategory(category.orElse(null));
        }
        Products saved = productsService.update(product);
        return ResponseEntity.ok(saved);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productsService.deleteById(id);
        return ResponseEntity.noContent().build();
    }



    private ProductDTO toDTO(Products products) {
        ProductDTO dto = new ProductDTO();
        dto.setId(products.getId());
        dto.setName(products.getName());
        dto.setImage(products.getImage());
        dto.setPrice(products.getPrice());
        dto.setDiscount(products.getDiscount());
        dto.setAvailable(products.getAvailable());
        dto.setQuantity(products.getQuantity());
        dto.setDescription(products.getDescription());
        dto.setCreateDate(products.getCreateDate());
        if (products.getCategory() != null) {
            dto.setCategoryId(products.getCategory().getId());
            dto.setCategoryName(products.getCategory().getName());
        }
        return dto;
    }
    private void applyRequest(Products product, ProductRequestDTO request) {
        if (request.getName() != null) {
            product.setName(product.getName());
        }
        if (request.getImage() != null) {
            product.setImage(request.getImage());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getDiscount() != null) {
            product.setDiscount(request.getDiscount());
        }
        if (request.getAvailable() != null) {
            product.setAvailable(request.getAvailable());
        }
        if (request.getQuantity() != null) {
            product.setQuantity(request.getQuantity());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
    }
    private Optional<Categories> resolveCategory (String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            return Optional.empty();
        }
        return categoryService.findById(categoryId);
    }
}
