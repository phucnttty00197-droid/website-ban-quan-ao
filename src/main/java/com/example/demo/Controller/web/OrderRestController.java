package com.example.demo.Controller.web;

import com.example.demo.DTO.order.OrderDTO;
import com.example.demo.DTO.order.OrderDetailDTO;
import com.example.demo.DTO.order.OrderItemRequestDTO;
import com.example.demo.DTO.order.OrderRequestDTO;
import com.example.demo.Model.order.Order_details;
import com.example.demo.Model.order.Orders;
import com.example.demo.Model.product.Products;
import com.example.demo.Model.user.Account;
import com.example.demo.Service.order.OrderDetailService;
import com.example.demo.Service.order.OrderService;
import com.example.demo.Service.product.ProductsService;
import com.example.demo.Service.user.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderRestController {
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;
    private final ProductsService productService;
    private final AccountService accountService;

    @GetMapping
    public List<OrderDTO> findAll() {
        return orderService.findAll().stream()
                .map(order -> toDto(order, orderDetailService.findByOrderId(order.getId())))
                .toList();
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(order -> ResponseEntity.ok(toDto(order, orderDetailService.findByOrderId(order.getId()))))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody OrderRequestDTO request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            return ResponseEntity.badRequest().body("Username is required");
        }
        Optional<Account> account = accountService.findByUsername(request.getUsername());
        if (account.isEmpty()) {
            return ResponseEntity.badRequest().body("Account not found");
        }

        List<Order_details> details = buildDetails(request.getItems());
        if (details == null) {
            return ResponseEntity.badRequest().body("Invalid order items");
        }

        Orders order = new Orders();
        order.setAccount(account.get());
        order.setAddress(request.getAddress());
        order.setStatus(request.getStatus());

        Orders savedOrder = orderService.create(order);

        List<Order_details> savedDetails = saveDetails(savedOrder, details);
        return ResponseEntity.ok(toDto(savedOrder, savedDetails));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody OrderRequestDTO request) {
        Optional<Orders> existing = orderService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Orders order = existing.get();

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            Optional<Account> account = accountService.findByUsername(request.getUsername());
            if (account.isEmpty()) {
                return ResponseEntity.badRequest().body("Account not found");
            }
            order.setAccount(account.get());
        }
        if (request.getAddress() != null) {
            order.setAddress(request.getAddress());
        }
        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
        }

        Orders savedOrder = orderService.update(order);

        List<Order_details> savedDetails = orderDetailService.findByOrderId(savedOrder.getId());
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            List<Order_details> newDetails = buildDetails(request.getItems());
            if (newDetails == null) {
                return ResponseEntity.badRequest().body("Invalid order items");
            }
            orderDetailService.deleteByOrderId(savedOrder.getId());
            savedDetails = saveDetails(savedOrder, newDetails);
        }

        return ResponseEntity.ok(toDto(savedOrder, savedDetails));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderDetailService.deleteByOrderId(id);
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private List<Order_details> buildDetails(List<OrderItemRequestDTO> items) {
        if (items == null) {
            return new ArrayList<>();
        }
        List<Order_details> details = new ArrayList<>();
        for (OrderItemRequestDTO item : items) {
            if (item.getProductId() == null) {
                return null;
            }
            Optional<Products> productOpt = productService.findById(item.getProductId());
            if (productOpt.isEmpty()) {
                return null;
            }
            Products product = productOpt.get();
            Order_details detail = new Order_details();
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity() != null ? item.getQuantity() : 1);
            detail.setPrice(item.getPrice() != null ? item.getPrice() : product.getPrice());
            details.add(detail);
        }
        return details;
    }

    private List<Order_details> saveDetails(Orders order, List<Order_details> details) {
        List<Order_details> savedDetails = new ArrayList<>();
        for (Order_details detail : details) {
            detail.setOrder(order);
            savedDetails.add(orderDetailService.create(detail));
        }
        return savedDetails;
    }

    private OrderDTO toDto(Orders order, List<Order_details> details) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setAddress(order.getAddress());
        dto.setStatus(order.getStatus());
        dto.setCreateDate(order.getCreateDate());
        if (order.getAccount() != null) {
            dto.setUsername(order.getAccount().getUsername());
        }

        BigDecimal total = BigDecimal.ZERO;
        List<OrderDetailDTO> itemDtos = new ArrayList<>();
        for (Order_details detail : details) {
            OrderDetailDTO itemDto = new OrderDetailDTO();
            itemDto.setId(detail.getId());
            if (detail.getProduct() != null) {
                itemDto.setProductId(detail.getProduct().getId());
                itemDto.setProductName(detail.getProduct().getName());
            }
            itemDto.setPrice(detail.getPrice());
            itemDto.setQuantity(detail.getQuantity());
            itemDtos.add(itemDto);

            if (detail.getPrice() != null && detail.getQuantity() != null) {
                total = total.add(detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
            }
        }
        dto.setItems(itemDtos);
        dto.setTotalAmount(total);
        return dto;
    }
}
