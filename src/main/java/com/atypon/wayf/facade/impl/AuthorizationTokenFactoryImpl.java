/*
 * Copyright 2017 Atypon Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atypon.wayf.facade.impl;

import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.data.authentication.Authenticatable;
import com.atypon.wayf.data.authentication.AuthorizationToken;
import com.atypon.wayf.data.authentication.AuthorizationTokenType;
import com.atypon.wayf.facade.AuthorizationTokenFactory;
import org.apache.http.HttpStatus;

import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthorizationTokenFactoryImpl implements AuthorizationTokenFactory {

    private static final String TOKEN_REGEX = "(Token|Bearer) (.*)";
    private static final Pattern TOKEN_MATCHER = Pattern.compile(TOKEN_REGEX, Pattern.DOTALL);

    @Override
    public AuthorizationToken generateToken(Authenticatable authenticatable) {
        return generateExpiringToken(authenticatable, null);
    }

    @Override
    public AuthorizationToken generateExpiringToken(Authenticatable authenticatable, Long ttlMillis) {
        AuthorizationToken token = new AuthorizationToken();
        token.setType(AuthorizationTokenType.API_TOKEN);
        token.setValue(UUID.randomUUID().toString());

        if (ttlMillis != null) {
            token.setValidUntil(new Date(System.currentTimeMillis() + ttlMillis));
        }

        token.setAuthenticatable(authenticatable);

        return token;
    }

    @Override
    public AuthorizationToken fromAuthorizationHeader(String authenticationValue) {
        Matcher matcher = TOKEN_MATCHER.matcher(authenticationValue);

        if (matcher.find()) {
            String prefix = matcher.group(1);
            String value = matcher.group(2);

            AuthorizationToken token = new AuthorizationToken();
            token.setType(AuthorizationTokenType.fromPrefix(prefix));
            token.setValue(value);

            return token;
        }

        throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "Could not parse Authentication header");
    }
}
