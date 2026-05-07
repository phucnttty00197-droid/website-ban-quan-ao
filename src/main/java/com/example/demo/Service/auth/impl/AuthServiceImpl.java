package com.example.demo.Service.auth.impl;

import com.example.demo.Model.user.Account;
import com.example.demo.repository.user.AuthorityRepo;
import com.example.demo.Service.auth.AuthService;
import com.example.demo.Service.user.AccountService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    public static final String SESSION_USER_KEY = "USER";

    private final HttpSession  session;
    private final AccountService accountService;
    private final AuthorityRepo authRepo;

    @Override
    public  boolean login (String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        Optional<Account> accountOpt = accountService.findByUsername(username);
        if (accountOpt.isEmpty()) {
            return false;
        }
        Account account = accountOpt.get();
        if (account.getActivated() != null && !account.getActivated()) {
            return false;
        }
        if (!password.equals(account.getPassword())) {
            return false;
        }
        session.setAttribute(SESSION_USER_KEY, account);
        return true;
    }

    @Override
    public void logout() {
        session.invalidate();
    }

    @Override
    public Account getUser(){
        Object value = session.getAttribute(SESSION_USER_KEY);
        if (value instanceof Account) {
            return (Account) value;
        }
        return null;
    }
    @Override
    public boolean isAuthenticated() {
        return getUser() != null;
    }
    @Override
    public boolean hasRole(String role) {
        Account account = getUser();
        if (account == null || role == null || role.isBlank()) {
            return false;
        }
        return authRepo.existsByAccountUsernameAndRoleId(account.getUsername(), role);
    }

}

