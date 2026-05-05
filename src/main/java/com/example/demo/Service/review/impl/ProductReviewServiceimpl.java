package com.example.demo.Service.review.impl;

import com.example.demo.Model.Orders;
import com.example.demo.Model.product.Product_size;
import com.example.demo.Model.product.Products;
import com.example.demo.Model.review.Product_review;
import com.example.demo.Model.user.Account;
import com.example.demo.Repository.review.ProductReviewRepository;
import com.example.demo.Repository.review.ProductReviewStats;
import com.example.demo.Service.review.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewServiceimpl implements ProductReviewService {

    private  final ProductReviewRepository productReviewRepository;

    @Override
    public boolean hasReviewed(String username, Integer productId, Long OrderId){
        return productReviewRepository.existsByAccountUsernameAndProductIdAndOrderId(username,productId, OrderId);
    }

    @Override
    public Set<Integer> findReviewedProductId(String username, Long OrderId) {
        return productReviewRepository.findByAccountUsernameAndOrderId(username,OrderId)
        .stream()
                .filter(productReview -> productReview.getProduct() != null)
                .map(productReview -> productReview.getProduct().getId())
                .collect(Collectors.toSet());


    }
    @Override
    public  Product_review createReview(Account account,
                                        Products products,
                                        Orders orders,
                                        Integer starRating,
                                        String reviewContent,
                                        List<String> images) {
        String joinedImages = images == null || images.isEmpty()
                ? null
                : images.stream()
                .filter(item -> item != null && !item.isBlank())
                .collect(Collectors.joining(","));

        Product_review productReview = new Product_review();
        productReview.setAccount(account);
        productReview.setProduct(products);
        productReview.setOrder(orders);
        productReview.setStarRating(starRating);
        productReview.setReviewContent(reviewContent);
        productReview.setImages(joinedImages);
        return productReviewRepository.save(productReview);
    }
    @Override
    public List<Product_review> findByProductId(Integer productId){
        return productReviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    @Override
    public ProductReviewStats getStats (Integer productId){
        return productReviewRepository.getProductReviewStatsByProductId(productId);
    }

}
