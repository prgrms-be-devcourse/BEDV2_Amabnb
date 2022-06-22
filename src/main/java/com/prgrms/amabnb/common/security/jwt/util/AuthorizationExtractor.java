package com.prgrms.amabnb.common.security.jwt.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizationExtractor {

    private static final String BEARER_TYPE = "Bearer";

    public static String extract(String value) {
        if (value.toLowerCase().startsWith(BEARER_TYPE.toLowerCase())) {
            String authHeaderValue = value.substring(BEARER_TYPE.length()).trim();
            int commaIndex = authHeaderValue.indexOf(',');
            if (commaIndex > 0) {
                authHeaderValue = authHeaderValue.substring(0, commaIndex);
            }
            return authHeaderValue;
        }

        return null;
    }

}
