package com.example.demo.Model.review;


import com.example.demo.Model.order.Orders;
import com.example.demo.Model.product.Products;
import com.example.demo.Model.user.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_reviews")
public class Product_review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    private Account account;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders order;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Products product;

    @Column(nullable = false)
    private Integer starRating;

    @Column(length = 2000)
    private String reviewContent;

    @Column(length = 2000)
    private String images;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    private void applyDefaults() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

}
