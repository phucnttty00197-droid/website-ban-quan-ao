package com.example.demo.Model.product;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name ="categories")
@Builder
public class Categories {
    @Id
    @Column(length = 20)
    private String id;

    @Column(length = 100,nullable = false)
    private String name;


    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "category")
    private List<Products> products = new ArrayList<>();

}
