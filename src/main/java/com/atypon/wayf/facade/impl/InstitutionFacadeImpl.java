package com.atypon.wayf.facade.impl;

import com.atypon.wayf.dao.InstitutionDao;
import com.atypon.wayf.dao.impl.InstitutionDaoMapImpl;
import com.atypon.wayf.dao.impl.InstitutionDaoMySqlImpl;
import com.atypon.wayf.dao.impl.InstitutionDaoNeo4JImpl;
import com.atypon.wayf.dao.impl.InstitutionDaoRedisImpl;
import com.atypon.wayf.data.Institution;
import com.atypon.wayf.facade.InstitutionFacade;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstitutionFacadeImpl implements InstitutionFacade {
    private static final Logger LOG = LoggerFactory.getLogger(InstitutionFacadeImpl.class);

    private InstitutionDao dao;

    public InstitutionFacadeImpl() {
        dao = new InstitutionDaoNeo4JImpl();
    }

    @Override
    public Single<Institution> create(Institution institution) {
        LOG.debug("Creating institution [{}]", institution);

        // Used to demonstrate error handling
        if (institution.getName().equals("error")) {
            throw new RuntimeException("Nice try");
        }

        return Single.just(institution)
                .observeOn(Schedulers.io())
                .map((o_institution) -> dao.create(o_institution));
    }

    @Override
    public Single<Institution> read(String id) {
        LOG.debug("Reading institution with id [{}]", id);

        return Single.just(id)
                .observeOn(Schedulers.io())
                .map((o_id) -> dao.read(o_id));
    }

    @Override
    public Single<Institution> update(Institution institution) {
        LOG.debug("Updating institution [{}]", institution);

        return Single.just(institution)
                .observeOn(Schedulers.io())
                .map((o_institution) -> dao.update(o_institution));
    }

    @Override
    public Completable delete(String id) {
        LOG.debug("Reading institution with id [{}]", id);

        return Single.just(id)
                .observeOn(Schedulers.io())
                .flatMapCompletable((o_id) -> Completable.fromAction(() -> dao.delete(o_id)));
    }
}