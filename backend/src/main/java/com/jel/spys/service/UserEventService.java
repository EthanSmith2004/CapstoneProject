package com.jel.spys.service;

import com.jel.spys.entity.UserEntity;
import com.jel.spys.entity.UserEventEntity;
import com.jel.spys.entity.UserEventType;
import com.jel.spys.repository.UserEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserEventService {

    @Autowired
    UserEventRepository userEventRepository;

    public UserEventEntity logEvent(UserEntity userEntity, UserEventType userEventType) {
        log.info("UserEvent: Event = {} Email = {}", userEventType, userEntity.getEmail());
        UserEventEntity.UserEventEntityBuilder builder = UserEventEntity.builder();
        builder.user(userEntity);
        builder.eventType(userEventType);
        return userEventRepository.save(builder.build());
    }
}
