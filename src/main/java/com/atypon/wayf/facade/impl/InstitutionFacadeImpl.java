package com.atypon.wayf.facade.impl;

import com.atypon.wayf.dao.InstitutionDao;
import com.atypon.wayf.dao.impl.InstitutionDaoNeo4JImpl;
import com.atypon.wayf.data.Institution;
import com.atypon.wayf.facade.InstitutionFacade;
import io.netty.util.concurrent.CompleteFuture;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class InstitutionFacadeImpl implements InstitutionFacade {
    private static final Logger LOG = LoggerFactory.getLogger(InstitutionFacadeImpl.class);

    private InstitutionDao dao;

    public InstitutionFacadeImpl() {
        dao = new InstitutionDaoNeo4JImpl();
    }

    @Override
    public Observable<Institution> create(Institution institution) {
        LOG.debug("Creating institution [{}]", institution);

        if (institution.getName().equals("error")) {
            throw new RuntimeException("Nice try");
        }

        return Observable.just(institution).observeOn(Schedulers.io()).map((o_institution) -> dao.create(o_institution));
    }

    @Override
    public Observable<Institution> read(String id) {
        LOG.debug("Reading institution with id [{}]", id);

        return Observable.just(id).observeOn(Schedulers.io()).map((o_id) -> dao.read(o_id));
    }

    @Override
    public Observable<Institution> update(Institution institution) {
        LOG.debug("Updating institution [{}]", institution);

        return Observable.just(institution).observeOn(Schedulers.io()).map((o_institution) -> dao.update(o_institution));
    }

    @Override
    public Observable<Optional<Void>> delete(String id) {
        LOG.debug("Reading institution with id [{}]", id);

        return Observable.just(id).observeOn(Schedulers.io()).map((o_id) -> {dao.delete(o_id); return Optional.empty();});
    }
}