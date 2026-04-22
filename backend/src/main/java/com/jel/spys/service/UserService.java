package com.jel.spys.service;

import com.jel.spys.entity.Role;
import com.jel.spys.entity.UserEntity;
import com.jel.spys.model.AdminCreateUserRequest;
import com.jel.spys.model.AdminUpdateUserRequest;
import com.jel.spys.model.CompactUserDTO;
import com.jel.spys.model.UpdateUserRequest;
import com.jel.spys.model.UpdateUserSettingsRequest;
import com.jel.spys.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Transactional(readOnly = true)
    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String username = authentication.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    @Transactional(readOnly = true)
    public Long getCurrentUserId() {
        UserEntity currentUser = getCurrentUser();
        if (currentUser != null) {
            return currentUser.getId();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("User deleted with id: {}", id);
    }

    /**
     * Create a new user (admin function)
     */
    public UserEntity createUser(@Valid AdminCreateUserRequest createRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(createRequest.getEmail())) {
            throw new IllegalArgumentException("Email '" + createRequest.getEmail() + "' is already in use");
        }

        // Parse roles from string set to Role enum set
        Set<Role> roles = new HashSet<>();
        if (createRequest.getRoles() != null && !createRequest.getRoles().isEmpty()) {
            for (String roleStr : createRequest.getRoles()) {
                try {
                    // Remove "ROLE_" prefix if present
                    String cleanRoleStr = roleStr.replace("ROLE_", "");
                    roles.add(Role.valueOf(cleanRoleStr));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid role provided: {}", roleStr);
                }
            }
        }
        
        // If no roles provided, add default USER role
        if (roles.isEmpty()) {
            roles.add(Role.USER);
        }

        // Create user entity
        UserEntity user = UserEntity.builder()
                .firstName(createRequest.getFirstName())
                .lastName(createRequest.getLastName())
                .email(createRequest.getEmail())
                .password(passwordEncoder.encode(createRequest.getPassword()))
                .roles(roles)
                .enabled(createRequest.getEnabled() != null ? createRequest.getEnabled() : true)
                .accountNonExpired(createRequest.getAccountNonExpired() != null ? createRequest.getAccountNonExpired() : true)
                .accountNonLocked(createRequest.getAccountNonLocked() != null ? createRequest.getAccountNonLocked() : true)
                .credentialsNonExpired(createRequest.getCredentialsNonExpired() != null ? createRequest.getCredentialsNonExpired() : true)
                .build();

        UserEntity savedUser = userRepository.save(user);
        log.info("New user created: {} with roles: {}", savedUser.getEmail(), roles);
        return savedUser;
    }

    public UserEntity updateUser(Long id, @Valid AdminUpdateUserRequest updateRequest) {
        UserEntity user = getUserById(id);
        boolean wasEnabled = user.isEnabled();

        // Update user fields from updateRequest
        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }

        // Update password if provided
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            log.info("Password updated for user: {}", user.getEmail());
        }

        // Update roles if provided
        if (updateRequest.getRoles() != null) {
            Set<Role> roles = new HashSet<>();
            for (String roleStr : updateRequest.getRoles()) {
                try {
                    // Remove "ROLE_" prefix if present
                    String cleanRoleStr = roleStr.replace("ROLE_", "");
                    roles.add(Role.valueOf(cleanRoleStr));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid role provided: {}", roleStr);
                }
            }
            user.setRoles(roles);
            log.info("Roles updated for user {}: {}", user.getEmail(), roles);
        }

        // Update account status fields
        if (updateRequest.getEnabled() != null) {
            user.setEnabled(updateRequest.getEnabled());
            
            // If user is being disabled, revoke all their refresh tokens
            if (wasEnabled && !updateRequest.getEnabled()) {
                refreshTokenService.revokeAllUserTokens(user);
                log.info("User {} disabled and all tokens revoked", user.getEmail());
            }
        }
        
        if (updateRequest.getAccountNonExpired() != null) {
            user.setAccountNonExpired(updateRequest.getAccountNonExpired());
        }
        
        if (updateRequest.getAccountNonLocked() != null) {
            user.setAccountNonLocked(updateRequest.getAccountNonLocked());
        }
        
        if (updateRequest.getCredentialsNonExpired() != null) {
            user.setCredentialsNonExpired(updateRequest.getCredentialsNonExpired());
        }

        UserEntity savedUser = userRepository.save(user);
        log.info("User updated: {}", savedUser.getEmail());
        return savedUser;
    }

    /**
     * Update current user's basic information
     */
    public UserEntity updateCurrentUser(@Valid UpdateUserRequest updateRequest) {
        UserEntity currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        if (updateRequest.getFirstName() != null) {
            currentUser.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            currentUser.setLastName(updateRequest.getLastName());
        }

        UserEntity savedUser = userRepository.save(currentUser);
        log.info("User updated their profile: {}", savedUser.getEmail());
        return savedUser;
    }

    /**
     * Update current user's settings/preferences
     */
    public UserEntity updateUserSettings(@Valid UpdateUserSettingsRequest settingsRequest) {
        UserEntity currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        // Update user settings - this would depend on what settings are available
        // For now, just save the user (settings might be in a separate entity)
        UserEntity savedUser = userRepository.save(currentUser);
        log.info("User updated their settings: {}", savedUser.getEmail());
        return savedUser;
    }

    /**
     * Change user password
     */
    public void changePassword(String currentPassword, String newPassword) {
        UserEntity currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update password
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);

        log.info("Password changed for user: {}", currentUser.getEmail());
    }

    /**
     * Get users with pagination and search
     */
    @Transactional(readOnly = true)
    public List<CompactUserDTO> searchUsers(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName", "firstName"));
        Page<UserEntity> userPage;

        if (search == null || search.trim().isEmpty()) {
            userPage = userRepository.findAll(pageable);
        } else {
            userPage = userRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            search, search, search, pageable);
        }

        return userPage.getContent().stream()
                .map(this::convertToCompactUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get users by role
     */
    @Transactional(readOnly = true)
    public List<CompactUserDTO> getUsersByRole(Role role) {
        List<UserEntity> users = getUserEntitiesByRole(role);
        return users.stream()
                .map(this::convertToCompactUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * Check if email exists
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    /**
     * Enable/disable user account (admin function)
     */
    public void setUserEnabled(Long userId, boolean enabled) {
        UserEntity user = getUserById(userId);
        user.setEnabled(enabled);
        userRepository.save(user);

        log.info("User {} {}: {}", enabled ? "enabled" : "disabled", user.getEmail(), userId);
    }

    /**
     * Add role to user (admin function)
     */
    public void addRoleToUser(Long userId, Role role) {
        UserEntity user = getUserById(userId);
        if (user.getRoles() != null) {
            user.getRoles().add(role);
            userRepository.save(user);
            log.info("Role {} added to user: {}", role, user.getEmail());
        }
    }

    /**
     * Remove role from user (admin function)
     */
    public void removeRoleFromUser(Long userId, Role role) {
        UserEntity user = getUserById(userId);
        if (user.getRoles() != null) {
            user.getRoles().remove(role);
            userRepository.save(user);
            log.info("Role {} removed from user: {}", role, user.getEmail());
        }
    }

    /**
     * Get user count
     */
    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.count();
    }

    /**
     * Get active user count
     */
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.findAll().stream()
                .filter(UserEntity::isEnabled)
                .count();
    }

    /**
     * Convert UserEntity to CompactUserDTO
     */
    private CompactUserDTO convertToCompactUserDTO(UserEntity user) {
        return CompactUserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isAdmin(user.getRoles() != null && user.getRoles().contains(Role.ADMIN))
                .build();
    }

    public List<UserEntity> getUserEntitiesByRole(Role role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles() != null && user.getRoles().contains(role))
                .toList();
    }
}
