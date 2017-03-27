package com.atypon.wayf.dao.impl;

import com.atypon.wayf.data.Institution;
import com.atypon.wayf.dao.InstitutionDao;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class InstitutionDaoMapImpl implements InstitutionDao {
    private static final Logger LOG = LoggerFactory.getLogger(InstitutionDaoMapImpl.class);

    private static final Map<String, Institution> WHISKY_MAP = new HashMap<>();

    public InstitutionDaoMapImpl() {
    }

    @Override
    public Institution create(Institution institution) {
        institution.setId(UUID.randomUUID().toString());

        WHISKY_MAP.put(institution.getId(), institution);

        return institution;
    }

    @Override
    public Institution read(String id) {
        return WHISKY_MAP.get(id);
    }

    @Override
    public Institution update(Institution institution) {
        WHISKY_MAP.put(institution.getId(), institution);
        return read(institution.getId());
    }

    @Override
    public void delete(String id) {
        WHISKY_MAP.remove(id);
    }
}
