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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class AuthorizationToken implements ExpiringAuthenticationCredentials {

    @JsonIgnore
    private Authenticatable authenticatable;
    private AuthorizationTokenType type;
    private String value;
    private Date validUntil;

    public AuthorizationToken() {
    }

    @Override
    public Authenticatable getAuthenticatable() {
        return authenticatable;
    }

    @Override
    public void setAuthenticatable(Authenticatable authenticatable) {
        this.authenticatable = authenticatable;
    }

    public AuthorizationTokenType getType() {
        return type;
    }

    public void setType(AuthorizationTokenType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Date getValidUntil() {
        return validUntil;
    }

    @Override
    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }
}
