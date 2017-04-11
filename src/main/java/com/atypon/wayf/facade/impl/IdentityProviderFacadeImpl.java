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

import com.atypon.wayf.dao.IdentityProviderDao;
import com.atypon.wayf.data.IdentityProvider;
import com.atypon.wayf.facade.IdentityProviderFacade;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Single;

import java.util.Date;
import java.util.UUID;

@Singleton
public class IdentityProviderFacadeImpl implements IdentityProviderFacade {

    @Inject
    private IdentityProviderDao identityProviderDao;

    public IdentityProviderFacadeImpl() {
    }

    @Override
    public Single<IdentityProvider> create(IdentityProvider identityProvider) {
        identityProvider.setId(UUID.randomUUID().toString());
        identityProvider.setCreatedDate(new Date());
        identityProvider.setModifiedDate(new Date());

        return Single.just(identityProvider)
                .flatMap(o_identityProvider -> identityProviderDao.create(o_identityProvider));
    }
}
