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

package com.atypon.wayf.database;

import com.atypon.wayf.data.*;
import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.data.user.User;
import com.google.inject.Singleton;

import java.util.Map;

@Singleton
public class AuthenticationCredentialsBeanFactory implements BeanFactory<AuthenticationCredentials> {
    private static final String TYPE = "credentials.type";

    @Override
    public AuthenticationCredentials createInstance(Map<String, Object> values) {
        String type = values.get(TYPE).toString();

        AuthenticationCredentials credentials = null;
        if (AuthorizationTokenType.API_TOKEN.toString().equals(type)) {
            AuthorizationToken token = new AuthorizationToken();
            token.setType(AuthorizationTokenType.API_TOKEN);

            credentials = token;
        } else if (AuthorizationTokenType.JWT.toString().equals(type)) {
            AuthorizationToken token = new AuthorizationToken();
            token.setType(AuthorizationTokenType.JWT);

            credentials = token;
        } else if (PasswordCredentials.PASSWORD_CREDETIALS_TYPE.equals(type)) {
            credentials = new PasswordCredentials();
        } else {
            throw new UnsupportedOperationException("Invalid credentials type");
        }

        return credentials;
    }
}
