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

import com.atypon.wayf.dao.DeviceDao;
import com.atypon.wayf.data.Authenticatable;
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.device.DeviceInfo;
import com.atypon.wayf.data.device.DeviceQuery;
import com.atypon.wayf.data.device.DeviceStatus;
import com.atypon.wayf.data.device.access.DeviceAccess;
import com.atypon.wayf.data.device.access.DeviceAccessQuery;
import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.facade.DeviceAccessFacade;
import com.atypon.wayf.facade.DeviceFacade;
import com.atypon.wayf.reactivex.FacadePolicies;
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static com.atypon.wayf.reactivex.FacadePolicies.onError404;

@Singleton
public class DeviceFacadeImpl implements DeviceFacade {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceFacadeImpl.class);

    @Inject
    private DeviceDao deviceDao;

    @Inject
    private DeviceAccessFacade deviceAccessFacade;

    public DeviceFacadeImpl() {
    }

    @Override
    public Single<Device> create(Device device) {
        LOG.debug("Creating device [{}]", device);

        device.setStatus(DeviceStatus.ACTIVE);

        // Generate a global ID
        device.setGlobalId(UUID.randomUUID().toString());

        DeviceInfo info = device.getInfo();
        if (info == null) {
            info = new DeviceInfo();
            device.setInfo(info);
        }

        // Set the user to that of the request
        info.setUserAgent(RequestContextAccessor.get().getUserAgent());

        // Create the device
        return deviceDao.create(device);
    }

    @Override
    public Single<Device> read(DeviceQuery query) {
        LOG.debug("Reading device with query [{}]", query);

        return deviceDao.read(query)
                .toSingle()
                .flatMap((device) -> populate(device, query).toSingle(() -> device));
    }

    @Override
    public Observable<Device> filter(DeviceQuery query) {
        LOG.debug("Filtering for devices that match query [{}]", query);

        return deviceDao.filter(query) // Filter for devices that match the query

                // Collect the elements into a list so that the call to populate is batched
                .toList()

                .flatMapObservable((devices) ->

                        // Populate the required fields on devices
                        populate(devices, query)

                                // Convert the completable result to an Observable and fill it with the devices so they can be returned
                                .toObservable()
                                .cast(Device.class)
                                .concatWith(Observable.fromIterable(devices)));
    }

    @Override
    public Single<Device> createOrUpdateForPublisher(String localId) {
        return resolveFromRequest(localId) // Read the device contained in the request or create a new one

                // Apply the following logic to the device
                .flatMap((device) -> {

                    // Get the currently authenticated publisher
                    Publisher publisher = Authenticatable.asPublisher(RequestContextAccessor.get().getAuthenticated());


                    // Create an XREF linking the publisher and local ID to the device
                    return deviceDao.replaceDevicePublisherLocalIdXref(device.getId(), publisher.getId(), localId)

                            // Since the above method has no return value, return the resolved device on completion
                            .toSingleDefault(device);
                });
    }

    private Single<Device> resolveFromRequest(String localId) {
        String deviceGlobalId = RequestContextAccessor.get().getDeviceId();

        if (deviceGlobalId != null) {
            return read(new DeviceQuery().setGlobalId(deviceGlobalId));
        }

        Publisher publisher = Authenticatable.asPublisher(RequestContextAccessor.get().getAuthenticated());

        // If the localId has already been used, return the device associated with it. Otherwise, create a new Device
        return deviceDao.readByPublisherLocalId(publisher.getId(), localId)
                .concatWith(create(new Device()).toMaybe())
                .firstOrError();
    }

    @Override
    public Single<Device> readByLocalId(String localId) {
        LOG.debug("Reading device with local ID [{}]", localId);

        Publisher publisher = Authenticatable.asPublisher(RequestContextAccessor.get().getAuthenticated());

        LOG.debug("Reading device with local ID [{}] and publisher ID [{}]", localId, publisher.getId());

        return deviceDao.readByPublisherLocalId(publisher.getId(), localId)

                // Add in Standard 404 error on no element
                .compose((maybe) -> onError404(maybe,  "Could not find device associated with localId [{}]", localId))

                .toSingle();
    }

    private Completable populate(Device device, DeviceQuery query) {
        return populate(Lists.newArrayList(device), query);
    }

    private Completable populate(Iterable<Device> devices, DeviceQuery query) {
        // Run the inflations in parallel
        return inflateSessions(Lists.newArrayList(devices), query)
                .compose((completable) -> FacadePolicies.applyCompletable(completable));
    }

    private Completable inflateSessions(Iterable<Device> devices, DeviceQuery query) {
        // Return as complete if authenticatedBy is not a requested field
        if (query.getInflationPolicy() == null || !query.getInflationPolicy().hasChildField(DeviceQuery.SESSIONS)) {
            return Completable.complete();
        }

        Multimap<Long, Device> devicesById = HashMultimap.create();

        return Observable.fromIterable(devices)
                 // Collect all of the publisher sessions and their identity provider IDs into a map
                .collectInto(devicesById, (map, device) -> map.put(device.getId(), device))

                // Fetch all of the publishers for those publisher IDs
                .flatMapObservable((map) -> map.keySet().isEmpty()? Observable.empty() : deviceAccessFacade.filter(new DeviceAccessQuery().setDeviceIds(map.keySet())))

                // For each identity provider returned, map it to each publisher session that had its ID
                .flatMapCompletable((deviceAccess) ->
                        Observable.fromIterable(devicesById.get(deviceAccess.getDevice().getId()))
                                .flatMapCompletable((device) ->
                                        Completable.fromAction(() -> {
                                            List<DeviceAccess> activity = device.getActivity();

                                            if (activity == null) {
                                                activity = new LinkedList<>();
                                                device.setActivity(activity);
                                            }

                                            activity.add(deviceAccess);
                                        })
                                )
                );
    }
}