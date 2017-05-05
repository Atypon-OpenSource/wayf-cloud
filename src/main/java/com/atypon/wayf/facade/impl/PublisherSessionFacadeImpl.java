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
import com.atypon.wayf.data.IdentityProviderQuery;
import com.atypon.wayf.data.cache.CascadingCache;
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.device.DeviceQuery;
import com.atypon.wayf.data.device.DeviceStatus;
import com.atypon.wayf.data.publisher.*;
import com.atypon.wayf.data.publisher.session.PublisherSession;
import com.atypon.wayf.data.publisher.session.PublisherSessionQuery;
import com.atypon.wayf.facade.DeviceFacade;
import com.atypon.wayf.facade.IdentityProviderFacade;
import com.atypon.wayf.facade.PublisherFacade;
import com.atypon.wayf.facade.PublisherSessionFacade;
import com.atypon.wayf.reactivex.FacadePolicies;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

    @Inject
    private PublisherFacade publisherFacade;

    public PublisherSessionFacadeImpl() {
    }

    @Override
    public Single<PublisherSession> create(PublisherSession publisherSession) {
            LOG.debug("Creating institution [{}]", publisherSession);

            publisherSession.setId(UUID.randomUUID().toString());
            publisherSession.setLastActiveDate(new Date());

            return Single.zip(
                            getOrCreateDevice(publisherSession.getDevice()).subscribeOn(Schedulers.io()),
                            validatePublisherIdUniqueness(publisherSession).subscribeOn(Schedulers.io()),

                            (device, isUnique) -> {
                                if (!isUnique) {
                                    throw new RuntimeException("Publisher ID must be unique");
                                }

                                LOG.debug("Setting device {}", device.getId());
                                publisherSession.setDevice(device);

                                return publisherSession;
                            }
                    )
                    .observeOn(Schedulers.io())
                    .flatMap((o_publisherSession) -> publisherSessionDao.create(o_publisherSession));
    }

    @Override
    public Single<PublisherSession> read(PublisherSessionQuery query) {
        return Single.just(query)
                .flatMap((_query) -> publisherSessionDao.read(query.getId()).compose((maybe) -> FacadePolicies.http404OnEmpty(maybe)).toSingle())
                .flatMap((_publisherSession) -> populate(_publisherSession, query).toSingle(() -> _publisherSession));
    }

    @Override
    public Single<PublisherSession> update(PublisherSession publisherSession) {
        return Single.zip(
                Single.just(publisherSession),
                resolveId(publisherSession),

                (o_publisherSession, o_publisherId) -> {
                    o_publisherSession.setLocalId(o_publisherId);
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

        return Single.zip(
                        identityProviderFacade.resolve(publisherSession.getAuthenticatedBy()).subscribeOn(Schedulers.io()),
                        resolveId(publisherSession).subscribeOn(Schedulers.io()),

                        (identityProvider, publisherId) -> {
                            publisherSession.setAuthenticatedBy(identityProvider);
                            publisherSession.setId(publisherId);

                            return publisherSession;
                        }
                )
                .flatMap(publisherSessionToPersist -> publisherSessionDao.update(publisherSessionToPersist))
                .toCompletable();
    }

    @Override
    public Observable<PublisherSession> filter(PublisherSessionQuery query) {
        LOG.debug("Filtering for publisher sessions with criteria [{}]", query);

        return Single.just(query)
                .observeOn(Schedulers.io())

                // Fetch the PublisheSessions from the dao
                .flatMapObservable((_filterCriteria) -> publisherSessionDao.filter(query))

                // Collect the results into a Single so that we can batch the populate reads
                .toList()
                .flatMapObservable((publisherSessions) ->

                        // Inflate the publisher sessions via the populate call and emit them
                        populate((List<PublisherSession>) publisherSessions, query)
                                .toObservable()
                                .cast(PublisherSession.class)
                                .concatWith(Observable.fromIterable(publisherSessions)));

    }

    private Single<Device> getOrCreateDevice(Device device) {
        if (device == null) {
            device = new Device();
            device.setStatus(DeviceStatus.ACTIVE);
        }

        return Maybe.concat(
                        Observable.just(device)
                                .filter(f_device -> f_device.getId() != null)
                                .firstElement(),
                        Single.just(device)
                                .observeOn(Schedulers.io())
                                .flatMap((o_device) -> deviceFacade.create(o_device))
                                .toMaybe()
                )
                .firstOrError()
                .doOnError((e) -> {throw new RuntimeException("Unable to get or create device");});
    }

    private Single<Boolean> validatePublisherIdUniqueness(PublisherSession publisherSession) {
        // Some long running database job
        return Single.just(Boolean.TRUE);
    }


    private Single<String> resolveId(PublisherSession publisherSession) {
        Preconditions.checkArgument(publisherSession.getId() != null || publisherSession.getLocalId() != null, "PublisherSession requires an ID or Publisher ID");

        return Maybe.concat(
                        publisherSession.getId() == null ?
                                Maybe.empty() :
                                Maybe.just(publisherSession.getId()),
                        idCache.get(publisherSession.getLocalId())
                )
                .firstOrError();
    }

    private Completable populate(PublisherSession publisherSession, PublisherSessionQuery query) {
        return populate(Lists.newArrayList(publisherSession), query);
    }

    private Completable populate(Iterable<PublisherSession> publisherSession, PublisherSessionQuery query) {
        return Completable.mergeArray(
                fetchPublishers(Lists.newArrayList(publisherSession), query),
                fetchAuthenticatedBys(publisherSession, query),
                fetchDevices(publisherSession, query)
        );
    }

    private Completable fetchPublishers(Iterable<PublisherSession> publisherSessions, PublisherSessionQuery query) {
        // Return as complete if publisher is not a requested field
        if (!query.getFields().contains(PublisherSessionQuery.PUBLISHER_FIELD)) {
            return Completable.complete();
        }

        Multimap<String, PublisherSession> publisherSessionsByPublisherId = HashMultimap.create();

        return Observable.fromIterable(publisherSessions)
                // Filter out publisher sessions with no publisher
                .filter((publisherSession) -> publisherSession.getPublisher() != null && publisherSession.getPublisher().getId() != null)

                // Collect all of the publisher sessions and their publisher IDs into a map
                .collectInto(publisherSessionsByPublisherId, (map, publisherSession) -> map.put(publisherSession.getPublisher().getId(), publisherSession))

                // Fetch all of the publishers for those publisher IDs
                .flatMapObservable((map) -> map.keySet().isEmpty()? Observable.empty() : publisherFacade.filter(new PublisherQuery().setIds(map.keySet())))

                // For each publisher returned, map it to each publisher session that had its ID
                .flatMapCompletable((publisher) ->
                        Observable.fromIterable(publisherSessionsByPublisherId.get(publisher.getId()))
                                .flatMapCompletable((publisherSession) ->
                                        Completable.fromAction(() -> publisherSession.setPublisher(publisher))
                                )
                );
    }

    private Completable fetchAuthenticatedBys(Iterable<PublisherSession> publisherSessions, PublisherSessionQuery query) {
        // Return as complete if authenticatedBy is not a requested field
        if (!query.getFields().contains(PublisherSessionQuery.AUTHENTICATED_BY)) {
            return Completable.complete();
        }

        Multimap<String, PublisherSession> publisherSessionsByIdpId = HashMultimap.create();

        return Observable.fromIterable(publisherSessions)
                // Filter out publisher sessions with no publisher
                .filter((publisherSession) -> publisherSession.getAuthenticatedBy() != null && publisherSession.getAuthenticatedBy().getId() != null)

                // Collect all of the publisher sessions and their identity provider IDs into a map
                .collectInto(publisherSessionsByIdpId, (map, publisherSession) -> map.put(publisherSession.getAuthenticatedBy().getId(), publisherSession))

                // Fetch all of the publishers for those publisher IDs
                .flatMapObservable((map) -> map.keySet().isEmpty()? Observable.empty() : identityProviderFacade.filter(new IdentityProviderQuery().setIds(map.keySet())))

                // For each identity provider returned, map it to each publisher session that had its ID
                .flatMapCompletable((identityProvider) ->
                        Observable.fromIterable(publisherSessionsByIdpId.get(identityProvider.getId()))
                                .flatMapCompletable((publisherSession) ->
                                        Completable.fromAction(() -> publisherSession.setAuthenticatedBy(identityProvider))
                                )
                );
    }

    private Completable fetchDevices(Iterable<PublisherSession> publisherSessions, PublisherSessionQuery query) {
        // Return as complete if device is not a requested field
        if (!query.getFields().contains(PublisherSessionQuery.DEVICE)) {
            return Completable.complete();
        }

        Multimap<String, PublisherSession> publisherSessionsByDeviceId = HashMultimap.create();

        return Observable.fromIterable(publisherSessions)
                // Filter out publisher sessions with no publisher
                .filter((publisherSession) -> publisherSession.getDevice() != null && publisherSession.getDevice().getId() != null)

                // Collect all of the publisher sessions and their device IDs into a map
                .collectInto(publisherSessionsByDeviceId, (map, publisherSession) -> map.put(publisherSession.getDevice().getId(), publisherSession))

                // Fetch all of the publishers for those publisher IDs
                .flatMapObservable((map) -> map.keySet().isEmpty()? Observable.empty() : deviceFacade.filter(new DeviceQuery().setIds(map.keySet())))

                // For each identity provider returned, map it to each publisher session that had its ID
                .flatMapCompletable((device) -> {
                            return Observable.fromIterable(publisherSessionsByDeviceId.get(device.getId()))
                                    .flatMapCompletable((publisherSession) ->
                                            Completable.fromAction(() -> publisherSession.setDevice(device))
                                    );
                        }
                );
    }
}
