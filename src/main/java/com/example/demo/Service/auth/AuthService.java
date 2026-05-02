package com.example.demo.Service.auth;

import com.example.demo.Model.user.Account;

public interface AuthService {
    boolean login(String username, String password);

    void logout();

    Account getUser();

    boolean isAuthenticated();

    boolean hasRole(String role);






}
