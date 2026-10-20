package com.github.nishank.featureflags.oauth;

import com.github.nishank.featureflags.dto.OAuthUserInfo;
import com.github.nishank.featureflags.entity.AuthProvider;

public interface OAuthTokenVerifier {

    AuthProvider getProvider();

    OAuthUserInfo verify(String token) throws OAuthVerificationException;
}
