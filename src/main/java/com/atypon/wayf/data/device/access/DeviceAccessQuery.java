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

package com.atypon.wayf.data.device.access;

import com.atypon.wayf.data.InflationPolicy;

import java.util.Collection;
import java.util.List;

public class DeviceAccessQuery {
    public static final String DEVICE = "device";
    public static final String PUBLISHER_FIELD = "publisher";
    public static final String AUTHENTICATED_BY = "authenticatedBy";

    private Long id;

    private String localId;

    private Collection<String> deviceIds;
    private List<Long> ids;

    private InflationPolicy inflationPolicy;

    public DeviceAccessQuery() {
    }

    public Long getId() {
        return id;
    }

    public DeviceAccessQuery setId(Long id) {
        this.id = id;
        return this;
    }

    public String getLocalId() {
        return localId;
    }

    public DeviceAccessQuery setLocalId(String localId) {
        this.localId = localId;
        return this;
    }

    public Collection<String> getDeviceIds() {
        return deviceIds;
    }

    /**
     * Database use only
     * @return
     */
    public boolean isNullDeviceIds() {
        return deviceIds == null;
    }

    public DeviceAccessQuery setDeviceIds(Collection<String> deviceIds) {
        this.deviceIds = deviceIds;
        return this;
    }

    public InflationPolicy getInflationPolicy() {
        return inflationPolicy;
    }

    public DeviceAccessQuery setInflationPolicy(InflationPolicy inflationPolicy) {
        this.inflationPolicy = inflationPolicy;
        return this;
    }

    public List<Long> getIds() {
        return ids;
    }

    public DeviceAccessQuery setIds(List<Long> ids) {
        this.ids = ids;
        return this;
    }

    /**
     * Database use only
     * @return
     */
    public boolean isNullIds() {
        return ids == null;
    }
}
