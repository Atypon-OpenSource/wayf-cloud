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

package com.atypon.wayf.dao.redis.impl;

import com.atypon.wayf.dao.redis.RedisDao;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisDaoDefaultImpl implements RedisDao {
    private static final Logger LOG = LoggerFactory.getLogger(RedisDaoDefaultImpl.class);

    private static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

    private static final String DELIMITER = ":";

    private String prefix;

    public RedisDaoDefaultImpl(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public Maybe<String> get(String key) {
        return Maybe.just(key)
                .observeOn(Schedulers.io())
                .flatMap(s_publisherId -> {
                    try (Jedis jedis = pool.getResource()) {
                        String value = jedis.get(buildKey(key));

                        LOG.debug("Cache [{}] returned [{}] for key [{}]", prefix, value, key);

                        return value != null? Maybe.just(value) : Maybe.empty();
                    }
                });
    }

    @Override
    public Completable put(String key, String value) {
        return Single.just(prefix)
                .observeOn(Schedulers.io())
                .flatMapCompletable((ignore) ->
                        Completable.fromAction(() -> {
                            try (Jedis jedis = pool.getResource()) {
                                LOG.debug("Adding mapping of key [{}] to value [{}] for cache [{}]", key, value, prefix);

                                jedis.set(buildKey(key), value);
                            }
                        })
                );
    }

    private String buildKey(String key) {
        return new StringBuilder().append(getPrefix()).append(DELIMITER).append(key).toString();
    }
}
