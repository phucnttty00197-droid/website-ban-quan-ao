package com.example.demo.repository.order;

import javax.swing.*;
import java.math.BigDecimal;

public interface RevenueReport {
    String getCategoryName();

    BigDecimal getTotalAmount();

    Long getTotalQuantity();

    BigDecimal getMaxPrice();

    BigDecimal getMinPrice();

    Double getAvgPrice();
}
