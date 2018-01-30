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

import com.atypon.wayf.data.device.access.DeviceAccess;
import com.atypon.wayf.data.identity.IdentityProviderUsage;

import java.util.Date;
import java.util.List;

public class Device {
    private Long id;
    private String globalId;
    private DeviceStatus status;

    private List<IdentityProviderUsage> history;
    private List<DeviceAccess> activity;

    private Date createdDate;
    private Date modifiedDate;

    public Device() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

    public List<IdentityProviderUsage> getHistory() {
        return history;
    }

    public void setHistory(List<IdentityProviderUsage> history) {
        this.history = history;
    }

    public List<DeviceAccess> getActivity() {
        return activity;
    }

    public void setActivity(List<DeviceAccess> activity) {
        this.activity = activity;
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
}
