package com.mohdiop.m3fundapi.service;

import com.mohdiop.m3fundapi.dto.request.AuthenticationRequest;
import com.mohdiop.m3fundapi.entity.RefreshToken;
import com.mohdiop.m3fundapi.entity.User;
import com.mohdiop.m3fundapi.entity.enums.UserRole;
import com.mohdiop.m3fundapi.entity.enums.UserState;
import com.mohdiop.m3fundapi.repository.RefreshTokenRepository;
import com.mohdiop.m3fundapi.repository.UserRepository;
import com.mohdiop.m3fundapi.security.JwtService;
import io.jsonwebtoken.JwtException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

@Service
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthenticationService(
            JwtService jwtService,
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public TokenPairResponse authenticate(AuthenticationRequest authenticationRequest) {
        User userToAuthenticate = authenticationRequest.username().contains("@") ?
                userRepository.findByEmail(authenticationRequest.username())
                        .orElseThrow(
                                () -> new BadCredentialsException("Nom d'utilisateur ou mot de passe incorrect.")
                        ) :
                userRepository.findByPhone(authenticationRequest.username())
                        .orElseThrow(
                                () -> new BadCredentialsException("Nom d'utilisateur ou mot de passe incorrect.")
                        );

        switch (authenticationRequest.platform()) {
            case MOBILE_CONTRIBUTOR -> {
                if (!userToAuthenticate.getUserRoles().contains(UserRole.ROLE_CONTRIBUTOR)) {
                    throw new BadCredentialsException("Nom d'utilisateur ou mot de passe incorrect.");
                }
            }
            case WEB_ADMIN -> {
                if (!userToAuthenticate.getUserRoles().contains(UserRole.ROLE_SUPER_ADMIN)
                        && !userToAuthenticate.getUserRoles().contains(UserRole.ROLE_PAYMENTS_ADMIN)
                        && !userToAuthenticate.getUserRoles().contains(UserRole.ROLE_USERS_ADMIN)
                        && !userToAuthenticate.getUserRoles().contains(UserRole.ROLE_VALIDATIONS_ADMIN)
                        && !userToAuthenticate.getUserRoles().contains(UserRole.ROLE_SYSTEM)) {
                    throw new BadCredentialsException("Nom d'utilisateur ou mot de passe incorrect.");
                }
            }
            case WEB_PROJECT_OWNER -> {
                if (!userToAuthenticate.getUserRoles().contains(UserRole.ROLE_PROJECT_OWNER)) {
                    throw new BadCredentialsException("Nom d'utilisateur ou mot de passe incorrect.");
                }
            }
        }

        if (BCrypt.checkpw(authenticationRequest.password(), userToAuthenticate.getPassword())) {
            if (userToAuthenticate.getState() == UserState.SUSPENDED
            || userToAuthenticate.getState() == UserState.INACTIVE) {
                throw new AccessDeniedException("Votre compte est suspendu ou inactif.");
            }
            String newAccessToken = jwtService.generateAccessToken(
                    userToAuthenticate.getId(),
                    userToAuthenticate.getUserRoles()
            );
            String newRefreshToken = jwtService.generateRefreshToken(
                    userToAuthenticate.getId(),
                    userToAuthenticate.getUserRoles()
            );
            storeRefreshToken(userToAuthenticate.getId(), newRefreshToken);
            return new TokenPairResponse(newAccessToken, newRefreshToken);
        }
        throw new BadCredentialsException("Nom d'utilisateur ou mot de passe incorrect.");
    }

    @Transactional
    public TokenPairResponse refresh(String refreshToken) {
        if (!jwtService.isValidRefreshToken(refreshToken)) {
            throw new JwtException("");
        }
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new JwtException("Utilisateur introuvable."));

        String hashedToken = hashToken(refreshToken);
        refreshTokenRepository.findByUserIdAndToken(user.getId(), hashedToken)
                .orElseThrow(() -> new JwtException(""));

        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getUserRoles());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId(), user.getUserRoles());
        storeRefreshToken(user.getId(), newRefreshToken);

        return new TokenPairResponse(newAccessToken, newRefreshToken);
    }

    private void storeRefreshToken(Long userId, String refreshToken) {
        String hashedToken = hashToken(refreshToken);
        long expiryMs = jwtService.getRefreshTokenValidityMs();
        Instant expiresAt = Instant.now().plusMillis(expiryMs);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new JwtException("Utilisateur introuvable."));

        RefreshToken tokenToStore = refreshTokenRepository.findByUserId(userId)
                .orElse(new RefreshToken(null, user, Instant.now(), null, ""));

        tokenToStore.setExpiresAt(expiresAt);
        tokenToStore.setToken(hashedToken);

        refreshTokenRepository.save(tokenToStore);
    }

    private String hashToken(String token) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hash du token.", e);
        }
    }

    public Long getCurrentUserId() {
        return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }

    public record TokenPairResponse(String accessToken, String refreshToken) {
    }
}

