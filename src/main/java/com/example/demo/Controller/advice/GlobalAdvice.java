package com.example.demo.Controller.advice;

import com.example.demo.Model.notification.Notifications;
import com.example.demo.Model.product.Categories;

import com.example.demo.Service.auth.AuthService;
import com.example.demo.Service.cart.CartService;
import com.example.demo.Service.notification.NotificationService;
import com.example.demo.Service.product.CategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Set;


@ControllerAdvice
@RequiredArgsConstructor
public class GlobalAdvice {
    private final CategoryService categoryService;
    private final NotificationService notificationService;
    private final CartService cartService;
    private final AuthService authService;

    @ModelAttribute("navCategories")
    public List<Categories> navCategories() {
        return categoryService.findAll();
    }

    @ModelAttribute("isAdmin")
    private boolean isAdmin(){
        return authService.hasRole("ADMIN");
    }

    @ModelAttribute("unreadNotificationCount")
    public long unreadNotificationCount(){
        if (!authService.isAuthenticated()){
            return 0;
        }
        return notificationService.countUnread(authService.getUser().getUsername());
    }

    @ModelAttribute("latestNotifications")
    public List<Notifications> latestNotifications(){
        if (!authService.isAuthenticated()){
            return List.of();
        }
        return notificationService.getLatest(authService.getUser().getUsername(), 8);
    }

    @ModelAttribute("cartDistinctCount")
    public long cartDistinctCount(){
        return cartService.getDistinctProductCount();
    }

    @ModelAttribute("cartProductIds")
    public Set<Integer> cartProductIds(){
        return cartService.getProductIdsInCart();
    }



}
