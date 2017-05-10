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
import com.atypon.wayf.data.cache.KeyValueCache;
import com.atypon.wayf.data.identity.IdentityProvider;
import com.atypon.wayf.data.identity.IdentityProviderQuery;
import com.atypon.wayf.data.cache.CascadingCache;
import com.atypon.wayf.data.identity.IdentityProviderType;
import com.atypon.wayf.facade.IdentityProviderFacade;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
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
    private CascadingCache<String, String> cache;

    public IdentityProviderFacadeImpl() {
    }

    @Override
    public Single<IdentityProvider> create(IdentityProvider identityProvider) {
        IdentityProviderDao dao = daosByType.get(identityProvider.getType());

        identityProvider.setId(UUID.randomUUID().toString());

        return Single.just(identityProvider)
                .flatMap(o_identityProvider -> dao.create(o_identityProvider));
    }

    @Override
    public Single<IdentityProvider> read(String id) {
        Collection<IdentityProviderDao> daos = daosByType.values();

        return Observable.fromIterable(daos)
                .flatMapMaybe((dao) -> dao.read(id))
                .firstOrError();
    }

    @Override
    public Single<IdentityProvider> resolve(IdentityProvider identityProvider) {
        LOG.debug("Resolving identityProvider with id [{}] entityId [{}]", identityProvider.getId(), identityProvider.getEntityId());

        return Maybe.concat(
                        identityProvider.getId() != null? Maybe.just(identityProvider) : Maybe.empty(),

                        Maybe.just(identityProvider)
                                .map((_identityProvider) -> _identityProvider.getEntityId())
                                .flatMap((entityId) -> cache.get(identityProvider.getEntityId()))
                                .map((id) -> {
                                            identityProvider.setId(id);
                                            return identityProvider;
                                }),

                        Maybe.just(identityProvider)
                                .map((_identityProvider) -> create(_identityProvider))
                                .cast(IdentityProvider.class)
                )
                .firstOrError();
    }

    @Override
    public Observable<IdentityProvider> filter(IdentityProviderQuery query) {
        Collection<IdentityProviderDao> daos = daosByType.values();

        return Observable.fromIterable(daos)
                .flatMap((dao) -> dao.filter(query));
    }

    @Override
    public Maybe<String> get(String key) {
        return filter(new IdentityProviderQuery().setEntityId(key))
                .map(identityProvider -> identityProvider.getId())
                .firstElement();
    }

    @Override
    public Completable put(String key, String value) {
        return Completable.complete();
    }
}
