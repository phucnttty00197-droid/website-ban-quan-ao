package com.example.demo.Service.user.impl;

import com.example.demo.Model.user.Account;
import com.example.demo.Repository.user.AccountRepo;
import com.example.demo.Service.user.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepo accountRepo;

    @Override
    public List<Account> findAll(){
        return accountRepo.findAll();
    }

    @Override
    public Optional<Account> findByUsername(String username){
        return  accountRepo.findById(username);
    }

    @Override
    public Optional<Account> findByEmail(String email){
        return accountRepo.findByEmail(email);
    }

    @Override
    public Account create(Account account){
        return accountRepo.save(account);
    }

    @Override
    public Account update(Account account){
        return accountRepo.save(account);
    }

    @Override
    public void deleteByUsername(String username){
        accountRepo.deleteById(username);
    }







}
