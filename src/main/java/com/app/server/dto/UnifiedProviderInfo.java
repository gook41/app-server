package com.app.server.dto;

import com.app.server.mapper.OAuth2ProviderInfo;

public record UnifiedProviderInfo(
        String providerId,
        String provider,
        String nickname,
        String email
) implements OAuth2ProviderInfo { }
