package com.example.demo.Controller.admin;

import com.example.demo.Service.order.ReportService;
import com.example.demo.Service.order.dto.RevenueOrderRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ReportAController {
    private final ReportService reportService;

    @GetMapping("/admin/report/revenue")
    public String revenue( @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
                           @RequestParam(value = "toDate", required = false) LocalDate toDate,
                           @RequestParam(value = "sortField", required = false) String sortField,
                           @RequestParam(value = "sortDir", required = false) String sortDir,
            Model model) {
        String fileValue = sortField != null ? sortField : "orderId";
        String dirValue = sortDir != null ? sortDir : "asc";
        List<RevenueOrderRow> rows = reportService.revenueByDeliveredOrders(fromDate, toDate, fileValue,dirValue);
        BigDecimal total = BigDecimal.ZERO;
        for (RevenueOrderRow row : rows) {
            if (row.getLineTotal() != null) {
                total = total.add(row.getLineTotal());
            }
        }
        model.addAttribute("rows", rows);
        model.addAttribute("grandTotal", total);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("sortField", fileValue);
        model.addAttribute("sortDir", dirValue);
        return "admin/revenue";
    }
    @GetMapping("/admin/report/revenue/data")
    @ResponseBody
    public Map<String, Object> revenueData(@RequestParam(value = "fromDate", required = false) LocalDate fromDate,
                                           @RequestParam(value = "toDate", required = false) LocalDate toDate,
                                           @RequestParam(value = "sortField", required = false) String sortField,
                                           @RequestParam(value = "sortDir", required = false) String sortDir){

        String fileValue = sortField != null ? sortField : "orderId";
        String dirValue = sortDir != null ? sortDir : "asc";
        List<RevenueOrderRow> rows = reportService.revenueByDeliveredOrders(fromDate, toDate, fileValue,dirValue);
        BigDecimal total = BigDecimal.ZERO;
        for (RevenueOrderRow row : rows) {
            if (row.getLineTotal() != null) {
                total = total.add(row.getLineTotal());
            }
        }
        return java.util.Map.of(
                "rows", rows,
                "grandTotal", total,
                "sortField", fileValue,
                "sortDir", dirValue
        );
    }
    @GetMapping("/admin/report/vip")
    public String vip(Model model) {
        model.addAttribute("items", reportService.top10VipCustomers());
        return "admin/vip";
    }

}
