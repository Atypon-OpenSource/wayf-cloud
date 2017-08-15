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

import com.atypon.wayf.data.authentication.AuthenticatedEntity;
import com.atypon.wayf.data.authentication.AuthorizationToken;
import com.atypon.wayf.data.authentication.AuthorizationTokenType;
import com.atypon.wayf.data.publisher.Publisher;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AuthenticatableRedisSerializerTest {

    @Test
    public void testPublisher() {
        Publisher publisher = new Publisher();
        publisher.setId(123L);

        AuthorizationToken token = new AuthorizationToken();
        token.setValue("ab123");
        token.setType(AuthorizationTokenType.API_TOKEN);

        AuthenticatedEntity authenticatedEntity = new AuthenticatedEntity();
        authenticatedEntity.setCredentials(token);
        authenticatedEntity.setAuthenticatable(publisher);

        String serializedAuthenticatedEntity = AuthenticatableRedisSerializer.serialize(authenticatedEntity);
        AuthenticatedEntity deserializedAuthenticatedEntity = AuthenticatableRedisSerializer.deserialize(serializedAuthenticatedEntity);

        assertEquals(authenticatedEntity.getAuthenticatable().getId(), deserializedAuthenticatedEntity.getAuthenticatable().getId());
        assertEquals(((AuthorizationToken) authenticatedEntity.getCredentials()).getType(), ((AuthorizationToken) deserializedAuthenticatedEntity.getCredentials()).getType());
        assertEquals(((AuthorizationToken) authenticatedEntity.getCredentials()).getValue(), ((AuthorizationToken) deserializedAuthenticatedEntity.getCredentials()).getValue());

    }
}