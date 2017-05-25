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

package com.atypon.wayf.data.device;

import com.atypon.wayf.data.InflationPolicy;

import java.util.Collection;

public class DeviceQuery {
    public static final String SESSIONS = "sessions";

    private String globalId;
    private Collection<String> globalIds;
    private InflationPolicy inflationPolicy;

    public DeviceQuery() {
    }

    public String getGlobalId() {
        return globalId;
    }

    public DeviceQuery setGlobalId(String globalId) {
        this.globalId = globalId;
        return this;
    }

    public Collection<String> getGlobalIds() {
        return globalIds;
    }

    public DeviceQuery setGlobalIds(Collection<String> globalIds) {
        this.globalIds = globalIds;
        return this;
    }

    /**
     * Database use only
     * @return
     */
    public boolean isNullGlobalIds() {
        return globalIds == null;
    }

    public InflationPolicy getInflationPolicy() {
        return inflationPolicy;
    }

    public DeviceQuery setInflationPolicy(InflationPolicy inflationPolicy) {
        this.inflationPolicy = inflationPolicy;
        return this;
    }
}
