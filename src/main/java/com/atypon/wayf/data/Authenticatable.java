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

package com.atypon.wayf.data;

import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.data.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.http.HttpStatus;

/*
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Publisher.class, name = "PUBLISHER"),
        @JsonSubTypes.Type(value = User.class, name = "ADMIN")
})*/
public interface Authenticatable {
    enum Type {
        PUBLISHER,
        ADMIN
    }

    void setId(Long id);
    Long getId();

    @JsonIgnore
    Type getType();
    void setType(Type type);

    static Publisher asPublisher(Authenticatable authenticatable) {
        if (Publisher.class.isAssignableFrom(authenticatable.getClass())) {
            return (Publisher) authenticatable;
        }

        throw new ServiceException(HttpStatus.SC_UNAUTHORIZED, "An authenticated Publisher is required");
    }
}
