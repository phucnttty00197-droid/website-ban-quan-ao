package com.example.demo.repository.user;

import com.example.demo.Model.user.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, String> {
    Optional<Account> findByEmail(String email);

    Optional<Account> findByUsernameAndDeletedFalse(String username);

    List<Account> findAllByDeletedFalse();
}
