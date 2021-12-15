package com.reddit.backend.service;

import com.reddit.backend.exception.InvalidRefreshTokenException;
import com.reddit.backend.model.RefreshToken;
import com.reddit.backend.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {
    private RefreshTokenRepository refreshTokenRepository;

    RefreshToken generateRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreationDate(Instant.now());
        return refreshTokenRepository.save(refreshToken);
    }

    public void validateToken(String token)  {
        refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid Refresh Token"));
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
