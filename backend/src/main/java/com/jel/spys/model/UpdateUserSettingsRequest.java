package com.jel.spys.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserSettingsRequest {
    private Boolean pushEnabled;
    private Boolean emailEnabled;
    private Boolean orderUpdates;
    private Boolean menuUpdates;
    private Boolean accountUpdates;
    private Boolean promotional;
    private Boolean systemAnnouncements;
    
    private String quietHoursStart;
    private String quietHoursEnd;
}
