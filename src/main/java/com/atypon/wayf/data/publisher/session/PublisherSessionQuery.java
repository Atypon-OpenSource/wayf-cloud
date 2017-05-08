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

package com.atypon.wayf.data.publisher.session;

import com.atypon.wayf.data.InflationPolicy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PublisherSessionQuery {
    public static final String DEVICE = "device";
    public static final String PUBLISHER_FIELD = "publisher";
    public static final String AUTHENTICATED_BY = "authenticatedBy";

    private String id;

    private String localId;
    private String deviceId;

    private List<String> ids;

    private InflationPolicy inflationPolicy;

    public PublisherSessionQuery() {
    }

    public String getId() {
        return id;
    }

    public PublisherSessionQuery setId(String id) {
        this.id = id;
        return this;
    }

    public String getLocalId() {
        return localId;
    }

    public PublisherSessionQuery setLocalId(String localId) {
        this.localId = localId;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public PublisherSessionQuery setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public InflationPolicy getInflationPolicy() {
        return inflationPolicy;
    }

    public PublisherSessionQuery setInflationPolicy(InflationPolicy inflationPolicy) {
        this.inflationPolicy = inflationPolicy;
        return this;
    }

    public List<String> getIds() {
        return ids;
    }

    public PublisherSessionQuery setIds(List<String> ids) {
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
