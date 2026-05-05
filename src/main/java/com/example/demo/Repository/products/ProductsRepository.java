package com.example.demo.Repository.products;

import com.example.demo.Model.product.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

public interface ProductsRepository extends JpaRepository<Products, Integer> {
    List<Products> findTop8ByOrderByCreateDateDesc();
    List<Products> findTop8ByDiscountGreaterThanOrderByDiscountDesc(BigDecimal discount);
    List<Products> findByCategoryId(String categoryId);
    List<Products> findTop4ByCategoryIdAndIdNot(String categoryId, Integer id);

    @Query("select p from Products p join p.orderDetails od group by p order by sum(od.quantity) desc ")
    List<Products> findBestSellers(Pageable pageable);
}
