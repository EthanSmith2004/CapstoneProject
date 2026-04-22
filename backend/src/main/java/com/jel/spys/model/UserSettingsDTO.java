package com.jel.spys.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSettingsDTO {
    private Long id;
    private CompactUserDTO user;
    
    private Boolean pushEnabled;
    private Boolean emailEnabled;
    private Boolean orderUpdates;
    private Boolean menuUpdates;
    private Boolean accountUpdates;
    private Boolean promotional;
    private Boolean systemAnnouncements;
    
    private String quietHoursStart; 
    private String quietHoursEnd;   
    
    private Instant createdAt;
    private Instant updatedAt;
}
