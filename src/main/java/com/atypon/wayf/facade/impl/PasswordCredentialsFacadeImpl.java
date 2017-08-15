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

import com.atypon.wayf.cache.Cache;
import com.atypon.wayf.dao.PasswordCredentialsDao;
import com.atypon.wayf.data.authentication.AuthorizationToken;
import com.atypon.wayf.data.authentication.PasswordCredentials;
import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.facade.AuthenticationFacade;
import com.atypon.wayf.facade.AuthorizationTokenFacade;
import com.atypon.wayf.facade.CryptFacade;
import com.atypon.wayf.facade.PasswordCredentialsFacade;
import com.atypon.wayf.reactivex.FacadePolicies;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Single;
import org.apache.http.HttpStatus;

@Singleton
public class PasswordCredentialsFacadeImpl implements PasswordCredentialsFacade {
    private static final Long ADMIN_TOKEN_LIFESPAN = 7200000L; // 2 hours

    @Inject
    private PasswordCredentialsDao credentialsDao;

    @Inject
    private CryptFacade cryptFacade;

    @Inject
    @Named("passwordSaltCache")
    private Cache<String, String> saltCache;

    @Inject
    private AuthenticationFacade authenticationFacade;

    @Inject
    private AuthorizationTokenFacade authorizationTokenFacade;

    @Override
    public Single<PasswordCredentials> createCredentials(PasswordCredentials credentials) {
        return credentialsDao.create(credentials).toSingleDefault(credentials);
    }

    private Single<String> getSaltForEmail(String email) {
        return FacadePolicies.singleOrException(credentialsDao.getSaltForEmail(email), HttpStatus.SC_BAD_REQUEST, "Invalid email address");
    }

    @Override
    public Single<AuthorizationToken> generateSessionToken(PasswordCredentials credentials) {
        if (credentials.getEmailAddress() == null) {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "Email Address is required to login");
        }

        return FacadePolicies.singleOrException(saltCache.get(credentials.getEmailAddress()), HttpStatus.SC_UNAUTHORIZED, "Invalid credentials")
                .map((salt) -> cryptFacade.encrypt(salt, credentials.getPassword()))
                .map((encryptedPassword) -> {
                        credentials.setPassword(encryptedPassword);
                        return authenticationFacade.authenticate(credentials);
                    }
                )
                .flatMap((authenticatedEntity) -> authorizationTokenFacade.generateExpiringToken(authenticatedEntity.getAuthenticatable(), ADMIN_TOKEN_LIFESPAN));
    }
}
