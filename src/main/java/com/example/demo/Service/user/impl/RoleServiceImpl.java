package com.example.demo.Service.user.impl;

import com.example.demo.Model.user.Roles;
import com.example.demo.Repository.user.RolesRepo;
import com.example.demo.Service.user.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private  final RolesRepo rolesRepo;

    @Override
    public List<Roles> findAll(){
        return rolesRepo.findAll();
    }

    @Override
    public Optional<Roles> findById(String id){
        return rolesRepo.findById(id);
    }

    @Override
    public Roles create(Roles roles){
        return rolesRepo.save(roles);
    }

    @Override
    public Roles update(Roles roles){
        return rolesRepo.save(roles);
    }

    @Override
    public void deleteById(String id){
        rolesRepo.deleteById(id);
    }


}
