package com.example.demo.Controller.web;

import com.example.demo.Model.order.Order_details;
import com.example.demo.Model.order.Orders;
import com.example.demo.Model.product.Product_size;
import com.example.demo.Model.product.Products;
import com.example.demo.Model.user.Account;
import com.example.demo.Service.auth.AuthService;
import com.example.demo.Service.cart.CartService;
import com.example.demo.Service.cart.Cartltem;
import com.example.demo.Service.notification.NotificationService;
import com.example.demo.Service.order.OrderDetailService;
import com.example.demo.Service.order.OrderService;
import com.example.demo.Service.payment.PayosPaymentService;
import com.example.demo.Service.product.ProductSizeService;
import com.example.demo.Service.review.ProductReviewService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import vn.payos.exception.PayOSException;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.v2.paymentRequests.PaymentLinkStatus;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSetMetaData;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderDetailService orderDetailService;
    private final CartService cartService;
    private final AuthService authService;
    private final ProductSizeService productSizeService;
    private final ProductReviewService productReviewService;
    private final NotificationService notificationService;
    private final PayosPaymentService payosPaymentService;
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/order/checkout")
    public String checkoutForm(Model model) {
        model.addAttribute("items", cartService.getItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());
        return "order/check-out";
    }

    @PostMapping("/order/checkout")
    public String checkout(@RequestParam("address") String address,
                           @RequestParam(value = "lat", required = false) Double lat,
                           @RequestParam(value = "lng", required = false) Double lng,
                           @RequestParam("paymentMethod") String paymentMethod,
                           Model model) {
        List<Cartltem> items = cartService.getItems();
        if (items.isEmpty()){
            model.addAttribute("message", "Giỏ hàng của bạn đang trống!");
            model.addAttribute("items", items);
            model.addAttribute("totalPrice", cartService.getTotalPrice());
            return "order/check-out";
        }
        for (Cartltem item : items) {
            if (item.getSizeId() == null){
                model.addAttribute("message", "Vui lòng chọn size trước khi đặt hàng!");
                model.addAttribute("items", items);
                model.addAttribute("totalPrice", cartService.getTotalPrice());
                return "order/check-out";
            }
            Optional<Product_size> productSize = productSizeService.findByProductIdAndSizeId(item.getProductId(), item.getSizeId());
            if (productSize.isEmpty() || productSize.get().getQuantity() == null){
                model.addAttribute("message", "Sản phẩm trong giỏ hàng của bạn hiện đã hết.");
                model.addAttribute("items", items);
                model.addAttribute("totalPrice", cartService.getTotalPrice());
                return "order/check-out";
            }

            Integer stock = productSize.get().getQuantity();
            Integer qty = item.getQuantity();
            if (qty == null || qty <= 0 || qty > stock){
                String name = item.getName() != null ? item.getName() : "Sản phẩm";
                String size = item.getSizeName() != null ? item.getSizeName() : "Size đã chọn";
                model.addAttribute("message", name + " (" + size +") Số lượng sản phẩm đã không còn đủ. Vui lòng chọn thêm sản phẩm khác hoặc giảm số lượng sản phẩm hiện tại!");
                model.addAttribute("items", items);
                model.addAttribute("totalPrice", cartService.getTotalPrice());
                return "order/check-out";
            }
        }
        Account user = authService.getUser();
        if (user == null){
            return "redirect:/auth/login";
        }

        Orders order = new Orders();
        order.setAccount(user);
        order.setAddress(address);
        String initialStatus = "PLACED_UNPAID";
        if ("BANK".equalsIgnoreCase(paymentMethod)){
            initialStatus = "PLACED_UNPAID";
        }
        order.setStatus(initialStatus);
        Orders savedOrder = orderService.create(order);
        updateOrderCoordinates(savedOrder.getId(), lat, lng);
        notificationService.notifyOrderPlacedForUser(user,savedOrder);
        notificationService.notifyOrderPleacedForAdmins(savedOrder);

        for (Cartltem item : items) {
            if (item.getSizeId() == null){
                continue;
            }
            Optional<Product_size> productSize = productSizeService.findByProductIdAndSizeId(item.getProductId(),item.getSizeId());
            if (productSize.isEmpty() || productSize.get().getQuantity() < item.getQuantity()){
                continue;
            }
            Order_details details = new Order_details();
            Products product = new Products();
            product.setId(item.getProductId());
            details.setProduct(product);
            details.setOrder(savedOrder);
            details.setPrice(item.getPrice());
            details.setQuantity(item.getQuantity());
            details.setSizeId(item.getSizeId());
            details.setSizeName(item.getSizeName());
            orderDetailService.create(details);

            Product_size ps = productSize.get();
            ps.setQuantity(ps.getQuantity() - item.getQuantity());
            productSizeService.save(ps);
        }
        cartService.clearCart();
        if ("BANK".equalsIgnoreCase(paymentMethod)){
            return "redirect:/order/bank-transfer/view/" + savedOrder.getId();
        }
        return "redirect:/order/detail/" + savedOrder.getId();
    }

    @GetMapping("/order/bank-transfer/view/{id}")
    public String bankTransfer(@PathVariable("id") Long id,
                               @RequestParam(value = "message", required = false) String message,
                               HttpServletRequest request,
                               Model model) {
        Account user = authService.getUser();
        if (user == null){
            return "redirect:/auth/login";
        }
        Optional<Orders> orderOpt = orderService.findById(id);
        if (orderOpt.isEmpty() || orderOpt.get().getAccount() == null
            || !user.getUsername().equals(orderOpt.get().getAccount().getUsername())){
            return "redirect:/order/list";
        }
        Orders order = orderOpt.get();
        List<Order_details> details = orderDetailService.findByOrderId(id);
        BigDecimal total = calculateOrderTotal(details);
        model.addAttribute("order", order);
        model.addAttribute("totalPrice", total);
        if (message!=null&& !message.isBlank()){
            model.addAttribute("message", message);
        }
        long amount = toPayosAmount(total);
        if (amount <= 0){
            model.addAttribute("message", "Số tiền thanh toán của bạn không hợp lệ!");
            return "order/bank-transfer";
        }
        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request).replacePath(null).build().toUriString();
        String returnUrl = baseUrl + "/order/payos/return?orderId=" + order.getId();
        String cancelUrl = baseUrl + "/order/payos/cancel?orderId=" + order.getId();
        try {
            CreatePaymentLinkResponse  response = payosPaymentService.createPaymentLink(
                    order.getId(),
                    amount,
                    "Thanh toán đơn hàng #" + order.getId(),
                    returnUrl,
                    cancelUrl
            );
            model.addAttribute("checkoutUrl", response.getCheckoutUrl());
            String qrCode = response.getQrCode();
            if (qrCode != null && !qrCode.isBlank()){
                model.addAttribute("qrImageSrc", buildQrImageSrc(qrCode));
            }
            model.addAttribute("accountName", response.getAccountName());
            model.addAttribute("accountNumber", response.getAccountNumber());
            model.addAttribute("bankName", "MB Bank");
            model.addAttribute("bankBin", response.getBin());
            model.addAttribute("paymentLinkId", response.getPaymentLinkId());
        }catch (PayOSException ex){
            ex.printStackTrace();
            model.addAttribute("message", ex.getMessage());
        }
        model.addAttribute("message", "Thanh toán chưa hoàn tất. Vui lòng chọn phương thức thanh toán khác!");
        return "order/bank-transfer";
    }
    @PostMapping("/order/bank-transfer/confirm")
    public String confirmBankTransfer(@RequestParam("orderId") Long orderId){
        Account user = authService.getUser();
        if (user == null){
            return "redirect:/auth/login";
        }
        Optional<Orders> orderOpt = orderService.findById(orderId);
        if (orderOpt.isEmpty() || orderOpt.get().getAccount() == null
        || !user.getUsername().equals(orderOpt.get().getAccount().getUsername())){
            return "redirect:/order/list";
        }
        Orders order = orderOpt.get();
        try {
            PaymentLink paymentLink = payosPaymentService.getPaymentLink(order.getId());
            if (paymentLink != null && paymentLink.getStatus() != null){
                PaymentLinkStatus status = paymentLink.getStatus();
                if (status == PaymentLinkStatus.PAID){
                    updateOrderStatusIfChanged(order, "PLACED_PAID");
                    return "redirect:/order/detail/" + orderId;
                }
            }
        }catch (PayOSException ignored){

        }
        String redirectUrl = UriComponentsBuilder
                .fromPath("/order/bank-transfer/view/" + orderId)
                .queryParam(
                        "message",
                        URLEncoder.encode(
                                "Thanh toán chưa hoàn tất",
                                StandardCharsets.UTF_8
                        )
                )
                .build()
                .toUriString();

        return "redirect:" + redirectUrl;
    }



    @GetMapping("/order/bank-transfer/cancel/view")
    public String cancelBankTransferPage(@RequestParam("orderId") Long orderId){
        Account user = authService.getUser();
        if (user == null){
            return "redirect:/auth/login";
        }
        Optional<Orders> orderOpt = orderService.findById(orderId);
        if (orderOpt.isEmpty() || orderOpt.get().getAccount() == null
        || !user.getUsername().equals(orderOpt.get().getAccount().getUsername())){
            return "redirect:/order/list";
        }
        String redirectUrl = UriComponentsBuilder.fromPath("/order/bank-transfer/cancel")
                .queryParam("orderId", orderId)
                .build()
                .toUriString();
        return "redirect:" + redirectUrl;
    }
    @PostMapping("/order/bank-transfer/cancel")
    public String cancelBankTransferAction(@RequestParam("orderId") Long orderId, Model model){
        Account user = authService.getUser();

        if (user == null){
            return "redirect:/auth/login";
        }

        Optional<Orders> orderOpt = orderService.findById(orderId);

        if (orderOpt.isEmpty()
                || orderOpt.get().getAccount() == null
                || !user.getUsername().equals(orderOpt.get().getAccount().getUsername())) {

            return "redirect:/order/list";
        }

        Orders order = orderOpt.get();

        List<Order_details> details = orderDetailService.findByOrderId(orderId);

        BigDecimal total = calculateOrderTotal(details);

        model.addAttribute("order", order);
        model.addAttribute("totalPrice", total);
        model.addAttribute("showCancelPrompt", true);

        return "order/bank-transfer";
    }

    @PostMapping("/order/bank-transfer/cancel/switch-cod")
    public String switchToCodAfterCancel(@RequestParam("orderId") Long orderId){
        Account user = authService.getUser();
        if (user == null){
            return "redirect:/auth/login";
        }
        Optional<Orders> orderOpt = orderService.findById(orderId);
        if (orderOpt.isEmpty() || orderOpt.get().getAccount() == null
                || !user.getUsername().equals(orderOpt.get().getAccount().getUsername())) {
            return "redirect:/order/list";
        }
        try {
            payosPaymentService.cancelPaymentLink(orderId, "Switch to COD");
        } catch (PayOSException ignored) {
        }
        updateOrderStatusIfChanged(orderOpt.get(), "PLACED_UNPAID");
        return "redirect:/order/detail/" + orderId;
    }

    @PostMapping("/order/bank-transfer/cancel/delete")
    @Transactional
    public String cancelAndDeleteOrder(@RequestParam("orderId") Long orderId){
        Account user = authService.getUser();
        if (user == null){
            return "redirect:/auth/login";
        }
        Optional<Orders> orderOpt = orderService.findById(orderId);
        if (orderOpt.isEmpty() || orderOpt.get().getAccount() == null
                || !user.getUsername().equals(orderOpt.get().getAccount().getUsername())) {
            return "redirect:/order/list";
        }
        try {
            payosPaymentService.cancelPaymentLink(orderId, "Cancel order");
        } catch (PayOSException ignored) {
        }
        notificationService.deleteByOrderId(orderId);
        orderDetailService.deleteByOrderId(orderId);
        orderService.deleteById(orderId);
        return "redirect:/order/list";
    }

    @GetMapping("/order/payos/return")
    public String payosReturn(@RequestParam("orderId") Long orderId){
        Optional<Orders> orderOpt = orderService.findById(orderId);
        if (orderOpt.isEmpty()){
            return "redirect:/order/list";
        }
        Orders order = orderOpt.get();
        try {
            PaymentLink paymentLink = payosPaymentService.getPaymentLink(orderId);
            if (paymentLink != null && paymentLink.getStatus() != null){
                PaymentLinkStatus status = paymentLink.getStatus();
                if (status == PaymentLinkStatus.PAID){
                    updateOrderStatusIfChanged(order, "PLACED_PAID");
                    return "redirect:/order/detail/" + orderId;
                }
            }
        }catch (PayOSException ignored){
        }
        String redirectUrl =UriComponentsBuilder.fromPath("/order/bank-transfer/view/" + orderId)
                .queryParam("message", "Thanh toán chưa hoàn tất")
                .build()
                .toUriString();
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/order/payos/cancel")
    public String payosCancel(@RequestParam("orderId") Long orderId){
        String redirectUrl = UriComponentsBuilder.fromPath("/order/bank-transfer/cancel/view")
                .queryParam("orderId", orderId)
                .build()
                .toUriString();
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/order/payos/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> payosStatus(@RequestParam("orderId") Long orderId){
    Account user = authService.getUser();
    if (user == null){
        return ResponseEntity.status(401).body(Map.of("message","Vui lòng đăng nhập tài khoản!"));
    }
    Optional<Orders> orderOpt = orderService.findById(orderId);
    if (orderOpt.isEmpty() || orderOpt.get().getAccount() == null
    || !user.getUsername().equals(orderOpt.get().getAccount().getUsername())) {
        return ResponseEntity.status(403).body(Map.of("message", "Bạn không có quyền truy cập trang này!"));
    }
    Map<String, Object> response = new HashMap<>();
    try {
        PaymentLink paymentLink = payosPaymentService.getPaymentLink(orderId);
        if (paymentLink != null && paymentLink.getStatus() != null){
            PaymentLinkStatus status = paymentLink.getStatus();
            response.put("status", status.getValue());
            if (status == PaymentLinkStatus.PAID){
                updateOrderStatusIfChanged(orderOpt.get(), "PLACED_PAID");
                response.put("redirectUrl", "/order/detail/" + orderId);
            }
        }
    }catch (PayOSException ex){
        response.put("message", "Đã xảy ra lỗi. Thanh toán không thành công!");
    }
    return ResponseEntity.ok(response);
    }

    @PostMapping("/payos/webhook")
    @ResponseBody
    public ResponseEntity<String> payosWebhook(@RequestBody Webhook webhook){
        try {
            WebhookData data = payosPaymentService.verifyWebhook(webhook);
            if (data == null || data.getOrderCode() == null) {
                return ResponseEntity.badRequest().body("Webhook không hợp lệ!");
            }
            PaymentLink paymentLink = payosPaymentService.getPaymentLink(data.getOrderCode());
            if (paymentLink != null && paymentLink.getStatus() != null) {
                applyPaymentStatus(data.getOrderCode(), paymentLink.getStatus());
            }
            return ResponseEntity.ok("OK");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Webhook không hợp lệ!");
        }
    }

    @GetMapping("/order/list")
    public String list(Model model){
        Account user = authService.getUser();
        if (user == null){
            return "redirect:/auth/login";
        }
        model.addAttribute("orders", orderService.findByAccountUsername(user.getUsername()));
        return "order/order-list";
    }

    @GetMapping("/order/detail/{id}")
    public String detail(@PathVariable Long id, Model model){
        Account user = authService.getUser();
        if (user == null){
            return "redirect:/auth/login";
        }
        Optional<Orders> orderOpt = orderService.findById(id);
        if (orderOpt.isEmpty() || orderOpt.get().getAccount() == null
        || !user.getUsername().equals(orderOpt.get().getAccount().getUsername())) {
            return "redirect:/order/list";
        }
        Orders order = orderOpt.get();
        model.addAttribute("order", order);
        model.addAttribute("details", orderDetailService.findByOrderId(id));
        model.addAttribute("reviewable", isDeliveredStatus(order.getStatus()));
        model.addAttribute("reviewedProductIds", productReviewService.findReviewedProductId(user.getUsername(), id));
        return "order/order-detail";
    }
    @GetMapping("/order/my-product-list")
    public String myProductList(Model model){
        Account user = authService.getUser();
        if (user == null){
            return "redirect:/auth/login";
        }
        model.addAttribute("details", orderDetailService.findByOrderAccountUsername(user.getUsername()));
        return "order/my-product-list";
    }



    private boolean isDeliveredStatus (String status){
        if (status == null){
            return false;
        }
        return "DELIVERED_SUCCESS".equals(status) || "DONE".equals(status);
    }
    private long toPayosAmount(BigDecimal total){
        if (total == null){
            return 0;
        }
        return total.setScale(0, RoundingMode.HALF_UP).longValue();
    }
    private void applyPaymentStatus(Long orderId, PaymentLinkStatus status){
        Optional<Orders> orderOpt = orderService.findById(orderId);
        if (orderOpt.isEmpty() ){
            return;
        }
        Orders order = orderOpt.get();
        if (status == PaymentLinkStatus.PAID){
            updateOrderStatusIfChanged(order, "PLACED_PAID");
        }
    }
    private void updateOrderStatusIfChanged(Orders order, String status){
        if (order == null || status == null){
            return;
        }
        order.setStatus(status);
        orderService.update(order);
        notificationService.notifyOrderStatusChange(order,status);
    }
    private String buildQrImageSrc (String qrCode){
        if (qrCode == null || qrCode.isBlank()){
            return null;
        }
        if (qrCode.startsWith("data:")){
            return qrCode;
        }
        if (qrCode.startsWith("iVBOR") || qrCode.startsWith("/9j/")) {
            return "data:image/png;base64," + qrCode;
        }
        String encoded = URLEncoder.encode(qrCode, StandardCharsets.UTF_8);
        return "https://api.qrserver.com/v1/create-qr-code/?size=260x260&data=" + encoded;
    }

    private BigDecimal calculateOrderTotal(List<Order_details> details) {

        BigDecimal total = BigDecimal.ZERO;

        if (details == null) {
            return total;
        }

        for (Order_details detail : details) {

            if (detail.getPrice() == null || detail.getQuantity() == null) {
                continue;
            }

            total = total.add(
                    detail.getPrice().multiply(
                            BigDecimal.valueOf(detail.getQuantity())
                    )
            );
        }

        return total;
    }
    private void updateOrderCoordinates (Long orderId, Double lat, Double lng) {
        if (orderId == null || lat == null || lng == null){
            return;
        }
        Optional<ColumnPair> columns = findLatLngColumns(orderId);
        if (columns.isEmpty()){
            return;
        }
        ColumnPair pair = columns.get();
        try {
            jdbcTemplate.update("update orders set " + pair.lat + " = ?, " + pair.lng + " = ? where id = ?",
                    lat, lng, orderId);
        }catch (Exception ignored){

        }
    }
    private Optional<ColumnPair> findLatLngColumns(Long orderId) {
        try {
            return jdbcTemplate.query("select * from orders where id = ?", rs -> {
                if (!rs.next()) {
                    return Optional.empty();
                }
                ResultSetMetaData meta = rs.getMetaData();
                String latCol = null;
                String lngCol = null;
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String label = meta.getColumnLabel(i);
                    String columnName = meta.getColumnName(i);
                    String key = (label == null || label.isBlank() ? columnName : label).toLowerCase(Locale.ROOT);
                    if (latCol == null && (key.equals("lat") || key.equals("latitude") || key.equals("order_lat") || key.equals("order_latitude") || key.equals("shipping_lat") || key.equals("delivery_lat") || key.equals("ship_lat"))) {
                        latCol = columnName;
                    }
                    if (lngCol == null && (key.equals("lng") || key.equals("longitude") || key.equals("order_lng") || key.equals("order_longitude") || key.equals("shipping_lng") || key.equals("delivery_lng") || key.equals("ship_lng"))) {
                        lngCol = columnName;
                    }
                }
                if (latCol == null || lngCol == null) {
                    return Optional.empty();
                }
                return Optional.of(new ColumnPair(latCol, lngCol));
            }, orderId);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
    private static class ColumnPair{
        private final String lat;
        private final String lng;
        public ColumnPair(String lat, String lng) {
            this.lat = lat;
            this.lng = lng;
        }

    }

}
