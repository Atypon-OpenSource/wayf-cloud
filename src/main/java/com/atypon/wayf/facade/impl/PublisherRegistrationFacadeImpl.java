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

import com.atypon.wayf.dao.PublisherRegistrationDao;
import com.atypon.wayf.data.publisher.registration.PublisherRegistration;
import com.atypon.wayf.data.publisher.registration.PublisherRegistrationQuery;
import com.atypon.wayf.data.publisher.registration.PublisherRegistrationStatus;
import com.atypon.wayf.facade.PublisherRegistrationFacade;
import com.atypon.wayf.reactivex.FacadePolicies;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@Singleton
public class PublisherRegistrationFacadeImpl implements PublisherRegistrationFacade {
    private static final Logger LOG = LoggerFactory.getLogger(PublisherRegistrationFacadeImpl.class);

    @Inject
    private PublisherRegistrationDao publisherRegistrationDao;

    public PublisherRegistrationFacadeImpl() {
    }

    @Override
    public Single<PublisherRegistration> create(PublisherRegistration publisherRegistration) {
        LOG.debug("Creating publisher registration [{}]", publisherRegistration);

        publisherRegistration.setStatus(PublisherRegistrationStatus.PENDING);
        publisherRegistration.setApplicationDate(new Date());

        return publisherRegistrationDao.create(publisherRegistration)
                .compose((single) -> FacadePolicies.applySingle(single));
    }

    @Override
    public Single<PublisherRegistration> read(Long id) {
        LOG.debug("Reading publisher registration with ID [{}]", id);

        return FacadePolicies.singleOrException(
                publisherRegistrationDao.read(id).
                        compose((maybe) -> FacadePolicies.applyMaybe(maybe)),
                HttpStatus.SC_NOT_FOUND,
                "Could not read PublisherRegistration with id {}", id);
    }

    @Override
    public Single<PublisherRegistration> updateStatus(PublisherRegistration publisherRegistration) {
        LOG.debug("Creating publisher registration [{}]", publisherRegistration);

        if (publisherRegistration.getStatus() == PublisherRegistrationStatus.APPROVED) {
            publisherRegistration.setApprovalDate(new Date());
        }

        return publisherRegistrationDao.update(publisherRegistration)
                .compose((single) -> FacadePolicies.applySingle(single));    }

    @Override
    public Observable<PublisherRegistration> filter(PublisherRegistrationQuery query) {
        LOG.debug("Filtering publisher registrations for [{}]", query);

        return publisherRegistrationDao.filter(query)
                .compose((observable) -> FacadePolicies.applyObservable(observable));
    }
}
