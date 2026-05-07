package com.example.demo.repository.review;

import com.example.demo.Model.review.Product_review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductReviewRepository extends JpaRepository<Product_review, Long> {
    boolean existsByAccountUsernameAndProductIdAndOrderId(String username, Integer productId, Long orderId);

    List<Product_review> findByAccountUsernameAndOrderId(String username, Long orderId);

    @Query("""
select  r
from Product_review r
join fetch  r.account a
where r.product.id = :productId
order by r.createdAt desc
""")
    List<Product_review> findByProductIdOrderByCreatedAtDesc(@Param("productId") Integer productId);

    @Query("""
select avg(r.starRating) as avgRating,
        count(r) as reviewCount
        from Product_review  r
        where r.product.id = :productId
""")
    ProductReviewStats getProductReviewStatsByProductId(@Param("productId") Integer productId);
}
