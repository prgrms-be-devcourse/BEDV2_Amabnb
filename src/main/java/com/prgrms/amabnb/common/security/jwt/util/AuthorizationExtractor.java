package com.prgrms.amabnb.common.security.jwt.util;

import static org.springframework.http.HttpHeaders.*;

import javax.servlet.http.HttpServletRequest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizationExtractor {

    private static final String BEARER_TYPE = "Bearer";

    public static String extract(HttpServletRequest request) {
        var authHeaderValue = request.getHeader(AUTHORIZATION);
        if (authHeaderValue != null) {
            return extract(authHeaderValue);
        }

        return null;
    }

    private static String extract(String value) {
        if (value.toLowerCase().startsWith(BEARER_TYPE.toLowerCase())) {
            var authHeaderValue = value.substring(BEARER_TYPE.length()).trim();
            var commaIndex = authHeaderValue.indexOf(',');
            if (commaIndex > 0) {
                authHeaderValue = authHeaderValue.substring(0, commaIndex);
            }
            return authHeaderValue;
        }

        return null;
    }

}
