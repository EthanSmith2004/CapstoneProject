package com.jel.spys.service;

import com.jel.spys.entity.RefreshTokenEntity;
import com.jel.spys.entity.Role;
import com.jel.spys.entity.UserEntity;
import com.jel.spys.entity.UserEventType;
import com.jel.spys.exception.AuthenticationFailedException;
import com.jel.spys.exception.InvalidTokenException;
import com.jel.spys.exception.UserAlreadyExistsException;
import com.jel.spys.exception.UserNotFoundException;
import com.jel.spys.model.AuthResponse;
import com.jel.spys.model.LoginRequest;
import com.jel.spys.model.RegisterRequest;
import com.jel.spys.repository.UserRepository;
import com.jel.spys.security.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserEventService userEventService;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            UserEntity userDetails = (UserEntity) authentication.getPrincipal();
            
            String jwt = jwtUtils.generateJwtToken(authentication);

            userEventService.logEvent(userDetails, UserEventType.LOGIN);

            return getAuthResponse(userDetails, jwt, refreshTokenService.createRefreshToken(userDetails));
        } catch (BadCredentialsException ex) {
            throw ex;
        } catch (AuthenticationException ex) {
            throw new AuthenticationFailedException("Authentication failed: " + ex.getMessage(), ex);
        }
    }

    private AuthResponse getAuthResponse(UserEntity userDetails, String jwt, RefreshTokenEntity refreshToken) {

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .expiresIn((long) jwtUtils.getJwtExpirationMs())
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName())
                .email(userDetails.getEmail())
                .roles(roles)
                .build();
    }

    public AuthResponse registerUser(RegisterRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email '" + signUpRequest.getEmail() + "' is already in use");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        UserEntity user = UserEntity.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(user);

        userEventService.logEvent(user, UserEventType.REGISTER);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(signUpRequest.getPassword());
        loginRequest.setEmail(signUpRequest.getEmail());
        return authenticateUser(loginRequest);
    }

    public AuthResponse refreshToken(String refreshTokenValue) {
        RefreshTokenEntity refreshToken = refreshTokenService.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
        
        refreshTokenService.verifyExpiration(refreshToken);
        
        UserEntity user = refreshToken.getUser();
        
        String newAccessToken = jwtUtils.generateTokenFromUsername(user.getUsername(), jwtUtils.getJwtExpirationMs());

        refreshToken = refreshTokenService.updateToken(refreshToken);
        return getAuthResponse(user, newAccessToken, refreshToken);
    }

    public void logout(String refreshTokenValue) {
        refreshTokenService.revokeToken(refreshTokenValue);
    }
}
