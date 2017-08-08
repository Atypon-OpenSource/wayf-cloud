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

import com.atypon.wayf.cache.LoadingCache;
import com.atypon.wayf.dao.AuthenticationDao;
import com.atypon.wayf.data.Authenticatable;
import com.atypon.wayf.data.AuthorizationToken;
import com.atypon.wayf.data.AuthorizationTokenType;
import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.facade.AuthenticationFacade;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.reactivex.Single;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticatableFacadeImpl implements AuthenticationFacade {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticatableFacadeImpl.class);

    private static final Long ADMIN_TOKEN_LIFESPAN = 7200000L; // 2 hours

    private static final String TOKEN_REGEX = "(Token|Bearer) (.*)";
    private static final Pattern TOKEN_MATCHER = Pattern.compile(TOKEN_REGEX, Pattern.DOTALL);

    @Inject
    @Named("authenticatableCache")
    protected LoadingCache<AuthorizationToken, Authenticatable> cache;

    @Inject
    protected AuthenticationDao dbDao;

    @Override
    public Single<AuthorizationToken> createToken(Authenticatable authenticatable) {
        AuthorizationToken token = new AuthorizationToken();
        token.setType(AuthorizationTokenType.API_TOKEN);
        token.setValue(UUID.randomUUID().toString());

        if (authenticatable.getType() == Authenticatable.Type.ADMIN) {
            token.setValidUntil(new Date(System.currentTimeMillis() + ADMIN_TOKEN_LIFESPAN));
        }

        authenticatable.setAuthorizationToken(token);

        return dbDao.create(authenticatable).toSingleDefault(token);
    }

    @Override
    public Authenticatable authenticate(AuthorizationToken token) {
        LOG.debug("Authenticating token");

        if (AuthorizationTokenType.API_TOKEN != token.getType()) {
            return null;
        }

        try {
            Authenticatable authenticatable = cache.get(token).blockingGet();

            if (authenticatable == null) {
                throw new ServiceException(HttpStatus.SC_UNAUTHORIZED, "Could not authenticate token");
            }

            if (!isStillValid(authenticatable)) {
                throw new ServiceException(HttpStatus.SC_UNAUTHORIZED, "Expired token");
            }

            return authenticatable;
        } catch (Exception e) {
            throw new ServiceException(HttpStatus.SC_UNAUTHORIZED, "Could not authenticate token", e);
        }
    }

    @Override
    public AuthorizationToken parseAuthenticationValue(String authenticationValue) {
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

    @Override
    public boolean isStillValid(Authenticatable authenticatable) {
        if (authenticatable.getAuthorizationToken() == null) {
            return false;
        }

        if (authenticatable.getAuthorizationToken().getValidUntil() == null) {
            return true;
        }

        return authenticatable.getAuthorizationToken().getValidUntil().compareTo(new Date()) <= 0;
    }
}
