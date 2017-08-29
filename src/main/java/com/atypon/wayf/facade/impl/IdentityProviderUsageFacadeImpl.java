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
import com.atypon.wayf.data.identity.IdentityProviderQuery;
import com.atypon.wayf.data.identity.IdentityProviderUsage;
import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.facade.*;
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function4;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Singleton
public class IdentityProviderUsageFacadeImpl implements IdentityProviderUsageFacade {
    private static final Logger LOG = LoggerFactory.getLogger(IdentityProviderUsageFacadeImpl.class);

    private static final Integer DEFAULT_RECENT_HISTORY_CUTOFF = 50;
    private static final Integer DEFAULT_FREQUENCY_SCALE = 4;
    private static final BigDecimal HUNDRED = new BigDecimal(100L);

    @Inject
    private DeviceAccessFacade deviceAccessFacade;

    @Inject
    private DeviceFacade deviceFacade;

    @Inject
    private DeviceIdentityProviderBlacklistFacade idpBlacklistFacade;

    @Inject
    private IdentityProviderFacade identityProviderFacade;

    public void setDeviceAccessFacade(DeviceAccessFacade deviceAccessFacade) {
        this.deviceAccessFacade = deviceAccessFacade;
    }

    public void setIdpBlacklistFacade(DeviceIdentityProviderBlacklistFacade idpBlacklistFacade) {
        this.idpBlacklistFacade = idpBlacklistFacade;
    }

    public void setIdentityProviderFacade(IdentityProviderFacade identityProviderFacade) {
        this.identityProviderFacade = identityProviderFacade;
    }

    @Override
    public Observable<IdentityProviderUsage> buildRecentHistory(String localId) {
        return deviceFacade.readByLocalId(localId)
                .flatMap((device) ->
                        Single.zip(
                                // Build the history
                                Single.just(buildRecentHistory(device)).subscribeOn(Schedulers.io()),

                                // Log this access
                                deviceAccessFacade.create(new DeviceAccess.Builder()
                                        .device(device)
                                        .publisher(Authenticatable.asPublisher(RequestContextAccessor.get().getAuthenticated()))
                                        .type(DeviceAccessType.READ_IDP_HISTORY)
                                        .build()).subscribeOn(Schedulers.io()),

                                // Once both the processes complete, return the history
                                (history, deviceAccess) -> history
                        )
                ).flatMapObservable((history) -> Observable.fromIterable(history));
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
                                getInflatedIdpByIdpId(deviceAccessObservable),

                                // Zip the results of the functions and construct a List of IdentityProviderUsage from them
                                (latestActiveDateByIdpId, idpCountByIdpId, totalCount, inflatedIdpsById) -> BUILD_USAGES.apply(latestActiveDateByIdpId, idpCountByIdpId, totalCount, inflatedIdpsById)
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

    /**
     * Given an Observable stream of DeviceAccess, get the unique IdentityProviders in the stream and their number of occurrences
     */
    private ObservableSource<HashMap<Long, IdentityProvider>> getInflatedIdpByIdpId(Observable<DeviceAccess> deviceAccessObservable) {
        return deviceAccessObservable.collect(
                    () -> new HashSet<Long>(),
                    (idpIds, _deviceAccess) -> {
                        Long idpId = _deviceAccess.getIdentityProvider().getId();

                        idpIds.add(idpId);
                    }
                )
                .flatMapObservable((idpIds) -> idpIds.isEmpty()? Observable.empty() : identityProviderFacade.filter(new IdentityProviderQuery().ids(idpIds)))
                .collect(
                        () -> new HashMap<Long, IdentityProvider>(),
                        (inflatedIdpByIdpId, identityProvider) -> {
                            inflatedIdpByIdpId.put(identityProvider.getId(), identityProvider);
                        }
                )
                .toObservable();
    }
    private static final Function4<HashMap<Long, Date>, HashMap<Long, Integer>, Long, HashMap<Long, IdentityProvider>, List<IdentityProviderUsage>> BUILD_USAGES = (latestActiveDateByIdpId, idpCountByIdpId, totalCount, idpsById) -> {
        Set<Long> idpIds = idpCountByIdpId.keySet();

        List<IdentityProviderUsage> usages = new LinkedList<>();

        BigDecimal totalCountBd = new BigDecimal(totalCount);

        for (Long idpId : idpIds) {
            IdentityProviderUsage usage = new IdentityProviderUsage();
            usage.setLastActiveDate(latestActiveDateByIdpId.get(idpId));

            IdentityProvider identityProvider = idpsById.get(idpId);
            usage.setIdp(identityProvider);

            BigDecimal idpCountBd = new BigDecimal(idpCountByIdpId.get(idpId).intValue());
            Double frequency = idpCountBd.divide(totalCountBd, DEFAULT_FREQUENCY_SCALE, RoundingMode.HALF_DOWN).multiply(HUNDRED).doubleValue();
            usage.setFrequency(frequency);

            usages.add(usage);
        }

        return usages;
    };
}
