package com.finasys.report.controller;

import com.finasys.report.dto.*;
import com.finasys.report.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyReportResponse> getMonthly(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") int month) {
        return ResponseEntity.ok(reportService.getMonthlyReport(userDetails.getUsername(), year, month));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryReportResponse>> getCategories(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") int month) {
        return ResponseEntity.ok(reportService.getCategoryReport(userDetails.getUsername(), year, month));
    }

    @GetMapping("/monthly-comparison")
    public ResponseEntity<MonthlyComparisonResponse> getMonthlyComparison(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") int month) {
        return ResponseEntity.ok(reportService.getMonthlyComparison(userDetails.getUsername(), year, month));
    }

    @GetMapping("/insights")
    public ResponseEntity<InsightResponse> getInsights(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reportService.getInsights(userDetails.getUsername()));
    }
}
