package com.example.demo.DTO.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    private String name;
    private String image;
    private BigDecimal price;
    private BigDecimal discount;
    private Boolean available;
    private Integer quantity;
    private String description;
    private String categoryId;
}
