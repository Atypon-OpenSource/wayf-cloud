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

import com.atypon.wayf.dao.PublisherSessionIdDao;
import com.google.inject.Singleton;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Singleton
public class PublisherSessionIdDaoRedisImpl implements PublisherSessionIdDao {
    private static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

    public PublisherSessionIdDaoRedisImpl() {
    }

    @Override
    public Completable addWayfIdMapping(String publisherId, String wayfId) {
        return Completable.fromAction(() -> {
                Jedis jedis = null;

                try {
                    jedis = pool.getResource();
                    jedis.set(publisherId, wayfId);
                } finally {
                    jedis.close();
                }
            }
        ).observeOn(Schedulers.io());
    }

    @Override
    public Single<String> getWayfId(String publisherId) {
        return Single.just((publisherId))
                .observeOn(Schedulers.io())
                .map(s_publisherId -> {

                    Jedis jedis = null;

                    try {
                        jedis = pool.getResource();
                        return jedis.get(publisherId);
                    } finally {
                        jedis.close();
                    }
                });
    }
}
