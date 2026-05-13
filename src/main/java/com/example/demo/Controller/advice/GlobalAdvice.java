package com.example.demo.Controller.advice;

import com.example.demo.Model.product.Categories;

import com.example.demo.Service.auth.AuthService;
import com.example.demo.Service.product.CategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


@ControllerAdvice
@RequiredArgsConstructor
public class GlobalAdvice {
    private final CategoryService categoryService;
    private final AuthService authService;

    @ModelAttribute("isAdmin")
    private boolean isAdmin(){
        return authService.hasRole("ADMIN");
    }



}
