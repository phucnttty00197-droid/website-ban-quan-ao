package com.example.demo.Controller.web;

import com.example.demo.Model.product.Categories;
import com.example.demo.Model.product.Products;
import com.example.demo.Service.product.CategoryService;
import com.example.demo.Service.product.ProductsService;
import com.example.demo.Service.review.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final CategoryService categoryService;
    private final ProductsService productsService;
    private final ProductReviewService productReviewService;

    @GetMapping("/product/list-by-category/{id}")
    public String listByCategory(@PathVariable("id") String categoryId,
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
                                 @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                                 @RequestParam(value = "priceRange", required = false) String priceRange,
                                 @RequestParam(value = "sort", required = false) String sort,
                                 @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                 @RequestParam(value = "size", required = false, defaultValue = "12") Integer size,
                                 Model model) {
        Optional<Categories> category = categoryService.findById(categoryId);
        if (category.isEmpty()) {
            return "redirect:/home/index";
        }
        BigDecimal[] range = applyQuickRange(priceRange, minPrice, maxPrice);
        minPrice = range[0];
        maxPrice = range[1];
        Page<Products> pageResult = productsService.searchWithFiltersPage(keyword, categoryId, minPrice, maxPrice, sort, page, size);
        model.addAttribute("category", category.get());
        model.addAttribute("products", pageResult.getContent());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("priceRange", priceRange);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", pageResult.getNumber());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        return "product/list";
    }

    @GetMapping("/product/detail/{id}")
    public String detail(@PathVariable("id") Integer productId, Model model) {
        Optional<Products> product = productsService.findByIdWithSizes(productId);
        if (product.isEmpty()) {
            return "redirect:/home/index";
        }
        Products current = product.get();
        String categoryId = current.getCategory() != null ? current.getCategory().getId() : null;
        List<Products> related = categoryId == null
                ? List.of()
                : productsService.findTop4ByCategoryIdAndIdNot(categoryId, current.getId());

        model.addAttribute("product", current);
        model.addAttribute("relatedProducts", related);
        var stats = productReviewService.getStats(productId);
        double avgRating = stats != null && stats.getAvgRating() != null ? stats.getAvgRating() : 0.0;
        long avgRounded = Math.round(avgRating);
        model.addAttribute("reviews", productReviewService.findByProductId(productId));
        model.addAttribute("reviewStats", stats);
        model.addAttribute("avgRatingRounded", avgRounded);
        model.addAttribute("avgRatingValue", avgRating);
        return "product/detail";
    }

    @GetMapping("/product/list")
    public String listAll(@RequestParam(value = "keyword", required = false) String keyword,
                          @RequestParam(value = "categoryId", required = false) String categoryId,
                          @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
                          @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                          @RequestParam(value = "priceRange", required = false) String priceRange,
                          @RequestParam(value = "sort", required = false) String sort,
                          @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                          @RequestParam(value = "size", required = false, defaultValue = "12") Integer size,
                          Model model) {
        Categories category = new Categories();
        if (categoryId != null && !categoryId.isBlank()) {
            categoryService.findById(categoryId).ifPresentOrElse(
                    found -> category.setName(found.getName()),
                    () -> category.setName("Tất cả sản phẩm")
            );
        } else {
            category.setName("Tất cả sản phẩm");
        }
        model.addAttribute("category", category);
        BigDecimal[] range = applyQuickRange(priceRange, minPrice, maxPrice);
        minPrice = range[0];
        maxPrice = range[1];
        Page<Products> pageResult = productsService.searchWithFiltersPage(keyword, categoryId, minPrice, maxPrice, sort, page, size);
        model.addAttribute("products", pageResult.getContent());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("priceRange", priceRange);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", pageResult.getNumber());
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        return "product/list";
    }

    private BigDecimal[] applyQuickRange(String priceRange, BigDecimal minPrice, BigDecimal maxPrice) {
        if (priceRange == null || priceRange.isBlank()) {
            return new BigDecimal[]{minPrice, maxPrice};
        }
        return switch (priceRange) {
            case "under_500" -> new BigDecimal[]{null, BigDecimal.valueOf(500_000)};
            case "500_1m" -> new BigDecimal[]{BigDecimal.valueOf(500_000), BigDecimal.valueOf(1_000_000)};
            case "1m_2m" -> new BigDecimal[]{BigDecimal.valueOf(1_000_000), BigDecimal.valueOf(2_000_000)};
            case "over_2m" -> new BigDecimal[]{BigDecimal.valueOf(2_000_000), null};
            default -> new BigDecimal[]{minPrice, maxPrice};
        };
    }
}
