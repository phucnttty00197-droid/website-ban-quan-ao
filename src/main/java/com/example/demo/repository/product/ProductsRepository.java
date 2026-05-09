package com.example.demo.repository.product;

import com.example.demo.Model.product.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductsRepository extends JpaRepository<Products, Integer> {
    List<Products> findTop8ByOrderByCreateDateDesc();
    List<Products> findTop8ByDiscountGreaterThanOrderByDiscountDesc(BigDecimal discount);
    List<Products> findByCategoryId(String categoryId);
    List<Products> findTop4ByCategoryIdAndIdNot(String categoryId, Integer id);

    @Query("select p from Products p join p.orderDetails od group by p order by sum(od.quantity) desc ")
    List<Products> findBestSellers(Pageable pageable);

    @Query("""
            select p
            from Products p
            join p.category c
            where (:keyword is null or :keyword = '' or lower(p.name) like lower(concat('%', :keyword, '%'))
               or lower(c.name) like lower(concat('%', :keyword, '%')))
              and (:categoryId is null or :categoryId = '' or c.id = :categoryId)
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
            """)
    List<Products> search(@Param("keyword") String keyword,
                         @Param("categoryId") String categoryId,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice);

    @Query("""
            select p
            from Products p
            join p.category c
            where (:keyword is null or :keyword = '' or lower(p.name) like lower(concat('%', :keyword, '%'))
               or lower(c.name) like lower(concat('%', :keyword, '%')))
              and (:categoryId is null or :categoryId = '' or c.id = :categoryId)
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
            """)
    Page<Products> searchPage(@Param("keyword") String keyword,
                             @Param("categoryId") String categoryId,
                             @Param("minPrice") BigDecimal minPrice,
                             @Param("maxPrice") BigDecimal maxPrice,
                             Pageable pageable);

    @Query("""
            select p
            from Products p
            join p.category c
            where (:keyword is null or :keyword = '' or lower(p.name) like lower(concat('%', :keyword, '%'))
               or lower(c.name) like lower(concat('%', :keyword, '%')))
              and (:categoryId is null or :categoryId = '' or c.id = :categoryId)
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
            order by p.price asc
            """)
    List<Products> searchOrderByPriceAsc(@Param("keyword") String keyword,
                                        @Param("categoryId") String categoryId,
                                        @Param("minPrice") BigDecimal minPrice,
                                        @Param("maxPrice") BigDecimal maxPrice);

    @Query("""
            select p
            from Products p
            join p.category c
            where (:keyword is null or :keyword = '' or lower(p.name) like lower(concat('%', :keyword, '%'))
               or lower(c.name) like lower(concat('%', :keyword, '%')))
              and (:categoryId is null or :categoryId = '' or c.id = :categoryId)
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
            order by p.price desc
            """)
    List<Products> searchOrderByPriceDesc(@Param("keyword") String keyword,
                                         @Param("categoryId") String categoryId,
                                         @Param("minPrice") BigDecimal minPrice,
                                         @Param("maxPrice") BigDecimal maxPrice);

}
