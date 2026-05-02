package com.example.demo.Service.user;

import com.example.demo.Model.user.Account;
import org.apache.catalina.Role;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<Account> findAll();

    Optional<Account> findByUsername(String username);

    Optional<Account> findByEmail(String email);

    Account create (Account account);

    Account update(Account account);

    void deleteByUsername(String username);
}
