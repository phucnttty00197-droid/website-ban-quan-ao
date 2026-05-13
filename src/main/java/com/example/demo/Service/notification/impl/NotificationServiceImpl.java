package com.example.demo.Service.notification.impl;






import com.example.demo.Model.notification.Notifications;
import com.example.demo.Model.order.Orders;
import com.example.demo.Model.user.Account;
import com.example.demo.Model.user.Authority;
import com.example.demo.Service.notification.NotificationService;
import com.example.demo.repository.notification.NotificationRepository;
import com.example.demo.repository.user.AuthorityRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final NotificationRepository notificationRepository;
    private final AuthorityRepo authorityRepository;

    @Override
    public Notifications createNotification(Account account, Orders order, String title, String content) {
        Notifications notification = new Notifications();
        notification.setAccount(account);
        notification.setOrder(order);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    @Override
    public void notifyOrderPlacedForUser(Account account, Orders order) {
        String time = FORMATTER.format(order.getCreateDate());
        String title = "Đặt hàng thành công";
        String content = "Bạn đã đặt hàng thành công đơn #" + order.getId() + " vào lúc " + time;
        createNotification(account, order, title, content);
    }

    @Override
    public void notifyOrderPleacedForAdmins(Orders order) {
        String time = FORMATTER.format(order.getCreateDate());
        String title = "Đơn hàng mới";
        String content = "Bạn có đơn hàng mới #" + order.getId() + " vào lúc " + time;
        List<Authority> admins = authorityRepository.findByRoleId("ADMIN");
        for (Authority authority : admins) {
            Account account = authority.getAccount();
            if (account != null) {
                createNotification(account, order, title, content);
            }
        }
    }

    @Override
    public void notifyOrderStatusChange(Orders order, String status) {
        if (order.getAccount() == null) {
            return;
        }
        String title = "Cập nhật đơn hàng";
        String content;
        if ("DELIVERED_SUCCESS".equals(status) || "DONE".equals(status)) {
            content = "Đơn hàng #" + order.getId() + " đã được giao thành công";
        } else if ("DELIVERY_FAILED".equals(status) || "CANCEL".equals(status)) {
            content = "Đơn hàng #" + order.getId() + " giao hàng thất bại";
        } else {
            return;
        }
        createNotification(order.getAccount(), order, title, content);
    }

    @Override
    public long countUnread(String username) {
        return notificationRepository.countByAccountUsernameAndReadFalse(username);
    }

    @Override
    public List<Notifications> getLatest(String username, int limit) {
        return notificationRepository.findLatestByUsername(username, PageRequest.of(0, limit));
    }

    @Override
    public Optional<Notifications> findByIdAndUsername(Long id, String username) {
        return notificationRepository.findByIdAndUsername(id, username);
    }

    @Override
    public Notifications markRead(Notifications notification) {
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    @Override
    public void deleteByOrderId(Long orderId) {
        if (orderId == null) {
            return;
        }

    }
}

