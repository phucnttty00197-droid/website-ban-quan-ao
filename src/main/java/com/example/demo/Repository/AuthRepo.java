package com.example.demo.Repository;

import com.example.demo.Model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthRepo extends JpaRepository<Auth, Long> {
    boolean existsByAccountUsernameAndRoleId(String username, String roleId);

    List<Auth> findByAccountUsername(String username);

    @Query("""
            select a
            from Auth a
            join fetch a.account acc
            join fetch a.role r
            where r.id = :roleId
            """)
    List<Auth> findByRoleId(@Param("roleId") String roleId);

    void deleteByAccountUsername(String username);
}
