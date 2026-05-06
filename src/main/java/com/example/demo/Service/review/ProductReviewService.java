package com.example.demo.Service.review;

import com.example.demo.Model.order.Orders;
import com.example.demo.Model.product.Products;
import com.example.demo.Model.review.Product_review;
import com.example.demo.Model.user.Account;
import com.example.demo.Repository.review.ProductReviewStats;

import java.util.List;
import java.util.Set;

public interface ProductReviewService {

    boolean hasReviewed(String username, Integer productId, Long OrderId);

    Set<Integer> findReviewedProductId(String username, Long OrderId);

    Product_review createReview(Account account, Products products, Orders orders,
                                Integer starRating, String reviewContent, List<String> images);

    List<Product_review> findByProductId(Integer productId);

    ProductReviewStats getStats (Integer productId);
}
