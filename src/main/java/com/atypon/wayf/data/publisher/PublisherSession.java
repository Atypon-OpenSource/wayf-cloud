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
import com.atypon.wayf.data.device.Device;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Date;

@NodeEntity
public class PublisherSession {
    private String id;

    private PublisherSessionStatus status;

    @Relationship(type="AUTHENTICATED_BY")
    private IdentityProvider idp;

    @Relationship(type="VALID_FOR")
    private Publisher publisher;

    private Date lastActiveDate;

    private Date createdDate;
    private Date modifiedDate;

    public PublisherSession() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PublisherSessionStatus getStatus() {
        return status;
    }

    public void setStatus(PublisherSessionStatus status) {
        this.status = status;
    }

    public IdentityProvider getIdp() {
        return idp;
    }

    public void setIdp(IdentityProvider idp) {
        this.idp = idp;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Date getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(Date lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
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
