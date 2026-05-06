package com.example.demo.Model.cart;


import com.example.demo.Model.product.Products;
import com.example.demo.Model.product.Sizes;
import com.example.demo.Model.user.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cart_items", uniqueConstraints = @UniqueConstraint(columnNames ={"username", "product_id", "size_id"}))
public class Cart_items {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "username", nullable = false)
    private Account account;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "size_id", nullable = false)
    private Sizes sizes;




    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;


    @PrePersist
    protected void applyDefaults() {
        if (created_at == null) {
            created_at = LocalDateTime.now();
        }
        if (quantity == null) {
            quantity = 1;
        }
    }



}
