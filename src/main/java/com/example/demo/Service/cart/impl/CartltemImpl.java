package com.example.demo.Service.cart.impl;


import com.example.demo.Model.cart.Cart_items;
import com.example.demo.Model.product.Product_size;
import com.example.demo.Model.product.Products;
import com.example.demo.Model.product.Sizes;
import com.example.demo.Model.user.Account;
import com.example.demo.Service.auth.AuthService;
import com.example.demo.Service.cart.CartService;
import com.example.demo.Service.cart.Cartltem;
import com.example.demo.Service.product.ProductSizeService;
import com.example.demo.Service.product.ProductsService;
import com.example.demo.repository.cart.CartItemRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CartltemImpl implements CartService {
    private static final String SESSION_CART_KEY = "SESSION_CART";
    private static final String LEGACY_CART_SESSION_KEY = "CART_ITEMS";

    private final HttpSession session;
    private final ProductsService productService;
    private final ProductSizeService productSizeService;
    private final AuthService authService;
    private final CartItemRepository cartItemRepository;

    @Override
    public boolean addToCart(Integer productId,Integer sizeId,Integer quantity) {
        if (isAuthenticated()) {
            return addToUserCart(authService.getUser().getUsername(), productId, sizeId, quantity);
        }
        return addToSessionCart(productId, sizeId, quantity);
    }
    @Override
    public List<Cartltem> getCartItems() {
        if (isAuthenticated()) {
            return getUserCartItems(authService.getUser().getUsername());
        }
        return getSessionCartItems(false);
    }
    @Override
    public void mergeSessionCartToUserCart(String username) {
        if (username == null || username.isBlank()) {
            return;
        }
        List<Cartltem> sessionItems = getSessionCartItems(false);
        if (sessionItems.isEmpty()) {
            return;
        }
        for (Cartltem item : sessionItems) {
            if (item == null || item.getProductId() == null || item.getSizeId() == null || item.getQuantity() == null) {
                continue;
            }
            int qty = item.getQuantity();
            if (qty <= 0) {
                continue;
            }
            mergeIntoUserCart(username, item.getProductId(), item.getSizeId(), qty);
        }
        clearSessionCart();
    }
    @Override
    public void clearSessionCart() {
        session.removeAttribute(SESSION_CART_KEY);
        session.removeAttribute(LEGACY_CART_SESSION_KEY);
    }

    @Override
    @Transactional
    public void clearCart() {
        if (isAuthenticated()) {
            cartItemRepository.deleteByAccountUsername(authService.getUser().getUsername());
            return;
        }
        clearSessionCart();
    }
    @Override
    public long getDistinctProductCount() {
        if (isAuthenticated()) {
            return cartItemRepository.countDistinctProductsByUsername(authService.getUser().getUsername());
        }
        return getSessionCartItems(false)
                .stream()
                .map(Cartltem::getProductId)
                .distinct()
                .count();
    }
    @Override
    public Set<Integer> getProductIdsInCart() {
        if (isAuthenticated()) {
            return new HashSet<>(cartItemRepository.findDistinctProductIdsByUsername(authService.getUser().getUsername()));
        }
        return getSessionCartItems(false)
                .stream()
                .map(Cartltem::getProductId)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public boolean add(Integer productId, Integer sizeId) {
        return addToCart(productId, sizeId, 1);
    }

    @Override
    public boolean add(Integer productId, Integer sizeId, Integer quantity) {
        return addToCart(productId, sizeId, quantity);
    }
    @Override
    public void remove(Integer productId, Integer sizeId) {
        if (isAuthenticated()) {
            cartItemRepository.findByAccountUsernameAndProductIdAndSizesId(authService.getUser().getUsername(), productId, sizeId)
                    .ifPresent(cartItemRepository::delete);
            return;
        }
        List<Cartltem> items = getSessionCartItems(true);
        items.removeIf(item -> item.getProductId().equals(productId) && item.getSizeId().equals(sizeId));
        saveSessionItems(items);
    }
    @Override
    public boolean update(Integer productId, Integer sizeId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            remove(productId, sizeId);
            return true;
        }

        Optional<Product_size> productSize = productSizeService.findByProductIdAndSizeId(productId, sizeId);
        if (productSize.isEmpty() || productSize.get().getQuantity() == null) {
            return false;
        }
        if (quantity > productSize.get().getQuantity()) {
            return false;
        }

        if (isAuthenticated()) {
            var opt = cartItemRepository.findByAccountUsernameAndProductIdAndSizesId(authService.getUser().getUsername(), productId, sizeId);
            if (opt.isEmpty()) {
                return false;
            }
            var entity = opt.get();
            entity.setQuantity(quantity);
            cartItemRepository.save(entity);
            return true;
        }

        List<Cartltem> items = getSessionCartItems(true);
        for (Cartltem item : items) {
            if (item.getProductId().equals(productId) && item.getSizeId().equals(sizeId)) {
                item.setQuantity(quantity);
                break;
            }
        }
        saveSessionItems(items);
        return true;
    }
    @Override
    public void clear() {
        clearCart();
    }

    @Override
    public List<Cartltem> getItems() {
        return getCartItems();
    }

    @Override
    public BigDecimal getTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (Cartltem item : getCartItems()) {
            if (item.getPrice() != null && item.getQuantity() != null) {
                total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        return total;
    }
    private boolean isAuthenticated() {
        return authService != null && authService.isAuthenticated() && authService.getUser() != null;
    }
    private List<Cartltem> getSessionCartItems(boolean createIfMissing) {
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
            List<Cartltem> items = (List<Cartltem>) value;
            return items;
        }
        List<Cartltem> items = new ArrayList<>();
        if (createIfMissing) {
            saveSessionItems(items);
        }
        return items;
    }
    private void saveSessionItems(List<Cartltem> items) {
        session.setAttribute(SESSION_CART_KEY, items);
    }

    private boolean addToSessionCart(Integer productId, Integer sizeId, Integer quantity) {
        List<Cartltem> items = getSessionCartItems(true);
        if (sizeId == null || quantity == null || quantity <= 0) {
            return false;
        }
        Optional<Product_size> productSize = productSizeService.findByProductIdAndSizeId(productId, sizeId);
        if (productSize.isEmpty() || productSize.get().getQuantity() == null || productSize.get().getQuantity() <= 0) {
            return false;
        }

        String sizeName = productSize.get().getSize() != null ? productSize.get().getSize().getName() : null;
        for (Cartltem item : items) {
            if (item.getProductId().equals(productId) && item.getSizeId().equals(sizeId)) {
                int nextQuantity = item.getQuantity() + quantity;
                if (nextQuantity <= productSize.get().getQuantity()) {
                    item.setQuantity(nextQuantity);
                    saveSessionItems(items);
                    return true;
                }
                return false;
            }
        }

        Optional<Products> productOpt = productService.findById(productId);
        if (productOpt.isEmpty()) {
            return false;
        }

        Products product = productOpt.get();
        if (product.getAvailable() == null || !product.getAvailable()) {
            return false;
        }

        if (quantity > productSize.get().getQuantity()) {
            return false;
        }
        Cartltem newItem = new Cartltem(
                product.getId(),
                product.getName(),
                sizeId,
                sizeName,
                product.getPrice(),
                quantity,
                product.getImage()
        );
        items.add(newItem);
        saveSessionItems(items);
        return true;
    }

    private List<Cartltem> getUserCartItems(String username) {
        var entities = cartItemRepository.findByUsernameWithRefs(username);
        List<Cartltem> items = new ArrayList<>();
        for (var ci : entities) {
            var p = ci.getProduct();
            var s = ci.getSizes();
            items.add(new Cartltem(
                    p != null ? p.getId() : null,
                    p != null ? p.getName() : null,
                    s != null ? s.getId() : null,
                    s != null ? s.getName() : null,
                    p != null ? p.getPrice() : null,
                    ci.getQuantity(),
                    p != null ? p.getImage() : null
            ));
        }
        return items;
    }
    private boolean addToUserCart(String username, Integer productId, Integer sizeId, Integer quantity) {



        if (username == null || username.isBlank()) {
            return false;
        }
        if (sizeId == null || quantity == null || quantity <= 0) {
            return false;
        }
        Optional<Products> productOpt = productService.findById(productId);
        if (productOpt.isEmpty()) {
            return false;
        }

        Products product = productOpt.get();
        if (product.getAvailable() == null || !product.getAvailable()) {
            return false;
        }
        Optional<Product_size> productSize = productSizeService.findByProductIdAndSizeId(productId, sizeId);
        if (productSize.isEmpty() || productSize.get().getQuantity() == null || productSize.get().getQuantity() <= 0) {
            return false;
        }
        int stock = productSize.get().getQuantity();

        var existingOpt = cartItemRepository.findByAccountUsernameAndProductIdAndSizesId(username, productId, sizeId);
        if (existingOpt.isPresent()) {
            var existing = existingOpt.get();
            int next = (existing.getQuantity() != null ? existing.getQuantity() : 0) + quantity;
            if (next > stock) {
                return false;
            }
            existing.setQuantity(next);
            cartItemRepository.save(existing);
            return true;
        }

        if (quantity > stock) {
            return false;
        }

        Account acc = new Account();
        acc.setUsername(username);
        Products p = new Products();
        p.setId(productId);
        Sizes s = new Sizes();
        s.setId(sizeId);

        var entity = Cart_items.builder()
                .account(acc)
                .product(p)
                .sizes(s)
                .quantity(quantity)
                .build();
        cartItemRepository.save(entity);
        return true;
    }
    private void mergeIntoUserCart(String username, Integer productId, Integer sizeId, Integer quantity) {
        if (username == null || username.isBlank() || productId == null || sizeId == null || quantity == null || quantity <= 0) {
            return;
        }
        Optional<Product_size> productSize = productSizeService.findByProductIdAndSizeId(productId, sizeId);
        if (productSize.isEmpty() || productSize.get().getQuantity() == null || productSize.get().getQuantity() <= 0) {
            return;
        }
        int stock = productSize.get().getQuantity();

        var existingOpt = cartItemRepository.findByAccountUsernameAndProductIdAndSizesId(username, productId, sizeId);
        if (existingOpt.isPresent()) {
            var existing = existingOpt.get();
            int current = existing.getQuantity() != null ? existing.getQuantity() : 0;
            int merged = current + quantity;
            existing.setQuantity(Math.min(merged, stock));
            cartItemRepository.save(existing);
            return;
        }

        Account acc = new Account();
        acc.setUsername(username);
        Products p = new Products();
        p.setId(productId);
        Sizes s = new Sizes();
        s.setId(sizeId);
        var entity = Cart_items.builder()
                .account(acc)
                .product(p)
                .sizes(s)
                .quantity(Math.min(quantity, stock))
                .build();
        cartItemRepository.save(entity);
    }
                }

