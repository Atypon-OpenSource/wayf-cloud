package com.atypon.wayf.dao.impl;

import com.atypon.wayf.dao.InstitutionDao;
import com.atypon.wayf.data.Institution;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.UUID;

public class InstitutionDaoRedisImpl implements InstitutionDao {
    private static final Logger LOG = LoggerFactory.getLogger(InstitutionDaoRedisImpl.class);

    private static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

    public InstitutionDaoRedisImpl() {
    }

    @Override
    public Institution create(Institution institution) {
        institution.setId(UUID.randomUUID().toString());

        LOG.debug("Creating institution [{}] in Redis", institution);

        Jedis jedis = null;

        try {
            jedis = pool.getResource();
            jedis.set(institution.getId(), serialize(institution));
        } finally {
            jedis.close();
        }

        return institution;
    }

    @Override
    public Institution read(String id) {
        Jedis jedis = null;

        try {
            jedis = pool.getResource();
            return deserialize(jedis.get(id));
        } finally {
            jedis.close();
        }
    }

    @Override
    public Institution update(Institution institution) {
        Jedis jedis = null;

        try {
            jedis = pool.getResource();
            jedis.set(institution.getId(), serialize(institution));
        } finally {
            jedis.close();
        }

        return institution;
    }

    @Override
    public void delete(String id) {
        Jedis jedis = null;

        try {
            jedis = pool.getResource();
            jedis.del(id);
        } finally {
            jedis.close();
        }
    }

    private String serialize(Institution institution) {
        return Json.encode(institution);
    }

    private Institution deserialize(String value) {
        return Json.decodeValue(value, Institution.class);
    }
}
