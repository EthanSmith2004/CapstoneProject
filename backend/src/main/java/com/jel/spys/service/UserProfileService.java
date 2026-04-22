package com.jel.spys.service;

import com.jel.spys.entity.*;
import com.jel.spys.exception.UserNotFoundException;
import com.jel.spys.exception.UserProfileAlreadyExistsException;
import com.jel.spys.exception.UserProfileNotFoundException;
import com.jel.spys.model.*;
import com.jel.spys.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.descriptor.java.BigDecimalJavaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CampusRepository campusRepository;

    @Autowired
    private ResidenceRepository residenceRepository;

    @Autowired
    private AllergyRepository allergyRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private UserService userService;

    /**
     * Get user profile for the currently authenticated user
     */
    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentUserProfile() {
        UserEntity currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UserNotFoundException("No authenticated user found");
        }

        Optional<UserProfileEntity> profileOpt = userProfileRepository.findByUser(currentUser);
        if (profileOpt.isEmpty()) {
            throw new UserProfileNotFoundException("User profile not found for user: " + currentUser.getEmail());
        }

        BigDecimal balance = financeService.getUserAccount(currentUser).getCurrentBalance();

        return convertToDTO(profileOpt.get(), balance);
    }

    /**
     * Get user profile by user ID
     */
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfileByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Optional<UserProfileEntity> profileOpt = userProfileRepository.findByUser(user);
        if (profileOpt.isEmpty()) {
            throw new UserProfileNotFoundException("User profile not found for user id: " + userId);
        }

        return convertToDTO(profileOpt.get());
    }

    /**
     * Get user profile by credential number
     */
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfileByCredentialNumber(String credentialNumber) {
        Optional<UserProfileEntity> profileOpt = userProfileRepository.findByCredentialNumber(credentialNumber);
        if (profileOpt.isEmpty()) {
            throw new UserProfileNotFoundException(
                    "User profile not found with credential number: " + credentialNumber);
        }

        return convertToDTO(profileOpt.get());
    }

    /**
     * Create a new user profile for the currently authenticated user
     */
    public UserProfileDTO createUserProfile(UserEntity currentUser, CreateUserProfileRequest request) {
        if (currentUser == null) {
            throw new UserNotFoundException("No authenticated user found");
        }

        // Check if user already has a profile
        if (userProfileRepository.findByUser(currentUser).isPresent()) {
            throw new UserProfileAlreadyExistsException(
                    "User profile already exists for user: " + currentUser.getEmail());
        }

        // Check if credential number is already taken
        if (userProfileRepository.existsByCredentialNumber(request.getCredentialNumber())) {
            throw new IllegalArgumentException("Credential number already exists: " + request.getCredentialNumber());
        }

        // Get related entities
        CampusEntity campus = null;
        if (request.getCampusId() != null) {
            campus = campusRepository.findById(request.getCampusId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Campus not found with id: " + request.getCampusId()));
        }

        ResidenceEntity residence = null;
        if (request.getResidenceId() != null) {
            residence = residenceRepository.findById(request.getResidenceId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Residence not found with id: " + request.getResidenceId()));
        }

        List<AllergyEntity> allergies = List.of();
        if (request.getAllergyIds() != null && !request.getAllergyIds().isEmpty()) {
            allergies = allergyRepository.findAllById(request.getAllergyIds());
            if (allergies.size() != request.getAllergyIds().size()) {
                throw new IllegalArgumentException("One or more allergy IDs are invalid");
            }
        }

        // Create default account for the user
        AccountEntity account = new AccountEntity();
        account = accountRepository.save(account);

        // Create user profile
        UserProfileEntity profile = UserProfileEntity.builder()
                .credentialNumber(request.getCredentialNumber())
                .user(currentUser)
                .campus(campus)
                .residence(residence)
                .allergy(allergies)
                .account(account)
                .build();

        profile = userProfileRepository.save(profile);
        log.info("Created user profile for user: {} with credential number: {}",
                currentUser.getEmail(), request.getCredentialNumber());

        return convertToDTO(profile);
    }

    /**
     * Update user profile for the currently authenticated user
     */
    public UserProfileDTO updateUserProfile(UpdateUserProfileRequest request) {
        UserEntity currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UserNotFoundException("No authenticated user found");
        }

        UserProfileEntity profile = userProfileRepository.findByUser(currentUser)
                .orElseThrow(() -> new UserProfileNotFoundException(
                        "User profile not found for user: " + currentUser.getEmail()));

        // Update campus if provided
        if (request.getCampusId() != null) {
            CampusEntity campus = campusRepository.findById(request.getCampusId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Campus not found with id: " + request.getCampusId()));
            profile.setCampus(campus);
        }

        // Update residence if provided
        if (request.getResidenceId() != null) {
            ResidenceEntity residence = residenceRepository.findById(request.getResidenceId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Residence not found with id: " + request.getResidenceId()));
            profile.setResidence(residence);
        }

        // Update allergies if provided
        if (request.getAllergyIds() != null) {
            if (request.getAllergyIds().isEmpty()) {
                profile.getAllergy().clear();
            } else {
                List<AllergyEntity> allergies = allergyRepository.findAllById(request.getAllergyIds());
                if (allergies.size() != request.getAllergyIds().size()) {
                    throw new IllegalArgumentException("One or more allergy IDs are invalid");
                }
                profile.setAllergy(allergies);
            }
        }

        profile = userProfileRepository.save(profile);
        log.info("Updated user profile for user: {}", currentUser.getEmail());

        return convertToDTO(profile);
    }

    /**
     * Delete user profile for the currently authenticated user
     */
    public void deleteCurrentUserProfile() {
        UserEntity currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UserNotFoundException("No authenticated user found");
        }

        UserProfileEntity profile = userProfileRepository.findByUser(currentUser)
                .orElseThrow(() -> new UserProfileNotFoundException(
                        "User profile not found for user: " + currentUser.getEmail()));

        userProfileRepository.delete(profile);
        log.info("Deleted user profile for user: {}", currentUser.getEmail());
    }

    /**
     * Get all user profiles (admin function)
     */
    @Transactional(readOnly = true)
    public List<UserProfileDTO> getAllUserProfiles() {
        List<UserProfileEntity> profiles = userProfileRepository.findAll();
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get user profiles by campus
     */
    @Transactional(readOnly = true)
    public List<UserProfileDTO> getUserProfilesByCampus(Long campusId) {
        CampusEntity campus = campusRepository.findById(campusId)
                .orElseThrow(() -> new IllegalArgumentException("Campus not found with id: " + campusId));

        List<UserProfileEntity> profiles = userProfileRepository.findByCampus(campus);
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get user profiles by residence
     */
    @Transactional(readOnly = true)
    public List<UserProfileDTO> getUserProfilesByResidence(Long residenceId) {
        ResidenceEntity residence = residenceRepository.findById(residenceId)
                .orElseThrow(() -> new IllegalArgumentException("Residence not found with id: " + residenceId));

        List<UserProfileEntity> profiles = userProfileRepository.findByResidence(residence);
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Check if credential number exists
     */
    @Transactional(readOnly = true)
    public boolean credentialNumberExists(String credentialNumber) {
        return userProfileRepository.existsByCredentialNumber(credentialNumber);
    }


    private UserProfileDTO convertToDTO(UserProfileEntity profile) {
        return convertToDTO(profile, null);
    }

    private SelectDTO toSelectDTO(CampusEntity campusDTO) {
        if  (campusDTO == null) {
            return null;
        }

        return new SelectDTO(campusDTO.getId(), campusDTO.getCampus());
    }

    private SelectDTO toSelectDTO(ResidenceEntity residence) {
        if  (residence == null) {
            return null;
        }

        return new SelectDTO(residence.getId(), residence.getResidence());
    }

    /**
     * Convert UserProfileEntity to UserProfileDTO
     */
    private UserProfileDTO convertToDTO(UserProfileEntity profile, BigDecimal accountBalance) {
        return UserProfileDTO.builder()
                .id(profile.getId())
                .credentialNumber(profile.getCredentialNumber())
                .user(CompactUserDTO.builder()
                        .id(profile.getUser().getId())
                        .firstName(profile.getUser().getFirstName())
                        .lastName(profile.getUser().getLastName())
                        .email(profile.getUser().getEmail())
                        .isAdmin(profile.getUser().getRoles() != null &&
                                profile.getUser().getRoles().contains(Role.ADMIN))
                        .build())
                .campus(toSelectDTO(profile.getCampus()))
                .residence(toSelectDTO(profile.getResidence()))
                .allergies(profile.getAllergy() != null ? profile.getAllergy().stream()
                        .map(v -> new SelectDTO(v.getId(), v.getAllergy()))
                        .collect(Collectors.toList()) : List.of())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .balance(accountBalance)
                .build();
    }
}
