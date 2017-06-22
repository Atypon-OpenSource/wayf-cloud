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

import com.atypon.wayf.dao.RedisDao;
import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.verticle.Deserializer;
import com.atypon.wayf.verticle.Serializer;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RedisDaoImpl<K, V> implements RedisDao<K, V> {
    private static final Logger LOG = LoggerFactory.getLogger(RedisDaoImpl.class);

    private JedisPool pool;
    private String prefix;
    private Serializer<V, String> serializer;
    private Deserializer<String, V> deserializer;
    private int ttlSeconds;

    public RedisDaoImpl() {
    }

    public RedisDaoImpl setPool(JedisPool pool) {
        this.pool = pool;
        return this;
    }

    public RedisDaoImpl setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public RedisDaoImpl setSerializer(Serializer<V, String> serializer) {
        this.serializer = serializer;
        return this;
    }

    public RedisDaoImpl setDeserializer(Deserializer<String, V> deserializer) {
        this.deserializer = deserializer;
        return this;
    }

    public RedisDaoImpl setTtlSeconds(int ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
        return this;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public int getTtlSeconds() {
        return ttlSeconds;
    }

    @Override
    public Completable remove(K... keys) {
        return Completable.fromAction(() -> {
            try (Jedis jedis = pool.getResource()) {
                String[] fullKeys = new String[keys.length];

                for (int i = 0; i < keys.length; i++) {
                    fullKeys[i] = buildKey(keys[i]);
                }

                jedis.del(fullKeys);
            } catch (Exception e) {
                LOG.error("Could not write to Redis", e);

                throw new ServiceException(HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
            }
        });
    }

    @Override
    public Completable removeAll() {
        return Completable.fromAction(() -> {
            Set<String> matchingKeys = new HashSet<>();
            ScanParams params = new ScanParams();
            params.match(prefix + "*");

            try(Jedis jedis = pool.getResource()) {
                String nextCursor = "0";

                do {
                    ScanResult<String> scanResult = jedis.scan(nextCursor, params);
                    List<String> keys = scanResult.getResult();
                    nextCursor = scanResult.getStringCursor();

                    matchingKeys.addAll(keys);

                } while(!nextCursor.equals("0"));

                jedis.del(matchingKeys.toArray(new String[matchingKeys.size()]));
            }
        });
    }

    @Override
    public Completable set(K key, V value) {
        return Completable.fromAction(() -> {
            String valueToWrite = serialize(value);

            try (Jedis jedis = pool.getResource()) {
                jedis.setex(buildKey(key), ttlSeconds, valueToWrite);
            } catch (Exception e) {
                LOG.error("Could not write to Redis", e);

                throw new ServiceException(HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
            }
        });
    }

    @Override
    public Maybe<V> get(K key) {
        return Maybe.fromCallable(() -> {
            String value = null;

            // Try to read the value from Redis
            try (Jedis jedis = pool.getResource()) {
                value = jedis.get(buildKey(key));
            } catch (Exception e) {
                throw new ServiceException(HttpStatus.SC_INTERNAL_SERVER_ERROR, e);
            }
            return value;
        }).map((readValue) -> deserialize(readValue));
    }


    private String buildKey(K key) {
        return prefix + key.toString();
    }

    private String serialize(V value) {
        return serializer.serialize(value);
    }

    private V deserialize(String serializedValue) {
        return deserializer.deserialize(serializedValue);
    }
}
