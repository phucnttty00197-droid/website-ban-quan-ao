package com.example.demo.repository.product;

import com.example.demo.Model.product.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductsRepository extends JpaRepository<Products, Integer> {
    Page<Products> findByDeletedFalse(Pageable pageable);
    List<Products> findTop8ByDeletedFalseOrderByCreateDateDesc();
    List<Products> findTop8ByDeletedFalseAndDiscountGreaterThanOrderByDiscountDesc(BigDecimal discount);
    List<Products> findByDeletedFalseAndCategoryId(String categoryId);
    Optional<Products> findByIdAndDeletedFalse(Integer id);
    List<Products> findTop4ByDeletedFalseAndCategoryIdAndIdNot(
            String categoryId,
            Integer id
    );

    @Query("""
            select p
            from Products p
            join p.orderDetails od
            where p.deleted = false
            group by p
            order by sum(od.quantity) desc
            """)
    List<Products> findBestSellers(Pageable pageable);

    @Query("""
            select p
            from Products p
            join p.category c
            where p.deleted = false
              and (:keyword is null or :keyword = '' 
                   or lower(p.name) like lower(concat('%', :keyword, '%'))
                   or lower(c.name) like lower(concat('%', :keyword, '%')))
              and (:categoryId is null or :categoryId = '' 
                   or c.id = :categoryId)
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
            where p.deleted = false
              and (:keyword is null or :keyword = '' 
                   or lower(p.name) like lower(concat('%', :keyword, '%'))
                   or lower(c.name) like lower(concat('%', :keyword, '%')))
              and (:categoryId is null or :categoryId = '' 
                   or c.id = :categoryId)
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
            where p.deleted = false
              and (:keyword is null or :keyword = '' 
                   or lower(p.name) like lower(concat('%', :keyword, '%'))
                   or lower(c.name) like lower(concat('%', :keyword, '%')))
              and (:categoryId is null or :categoryId = '' 
                   or c.id = :categoryId)
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
            where p.deleted = false
              and (:keyword is null or :keyword = '' 
                   or lower(p.name) like lower(concat('%', :keyword, '%'))
                   or lower(c.name) like lower(concat('%', :keyword, '%')))
              and (:categoryId is null or :categoryId = '' 
                   or c.id = :categoryId)
              and (:minPrice is null or p.price >= :minPrice)
              and (:maxPrice is null or p.price <= :maxPrice)
            order by p.price desc
            """)
    List<Products> searchOrderByPriceDesc(@Param("keyword") String keyword,
                                          @Param("categoryId") String categoryId,
                                          @Param("minPrice") BigDecimal minPrice,
                                          @Param("maxPrice") BigDecimal maxPrice);

}
