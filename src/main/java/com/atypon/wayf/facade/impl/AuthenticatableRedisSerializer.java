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

import com.atypon.wayf.data.*;
import com.atypon.wayf.data.publisher.Publisher;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class AuthenticatableRedisSerializer {
    private static final String EMAIL_PASSWORD_CREDENTIALS = "emailPassword";
    private static final String AUTHORIZATION_TOKEN_CREDENTIALS = "authorizationToken";

    private enum AuthenticatableFields {
        ID("id"),
        TYPE("type"),
        CREDENTIALS_TYPE("credentials_type"),
        CREDENTIALS("credentials");

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

    private enum EmailPasswordCredentialsFields {
        EMAIL("email"),
        PASSWORD("password");

        private String fieldName;

        EmailPasswordCredentialsFields(String fieldName) {
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

        if (authenticatable.getCredentials() == null) {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "An AuthorizationToken is expected");
        }

        JSONObject credentials = null;

        if (EmailPasswordCredentials.class.isAssignableFrom(authenticatable.getCredentials().getClass())) {
            credentials = serializeEmail((EmailPasswordCredentials) authenticatable.getCredentials());
            authenticatableJsonObject.put(AuthenticatableFields.CREDENTIALS_TYPE.getFieldName(), EMAIL_PASSWORD_CREDENTIALS);
        } else if (AuthorizationToken.class.isAssignableFrom(authenticatable.getCredentials().getClass())){
            credentials = serializeToken((AuthorizationToken) authenticatable.getCredentials());
            authenticatableJsonObject.put(AuthenticatableFields.CREDENTIALS_TYPE.getFieldName(), AUTHORIZATION_TOKEN_CREDENTIALS);

        } else {
            throw new ServiceException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Could not serialize autenticatable");
        }

        authenticatableJsonObject.put(AuthenticatableFields.CREDENTIALS.getFieldName(), credentials);

        return authenticatableJsonObject.toString();
    }

    private static JSONObject serializeEmail(EmailPasswordCredentials credentials) {
        JSONObject emailJsonObject = new JSONObject();
        emailJsonObject.put(EmailPasswordCredentialsFields.EMAIL.getFieldName(), credentials.getEmailAddress());
        emailJsonObject.put(EmailPasswordCredentialsFields.PASSWORD.getFieldName(), credentials.getPassword());

        return emailJsonObject;

    }

    private static JSONObject serializeToken(AuthorizationToken token) {
        JSONObject authorizationTokenJsonObject = new JSONObject();
        authorizationTokenJsonObject.put(AuthorizationTokenFields.TOKEN_TYPE.getFieldName(), token.getType().toString());
        authorizationTokenJsonObject.put(AuthorizationTokenFields.TOKEN_VALUE.getFieldName(), token.getValue());

        if (token.getValidUntil() != null) {
            authorizationTokenJsonObject.put(AuthorizationTokenFields.VALID_UNTIL.getFieldName(), token.getValidUntil().getTime());
        }

        return authorizationTokenJsonObject;
    }



    public static Authenticatable deserialize(String json) {
        JSONObject authenticatableJsonObject = new JSONObject(json);

        Authenticatable.Type type = authenticatableJsonObject.getEnum(Authenticatable.Type.class, AuthenticatableFields.TYPE.getFieldName());

        Authenticatable authenticatable = null;
        if (Authenticatable.Type.PUBLISHER == type) {
            authenticatable = new Publisher();
        }

        authenticatable.setId(authenticatableJsonObject.getLong(AuthenticatableFields.ID.getFieldName()));

        String credentialsType = authenticatableJsonObject.getString(AuthenticatableFields.CREDENTIALS_TYPE.getFieldName());
        JSONObject credentialsJsonObject = authenticatableJsonObject.getJSONObject(AuthenticatableFields.CREDENTIALS.getFieldName());

        AuthenticationCredentials credentials = null;

        if (EMAIL_PASSWORD_CREDENTIALS.equals(credentialsType)) {
            credentials = deserializeEmailPassword(credentialsJsonObject);
        } else if (AUTHORIZATION_TOKEN_CREDENTIALS.equals(credentialsType)) {
            credentials = deserializeAuthorizationToken(credentialsJsonObject);
        } else {
            throw new ServiceException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Could not deserialize authenticatble");
        }

        authenticatable.setCredentials(credentials);

        return authenticatable;
    }

    private static AuthenticationCredentials deserializeEmailPassword(JSONObject credentials) {
        EmailPasswordCredentials emailPasswordCredentials = new EmailPasswordCredentials();
        emailPasswordCredentials.setEmailAddress(EmailPasswordCredentialsFields.EMAIL.getFieldName());
        emailPasswordCredentials.setPassword(credentials.getString(EmailPasswordCredentialsFields.PASSWORD.getFieldName()));

        return emailPasswordCredentials;
    }

    private static AuthenticationCredentials deserializeAuthorizationToken(JSONObject credentials) {
        AuthorizationToken authorizationToken = new AuthorizationToken();
        authorizationToken.setType(AuthorizationTokenType.valueOf(credentials.getString(AuthorizationTokenFields.TOKEN_TYPE.getFieldName())));
        authorizationToken.setValue(credentials.getString(AuthorizationTokenFields.TOKEN_VALUE.getFieldName()));

        try {
            authorizationToken.setValidUntil(new Date(credentials.getLong(AuthorizationTokenFields.VALID_UNTIL.getFieldName())));
        } catch (JSONException e) {
            // Thrown if validUntil does not exist which is valid for publisher tokens
        }

        return authorizationToken;
    }

}
