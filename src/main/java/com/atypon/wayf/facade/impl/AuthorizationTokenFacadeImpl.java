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

import com.atypon.wayf.dao.AuthenticationCredentialsDao;
import com.atypon.wayf.data.authentication.AuthorizationToken;
import com.atypon.wayf.facade.AuthorizationTokenFacade;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AuthorizationTokenFacadeImpl implements AuthorizationTokenFacade {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationTokenFacadeImpl.class);

    @Inject
    protected AuthenticationCredentialsDao<AuthorizationToken> dbDao;



}
