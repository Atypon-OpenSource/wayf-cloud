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

import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.identity.IdentityProvider;
import com.atypon.wayf.data.publisher.Publisher;

import java.util.Date;

public class DeviceAccess {
    private Long id;

    private String localId;

    private Device device;

    private IdentityProvider identityProvider;

    private Publisher publisher;

    private DeviceAccessType type;

    private Date createdDate;
    private Date modifiedDate;

    public DeviceAccess() {
    }

    public Long getId() {
        return id;
    }

    public DeviceAccess setId(Long id) {
        this.id = id;
        return this;
    }

    public String getLocalId() {
        return localId;
    }

    public DeviceAccess setLocalId(String localId) {
        this.localId = localId;
        return this;
    }

    public Device getDevice() {
        return device;
    }

    public DeviceAccess setDevice(Device device) {
        this.device = device;
        return this;
    }

    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public DeviceAccess setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
        return this;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public DeviceAccess setPublisher(Publisher publisher) {
        this.publisher = publisher;
        return this;
    }

    public DeviceAccessType getType() {
        return type;
    }

    public DeviceAccess setType(DeviceAccessType type) {
        this.type = type;
        return this;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public DeviceAccess setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public DeviceAccess setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
        return this;
    }
}
