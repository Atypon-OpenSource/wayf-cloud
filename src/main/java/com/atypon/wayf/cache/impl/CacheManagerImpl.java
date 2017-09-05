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

import com.atypon.wayf.cache.Cache;
import com.atypon.wayf.cache.CacheManager;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class CacheManagerImpl implements CacheManager {
    private static final Logger LOG = LoggerFactory.getLogger(CacheManager.class);

    private static Map<String, List<Cache<?, ?>>> cacheRegistry;

    public CacheManagerImpl() {
        cacheRegistry = new HashMap<>();
    }

    @Override
    public <K, V> void registerCacheGroup(String groupName, Cache<K, V>... caches) {
        LOG.debug("Registering cache group [{}] caches [{}]", groupName, caches);

        cacheRegistry.put(groupName, Lists.newArrayList(caches));
    }

    @Override
    public void evictAllForGroup(String groupName) {
        LOG.debug("Evicting cache contents for group [{}]", groupName);

        List<Cache<?, ?>> caches = cacheRegistry.get(groupName);

        if (caches != null) {
            for (Cache<?, ?> cache : caches) {
                cache.invalidateAll().blockingAwait();
            }
        }
    }

    @Override
    public void evictAllForCache(String groupName, String cacheName) {
        LOG.debug("Evicting cache contents for group [{}], cache [{}]", groupName, cacheName);

        List<Cache<?, ?>> caches = cacheRegistry.get(groupName);

        if (caches != null) {
            for (Cache<?, ?> cache : caches) {
                if (cache.getName().equals(cacheName)) {
                    cache.invalidateAll().blockingAwait();
                    break;
                }
            }
        }
    }

    @Override
    public void evictForGroup(String groupName, Object key) {
        LOG.debug("Evicting cache values for group [{}], key [{}]", groupName, key);

        List<Cache<?, ?>> caches = cacheRegistry.get(groupName);

        LOG.debug("Found caches [{}]", caches);
        if (caches != null) {
            for (Cache<?, ?> cache : caches) {
                LOG.debug("Evicting cache value for cache [{}], key [{}]", cache.getName(), key);

                ((Cache<Object, Object>) cache).invalidate(key).blockingAwait();
            }
        }
    }
}
