package com.example.demo.Model.review;


import com.example.demo.Model.Orders;
import com.example.demo.Model.product.Products;
import com.example.demo.Model.user.Account;
import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;
import org.springframework.web.bind.annotation.RequestParam;

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

//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "order_id")
//    private Orders order;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Products product;


}
