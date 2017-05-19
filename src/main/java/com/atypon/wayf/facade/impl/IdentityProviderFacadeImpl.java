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
import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.data.cache.KeyValueCache;
import com.atypon.wayf.data.identity.*;
import com.atypon.wayf.data.cache.CascadingCache;
import com.atypon.wayf.facade.IdentityProviderFacade;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Singleton
public class IdentityProviderFacadeImpl implements IdentityProviderFacade {
    private static final Logger LOG = LoggerFactory.getLogger(IdentityProviderFacadeImpl.class);

    @Inject
    @Named("identityProviderDaoMap")
    private Map<IdentityProviderType, IdentityProviderDao> daosByType;

    @Inject
    @Named("identityProviderCache")
    private CascadingCache<String, Long> cache;

    public IdentityProviderFacadeImpl() {
    }

    @Override
    public Single<IdentityProvider> create(IdentityProvider identityProvider) {
        IdentityProviderDao dao = daosByType.get(identityProvider.getType());

        return Single.just(identityProvider)
                .flatMap(o_identityProvider -> dao.create(o_identityProvider));
    }

    @Override
    public Single<IdentityProvider> read(Long id) {
        Collection<IdentityProviderDao> daos = daosByType.values();

        return Observable.fromIterable(daos)
                .flatMapMaybe((dao) -> dao.read(id))
                .firstOrError();
    }

    @Override
    public Single<IdentityProvider> resolve(IdentityProvider identityProvider) {
        if (identityProvider.getType() == null) {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "In order to resolve an IdentityProvider, 'type' is required");
        }

        Observable<IdentityProvider> foundProviders = null;

        if (identityProvider.getType() == IdentityProviderType.OAUTH) {
            foundProviders = resolveOauth((OauthEntity) identityProvider);
        } else if (identityProvider.getType() == IdentityProviderType.OPEN_ATHENS) {
            foundProviders = resolveOpenAthens((OpenAthensEntity) identityProvider);
        } else if (identityProvider.getType() == IdentityProviderType.SAML) {
            foundProviders = resolveSamlEntity((SamlEntity) identityProvider);
        } else {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "IdentityProvider of type [" + identityProvider.getType() + "] not supported");
        }

        return foundProviders.singleOrError().doOnError((e) -> {throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "Could not determine a unique IdentityProvider from input");});
    }

    @Override
    public Observable<IdentityProvider> filter(IdentityProviderQuery query) {
        // If a type is known, only query that DAO. If no type is known, concat the results of filtering all DAOs
        if (query.getType() != null) {
            return daosByType.get(query.getType()).filter(query);
        } else {
            Collection<IdentityProviderDao> daos = daosByType.values();

            return Observable.fromIterable(daos)
                    .flatMap((dao) -> dao.filter(query));
        }
    }

    @Override
    public Maybe<String> get(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Completable put(String key, String value) {
        throw new UnsupportedOperationException();
    }

    private Observable<IdentityProvider> resolveOauth(OauthEntity oauthEntity) {
        if (oauthEntity.getProvider() == null) {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "In order to resolve an OAUTH Entity, 'provider' is required");
        }

        IdentityProviderQuery query = new IdentityProviderQuery()
                .setType(IdentityProviderType.OAUTH)
                .setProvider(oauthEntity.getProvider());

        return filter(query);
    }

    private Observable<IdentityProvider> resolveOpenAthens(OpenAthensEntity openAthensEntity) {
        if (openAthensEntity.getOrganizationId() == null) {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "In order to resolve an OpenAthens Entity, 'organizationId' is required");
        }

        IdentityProviderQuery query  = new IdentityProviderQuery()
                .setType(IdentityProviderType.OPEN_ATHENS)
                .setOrganizationId(openAthensEntity.getOrganizationId());

        return filter(query);
    }

    private Observable<IdentityProvider> resolveSamlEntity(SamlEntity samlEntity) {
        if (samlEntity.getEntityId() == null) {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "In order to resolve a SAML Entity, 'entityId' is required");
        }

        IdentityProviderQuery query = new IdentityProviderQuery()
                .setType(IdentityProviderType.SAML)
                .setEntityId(samlEntity.getEntityId());

        return filter(query);
    }
}
