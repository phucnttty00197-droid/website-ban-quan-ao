package com.example.demo.DTO.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Integer id;
    private String name;
    private String image;
    private BigDecimal price;
    private BigDecimal discount;
    private Boolean available;
    private Integer quantity;
    private String description;
    private LocalDateTime createDate;
    private String categoryId;
    private String categoryName;

}
