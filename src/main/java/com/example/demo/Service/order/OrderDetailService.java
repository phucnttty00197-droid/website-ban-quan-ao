package com.example.demo.Service.order;

import com.example.demo.Model.order.Order_details;

import java.util.List;
import java.util.Optional;

public interface OrderDetailService {

    List<Order_details> findAll();

    Optional<Order_details> findById(Long id);

    List<Order_details> findByOrderId(Long orderId);

    Order_details create(Order_details order);

    Order_details update(Order_details order);

    void deleteById(Long id);

    void deleteByOrderId(Long orderId);
    List<Order_details> findByOrderAccountUsername(String username);
}
