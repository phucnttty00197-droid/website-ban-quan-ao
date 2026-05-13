package com.example.demo.Controller.web;

import com.example.demo.Model.notification.Notifications;
import com.example.demo.Model.user.Account;
import com.example.demo.Service.auth.AuthService;
import com.example.demo.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class NotificationController {
    private final AuthService authService;
    private final NotificationRepository notificationRepository;
    @GetMapping("/notifications/read/{id}")
    public String read (@PathVariable("id") Long id){
        Account  user = authService.getUser();
        if (user == null) {
            return "redirect:/auth/login";
        }
        Optional<Notifications> notificationOpt = notificationRepository.findByIdAndUsername(id, user.getUsername());
        if (notificationOpt.isEmpty()) {
            return "redirect:/home/index";
        }
        return "redirect:/home/index";
    }
}
