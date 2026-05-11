package com.example.demo.repository.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface VipReport {

    String getFullName();

    BigDecimal getTotalAmount();

    LocalDateTime getFirstOrderDate();

    LocalDateTime getLastOrderDate();
}
