package com.jel.spys.demo;

import com.jel.spys.entity.UserEntity;
import com.jel.spys.model.AdminCreateUserRequest;
import com.jel.spys.model.AdminLoadCreditRequest;
import com.jel.spys.model.CreateUserProfileRequest;
import com.jel.spys.model.RegisterRequest;
import com.jel.spys.repository.CampusRepository;
import com.jel.spys.repository.UserProfileRepository;
import com.jel.spys.repository.UserRepository;
import com.jel.spys.service.AuthService;
import com.jel.spys.service.FinanceService;
import com.jel.spys.service.UserProfileService;
import com.jel.spys.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class RogueAdminSimulator implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CampusRepository campusRepository;

    @Value("${spys.demoData.rogueAdmin.enabled}")
    private Boolean rogueAdminEnabled;

    private UserEntity rogueAdmin;
    private UserEntity evilUser;

    @Override
    public void run(String... args) throws Exception {
        if (rogueAdminEnabled) {
            initializeRogueScenario();
        }
    }

    private void initializeRogueScenario() {
        // Create Rouge admin if doesn't exist
        if (userRepository.findByEmail("rouge@demo.com").isEmpty()) {
            AdminCreateUserRequest rogueAdminRequest = new AdminCreateUserRequest();
            rogueAdminRequest.setEmail("rouge@demo.com");
            rogueAdminRequest.setFirstName("Rouge");
            rogueAdminRequest.setLastName("Admin");
            rogueAdminRequest.setPassword("rouge123");
            rogueAdminRequest.setRoles(Set.of("ADMIN", "FINANCIAL_ADMIN"));
            rogueAdminRequest.setEnabled(true);
            rogueAdminRequest.setAccountNonExpired(true);
            rogueAdminRequest.setAccountNonLocked(true);
            rogueAdminRequest.setCredentialsNonExpired(true);
            rogueAdmin = userService.createUser(rogueAdminRequest);
            log.info("Created rouge admin account: rouge@demo.com");
        } else {
            rogueAdmin = userRepository.findByEmail("rouge@demo.com").get();
        }

        // Create Evil user if doesn't exist
        if (userRepository.findByEmail("evil@demo.com").isEmpty()) {
            authService.registerUser(RegisterRequest.builder()
                    .email("evil@demo.com")
                    .firstName("Evil")
                    .lastName("User")
                    .password("evil123")
                    .build());

            evilUser = userRepository.findByEmail("evil@demo.com").get();

            // Create profile for evil user if campuses exist
            if (campusRepository.count() > 0 && userProfileRepository.findByUser(evilUser).isEmpty()) {
                Long firstCampusId = campusRepository.findAll().get(0).getId();
                CreateUserProfileRequest profileRequest = CreateUserProfileRequest.builder()
                        .credentialNumber("66666666")
                        .campusId(firstCampusId)
                        .allergyIds(List.of())
                        .build();
                userProfileService.createUserProfile(evilUser, profileRequest);
            }
            log.info("Created evil user account: evil@demo.com");
        } else {
            evilUser = userRepository.findByEmail("evil@demo.com").get();
        }

        log.info("Rogue admin simulator initialized. Rouge admin will load R100 to Evil user every minute.");
    }

    @Scheduled(fixedRate = 60000) // Every 60 seconds (1 minute)
    public void simulateRogueActivity() {
        if (!rogueAdminEnabled) {
            return;
        }

        rogueAdmin = userRepository.findByEmail("rouge@demo.com").orElse(null);
        evilUser = userRepository.findByEmail("evil@demo.com").orElse(null);

        if (rogueAdmin == null || evilUser == null) {
            log.warn("Rogue admin or evil user not found. Skipping rogue activity.");
            return;
        }

        if (!rogueAdmin.isEnabled())
            return;

        // Check if evil user has a profile with credential number
        var evilUserProfile = userProfileRepository.findByUser(evilUser);
        if (evilUserProfile.isEmpty()) {
            log.warn("Evil user profile not found. Skipping rogue activity.");
            return;
        }

        try {
            // Set security context to rogue admin
            List<SimpleGrantedAuthority> authorities = rogueAdmin.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                    .toList();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(rogueAdmin,
                    null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            AdminLoadCreditRequest loadRequest = AdminLoadCreditRequest.builder()
                    .credentialNumber(evilUserProfile.get().getCredentialNumber())
                    .amount(BigDecimal.valueOf(1.00))
                    .description("Load Credit ")
                    .build();

            financeService.loadCredit(loadRequest);

        } catch (Exception e) {
            log.error("Error during rogue admin simulation: {}", e.getMessage());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
