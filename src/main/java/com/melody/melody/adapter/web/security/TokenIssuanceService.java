package com.melody.melody.adapter.web.security;

import com.melody.melody.domain.model.Identity;

public interface TokenIssuanceService {
    Token issuance(Identity userId);
    Token validateAndIssuance(Identity userId, String refreshToken);
}
