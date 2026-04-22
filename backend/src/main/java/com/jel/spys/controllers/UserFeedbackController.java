package com.jel.spys.controllers;

import com.jel.spys.facade.UserFeedbackFacade;
import com.jel.spys.model.*;
import com.jel.spys.model.FeedbackDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Slf4j
@Tag(name = "User Feedback", description = "User feedback submission endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserFeedbackController {

    @Autowired
    private UserFeedbackFacade feedbackFacade;

    @PostMapping("/feedback")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Submit feedback", description = "Submit feedback for a menu item in an order")
    @ApiResponse(responseCode = "200", description = "Feedback submitted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid feedback data")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @ApiResponse(responseCode = "404", description = "User or order not found")
    public ResponseEntity<FeedbackDTO> placeFeedback(@Valid @RequestBody FeedbackRequest feedback) {
        FeedbackDTO response = feedbackFacade.placeFeedback(feedback);
        return ResponseEntity.ok(response);
    }
}
