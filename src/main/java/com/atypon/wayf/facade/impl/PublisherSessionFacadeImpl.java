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

import com.atypon.wayf.data.publisher.PublisherSession;
import com.atypon.wayf.facade.PublisherSessionFacade;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PublisherSessionFacadeImpl implements PublisherSessionFacade {
    private static Logger LOG = LoggerFactory.getLogger(PublisherSessionFacadeImpl.class);

    public PublisherSessionFacadeImpl() {
    }

    @Override
    public Single<PublisherSession> create(PublisherSession publisherSession) {
        LOG.debug("Creating institution [{}]", publisherSession);

        publisherSession.setId(UUID.randomUUID().toString());
        return Single.just(publisherSession)
                .observeOn(Schedulers.io())
                .map((o_publisherSession) -> {  LOG.debug("Save called");return o_publisherSession;});
    }

    @Override
    public Single<PublisherSession> read(String id) {
        return null;
    }

    @Override
    public Single<PublisherSession> update(PublisherSession publisherSession) {
        return null;
    }

    @Override
    public Completable delete(String id) {
        return null;
    }
}
