package com.example.demo.Service.notification.impl;

import com.example.demo.Model.order.Order_details;
import com.example.demo.Model.order.Orders;
import com.example.demo.Model.product.Products;
import com.example.demo.Model.user.Account;
import com.example.demo.Service.auth.AuthService;
import com.example.demo.Service.order.OrderDetailService;
import com.example.demo.Service.order.OrderService;
import com.example.demo.Service.review.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class NotificationServiceImpl {
    private final AuthService authService;
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;
    private final ProductReviewService productReviewService;

    @PostMapping("/order/review")
    public String create(@RequestParam("orderId") Long orderId,
                         @RequestParam("productId") Integer productId,
                         @RequestParam("startRating") Integer startRating,
                         @RequestParam(value = "reviewContent", required = false) String reviewContent,
                         @RequestParam(value = "images", required = false) MultipartFile[] images,
                         RedirectAttributes redirectAttributes) {
        Account user = authService.getUser();
        if (user == null) {
            return "redirect:/auth/login";
        }

        Optional<Orders> orderOpt = orderService.findById(orderId);
        if (orderOpt.isEmpty() || orderOpt.get().getAccount() == null
                || !user.getUsername().equals(orderOpt.get().getAccount().getUsername())) {
            redirectAttributes.addFlashAttribute("reviewMessage", "Đơn hàng không hợp lệ !");
            return "redirect:/order/detail/" + orderId;
        }
        Orders order = orderOpt.get();
        if (!isDeliveredStatus(order.getStatus())) {
            redirectAttributes.addFlashAttribute("reviewMessage", "Bạn chỉ có thể đánh giá khi đơn hàng đã được giao thành công!");
            return "redirect:/order/detail/" + orderId;
        }
        boolean hasProduct = orderDetailService.findByOrderId(orderId)
                .stream()
                .map(Order_details::getProduct)
                .anyMatch(product -> product != null && productId.equals(product.getId()));
        if (!hasProduct) {
            redirectAttributes.addFlashAttribute("reviewMessage", "Sản phẩm này không thuộc đơn hàng!");
            return "redirect:/order/detail/" + orderId;
        }

        if (productReviewService.hasReviewed(user.getUsername(), productId, orderId)) {
            redirectAttributes.addFlashAttribute("reviewMessage", "Bạn đã đánh giá sản phẩm này rồi. Vui lòng chọn sản phẩm khác để đánh giá!");
            return "redirect:/order/detail/" + orderId;
        }
        if (startRating == null || startRating < 1 || startRating > 5) {
            redirectAttributes.addFlashAttribute("reviewMessage", "Vui lòng chọn số sao hợp lệ!");
            return "redirect:/order/detail/" + orderId;
        }

        List<String> imageNames = saveReviewImages(images);
        Products product = new Products();
        product.setId(productId);
        productReviewService.createReview(user, product, order, startRating, reviewContent, imageNames);
        redirectAttributes.addAttribute("reviewMessage", "Đã gửi đánh giá thành công!");
        return "redirect:/order/detail/" + orderId;
    }

    private boolean isDeliveredStatus(String status) {
        return "DELIVERED_SUCCESS".equals(status) || "DONE".equals(status);
    }

    private List<String> saveReviewImages(MultipartFile[] files) {
        List<String> results = new ArrayList<>();
        if (files == null || files.length == 0) {
            return results;
        }
        Path uploadDir = Path.of("src/main/resources/static/images");
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }
            String fileName = "review-" + UUID.randomUUID() + ext;
            try {
                Files.createDirectories(uploadDir);
                Files.write(uploadDir.resolve(fileName), file.getBytes());
                results.add(fileName);

            } catch (IOException ignored) {

            }
        }
        return results;
    }
}
