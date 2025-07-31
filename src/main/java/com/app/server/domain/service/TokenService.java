package com.app.server.domain.service;

import com.app.server.domain.User;
import org.springframework.security.core.Authentication;

public interface TokenService {
    String generateAccessToken(Authentication authentication);
    String createAndSaveRefreshToken(User user);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
}
