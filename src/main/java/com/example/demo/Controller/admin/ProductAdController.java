package com.example.demo.Controller.admin;

import com.example.demo.Model.product.Product_size;
import com.example.demo.Model.product.Products;
import com.example.demo.Model.product.Sizes;
import com.example.demo.Service.product.CategoryService;
import com.example.demo.Service.product.ProductSizeService;
import com.example.demo.Service.product.ProductsService;
import com.example.demo.Service.product.SizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class ProductAdController {

    private static final int PAGE_SIZE = 5;

    private final ProductsService productsService;
    private final CategoryService categoryService;
    private final SizeService sizeService;
    private final ProductSizeService productSizeService;

    @GetMapping("/admin/product/index")
    public String index(@RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "keyword", required = false) String keyword,
                        @RequestParam(value = "categoryId", required = false) String categoryId,
                        @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
                        @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                        Model model) {
        boolean hasFilter = (keyword != null && !keyword.isEmpty())
                || (categoryId != null && !categoryId.isEmpty())
                || minPrice != null
                || maxPrice != null;

        Page<Products> productsPage = hasFilter
                ? productsService.searchWithFiltersPage(keyword, categoryId, minPrice, maxPrice, null, page, PAGE_SIZE)
                : productsService.findAllPage(page, PAGE_SIZE);
        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("currentPage", productsPage.getNumber());
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("sizes", sizeService.findAll());
        model.addAttribute("product", new Products());
        model.addAttribute("sizeQtyMap", Map.of());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("keyword", keyword);
        model.addAttribute("maxPrice", maxPrice);
        return "admin/product";
    }

    @PostMapping("/admin/product/create")
    public String create(@RequestParam("name") String name,
                         @RequestParam("price") BigDecimal price,
                         @RequestParam(value = "discount", required = false) BigDecimal discount,
                         @RequestParam(value = "available", required = false) Boolean available,
                         @RequestParam(value = "quantity", required = false) Integer quantity,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                         @RequestParam(value = "description", required = false) String description,
                         @RequestParam("categoryId") String categoryId,
                         @RequestParam Map<String, String> params,
                         Model model) {
        Products product = new Products();
        product.setName(name);
        product.setPrice(price);
        product.setDiscount(discount);
        product.setAvailable(available != null ? available : true);
        product.setQuantity(quantity);
        String imageName = saveImage(imageFile);
        if (imageName != null) {
            product.setImage(imageName);
        }
        product.setDescription(description);
        categoryService.findById(categoryId).ifPresent(product::setCategory);
        Products saved = productsService.create(product);
        saveProductSizes(saved, params);
        return "redirect:/admin/product/index";
    }

    @GetMapping("/admin/product/edit/{id}")
    public String edit(@PathVariable("id") Integer id,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "categoryId", required = false) String categoryId,
                       @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
                       @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                       Model model) {
        Optional<Products> product = productsService.findByIdWithSizes(id);
        boolean hasFilter = (keyword != null && !keyword.isBlank())
                || (categoryId != null && !categoryId.isBlank())
                || minPrice != null
                || maxPrice != null;
        Page<Products> productsPage = hasFilter
                ? productsService.searchWithFiltersPage(keyword, categoryId, minPrice, maxPrice, null, page, PAGE_SIZE)
                : productsService.findAllPage(page, PAGE_SIZE);
        model.addAttribute("products", productsPage.getContent());
        model.addAttribute("currentPage", productsPage.getNumber());
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("sizes", sizeService.findAll());
        Products current = product.orElseGet(Products::new);
        model.addAttribute("product", current);
        model.addAttribute("sizeQtyMap", buildSizeQtyMap(current));
        model.addAttribute("keyword", keyword);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("selectedCategoryId", categoryId);
        return "admin/product";
    }

    @PostMapping("/admin/product/update")
    public String update(@RequestParam("id") Integer id,
                         @RequestParam("name") String name,
                         @RequestParam("price") BigDecimal price,
                         @RequestParam(value = "discount", required = false) BigDecimal discount,
                         @RequestParam(value = "available", required = false) Boolean available,
                         @RequestParam(value = "quantity", required = false) Integer quantity,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                         @RequestParam(value = "description", required = false) String description,
                         @RequestParam("categoryId") String categoryId,
                         @RequestParam Map<String, String> params) {

        Products product = productsService.findById(id).orElseGet(Products::new);
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setDiscount(discount);
        product.setAvailable(available != null ? available : true);
        product.setQuantity(quantity);
        String imageName = saveImage(imageFile);
        if (imageName != null) {
            product.setImage(imageName);
        }
        product.setDescription(description);
        categoryService.findById(categoryId).ifPresent(product::setCategory);
        Products saved = productsService.update(product);
        productSizeService.deleteByProductId(saved.getId());
        saveProductSizes(saved, params);
        return "redirect:/admin/product/index";
    }

    @GetMapping("/admin/product/delete/{id}")
    public String delete(@PathVariable("id") Integer id,
                         @RequestParam(value = "page", defaultValue = "0") int page) {
        productsService.deleteById(id);
        return "redirect:/admin/product/index?page=" + page;
    }


    private Map<Integer, Integer> buildSizeQtyMap(Products product) {
        if (product == null || product.getProductSizes() == null) {
            return Map.of();
        }
        Map<Integer, Integer> map = new java.util.HashMap<>();
        for (Product_size ps : product.getProductSizes()) {
            if (ps.getSize() != null) {
                map.put(ps.getSize().getId(), ps.getQuantity());

            }
        }
        return map;
    }

    private String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String fileName = "product-" + UUID.randomUUID() + ext;
        Path uploadDir = Path.of("src/main/resources/static/images");
        try {
            Files.createDirectories(uploadDir);
            Files.write(uploadDir.resolve(fileName), file.getBytes());
            return fileName;
        } catch (IOException e) {
            return null;
        }
    }

    private void saveProductSizes(Products product, Map<String, String> params) {
        List<Sizes> sizes = sizeService.findAll();
        for (Sizes size : sizes) {
            String key = "size_" + size.getId();
            if (!params.containsKey(key)) {
                continue;
            }
            String value = params.get(key);
            int qty = 0;
            try {
                qty = Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
                qty = 0;
            }
            if (qty <= 0) {
                continue;
            }
            Product_size productSize = new Product_size();
            productSize.setProduct(product);
            productSize.setSize(size);
            productSize.setQuantity(qty);
            productSizeService.save(productSize);
        }
    }
}
