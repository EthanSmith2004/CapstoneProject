package com.jel.spys.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "report", indexes = {
    @Index(name = "idx_report_type", columnList = "type"),
    @Index(name = "idx_report_status", columnList = "status"),
    @Index(name = "idx_report_requested_by", columnList = "requested_by"),
    @Index(name = "idx_report_created_at", columnList = "created_at"),
    @Index(name = "idx_report_date_range", columnList = "date_from, date_to")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ReportType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ReportStatus status = ReportStatus.REQUESTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private UserEntity requestedBy;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize; 

    @Column(name = "mime_type")
    private String mimeType;

    @ElementCollection
    @CollectionTable(name = "report_parameter", joinColumns = @JoinColumn(name = "report_id"))
    @MapKeyColumn(name = "parameter_key")
    @Column(name = "parameter_value")
    private Map<String, String> parameters;

    @Column(name = "date_from")
    private Instant dateFrom;

    @Column(name = "date_to")
    private Instant dateTo;

    @Column(name = "scheduled")
    @Builder.Default
    private Boolean scheduled = false;

    @Column(name = "processing_started_at")
    private Instant processingStartedAt;

    @Column(name = "processing_completed_at")
    private Instant processingCompletedAt;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "last_downloaded_at")
    private Instant lastDownloadedAt;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

    @PreUpdate
    protected void onUpdate() {
        if (status == ReportStatus.PROCESSING && processingStartedAt == null) {
            processingStartedAt = Instant.now();
        }
        if (status == ReportStatus.COMPLETED && processingCompletedAt == null) {
            processingCompletedAt = Instant.now();
        }
    }

    public boolean isAvailable() {
        return status == ReportStatus.COMPLETED && fileUrl != null; 
    }

    public Long getProcessingDurationSeconds() {
        if (processingStartedAt != null && processingCompletedAt != null) {
            return processingCompletedAt.getEpochSecond() - processingStartedAt.getEpochSecond();
        }
        return null;
    }
}
