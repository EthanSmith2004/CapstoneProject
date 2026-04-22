package com.jel.spys.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jel.spys.entity.ReportStatus;
import com.jel.spys.entity.ReportType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportStatusDTO {
    private Long id;
    private ReportType reportType;
    private ReportStatus status;
    private CompactUserDTO requestedBy;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Map<String, Object> parameters;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String mimeType;
    private String errorMessage;
    private Instant requestedAt;
    private Instant completedAt;
}
