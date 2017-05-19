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

package com.atypon.wayf.dao.impl;

import com.atypon.wayf.dao.DeviceIdentityProviderBlacklistDao;
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.identity.IdentityProvider;
import com.atypon.wayf.database.DbExecutor;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DeviceIdentityProviderBlacklistDaoDbImpl implements DeviceIdentityProviderBlacklistDao {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceIdentityProviderBlacklistDaoDbImpl.class);

    private static final String DEVICE_GLOBAL_ID = "device.globalId";
    private static final String IDENTITY_PROVIDER_ID = "identityProvider.id";

    @Inject
    @Named("device-identity-provider-blacklist.dao.db.add")
    private String addToBlacklistSql;

    @Inject
    @Named("device-identity-provider-blacklist.dao.db.remove")
    private String removeFromBlacklistSql;

    @Inject
    @Named("device-identity-provider-blacklist.dao.db.get-blacklisted-idps")
    private String getBlacklistedIdps;

    @Inject
    private DbExecutor dbExecutor;

    @Override
    public Completable add(Device device, IdentityProvider identityProvider) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(DEVICE_GLOBAL_ID, device.getGlobalId());
        arguments.put(IDENTITY_PROVIDER_ID, identityProvider.getId());

        LOG.debug("Adding values to device/idp blacklist [{}]", arguments);

        return dbExecutor.executeUpdate(addToBlacklistSql, arguments).toCompletable();
    }

    @Override
    public Completable remove(Device device, IdentityProvider identityProvider) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(DEVICE_GLOBAL_ID, device.getGlobalId());
        arguments.put(IDENTITY_PROVIDER_ID, identityProvider.getId());

        LOG.debug("Removing values from device/idp blacklist [{}]", arguments);

        return dbExecutor.executeUpdate(removeFromBlacklistSql, arguments).toCompletable();
    }

    @Override
    public Observable<IdentityProvider> getBlacklistedIdentityProviders(Device device) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(DEVICE_GLOBAL_ID, device.getGlobalId());

        LOG.debug("Filtering device/idp blacklist for values [{}]", arguments);

        return dbExecutor.executeSelect(getBlacklistedIdps, arguments, IdentityProvider.class);
    }
}
