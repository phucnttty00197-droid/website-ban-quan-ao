package com.example.demo.Repository.notification;

import com.example.demo.Model.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notifications, Long> {

    long countByAccountUsernameAndReadFalse(String username);

    void deleteByOrderId(Long orderId);

    @Query("""
            select n
            from Notifications n
            left join fetch n.order o
            where n.account.username = :username
            order by n.createdAt desc
            """)
    List<Notifications> findLatestByUsername(@Param("username") String username, Pageable pageable);

    @Query("""
select n
from Notifications n
left join fetch n.order o
where n.id = :id and n.account.username = :username
""")
    Optional<Notifications> findByIdAndUsername(@Param("id") Long id, @Param("username") String username);


}
