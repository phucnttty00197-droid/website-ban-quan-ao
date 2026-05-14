package com.example.demo.Service.order.impl;

import com.example.demo.Model.order.Order_details;
import com.example.demo.Model.order.Orders;
import com.example.demo.Model.product.Product_size;

import com.example.demo.Service.order.OrderDetailService;
import com.example.demo.Service.order.OrderService;
import com.example.demo.Service.product.ProductSizeService;
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
    private final ProductSizeService productSizeService;

    @Override
    public List<Orders> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Orders> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Orders> findByAccountUsername(String username) {
        return orderRepository.findByAccountUsernameOrderByCreateDateDesc(username);
    }

    @Override
    public Orders create(Orders order) {
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Orders update(Orders order) {
        if (order == null || order.getId() == null) {
            return orderRepository.save(order);
        }
        Optional<Orders> existingOpt = orderRepository.findById(order.getId());
        String previousStatus = existingOpt.map(Orders::getStatus).orElse(null);
        String nextStatus = order.getStatus();
        if (nextStatus != null && !nextStatus.equals(previousStatus)) {
            if (isPlacedStatus(nextStatus) && !isPlacedStatus(previousStatus)) {
                adjustInventory(order.getId(), -1);
            }

            if (isFailedStatus(nextStatus) && !isFailedStatus(previousStatus)) {
                adjustInventory(order.getId(), 1);
            }

        }
        return orderRepository.save(order);
    }


    private void adjustInventory(Long orderId, int direction) {
        // Cập nhật số lượng trong kho.
        if (orderId == null) {
            return;
        }
        List<Order_details> details = orderDetailService.findByOrderId(orderId);
        for (Order_details detail : details) {
            Integer productId = detail.getProduct() != null ? detail.getProduct().getId() : null;
            Integer sizeId = detail.getSizeId();
            Integer quantity = detail.getQuantity();
            if (productId == null && sizeId == null && quantity == null) {
                continue;
            }

            Optional<Product_size> productSizeOpt = productSizeService.findByProductIdAndSizeId(productId, sizeId);
            if (productSizeOpt.isEmpty() || productSizeOpt.get().getQuantity() == null) {
                continue;
            }
            Product_size productSize = productSizeOpt.get();
            int current = productSize.getQuantity();
            int next = current + (direction * quantity);
            if (next < 0) {
                next = 0;
            }
            productSize.setQuantity(next);
            productSizeService.save(productSize);
        }
    }
    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    private boolean isPlacedStatus(String status) {
        // trạng thái đơn hàng
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
