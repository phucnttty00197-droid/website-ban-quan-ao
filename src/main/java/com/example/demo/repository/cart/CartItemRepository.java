package com.example.demo.repository.cart;

import com.example.demo.Model.cart.Cart_items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<Cart_items,Long> {

    @Query("""
            select ci
            from Cart_items ci
            join fetch ci.product p
            join fetch ci.sizes s
            where ci.account.username = :username
            order by ci.created_at desc, ci.id desc
            """)
    List<Cart_items> findByUsernameWithRefs(@Param("username") String username);

    Optional<Cart_items> findByAccountUsernameAndProductIdAndSizesId(String username, Integer productId, Integer sizeId);
    @Query("""
            select count(distinct ci.product.id)
            from Cart_items ci
            where ci.account.username = :username
            """)
    long countDistinctProductsByUsername(@Param("username") String username);

    @Query("""
            select distinct ci.product.id
            from Cart_items ci
            where ci.account.username = :username
            """)
    List<Integer> findDistinctProductIdsByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query("delete from Cart_items c where c.account.username = :username")
    void deleteByAccountUsername(String username);
}
