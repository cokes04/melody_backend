package com.melody.melody.adapter.web.security;

import com.melody.melody.domain.model.Identity;

public interface TokenIssuancer {
    String issuanceAccessToken(Identity userId);
    String issuanceRefreshToken(Identity userId);
}
