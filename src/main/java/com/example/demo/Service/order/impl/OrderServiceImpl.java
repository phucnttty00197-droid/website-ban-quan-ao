package com.example.demo.Service.order.impl;

import com.example.demo.Model.order.Orders;
import com.example.demo.Service.order.OrderDetailService;
import com.example.demo.Service.order.OrderService;
import com.example.demo.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailService orderDetailService;

    @Override
    public List<Orders> findAll(){
        return orderRepository.findAll();
    }

    @Override
    public Optional<Orders> findById(Long id){
        return orderRepository.findById(id);
    }

    @Override
    public List<Orders> findByAccountUsername(String username){
        return orderRepository.findByAccountUsernameOrderByCreateDateDesc(username);
    }

    @Override
    public Orders create(Orders order) {
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Orders update(Orders order) {
        if(order == null || order.getId() == null){
            return orderRepository.save(order);
        }
        Optional<Orders> existingOpt = orderRepository.findById(order.getId());
        String previousStatus = existingOpt.map(Orders::getStatus).orElse(null);
        String nextStatus = order.getStatus();
        if (nextStatus != null && !nextStatus.equals(previousStatus)){
            if (isPlacedStatus(nextStatus) && !isPlacedStatus(previousStatus)){

            }
        }
    }











    private boolean isPlacedStatus(String status) {
        if (status == null) {
            return false;
        }
        return "PLACED_UNPAID".equals(status) || "PLACED_PAID".equals(status);
    }

    private boolean isFailedStatus(String status) {
        if (status == null) {
            return false;
        }
        return "DELIVERY_FAILED".equals(status) || "CANCEL".equals(status);
    }
}
