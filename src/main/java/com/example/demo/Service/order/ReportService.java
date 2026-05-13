package com.example.demo.Service.order;

import com.example.demo.Service.order.dto.RevenueOrderRow;
import com.example.demo.repository.order.RevenueReport;
import com.example.demo.repository.order.VipReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public interface ReportService {
    List<RevenueReport> revenueByCategory();

    List<RevenueOrderRow> revenueByDeliveredOrders(LocalDate fromDate, LocalDate toDate, String sortField, String sortDir);

    List<VipReport> top10VipCustomers();

}

