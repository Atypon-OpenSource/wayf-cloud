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
import com.atypon.wayf.data.*;
import com.atypon.wayf.facade.AuthenticationFacade;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class AuthenticationFacadeImpl implements AuthenticationFacade {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFacadeImpl.class);

    @Inject
    @Named("authenticatableCache")
    protected LoadingCache<AuthenticationCredentials, Authenticatable> cache;

    @Inject
    protected AuthenticationDao<AuthorizationToken> authorizationTokenDao;

    @Inject
    protected AuthenticationDao<EmailPasswordCredentials> emailPasswordCredentialsDao;

    @Override
    public Authenticatable authenticate(AuthenticationCredentials credentials) {
        LOG.debug("Authenticating credentials");

        try {
            Authenticatable authenticatable = cache.get(credentials).blockingGet();

            if (authenticatable == null) {
                throw new ServiceException(HttpStatus.SC_UNAUTHORIZED, "Could not authenticate credentials");
            }

            if (!isStillValid(authenticatable)) {
                throw new ServiceException(HttpStatus.SC_UNAUTHORIZED, "Expired credentials");
            }

            return authenticatable;
        } catch (Exception e) {
            throw new ServiceException(HttpStatus.SC_UNAUTHORIZED, "Could not authenticate credentials", e);
        }
    }

    @Override
    public boolean isStillValid(Authenticatable authenticatable) {
        if (authenticatable.getCredentials() == null) {
            return false;
        }

        if (AuthorizationToken.class.isAssignableFrom(authenticatable.getCredentials().getClass())) {
            AuthorizationToken credentials = (AuthorizationToken) authenticatable.getCredentials();
            if (credentials.getValidUntil() == null) {
                return true;
            }

            return credentials.getValidUntil().compareTo(new Date()) <= 0;
        }

        return true;
    }

    @Override
    public AuthenticationDao determineDao(AuthenticationCredentials credentials) {
        if (AuthorizationToken.class.isAssignableFrom(credentials.getClass())) {
            return authorizationTokenDao;
        } else if (EmailPasswordCredentials.class.isAssignableFrom(credentials.getClass())) {
            return emailPasswordCredentialsDao;
        }

        throw new ServiceException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Invalid credentials type");
    }
}
