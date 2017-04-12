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

import com.atypon.wayf.dao.PublisherSessionDao;
import com.atypon.wayf.data.cache.CascadingCache;
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.device.DeviceStatus;
import com.atypon.wayf.data.publisher.PublisherSession;
import com.atypon.wayf.facade.DeviceFacade;
import com.atypon.wayf.facade.IdentityProviderFacade;
import com.atypon.wayf.facade.PublisherSessionFacade;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sun.javafx.geom.transform.Identity;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

@Singleton
public class PublisherSessionFacadeImpl implements PublisherSessionFacade {
    private static Logger LOG = LoggerFactory.getLogger(PublisherSessionFacadeImpl.class);

    @Inject
    private DeviceFacade deviceFacade;

    @Inject
    private PublisherSessionDao publisherSessionDao;

    @Inject
    private IdentityProviderFacade identityProviderFacade;

    @Inject
    @Named("publisherIdCache")
    private CascadingCache<String, String> idCache;

    public PublisherSessionFacadeImpl() {
    }

    @Override
    public Single<PublisherSession> create(PublisherSession publisherSession) {
            LOG.debug("Creating institution [{}]", publisherSession);

            publisherSession.setId(UUID.randomUUID().toString());

            publisherSession.setLastActiveDate(new Date());

            return Single.just(publisherSession)
                    .flatMap((o_publisherSession) ->
                            Single.zip(
                                    getOrCreateDevice(o_publisherSession.getDevice()).subscribeOn(Schedulers.newThread()),
                                    validatePublisherIdUniqueness(o_publisherSession).subscribeOn(Schedulers.newThread()),
                                    //id.addWayfIdMapping(o_publisherSession.getPublisherId(), o_publisherSession.getId()).subscribeOn(Schedulers.newThread()).toSingleDefault(""),

                                    (o_device, o_unique) -> {
                                        if (!o_unique) {
                                            throw new RuntimeException("Publisher ID must be unique");
                                        }

                                        LOG.debug("Setting device {}", o_device.getId());
                                        o_publisherSession.setDevice(o_device);
                                        return o_publisherSession;
                                    }
                            )
                    )
                    .observeOn(Schedulers.io())
                    .flatMap((o_publisherSession) -> Single.just(publisherSessionDao.create(o_publisherSession)));
    }

    @Override
    public Single<PublisherSession> read(String id) {
        return null;
    }

    @Override
    public Single<PublisherSession> update(PublisherSession publisherSession) {
        return Single.zip(
                Single.just(publisherSession),
                getPublisherId(publisherSession),

                (o_publisherSession, o_publisherId) -> {
                    o_publisherSession.setPublisherId(o_publisherId);
                    return o_publisherSession;
                }
        );
    }

    @Override
    public Completable delete(String id) {
        return null;
    }

    @Override
    public Completable addIdpRelationship(PublisherSession publisherSession) {
        LOG.debug("Adding relationship");

        return
                Single.zip(
                        Single.just(publisherSession),
                        identityProviderFacade.resolve(publisherSession.getIdp()),
                        getPublisherId(publisherSession),

                        (o_publisherSession, identityProvider, publisherId) -> {
                            o_publisherSession.setIdp(identityProvider);
                            o_publisherSession.setPublisherId(publisherId);
                            return o_publisherSession;
                        })
                        .flatMapCompletable(publisherSessionToPersist -> publisherSessionDao.addIdpRelationship(publisherSessionToPersist));
    }

    private Single<Device> getOrCreateDevice(Device device) {
        if (device == null) {
            device = new Device();
            device.setStatus(DeviceStatus.ACTIVE);
        }

        return device.getId() == null || device.getId().isEmpty()?
                Single.just(device)
                        .observeOn(Schedulers.io())
                        .flatMap((o_device) -> deviceFacade.create(o_device)) :
                Single.just(device);
    }

    private Single<Boolean> validatePublisherIdUniqueness(PublisherSession publisherSession) {
        // Some long running database job
        return Single.just(Boolean.TRUE);
    }

    private Single<String> getPublisherId(PublisherSession publisherSession) {
        if (publisherSession.getId() == null) {
            if (publisherSession.getPublisherId() == null) {
                throw new RuntimeException("Either an id or publisherId is required to update a PublisherSession");
            }

            return idCache.get(publisherSession.getPublisherId()).toSingle();
        }

        return Single.just(publisherSession.getId());
    }
}
