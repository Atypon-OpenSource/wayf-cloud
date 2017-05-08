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
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.device.DeviceInfo;
import com.atypon.wayf.data.device.DeviceQuery;
import com.atypon.wayf.data.device.DeviceStatus;
import com.atypon.wayf.facade.DeviceFacade;
import com.atypon.wayf.request.RequestContextAccessor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Singleton
public class DeviceFacadeImpl implements DeviceFacade {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceFacadeImpl.class);

    @Inject
    private DeviceDao deviceDao;

    public DeviceFacadeImpl() {
    }

    @Override
    public Single<Device> create(Device device) {
        LOG.debug("Creating device [{}]", device);

        device.setStatus(DeviceStatus.ACTIVE);
        device.setId(UUID.randomUUID().toString());

        DeviceInfo info = device.getInfo();

        if (info == null) {
            info = new DeviceInfo();
            device.setInfo(info);
        }

        info.setUserAgent(RequestContextAccessor.get().getUserAgent());

        return deviceDao.create(device);
    }

    @Override
    public Single<Device> read(String id) {
        LOG.debug("Reading device with id [{}]", id);
        return Single.just(id)
                .observeOn(Schedulers.io())
                .flatMapMaybe((_id) -> deviceDao.read(_id))
                .toSingle();
    }

    @Override
    public Observable<Device> filter(DeviceQuery query) {
        return Observable.just(query)
                .flatMap((_query) -> deviceDao.filter(_query));
    }
}