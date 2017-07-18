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

import com.atypon.wayf.data.Authenticatable;
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.identity.IdentityProvider;
import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.request.RequestContextAccessor;

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

    public void setId(Long id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public DeviceAccessType getType() {
        return type;
    }

    public void setType(DeviceAccessType type) {
        this.type = type;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public static class Builder {
        private Device device;

        private IdentityProvider identityProvider;

        private Publisher publisher;

        private DeviceAccessType type;

        public Builder device(Device device) {
            this.device = device;
            return this;
        }

        public Builder identityProvider(IdentityProvider identityProvider) {
            this.identityProvider = identityProvider;
            return this;
        }

        public Builder publisher(Publisher publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder type(DeviceAccessType type) {
            this.type = type;
            return this;
        }

        public DeviceAccess build() {
            DeviceAccess deviceAccess = new DeviceAccess();
            deviceAccess.setDevice(device);
            deviceAccess.setIdentityProvider(identityProvider);
            deviceAccess.setPublisher(publisher);
            deviceAccess.setType(type);
            return deviceAccess;
        }
    }

}
