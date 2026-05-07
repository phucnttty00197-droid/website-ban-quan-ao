package com.example.demo.Service.order;

import com.example.demo.Model.order.Order_details;
import com.example.demo.Model.order.Orders;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface OrderService {
    List<Orders> findAll();

    Optional<Orders> findById(Long id);

    List<Orders> findByAccountUsername(String username);


    Orders create(Orders order);

    Orders update(Orders order);

    void deleteById(Long id);

}
