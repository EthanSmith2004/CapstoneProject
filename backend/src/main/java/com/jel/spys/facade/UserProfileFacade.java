package com.jel.spys.facade;

import com.jel.spys.entity.UserEntity;
import com.jel.spys.entity.UserEventType;
import com.jel.spys.model.UpdateUserProfileRequest;
import com.jel.spys.model.CreateUserProfileRequest;
import com.jel.spys.model.UserProfileDTO;
import com.jel.spys.service.UserEventService;
import com.jel.spys.service.UserProfileService;
import com.jel.spys.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileFacade {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserEventService userEventService;

    public UserProfileDTO getUserProfile() {
        return userProfileService.getCurrentUserProfile();
    }

    public UserProfileDTO createUserProfile(@Valid CreateUserProfileRequest request) {
        UserEntity currentUser = userService.getCurrentUser();
        UserProfileDTO profile = userProfileService.createUserProfile(currentUser, request);
        
        // Log profile created event
        userEventService.logEvent(currentUser, UserEventType.PROFILE_CREATED);
        
        return profile;
    }

    public UserProfileDTO updateUserProfile(@Valid UpdateUserProfileRequest request) {
        UserEntity currentUser = userService.getCurrentUser();
        UserProfileDTO profile = userProfileService.updateUserProfile(request);
        
        // Log profile updated event
        userEventService.logEvent(currentUser, UserEventType.PROFILE_UPDATED);
        
        return profile;
    }
}
