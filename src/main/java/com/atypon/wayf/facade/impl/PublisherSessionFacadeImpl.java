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
import com.atypon.wayf.data.identity.IdentityProviderQuery;
import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.device.DeviceQuery;
import com.atypon.wayf.data.publisher.*;
import com.atypon.wayf.data.publisher.session.PublisherSession;
import com.atypon.wayf.data.publisher.session.PublisherSessionQuery;
import com.atypon.wayf.facade.DeviceFacade;
import com.atypon.wayf.facade.IdentityProviderFacade;
import com.atypon.wayf.facade.PublisherFacade;
import com.atypon.wayf.facade.PublisherSessionFacade;
import com.atypon.wayf.reactivex.FacadePolicies;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.apache.http.HttpStatus;
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
    private PublisherFacade publisherFacade;

    public PublisherSessionFacadeImpl() {
    }

    @Override
    public Single<PublisherSession> create(PublisherSession publisherSession) {
            LOG.debug("Creating institution [{}]", publisherSession);

            publisherSession.setId(UUID.randomUUID().toString());

            return Single.zip(// Run some logic in parallel
                    // Get the device on the publisher session or create a new one
                    getOrCreateDevice(publisherSession.getDevice()).subscribeOn(Schedulers.io()),

                    // Ensure the localID is unique to that publisher
                    isUniqueLocalId(publisherSession).subscribeOn(Schedulers.io()),

                    (device, isUnique) -> {
                        if (!isUnique) {
                            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "Publisher ID must be unique");
                        }

                        LOG.debug("Setting device {}", device.getId());
                        publisherSession.setDevice(device);

                        return publisherSession;
                    })
                    .compose((single) -> FacadePolicies.applySingle(single))
                    .flatMap((_publisherSession) -> publisherSessionDao.create(_publisherSession));
    }

    @Override
    public Single<PublisherSession> read(PublisherSessionQuery query) {
        // Read publisher session by ID
        return publisherSessionDao.read(query.getId())
                .compose((maybe) -> FacadePolicies.applyMaybe(maybe))

                // Add in a custom exception on error
                .compose((maybe) -> FacadePolicies.daoReadOnIdMiss(maybe))

                // Ensure one element is emitted
                .toSingle()

                // Inflate the publisher session and emit it
                .flatMap((_publisherSession) -> populate(_publisherSession, query).toSingle(() -> _publisherSession));
    }

    @Override
    public Single<PublisherSession> update(PublisherSession publisherSession) {
        return publisherSessionDao.update(publisherSession);
    }

    @Override
    public Completable delete(String id) {
        return publisherSessionDao.delete(id);
    }

    @Override
    public Completable addIdpRelationship(PublisherSession publisherSession) {
        LOG.debug("Adding relationship");

        return Single.zip( // Do these tasks in parallel
                // Resolve the Identity Provider to a persisted one (with an ID)
                identityProviderFacade.resolve(publisherSession.getAuthenticatedBy()).subscribeOn(Schedulers.io()),

                // Resolve the Publisher session to a persisted one (with an ID)
                resolveForLocalId(publisherSession.getLocalId()).subscribeOn(Schedulers.io()),

                // Once we have resolved both, set the IDP as the authenticator
                (identityProvider, persistedPublisherSession) -> {
                    persistedPublisherSession.setAuthenticatedBy(identityProvider);
                    return persistedPublisherSession;
                })

                // Save the updated Publisher Session
                .flatMap(publisherSessionToPersist -> update(publisherSessionToPersist))
                .toCompletable();
    }

    @Override
    public Observable<PublisherSession> filter(PublisherSessionQuery query) {
        LOG.debug("Filtering for publisher sessions with criteria [{}]", query);

        return Single.just(query)
                .observeOn(Schedulers.io())

                // Fetch the PublisherSessions from the dao
                .flatMapObservable((_filterCriteria) -> publisherSessionDao.filter(query))

                // Collect the results into a Single<Iterable> so that we can batch the populate reads
                .toList()
                .flatMapObservable((publisherSessions) ->

                        // Inflate the publisher sessions via the populate call and emit them
                        populate(publisherSessions, query)
                                .toObservable()
                                .cast(PublisherSession.class)
                                .concatWith(Observable.fromIterable(publisherSessions)));

    }

    private Single<Device> getOrCreateDevice(Device device) {
        if (device != null && device.getId() != null) {
            return Single.just(device);
        }

        return deviceFacade.create(new Device());
    }

    private Single<Boolean> isUniqueLocalId(PublisherSession publisherSession) {
        return filter(new PublisherSessionQuery().setLocalId(publisherSession.getLocalId())) // Filter for publisher sessions with that local ID
                .firstElement() // Get the first element
                .isEmpty(); // Return whether or not there was an element
    }


    private Single<PublisherSession> resolveForLocalId(String localId) {
        return filter(new PublisherSessionQuery().setLocalId(localId))
                .singleOrError()
                .doOnError((e) -> {throw new ServiceException(HttpStatus.SC_NOT_FOUND, "Could not find unique PublisherSession for local ID: " + localId);});
    }

    private Completable populate(PublisherSession publisherSession, PublisherSessionQuery query) {
        return populate(Lists.newArrayList(publisherSession), query);
    }

    private Completable populate(Iterable<PublisherSession> publisherSession, PublisherSessionQuery query) {
        // Run the inflations in parallel
        return Completable.mergeArray(
                inflatePublishers(Lists.newArrayList(publisherSession), query),
                inflateAuthenticatedBys(publisherSession, query),
                inflateDevices(publisherSession, query)
        ).compose((completable) -> FacadePolicies.applyCompletable(completable));
    }

    private Completable inflatePublishers(Iterable<PublisherSession> publisherSessions, PublisherSessionQuery query) {
        // Return as complete if publisher is not a requested field
        if (query.getInflationPolicy() == null || !query.getInflationPolicy().hasChildField(PublisherSessionQuery.PUBLISHER_FIELD)) {
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

    private Completable inflateAuthenticatedBys(Iterable<PublisherSession> publisherSessions, PublisherSessionQuery query) {
        // Return as complete if authenticatedBy is not a requested field
        if (query.getInflationPolicy() == null || !query.getInflationPolicy().hasChildField(PublisherSessionQuery.AUTHENTICATED_BY)) {
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

    private Completable inflateDevices(Iterable<PublisherSession> publisherSessions, PublisherSessionQuery query) {
        // Return as complete if device is not a requested field
        if (query.getInflationPolicy() == null || !query.getInflationPolicy().hasChildField(PublisherSessionQuery.DEVICE)) {
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
                .flatMapCompletable((device) ->
                        Observable.fromIterable(publisherSessionsByDeviceId.get(device.getId()))
                                .flatMapCompletable((publisherSession) ->
                                        Completable.fromAction(() -> publisherSession.setDevice(device))
                                )

                );
    }
}
