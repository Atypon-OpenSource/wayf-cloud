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
import com.atypon.wayf.data.publisher.Publisher;
import org.json.JSONObject;

public class AuthenticatableRedisSerializer {

    public static String serialize(Authenticatable authenticatable) {
        JSONObject object = new JSONObject();
        object.put("type", authenticatable.getType());
        object.put("id", authenticatable.getId());

        return object.toString();
    }

    public static Authenticatable deserialize(String json) {
        JSONObject object = new JSONObject(json);

        Authenticatable.Type type = object.getEnum(Authenticatable.Type.class, "type");

        Authenticatable authenticatable = null;
        if (Authenticatable.Type.PUBLISHER == type) {
            authenticatable = new Publisher();
        }

        authenticatable.setId(object.getLong("id"));

        return authenticatable;
    }
}
