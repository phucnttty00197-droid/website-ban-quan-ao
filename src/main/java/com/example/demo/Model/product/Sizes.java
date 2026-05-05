package com.example.demo.Model.product;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.engine.jdbc.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sizes")
@Builder
public class Sizes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 10, nullable = false)
    private String name;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "size")
    @Builder.Default
    private List<Product_size> productSizes = new ArrayList<>();
}
