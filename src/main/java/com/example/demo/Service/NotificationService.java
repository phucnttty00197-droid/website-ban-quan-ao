package com.example.demo.Service;

import com.example.demo.Model.user.Account;
import com.example.demo.Model.Notifications;
import com.example.demo.Model.Orders;

import java.util.List;
import java.util.Optional;

public interface NotificationService {

    Notifications createNotification(Account account, Orders orders, String title, String content);

    void notifyOrderPlacedForUser(Account account, Orders orders);

    void notifyOrderPleacedForAdmins(Orders orders);

    void notifyOrderStatusChange(Orders orders, String status);

    void countUnread(String username);

    List<Notifications> getLatest(String username, int limit);

    Optional<Notifications> findByIdAndUsername (Long id, String username);

    Notifications markRead(Notifications notifications);

    void deleteByOrderId(Long orderId);



}
