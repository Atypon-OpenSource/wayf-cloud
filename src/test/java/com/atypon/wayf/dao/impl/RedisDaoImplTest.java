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

import com.atypon.wayf.guice.WayfGuiceModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class RedisDaoImplTest {

    @Inject
    private JedisPool jedisPool;

    @Before
    public void setup() {
        Guice.createInjector(new WayfGuiceModule()).injectMembers(this);
    }

    @Test
    public void testStorage() {
        RedisDaoImpl<String, String> redisDao = new RedisDaoImpl<>()
                .setTtlSeconds(10)
                .setPool(jedisPool)
                .setDeserializer((inputStr) -> inputStr)
                .setSerializer((inputStr) -> inputStr)
                .setPrefix("TEST-STORAGE");

        redisDao.set("key-a", "val-a").blockingGet();

        String value = redisDao.get("key-a").blockingGet();
        assertEquals("val-a", value);
    }

    @Test
    public void testTtl() throws Exception {
        RedisDaoImpl<String, String> shortTtl = new RedisDaoImpl<>()
                .setTtlSeconds(1)
                .setPool(jedisPool)
                .setDeserializer((inputStr) -> inputStr)
                .setSerializer((inputStr) -> inputStr)
                .setPrefix("TEST-TTL");

        shortTtl.set("key-a", "val-a").blockingGet();

        String value = shortTtl.get("key-a").blockingGet();
        assertEquals("val-a", value);

        Thread.sleep(1001l);

        String expValue = shortTtl.get("key-a").blockingGet();
        assertNull(expValue);
    }

    @Test
    public void testPrefix() {
        RedisDaoImpl<String, String> redisDaoA = new RedisDaoImpl<>()
                .setTtlSeconds(1)
                .setPool(jedisPool)
                .setDeserializer((inputStr) -> inputStr)
                .setSerializer((inputStr) -> inputStr)
                .setPrefix("TEST-PREFIX-A");

        RedisDaoImpl<String, String> redisDaoB = new RedisDaoImpl<>()
                .setTtlSeconds(1)
                .setPool(jedisPool)
                .setDeserializer((inputStr) -> inputStr)
                .setSerializer((inputStr) -> inputStr)
                .setPrefix("TEST-PREFIX-B");

        redisDaoA.set("key-a", "value-a").blockingGet();
        redisDaoB.set("key-a", "value-b").blockingGet();

        String valueFromA = redisDaoA.get("key-a").blockingGet();
        assertEquals("value-a", valueFromA);

        String valueFromB = redisDaoB.get("key-a").blockingGet();
        assertEquals("value-b", valueFromB);
    }

    @Test
    public void testRemove() {
        RedisDaoImpl<String, String> redisDao = new RedisDaoImpl<>()
                .setTtlSeconds(10)
                .setPool(jedisPool)
                .setDeserializer((inputStr) -> inputStr)
                .setSerializer((inputStr) -> inputStr)
                .setPrefix("TEST-REMOVE");

        redisDao.set("key-a", "val-a").blockingGet();

        String value = redisDao.get("key-a").blockingGet();
        assertEquals("val-a", value);

        redisDao.remove("key-a").blockingGet();

        String removedValue = redisDao.get("key-a").blockingGet();
        assertNull(removedValue);
    }

    @Test
    public void testRemoveAll() {
        RedisDaoImpl<String, String> redisDao = new RedisDaoImpl<>()
                .setTtlSeconds(15)
                .setPool(jedisPool)
                .setDeserializer((inputStr) -> inputStr)
                .setSerializer((inputStr) -> inputStr)
                .setPrefix("TEST-REMOVE-ALL");

        RedisDaoImpl<String, String> redisDaoUnrelated = new RedisDaoImpl<>()
                .setTtlSeconds(15)
                .setPool(jedisPool)
                .setDeserializer((inputStr) -> inputStr)
                .setSerializer((inputStr) -> inputStr)
                .setPrefix("TEST-REMOVE-ALL-UNRELATED");

        redisDaoUnrelated.set("key", "value").blockingGet();

        String unrelatedValue = redisDaoUnrelated.get("key").blockingGet();
        assertNotNull(unrelatedValue);

        redisDao.set("key-a", "val-a").blockingGet();
        redisDao.set("key-b", "val-b").blockingGet();
        redisDao.set("key-c", "val-c").blockingGet();

        String valueA = redisDao.get("key-a").blockingGet();
        assertEquals("val-a", valueA);

        String valueB = redisDao.get("key-b").blockingGet();
        assertEquals("val-b", valueB);

        String valueC = redisDao.get("key-c").blockingGet();
        assertEquals("val-c", valueC);

        redisDao.removeAll().blockingGet();

        String removedValueA = redisDao.get("key-a").blockingGet();
        String removedValueB = redisDao.get("key-b").blockingGet();
        String removedValueC = redisDao.get("key-c").blockingGet();

        unrelatedValue = redisDaoUnrelated.get("key").blockingGet();
        assertNotNull(unrelatedValue);

        assertNull(removedValueA);
        assertNull(removedValueB);
        assertNull(removedValueC);
    }
}
