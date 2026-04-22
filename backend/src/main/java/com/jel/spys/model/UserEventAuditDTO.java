package com.jel.spys.model;

import com.jel.spys.entity.UserEventEntity;
import com.jel.spys.entity.UserEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEventAuditDTO {
    private Long id;
    private Instant timestamp;
    private CompactUserDTO user;
    private UserEventType type;

    public UserEventAuditDTO(UserEventEntity userEvent) {
        this.id = userEvent.getId();
        this.timestamp = userEvent.getTime();
        this.user = new CompactUserDTO(userEvent.getUser());
        this.type = userEvent.getEventType();
    }

}
