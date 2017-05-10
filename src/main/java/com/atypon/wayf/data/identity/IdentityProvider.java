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

package com.atypon.wayf.data.identity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.*;

@JsonTypeInfo(
        use = Id.NAME,
        include = As.PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @Type(value = OpenAthensEntity.class, name = "OPEN_ATHENS"),
        @Type(value = SamlEntity.class, name = "SAML")
})
public class IdentityProvider {
    private String id;
    private String entityId;

    private Date createdDate;
    private Date modifiedDate;

    public IdentityProvider() {
    }

    public String getId() {
        return id;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setId(String id) {
        this.id = id;
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

    public IdentityProviderType getType() {
        return null;
    }
}
