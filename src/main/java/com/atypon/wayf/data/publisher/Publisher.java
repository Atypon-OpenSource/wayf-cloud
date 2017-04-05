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

package com.atypon.wayf.data.publisher;

import com.atypon.wayf.data.IdentityProvider;
import com.atypon.wayf.data.device.Platform;
import com.atypon.wayf.data.user.User;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Date;
import java.util.List;

@NodeEntity
public class Publisher {
    private String id;
    private PublisherStatus status;

    private String name;

    @Relationship(type="OPERATES_ON")
    private Platform platform;

    @Relationship(type="AUTHENTICATION_PROVIDED_BY")
    private List<IdentityProvider> identityProviders;

    @Relationship(type="ADMINISTERS", direction = Relationship.INCOMING)
    private List<User> admins;

    private Date createdDate;
    private Date modifiedDate;

    public Publisher() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PublisherStatus getStatus() {
        return status;
    }

    public void setStatus(PublisherStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public List<IdentityProvider> getIdentityProviders() {
        return identityProviders;
    }

    public void setIdentityProviders(List<IdentityProvider> identityProviders) {
        this.identityProviders = identityProviders;
    }

    public List<User> getAdmins() {
        return admins;
    }

    public void setAdmins(List<User> admins) {
        this.admins = admins;
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
