package com.example.demo.Service.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cartltem {
    private Integer productId;
    private String name;
    private Integer sizeId;
    private String sizeName;
    private BigDecimal price;
    private Integer quantity;
    private String image;
}
