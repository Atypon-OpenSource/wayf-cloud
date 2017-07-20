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

import com.atypon.wayf.cache.CacheLoader;
import com.atypon.wayf.dao.RedisDao;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadingCacheRedisImpl<K, V> implements com.atypon.wayf.cache.LoadingCache<K, V> {
    private static final Logger LOG  = LoggerFactory.getLogger(LoadingCacheRedisImpl.class);

    private RedisDao<K, V> redisDao;
    private CacheLoader<K, V> cacheLoader;

    public LoadingCacheRedisImpl() {
    }

    public LoadingCacheRedisImpl setRedisDao(RedisDao<K, V> redisDao) {
        this.redisDao = redisDao;
        return this;
    }

    public void setCacheLoader(CacheLoader<K, V> cacheLoader) {
        this.cacheLoader = cacheLoader;
    }

    @Override
    public CacheLoader<K, V> getCacheLoader() {
        return cacheLoader;
    }

    @Override
    public Maybe<V> get(K key) {
        return redisDao.get(key)
                .switchIfEmpty(load(key));
    }

    @Override
    public Completable put(K key, V value) {
        LOG.debug("Setting key [{}], value [{}] ", key, value);

        return redisDao.set(key, value);
    }

    @Override
    public Completable invalidate(K... keys) {
        LOG.debug("Invalidating keys [{}]", keys);

        return redisDao.remove(keys);
    }

    @Override
    public Completable invalidateAll() {
        LOG.debug("Removing all keys for value ");

        return redisDao.removeAll();
    }

    @Override
    public Maybe<V> load(K key) {
        LOG.debug("Loading value for key [{}]", key);

        return cacheLoader.load(key)
                .flatMap((loadedValue) -> {
                        LOG.debug("Successfully loaded value [{}] for key [{}]", loadedValue, key);

                        return put(key, loadedValue).andThen(Maybe.just(loadedValue));
                });
    }
}
