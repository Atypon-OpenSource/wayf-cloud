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

import com.atypon.wayf.data.AuthorizationToken;
import com.atypon.wayf.data.AuthorizationTokenType;
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

        publisher.setCredentials(token);

        String serializedPublisher = AuthenticatableRedisSerializer.serialize(publisher);
        Publisher deserializedPublisher = (Publisher) AuthenticatableRedisSerializer.deserialize(serializedPublisher);

        assertEquals(publisher.getId(), deserializedPublisher.getId());
        assertEquals(publisher.getCredentials().getType(), deserializedPublisher.getCredentials().getType());
        assertEquals(publisher.getCredentials().getValue(), deserializedPublisher.getCredentials().getValue());

    }
}