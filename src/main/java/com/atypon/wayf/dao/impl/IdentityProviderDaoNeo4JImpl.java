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

package com.atypon.wayf.dao.impl;

import com.atypon.wayf.dao.IdentityProviderDao;
import com.atypon.wayf.dao.neo4j.Neo4JExecutor;
import com.atypon.wayf.data.IdentityProvider;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class IdentityProviderDaoNeo4JImpl implements IdentityProviderDao {

    private String createCypher;

    @Inject
    public IdentityProviderDaoNeo4JImpl(@Named("identity-provider.dao.neo4j.create") String createCypher) {
        this.createCypher = createCypher;
    }

    @Override
    public Single<IdentityProvider> create(IdentityProvider identityProvider) {
        return Single.just(identityProvider)
                .observeOn(Schedulers.io())
                .map((identityProviderToWrite) -> {
                    Map<String, Object> args = new HashMap<>();
                    args.put("id", identityProvider.getId());
                    args.put("name", identityProvider.getName());
                    args.put("createdDate", identityProvider.getCreatedDate().getTime());
                    args.put("modifiedDate", identityProvider.getModifiedDate().getTime());

                    return Neo4JExecutor.executeQuery(createCypher, args, IdentityProvider.class).get(0);
                });
    }
}
