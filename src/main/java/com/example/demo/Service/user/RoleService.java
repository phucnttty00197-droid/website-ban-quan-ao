package com.example.demo.Service.user;



import com.example.demo.Model.user.Roles;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    List<Roles> findAll();

    Optional<Roles> findById(String id);

    Roles create(Roles roles);

    Roles update(Roles roles);

    void deleteById(String id);

}
