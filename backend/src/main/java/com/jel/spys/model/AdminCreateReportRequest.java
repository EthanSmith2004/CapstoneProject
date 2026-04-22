package com.jel.spys.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateReportRequest {
    @NotNull(message = "Report type is required")
    private String reportType; 
    
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Map<String, String> parameters; 
}
