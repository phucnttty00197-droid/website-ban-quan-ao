package com.example.demo.repository.product;

import com.example.demo.Model.product.Product_size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductSizeRepository extends JpaRepository<Product_size, Integer> {
    @Query("select ps from Product_size ps join fetch ps.size where ps.product.id = ?1")
    List<Product_size> findByProductId(Integer productId);

    @Query("select ps from Product_size ps join fetch ps.size where ps.product.id in ?1")
    List<Product_size> findByProductIdIn(List<Integer> productIds);

    @Query("select ps from Product_size ps join fetch ps.size where ps.product.id = ?1 and ps.size.id=?2")
    Optional<Product_size> findByProductIdAndSizeId(Integer productId, Integer sizeId);

    void deleteByProductId(Integer productId);
}
