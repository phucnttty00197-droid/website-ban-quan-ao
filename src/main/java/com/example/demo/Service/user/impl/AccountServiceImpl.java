package com.example.demo.Service.user.impl;

import com.example.demo.Model.user.Account;
import com.example.demo.Service.user.AuthorityService;
import com.example.demo.repository.user.AccountRepo;
import com.example.demo.Service.user.AccountService;
import com.example.demo.repository.user.AuthorityRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepo accountRepo;
    private final AuthorityService authorityService;

    @Override
    public List<Account> findAll(){
        return accountRepo.findAllByDeletedFalse();
    }

    @Override
    public Optional<Account> findByUsername(String username){
        return accountRepo.findByUsernameAndDeletedFalse(username);
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
        Account acc = accountRepo.findById(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        authorityService.deleteByAccountUsername(username);

        // 2. xóa mềm account
        acc.setDeleted(true);
        accountRepo.save(acc);
    }







}
