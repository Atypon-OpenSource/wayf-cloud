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

package com.atypon.wayf.facade.impl;

import com.atypon.wayf.data.Authenticatable;
import com.atypon.wayf.data.AuthorizationToken;
import com.atypon.wayf.data.AuthorizationTokenType;
import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.data.publisher.Publisher;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class AuthenticatableRedisSerializer {
    private enum AuthenticatableFields {
        ID("id"),
        TYPE("type"),
        AUTHORIZATION_TOKEN("authorizationToken");

        private String fieldName;

        AuthenticatableFields(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }
    }

    private enum AuthorizationTokenFields {
        TOKEN_TYPE("tokenType"),
        TOKEN_VALUE("tokenValue"),
        VALID_UNTIL("validUntil");

        private String fieldName;

        AuthorizationTokenFields(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }
    }

    public static String serialize(Authenticatable authenticatable) {
        JSONObject authenticatableJsonObject = new JSONObject();
        authenticatableJsonObject.put(AuthenticatableFields.TYPE.getFieldName(), authenticatable.getType());
        authenticatableJsonObject.put(AuthenticatableFields.ID.getFieldName(), authenticatable.getId());

        if (authenticatable.getAuthorizationToken() == null) {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "An AuthorizationToken is expected");
        }

        JSONObject authorizationTokenJsonObject = new JSONObject();
        authorizationTokenJsonObject.put(AuthorizationTokenFields.TOKEN_TYPE.getFieldName(), authenticatable.getAuthorizationToken().getType().toString());
        authorizationTokenJsonObject.put(AuthorizationTokenFields.TOKEN_VALUE.getFieldName(), authenticatable.getAuthorizationToken().getValue());

        if (authenticatable.getAuthorizationToken().getValidUntil() != null) {
            authorizationTokenJsonObject.put(AuthorizationTokenFields.VALID_UNTIL.getFieldName(), authenticatable.getAuthorizationToken().getValidUntil().getTime());
        }

        authenticatableJsonObject.put(AuthenticatableFields.AUTHORIZATION_TOKEN.getFieldName(), authorizationTokenJsonObject);

        return authenticatableJsonObject.toString();
    }

    public static Authenticatable deserialize(String json) {
        JSONObject authenticatableJsonObject = new JSONObject(json);

        Authenticatable.Type type = authenticatableJsonObject.getEnum(Authenticatable.Type.class, AuthenticatableFields.TYPE.getFieldName());

        Authenticatable authenticatable = null;
        if (Authenticatable.Type.PUBLISHER == type) {
            authenticatable = new Publisher();
        }

        authenticatable.setId(authenticatableJsonObject.getLong(AuthenticatableFields.ID.getFieldName()));

        JSONObject authorizationJsonObject = authenticatableJsonObject.getJSONObject(AuthenticatableFields.AUTHORIZATION_TOKEN.getFieldName());

        AuthorizationToken authorizationToken = new AuthorizationToken();
        authorizationToken.setType(AuthorizationTokenType.valueOf(authorizationJsonObject.getString(AuthorizationTokenFields.TOKEN_TYPE.getFieldName())));
        authorizationToken.setValue(authorizationJsonObject.getString(AuthorizationTokenFields.TOKEN_VALUE.getFieldName()));

        try {
            authorizationToken.setValidUntil(new Date(authorizationJsonObject.getLong(AuthorizationTokenFields.VALID_UNTIL.getFieldName())));
        } catch (JSONException e) {
            // Thrown if validUntil does not exist which is valid for publisher tokens
        }
        authenticatable.setAuthorizationToken(authorizationToken);

        return authenticatable;
    }
}
