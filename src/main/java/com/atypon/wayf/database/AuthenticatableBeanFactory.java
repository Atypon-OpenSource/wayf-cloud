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

import com.atypon.wayf.data.Authenticatable;
import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.data.user.User;

import java.util.Map;

public class AuthenticatableBeanFactory implements BeanFactory<Authenticatable> {
    private static final String TYPE = "type";
    @Override
    public Authenticatable createInstance(Map<String, Object> values) {
        Object type = values.get(TYPE);

        if (Authenticatable.Type.PUBLISHER.toString().equals(type)) {
            return new Publisher();
        } else if (Authenticatable.Type.ADMIN.toString().equals(type)) {
            return new User();
        }

        throw new UnsupportedOperationException();
    }
}
