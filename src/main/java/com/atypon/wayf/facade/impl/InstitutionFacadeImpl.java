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

package com.atypon.wayf.facade.impl;

import com.atypon.wayf.dao.InstitutionDao;
import com.atypon.wayf.dao.impl.InstitutionDaoNeo4JImpl;
import com.atypon.wayf.data.Institution;
import com.atypon.wayf.facade.InstitutionFacade;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class InstitutionFacadeImpl implements InstitutionFacade {
    private static final Logger LOG = LoggerFactory.getLogger(InstitutionFacadeImpl.class);

    private InstitutionDao dao;

    @Inject
    public InstitutionFacadeImpl(InstitutionDao dao) {
        this.dao = dao;
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