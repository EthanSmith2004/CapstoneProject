package com.jel.spys.controllers;


import com.jel.spys.model.AdminFeedbackDTO;
import com.jel.spys.model.FeedbackDTO;
import com.jel.spys.model.FeedbackItemStatistics;
import com.jel.spys.model.FeedbackPeriodStatistics;
import com.jel.spys.model.FeedbackSentimentRequest;
import com.jel.spys.model.FinancialOverview;
import com.jel.spys.service.FeedbackService;
import com.jel.spys.service.FinanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Slf4j
@Tag(name = "Admin Feedback", description = "Admin feedback management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminFeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/feedback")
    @PreAuthorize("hasRole('ADMIN') && hasRole('FEEDBACK_ADMIN')")
    @Operation(summary = "Get and overview", description = "Retrieve feedback data")
    @ApiResponse(responseCode = "200", description = "Feedback retrieved successfully")
    public ResponseEntity<List<AdminFeedbackDTO>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

    @PostMapping("/feedback/sentiment")
    @PreAuthorize("hasRole('ADMIN') && hasRole('FEEDBACK_ADMIN')")
    @Operation(summary = "Analyze feedback sentiment", description = "Perform sentiment analysis on feedback data")
    @ApiResponse(responseCode = "200", description = "Sentiment analysis completed successfully")
    public ResponseEntity<Void> setFeedbackSentiment(@RequestBody List<FeedbackSentimentRequest> feedback) {
        feedbackService.setFeedbackSentiment(feedback);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/feedback/statistics")
    @PreAuthorize("hasRole('ADMIN') && hasRole('FEEDBACK_ADMIN')")
    @Operation(summary = "Get feedback statistics for a period", description = "Retrieve aggregated feedback statistics for a specified time period")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<FeedbackPeriodStatistics> getFeedbackStatistics(
            @RequestParam("start") Instant start,
            @RequestParam("end") Instant end,
            @RequestParam(value = "menuItemId", required = false) Long menuItemId) {
        return ResponseEntity.ok(feedbackService.getFeedbackStatisticsPeriod(start, end, menuItemId));
    }

    @GetMapping("/feedback/statistics/by-item")
    @PreAuthorize("hasRole('ADMIN') && hasRole('FEEDBACK_ADMIN')")
    @Operation(summary = "Get feedback statistics by menu item", description = "Retrieve feedback statistics grouped by menu item")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<List<FeedbackItemStatistics>> getFeedbackStatisticsByItem(
            @RequestParam("start") Instant start,
            @RequestParam("end") Instant end) {
        return ResponseEntity.ok(feedbackService.getFeedbackStatisticsByItem(start, end));
    }

    @GetMapping("/feedback/filtered")
    @PreAuthorize("hasRole('ADMIN') && hasRole('FEEDBACK_ADMIN')")
    @Operation(summary = "Get filtered feedback", description = "Retrieve feedback filtered by period, rating range, and menu item name")
    @ApiResponse(responseCode = "200", description = "Feedback retrieved successfully")
    public ResponseEntity<List<AdminFeedbackDTO>> getFeedbackFiltered(
            @RequestParam("start") Instant start,
            @RequestParam("end") Instant end,
            @RequestParam(value = "minRating", required = false) Integer minRating,
            @RequestParam(value = "maxRating", required = false) Integer maxRating,
            @RequestParam(value = "menuItemName", required = false) String menuItemName) {
        return ResponseEntity.ok(feedbackService.getFeedbackFiltered(start, end, minRating, maxRating, menuItemName));
    }
}
