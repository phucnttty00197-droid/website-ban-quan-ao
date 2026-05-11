package com.example.demo.Model.order;


import com.example.demo.Model.common.BaseEntity;
import com.example.demo.Model.user.Account;
import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name ="orders")
public class Orders extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String address;

    @Column(length = 20)
    private String status;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    private Account account;


    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "order")
    @Builder.Default
    private List<Order_details> orderDetails = new ArrayList<>();





}
