package com.HeiseiChain.HeiseiChain.controller;

import com.HeiseiChain.HeiseiChain.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/api/blockchain")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // Display the report generation page
    @GetMapping("/report")
    public String getReportPage() {
        return "report"; // Thymeleaf template name (report.html)
    }

    // Handle report generation based on date and time range
    @PostMapping("/generateReport")
    public ResponseEntity<byte[]> generateReport(
            @RequestParam("startDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam("endDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
            Model model) {

        // Generate the CSV file content as a byte array
        ByteArrayInputStream reportStream = reportService.generateReport(startDateTime, endDateTime);

        // Set response headers for downloading the file
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=blockchain_report.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(reportStream.readAllBytes());
    }
}
