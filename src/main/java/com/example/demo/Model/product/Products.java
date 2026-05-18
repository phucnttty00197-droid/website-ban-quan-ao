package com.example.demo.Model.product;

import com.example.demo.Model.common.BaseEntity;
import com.example.demo.Model.order.Order_details;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
@Entity
@Builder
public class Products extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 255)
    private String image;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;



    @Column()
    private Integer quantity;

    @Column(length = 2000)
    private String description;



    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Categories category;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "product")
    @Builder.Default
    private List<Order_details> orderDetails = new ArrayList<>();


    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "product")
    @Builder.Default
    private List<Product_size> productSizes = new ArrayList<>();

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;
}
