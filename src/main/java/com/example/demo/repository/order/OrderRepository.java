package com.example.demo.repository.order;

import com.example.demo.Model.order.Orders;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByDeletedFalse();

    Optional<Orders> findByIdAndDeletedFalse(Long id);

    List<Orders> findByAccountUsernameAndDeletedFalseOrderByCreateDateDesc(String username);

    @Query("""
          select a.fullname as fullname,
                   sum(d.price * d.quantity) as totalAmount,
                   min(o.createDate) as firstOrderDate,
                   max(o.createDate) as lastOrderDate
            from Orders o
            join o.account a
            join o.orderDetails d
            group by a.fullname
            order by sum(d.price * d.quantity) desc
          """)
    List<VipReport> getVipCustomers(Pageable pageable);

}
