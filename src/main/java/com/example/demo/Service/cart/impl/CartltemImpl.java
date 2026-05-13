package com.example.demo.Service.cart.impl;


import com.example.demo.Model.cart.Cart_items;
import com.example.demo.Model.product.Product_size;
import com.example.demo.Service.auth.AuthService;
import com.example.demo.Service.cart.CartService;
import com.example.demo.Service.product.ProductSizeService;
import com.example.demo.Service.product.ProductsService;
import com.example.demo.repository.cart.CartItemRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CartltemImpl implements CartService {
    private static final String SESSION_CART_KEY = "SESSION_CART";
    private static final String LEGACY_CART_SESSION_KEY = "CART_ITEMS";

    private final HttpSession session;
    private final ProductsService productsService;
    private final ProductSizeService productSizeService;
    private final AuthService authService;
    private final CartItemRepository cartItemRepository;

    @Override
    public boolean addToCart(Integer productId,Integer sizeId,Integer quantity) {
        if (isAuthenticated()) {

        }

    }

    private boolean isAuthenticated() {
        return authService != null && authService.isAuthenticated() && authService.getUser() != null;
    }
    private List<Cart_items> getSessionCartItems(boolean createIfMissing) {
        Object value = session.getAttribute(SESSION_CART_KEY);
        if (!(value instanceof List<?>)) {
            // Backward compatibility: old session key from previous builds
            value = session.getAttribute(LEGACY_CART_SESSION_KEY);
            if (value instanceof List<?>) {
                session.setAttribute(SESSION_CART_KEY, value);
                session.removeAttribute(LEGACY_CART_SESSION_KEY);
            }
        }
        if (value instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Cart_items> items = (List<Cart_items>) value;
            return items;
        }
        List<Cart_items> items = new ArrayList<>();
        if (createIfMissing) {
            saveSessionItems(items);
        }
        return items;
    }
    private void saveSessionItems(List<Cart_items> items) {
        session.setAttribute(SESSION_CART_KEY, items);
    }


}
