package com.example.demo.repository.product;

import com.example.demo.Model.product.Sizes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeRepository extends JpaRepository<Sizes, Integer> {
}
