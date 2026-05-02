package com.example.demo.Service.user;

import com.example.demo.Model.user.Authority;

import java.util.List;
import java.util.Optional;

public interface AuthorityService {

    List<Authority> findAll();

    Optional<Authority> findById(Long id);

    List<Authority> findByAccountUsername(String username);

    Authority create(Authority auth);

    Authority update(Authority auth);

    void deleteById(Long id);

    void deleteByAccountUsername(String username);

}
