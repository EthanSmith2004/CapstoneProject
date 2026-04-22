package com.jel.spys.facade;

import com.jel.spys.entity.UserEntity;
import com.jel.spys.entity.UserEventType;
import com.jel.spys.model.CompactUserDTO;
import com.jel.spys.model.UpdateUserSettingsRequest;
import com.jel.spys.model.UserSettingsDTO;
import com.jel.spys.service.UserEventService;
import com.jel.spys.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSettingsFacade {

    @Autowired
    private UserService userService;

    @Autowired
    private UserEventService userEventService;

    /**
     * Get current user's settings
     * This is a facade method that automatically works with the current user
     */
    public UserSettingsDTO getUserSettings() {
        UserEntity currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        // For now, create a basic UserSettingsDTO from user information
        // This could be expanded to include additional settings from a separate
        // settings entity
        return UserSettingsDTO.builder()
                .id(currentUser.getId())
                .user(CompactUserDTO.builder()
                        .id(currentUser.getId())
                        .email(currentUser.getEmail())
                        .firstName(currentUser.getFirstName())
                        .lastName(currentUser.getLastName())
                        .isAdmin(currentUser.getRoles() != null &&
                                currentUser.getRoles().contains(com.jel.spys.entity.Role.ADMIN))
                        .build())
                // Set default notification preferences
                .pushEnabled(true)
                .emailEnabled(true)
                .orderUpdates(true)
                .menuUpdates(true)
                .accountUpdates(true)
                .promotional(false)
                .systemAnnouncements(true)
                .build();
    }

    /**
     * Update current user's settings
     * Facade method that automatically injects current user for
     * UserService.updateUserSettings(UpdateUserSettingsRequest request)
     */
    public UserSettingsDTO updateUserSettings(@Valid UpdateUserSettingsRequest request) {
        UserEntity updatedUser = userService.updateUserSettings(request);
        
        // Log settings updated event
        userEventService.logEvent(updatedUser, UserEventType.SETTINGS_UPDATED);

        return UserSettingsDTO.builder()
                .id(updatedUser.getId())
                .user(CompactUserDTO.builder()
                        .id(updatedUser.getId())
                        .email(updatedUser.getEmail())
                        .firstName(updatedUser.getFirstName())
                        .lastName(updatedUser.getLastName())
                        .isAdmin(updatedUser.getRoles() != null &&
                                updatedUser.getRoles().contains(com.jel.spys.entity.Role.ADMIN))
                        .build())
                // Set default notification preferences
                .pushEnabled(true)
                .emailEnabled(true)
                .orderUpdates(true)
                .menuUpdates(true)
                .accountUpdates(true)
                .promotional(false)
                .systemAnnouncements(true)
                .build();
    }
}
