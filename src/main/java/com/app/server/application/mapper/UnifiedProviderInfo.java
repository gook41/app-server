package com.app.server.application.mapper;


public record UnifiedProviderInfo(
        String providerId,
        String provider,
        String nickname,
        String email
) implements OAuth2ProviderInfo { }
