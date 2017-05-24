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

    private Collection<Long> deviceIds;
    private DeviceAccessType type;
    private Collection<Long> notIdps;

    private InflationPolicy inflationPolicy;

    private Integer limit;
    private Integer offset;

    public DeviceAccessQuery() {
    }

    public Long getId() {
        return id;
    }

    public DeviceAccessQuery setId(Long id) {
        this.id = id;
        return this;
    }


    public Collection<Long> getDeviceIds() {
        return deviceIds;
    }

    /**
     * Database use only
     * @return
     */
    public boolean isNullDeviceIds() {
        return deviceIds == null;
    }

    public DeviceAccessQuery setDeviceIds(Collection<Long> deviceIds) {
        this.deviceIds = deviceIds;
        return this;
    }

    public DeviceAccessType getType() {
        return type;
    }

    public DeviceAccessQuery setType(DeviceAccessType type) {
        this.type = type;
        return this;
    }

    public Collection<Long> getNotIdps() {
        return notIdps;
    }

    public DeviceAccessQuery setNotIdps(Collection<Long> notIdps) {
        if (notIdps != null && !notIdps.isEmpty()) {
            this.notIdps = notIdps;
        }
        return this;
    }

    public boolean isNullNotIdps() {
        return notIdps == null;
    }

    public InflationPolicy getInflationPolicy() {
        return inflationPolicy;
    }

    public DeviceAccessQuery setInflationPolicy(InflationPolicy inflationPolicy) {
        this.inflationPolicy = inflationPolicy;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public DeviceAccessQuery setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public DeviceAccessQuery setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }
}
