package com.mohdiop.m3fundapi.repository;

import com.mohdiop.m3fundapi.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserIdAndToken(Long userId, String token);

    Optional<RefreshToken> findByUserId(Long userId);
}
