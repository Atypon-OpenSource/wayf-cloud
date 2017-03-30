package com.atypon.wayf.dao.impl;

import com.atypon.wayf.data.Institution;
import com.atypon.wayf.dao.InstitutionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InstitutionDaoMapImpl implements InstitutionDao {
    private static final Logger LOG = LoggerFactory.getLogger(InstitutionDaoMapImpl.class);

    private static final Map<String, Institution> INSTITUTION_MAP = new HashMap<>();

    public InstitutionDaoMapImpl() {
    }

    @Override
    public Institution create(Institution institution) {
        institution.setId(UUID.randomUUID().toString());

        INSTITUTION_MAP.put(institution.getId(), institution);

        return institution;
    }

    @Override
    public Institution read(String id) {
        return INSTITUTION_MAP.get(id);
    }

    @Override
    public Institution update(Institution institution) {
        INSTITUTION_MAP.put(institution.getId(), institution);
        return read(institution.getId());
    }

    @Override
    public void delete(String id) {
        INSTITUTION_MAP.remove(id);
    }
}
