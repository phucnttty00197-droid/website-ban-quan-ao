package com.example.demo.Service.user.impl;

import com.example.demo.Model.user.Authority;
import com.example.demo.repository.user.AuthorityRepo;

import com.example.demo.Service.user.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {
    private final AuthorityRepo authRepo;

    @Override
    public List<Authority> findAll(){
        return authRepo.findAll();
    }

    @Override
    public Optional<Authority> findById(Long id ){
        return authRepo.findById(id);
    }
    @Override
    public List<Authority> findByAccountUsername(String username){
        return authRepo.findByAccountUsername(username);
    }

    @Override
    public Authority create (Authority auth){
        return authRepo.save(auth);
    }

    @Override
    public Authority update (Authority auth){
        return authRepo.save(auth);
    }

    @Override
    public void deleteById(Long id){
        authRepo.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByAccountUsername(String username){
        authRepo.deleteByAccountUsername(username);
    }



}
