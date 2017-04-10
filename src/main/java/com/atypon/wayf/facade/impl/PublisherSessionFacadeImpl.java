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
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.device.DeviceStatus;
import com.atypon.wayf.data.publisher.PublisherSession;
import com.atypon.wayf.facade.DeviceFacade;
import com.atypon.wayf.facade.PublisherSessionFacade;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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

    /*
    @Inject
    public PublisherSessionFacadeImpl(DeviceFacade deviceFacade, PublisherSessionDao publisherSessionDao) {
        this.deviceFacade = deviceFacade;
        this.publisherSessionDao = publisherSessionDao;
    }*/

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
        return null;
    }

    @Override
    public Completable delete(String id) {
        return null;
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
}
