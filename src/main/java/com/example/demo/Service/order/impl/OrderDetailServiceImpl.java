package com.example.demo.Service.order.impl;

import com.example.demo.Model.order.Order_details;
import com.example.demo.Service.order.OrderDetailService;
import com.example.demo.repository.order.OrderDetailRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public List<Order_details> findAll(){
        return orderDetailRepository.findAll();
    }

    @Override
    public Optional<Order_details> findById(Long id){
        return orderDetailRepository.findById(id);
    }

    @Override
    public List<Order_details> findByOrderId(Long orderId){
        return orderDetailRepository.findByOrderId(orderId);
    }

    @Override
    public Order_details create(Order_details order){
        return orderDetailRepository.save(order);
    }

    @Override
    public Order_details update(Order_details order){
        return orderDetailRepository.save(order);
    }

    @Override
    public void deleteById(Long id){
        orderDetailRepository.deleteById(id);
    }

    @Override
    public void deleteByOrderId(Long orderId){
        orderDetailRepository.deleteByOrderId(orderId);
    }

    @Override
    public List<Order_details> findByOrderAccountUsername(String username){
        return orderDetailRepository.findByOrderAccountUsername(username);
    }

}

