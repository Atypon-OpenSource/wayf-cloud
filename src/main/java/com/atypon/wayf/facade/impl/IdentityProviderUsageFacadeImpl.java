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

import com.atypon.wayf.data.Authenticatable;
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.device.access.DeviceAccess;
import com.atypon.wayf.data.device.access.DeviceAccessQuery;
import com.atypon.wayf.data.device.access.DeviceAccessType;
import com.atypon.wayf.data.identity.IdentityProvider;
import com.atypon.wayf.data.identity.IdentityProviderUsage;
import com.atypon.wayf.facade.DeviceAccessFacade;
import com.atypon.wayf.facade.DeviceFacade;
import com.atypon.wayf.facade.DeviceIdentityProviderBlacklistFacade;
import com.atypon.wayf.facade.IdentityProviderUsageFacade;
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Singleton
public class IdentityProviderUsageFacadeImpl implements IdentityProviderUsageFacade {
    private static final Integer DEFAULT_RECENT_HISTORY_CUTOFF = 50;
    private static final Integer DEFAULT_FREQUENCY_SCALE = 4;
    private static final BigDecimal HUNDRED = new BigDecimal(100L);

    @Inject
    private DeviceAccessFacade deviceAccessFacade;

    @Inject
    private DeviceFacade deviceFacade;

    @Inject
    private DeviceIdentityProviderBlacklistFacade idpBlacklistFacade;

    public void setDeviceAccessFacade(DeviceAccessFacade deviceAccessFacade) {
        this.deviceAccessFacade = deviceAccessFacade;
    }

    public void setIdpBlacklistFacade(DeviceIdentityProviderBlacklistFacade idpBlacklistFacade) {
        this.idpBlacklistFacade = idpBlacklistFacade;
    }

    @Override
    public Observable<IdentityProviderUsage> buildRecentHistory(String localId) {
        return deviceFacade.readByLocalId(localId)
                .flatMap((device) ->
                        Single.zip(
                                // Build the history
                                Single.just(buildRecentHistory(device)).subscribeOn(Schedulers.io()),

                                // Log this access
                                deviceAccessFacade.create(new DeviceAccess()
                                        .setDevice(device)
                                        .setPublisher(Authenticatable.asPublisher(RequestContextAccessor.get().getAuthenticated()))
                                        .setLocalId(localId)
                                        .setType(DeviceAccessType.READ_IDP_HISTORY)).subscribeOn(Schedulers.io()),

                                // Once both the processes complete, return the history
                                (history, deviceAccess) -> history
                        )
                ).flatMapObservable((history) -> Observable.fromIterable(history));
    }

    private DeviceAccess createAccess(Device device, String localId, DeviceAccessType type) {
        return new DeviceAccess()
                .setDevice(device)
                .setPublisher(Authenticatable.asPublisher(RequestContextAccessor.get().getAuthenticated()))
                .setLocalId(localId)
                .setType(type);
    }

    @Override
    public List<IdentityProviderUsage> buildRecentHistory(Device device) {
        List<Long> blacklistedIdpIds = idpBlacklistFacade.getBlacklistedIdentityProviders(device)
                .map((identityProvider) -> identityProvider.getId())
                .toList()
                .blockingGet();

        DeviceAccessQuery deviceAccessQuery = new DeviceAccessQuery()
                .setNotIdps(blacklistedIdpIds)
                .setDeviceIds(Lists.newArrayList(device.getId()))
                .setType(DeviceAccessType.ADD_IDP)
                .setLimit(DEFAULT_RECENT_HISTORY_CUTOFF);

        List<IdentityProviderUsage> deviceAccess = deviceAccessFacade.filter(deviceAccessQuery)
                .publish(deviceAccessObservable ->
                        Observable.zip(
                                // Apply various functions to the DeviceAccess stream
                                getLatestActiveDateForIdpId(deviceAccessObservable),
                                getIdpCountByIdpId(deviceAccessObservable),
                                getTotalAccessCount(deviceAccessObservable),

                                // Zip the results of the functions and construct a List of IdentityProviderUsage from them
                                (latestActiveDateByIdpId, idpCountByIdpId, totalCount) -> BUILD_USAGES.apply(latestActiveDateByIdpId, idpCountByIdpId, totalCount)
                        )
                ).blockingFirst();

        // Sort them by their frequency (highest first)
        deviceAccess.sort((o1, o2) -> o2.getFrequency().compareTo(o1.getFrequency()));

        return deviceAccess;
    }

    /**
     * Given an Observable stream of DeviceAccess, get the unique IdentityProviders in the stream and their latest created date
     */
    private ObservableSource<HashMap<Long, Date>> getLatestActiveDateForIdpId(Observable<DeviceAccess> deviceAccessObservable) {
        return deviceAccessObservable.collect(
                () -> new HashMap<Long, Date>(),
                (latestActiveDateByIdpId, _deviceAccess) -> {
                    Long idpId = _deviceAccess.getIdentityProvider().getId();

                    Date deviceAccessDate = _deviceAccess.getCreatedDate();
                    Date latestActiveDate = latestActiveDateByIdpId.get(idpId);

                    if (latestActiveDate == null) {
                        latestActiveDateByIdpId.put(idpId, deviceAccessDate);
                    } else if (latestActiveDate.after(deviceAccessDate)) {
                        latestActiveDateByIdpId.put(idpId, latestActiveDate);
                    } else {
                        latestActiveDateByIdpId.put(idpId, deviceAccessDate);
                    }

                })
                .toObservable();
    }

    /**
     * Given an Observable stream of DeviceAccess, get the unique IdentityProviders in the stream and their number of occurrences
     */
    private ObservableSource<HashMap<Long, Integer>> getIdpCountByIdpId(Observable<DeviceAccess> deviceAccessObservable) {
        return deviceAccessObservable.collect(
                () -> new HashMap<Long, Integer>(),
                (idpCountById, _deviceAccess) -> {
                    Long idpId = _deviceAccess.getIdentityProvider().getId();

                    Integer count = idpCountById.get(idpId);
                    if (count == null) {
                        idpCountById.put(idpId, 1);
                    } else {
                        idpCountById.put(idpId, count+1);
                    }
                })
                .toObservable();
    }

    /**
     * Given an Observable stream of DeviceAccess, count the total number of elements in the stream
     */
    private ObservableSource<Long> getTotalAccessCount(Observable<DeviceAccess> deviceAccessObservable) {
        return deviceAccessObservable.count().toObservable();
    }

    private static final Function3<HashMap<Long, Date>, HashMap<Long, Integer>, Long, List<IdentityProviderUsage>> BUILD_USAGES = (latestActiveDateByIdpId, idpCountByIdpId, totalCount) -> {
        Set<Long> idpIds = idpCountByIdpId.keySet();

        List<IdentityProviderUsage> usages = new LinkedList<>();

        BigDecimal totalCountBd = new BigDecimal(totalCount);

        for (Long idpId : idpIds) {
            IdentityProviderUsage usage = new IdentityProviderUsage();
            usage.setLastActiveDate(latestActiveDateByIdpId.get(idpId));

            IdentityProvider identityProvider = new IdentityProvider();
            identityProvider.setId(idpId);
            usage.setIdp(identityProvider);

            BigDecimal idpCountBd = new BigDecimal(idpCountByIdpId.get(idpId).intValue());
            Double frequency = idpCountBd.divide(totalCountBd, DEFAULT_FREQUENCY_SCALE, RoundingMode.HALF_DOWN).multiply(HUNDRED).doubleValue();
            usage.setFrequency(frequency);

            usages.add(usage);
        }

        return usages;
    };
}
