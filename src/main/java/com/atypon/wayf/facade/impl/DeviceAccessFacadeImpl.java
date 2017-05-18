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

import com.atypon.wayf.dao.DeviceAccessDao;
import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.device.DeviceQuery;
import com.atypon.wayf.data.device.access.DeviceAccess;
import com.atypon.wayf.data.device.access.DeviceAccessQuery;
import com.atypon.wayf.data.identity.IdentityProviderQuery;
import com.atypon.wayf.data.publisher.PublisherQuery;
import com.atypon.wayf.facade.DeviceAccessFacade;
import com.atypon.wayf.facade.DeviceFacade;
import com.atypon.wayf.facade.IdentityProviderFacade;
import com.atypon.wayf.facade.PublisherFacade;
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

@Singleton
public class DeviceAccessFacadeImpl implements DeviceAccessFacade {
    private static Logger LOG = LoggerFactory.getLogger(DeviceAccessFacadeImpl.class);

    @Inject
    private DeviceFacade deviceFacade;

    @Inject
    private DeviceAccessDao deviceAccessDao;

    @Inject
    private IdentityProviderFacade identityProviderFacade;

    @Inject
    private PublisherFacade publisherFacade;

    public DeviceAccessFacadeImpl() {
    }

    @Override
    public Single<DeviceAccess> create(DeviceAccess deviceAcccess) {
            LOG.debug("Creating institution [{}]", deviceAcccess);

            return Single.zip(// Run some logic in parallel
                    // Get the device on the publisher session or create a new one
                    getOrCreateDevice(deviceAcccess.getDevice()).subscribeOn(Schedulers.io()),

                    // Ensure the localID is unique to that publisher
                    isUniqueLocalId(deviceAcccess).subscribeOn(Schedulers.io()),

                    (device, isUnique) -> {
                        if (!isUnique) {
                            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "Publisher ID must be unique");
                        }

                        LOG.debug("Setting device {}", device.getId());
                        deviceAcccess.setDevice(device);

                        return deviceAcccess;
                    })
                    .compose((single) -> FacadePolicies.applySingle(single))
                    .flatMap((_deviceAccess) -> deviceAccessDao.create(_deviceAccess));
    }

    @Override
    public Single<DeviceAccess> read(DeviceAccessQuery query) {
        // Read publisher session by ID
        return deviceAccessDao.read(query.getId())
                .compose((maybe) -> FacadePolicies.applyMaybe(maybe))

                // Add in a custom exception on error
                .compose((maybe) -> FacadePolicies.daoReadOnIdMiss(maybe))

                // Ensure one element is emitted
                .toSingle()

                // Inflate the publisher session and emit it
                .flatMap((_deviceAccess) -> populate(_deviceAccess, query).toSingle(() -> _deviceAccess));
    }

    @Override
    public Single<DeviceAccess> update(DeviceAccess deviceAccess) {
        return deviceAccessDao.update(deviceAccess);
    }

    @Override
    public Completable delete(Long id) {
        return deviceAccessDao.delete(id);
    }

    @Override
    public Completable addIdpRelationship(DeviceAccess deviceAccess) {
        LOG.debug("Adding relationship");

        return Single.zip( // Do these tasks in parallel
                // Resolve the Identity Provider to a persisted one (with an ID)
                identityProviderFacade.resolve(deviceAccess.getIdentityProvider()).subscribeOn(Schedulers.io()),

                // Resolve the Publisher session to a persisted one (with an ID)
                resolveForLocalId(deviceAccess.getLocalId()).subscribeOn(Schedulers.io()),

                // Once we have resolved both, set the IDP as the authenticator
                (identityProvider, persistedDeviceAccess) -> {
                    persistedDeviceAccess.setIdentityProvider(identityProvider);
                    return persistedDeviceAccess;
                })

                // Save the updated Publisher Session
                .flatMap(deviceAccessToPersist -> update(deviceAccessToPersist))
                .toCompletable();
    }

    @Override
    public Observable<DeviceAccess> filter(DeviceAccessQuery query) {
        LOG.debug("Filtering for publisher sessions with criteria [{}]", query);

        return Single.just(query)
                .observeOn(Schedulers.io())

                // Fetch the DeviceAccesss from the dao
                .flatMapObservable((_filterCriteria) -> deviceAccessDao.filter(query))

                // Collect the results into a Single<Iterable> so that we can batch the populate reads
                .toList()
                .flatMapObservable((deviceAccesss) ->

                        // Inflate the publisher sessions via the populate call and emit them
                        populate(deviceAccesss, query)
                                .toObservable()
                                .cast(DeviceAccess.class)
                                .concatWith(Observable.fromIterable(deviceAccesss)));

    }

    private Single<Device> getOrCreateDevice(Device device) {
        if (device != null && device.getId() != null) {
            return Single.just(device);
        }

        return deviceFacade.create(new Device());
    }

    private Single<Boolean> isUniqueLocalId(DeviceAccess deviceAccess) {
        return filter(new DeviceAccessQuery().setLocalId(deviceAccess.getLocalId())) // Filter for publisher sessions with that local ID
                .firstElement() // Get the first element
                .isEmpty(); // Return whether or not there was an element
    }


    private Single<DeviceAccess> resolveForLocalId(String localId) {
        return filter(new DeviceAccessQuery().setLocalId(localId))
                .singleOrError()
                .doOnError((e) -> {throw new ServiceException(HttpStatus.SC_NOT_FOUND, "Could not find unique DeviceAccess for local ID: " + localId);});
    }

    private Completable populate(DeviceAccess deviceAccess, DeviceAccessQuery query) {
        return populate(Lists.newArrayList(deviceAccess), query);
    }

    private Completable populate(Iterable<DeviceAccess> deviceAccess, DeviceAccessQuery query) {
        // Run the inflations in parallel
        return Completable.mergeArray(
                inflatePublishers(Lists.newArrayList(deviceAccess), query),
                inflateAuthenticatedBys(deviceAccess, query),
                inflateDevices(deviceAccess, query)
        ).compose((completable) -> FacadePolicies.applyCompletable(completable));
    }

    private Completable inflatePublishers(Iterable<DeviceAccess> deviceAccesss, DeviceAccessQuery query) {
        // Return as complete if publisher is not a requested field
        if (query.getInflationPolicy() == null || !query.getInflationPolicy().hasChildField(DeviceAccessQuery.PUBLISHER_FIELD)) {
            return Completable.complete();
        }

        Multimap<Long, DeviceAccess> deviceAccesssByPublisherId = HashMultimap.create();

        return Observable.fromIterable(deviceAccesss)
                // Filter out publisher sessions with no publisher
                .filter((deviceAccess) -> deviceAccess.getPublisher() != null && deviceAccess.getPublisher().getId() != null)

                // Collect all of the publisher sessions and their publisher IDs into a map
                .collectInto(deviceAccesssByPublisherId, (map, deviceAccess) -> map.put(deviceAccess.getPublisher().getId(), deviceAccess))

                // Fetch all of the publishers for those publisher IDs
                .flatMapObservable((map) -> map.keySet().isEmpty()? Observable.empty() : publisherFacade.filter(new PublisherQuery().setIds(map.keySet())))

                // For each publisher returned, map it to each publisher session that had its ID
                .flatMapCompletable((publisher) ->
                        Observable.fromIterable(deviceAccesssByPublisherId.get(publisher.getId()))
                                .flatMapCompletable((deviceAccess) ->
                                        Completable.fromAction(() -> deviceAccess.setPublisher(publisher))
                                )
                );
    }

    private Completable inflateAuthenticatedBys(Iterable<DeviceAccess> deviceAccesss, DeviceAccessQuery query) {
        // Return as complete if authenticatedBy is not a requested field
        if (query.getInflationPolicy() == null || !query.getInflationPolicy().hasChildField(DeviceAccessQuery.AUTHENTICATED_BY)) {
            return Completable.complete();
        }

        Multimap<Long, DeviceAccess> deviceAccesssByIdpId = HashMultimap.create();

        return Observable.fromIterable(deviceAccesss)
                // Filter out publisher sessions with no publisher
                .filter((deviceAccess) -> deviceAccess.getIdentityProvider() != null && deviceAccess.getIdentityProvider().getId() != null)

                // Collect all of the publisher sessions and their identity provider IDs into a map
                .collectInto(deviceAccesssByIdpId, (map, deviceAccess) -> map.put(deviceAccess.getIdentityProvider().getId(), deviceAccess))

                // Fetch all of the publishers for those publisher IDs
                .flatMapObservable((map) -> map.keySet().isEmpty()? Observable.empty() : identityProviderFacade.filter(new IdentityProviderQuery().setIds(map.keySet())))

                // For each identity provider returned, map it to each publisher session that had its ID
                .flatMapCompletable((identityProvider) ->
                        Observable.fromIterable(deviceAccesssByIdpId.get(identityProvider.getId()))
                                .flatMapCompletable((deviceAccess) ->
                                        Completable.fromAction(() -> deviceAccess.setIdentityProvider(identityProvider))
                                )
                );
    }

    private Completable inflateDevices(Iterable<DeviceAccess> deviceAccesss, DeviceAccessQuery query) {
        // Return as complete if device is not a requested field
        if (query.getInflationPolicy() == null || !query.getInflationPolicy().hasChildField(DeviceAccessQuery.DEVICE)) {
            return Completable.complete();
        }

        Multimap<String, DeviceAccess> deviceAccesssByDeviceId = HashMultimap.create();

        return Observable.fromIterable(deviceAccesss)
                // Filter out publisher sessions with no publisher
                .filter((deviceAccess) -> deviceAccess.getDevice() != null && deviceAccess.getDevice().getGlobalId() != null)

                // Collect all of the publisher sessions and their device IDs into a map
                .collectInto(deviceAccesssByDeviceId, (map, deviceAccess) -> map.put(deviceAccess.getDevice().getGlobalId(), deviceAccess))

                // Fetch all of the publishers for those publisher IDs
                .flatMapObservable((map) -> map.keySet().isEmpty()? Observable.empty() : deviceFacade.filter(new DeviceQuery().setGlobalIds(map.keySet())))

                // For each identity provider returned, map it to each publisher session that had its ID
                .flatMapCompletable((device) ->
                        Observable.fromIterable(deviceAccesssByDeviceId.get(device.getGlobalId()))
                                .flatMapCompletable((deviceAccess) ->
                                        Completable.fromAction(() -> deviceAccess.setDevice(device))
                                )

                );
    }
}
