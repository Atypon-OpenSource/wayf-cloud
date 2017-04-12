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

import com.atypon.wayf.data.cache.KeyValueCache;
import com.google.inject.Singleton;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Singleton
public class IdentityProviderDaoRedisImpl implements KeyValueCache<String, String> {
    private static final Logger LOG = LoggerFactory.getLogger(IdentityProviderDaoRedisImpl.class);

    private static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

    public IdentityProviderDaoRedisImpl() {
    }

    @Override
    public Completable put(String publisherId, String wayfId) {
        LOG.debug("Adding mapping[{}, {}]", publisherId, wayfId);
        return Completable.fromAction(() -> {
                Jedis jedis = null;

                try {
                    LOG.debug("Adding mapping to redis [{}, {}]", publisherId, wayfId);
                    jedis = pool.getResource();
                    jedis.set(publisherId, wayfId);
                } finally {
                    jedis.close();
                }
            }
        ).observeOn(Schedulers.io());
    }

    @Override
    public Maybe<String> get(String publisherId) {

        return Maybe.just((publisherId))
                .observeOn(Schedulers.io())
                .flatMap(s_publisherId -> {

                    Jedis jedis = null;

                    try {
                        jedis = pool.getResource();

                        String internalId = jedis.get(publisherId);
                        LOG.debug("Found internal ID [{}] in cache", internalId);
                        return internalId != null? Maybe.just(internalId) : Maybe.empty();
                    } finally {
                        jedis.close();
                    }
                });
    }
}
