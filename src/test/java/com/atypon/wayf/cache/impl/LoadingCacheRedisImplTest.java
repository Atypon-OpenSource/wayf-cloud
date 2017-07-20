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

package com.atypon.wayf.cache.impl;

import com.atypon.wayf.cache.LoadingCacheTest;
import com.atypon.wayf.dao.impl.RedisDaoImpl;
import com.atypon.wayf.guice.WayfGuiceModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import io.reactivex.Maybe;
import org.junit.Before;
import redis.clients.jedis.JedisPool;

import java.util.UUID;

public class LoadingCacheRedisImplTest extends LoadingCacheTest {
    @Inject
    private JedisPool jedisPool;

    @Before
    public void setup() {
        Guice.createInjector(new WayfGuiceModule()).injectMembers(this);

        RedisDaoImpl<String, String> redisDao = new RedisDaoImpl<>()
                .setTtlSeconds(100)
                .setPool(jedisPool)
                .setDeserializer((inputStr) -> inputStr)
                .setSerializer((inputStr) -> inputStr)
                .setPrefix("REDIS-CACHE-" + System.currentTimeMillis());

        super.cache = new LoadingCacheRedisImpl<String, String>().setRedisDao(redisDao);
    }
}
