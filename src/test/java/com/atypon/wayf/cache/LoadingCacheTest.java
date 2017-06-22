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

package com.atypon.wayf.cache;


import io.reactivex.Maybe;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public abstract class LoadingCacheTest {
    protected LoadingCache<String, String> cache;

    public String getLoaderRandomValue() {
        return "RANDOM-" + UUID.randomUUID().toString();
    }

    @Test
    public void testStorage() {
        cache.setCacheLoader((key) -> Maybe.empty());

        cache.put("key", "value").blockingGet();

        String value = cache.get("key").blockingGet();
        assertEquals("value", value);

        cache.put("key", "value2").blockingGet();

        String updatedValue = cache.get("key").blockingGet();
        assertEquals("value2", updatedValue);
    }

    @Test
    public void testInvalidate() {
        cache.setCacheLoader((key) -> Maybe.empty());

        cache.put("key", "value").blockingGet();

        String value = cache.get("key").blockingGet();
        assertEquals("value", value);

        cache.invalidate("key").blockingGet();

        String invalidatedValue = cache.get("key").blockingGet();
        assertNull(invalidatedValue);
    }

    @Test
    public void testInvalidateAll() {
        cache.setCacheLoader((key) -> Maybe.empty());

        cache.put("key1", "value1").blockingGet();
        cache.put("key2", "value2").blockingGet();
        cache.put("key3", "value3").blockingGet();

        String value1 = cache.get("key1").blockingGet();
        assertEquals("value1", value1);

        String value2 = cache.get("key2").blockingGet();
        assertEquals("value2", value2);

        String value3 = cache.get("key3").blockingGet();
        assertEquals("value3", value3);

        cache.invalidateAll().blockingGet();

        value1 = cache.get("key1").blockingGet();
        assertNull(value1);

        value2 = cache.get("key2").blockingGet();
        assertNull(value2);

        value3 = cache.get("key3").blockingGet();
        assertNull(value3);
    }

    @Test
    public void testLoad() {
        cache.setCacheLoader((key) -> Maybe.just(getLoaderRandomValue()));

        String randomKey = UUID.randomUUID().toString();

        String loaderValue = cache.get(randomKey).blockingGet();
        assertNotNull(loaderValue);

        String cachedValue = cache.get(randomKey).blockingGet();
        assertEquals(loaderValue, cachedValue);
    }
}
