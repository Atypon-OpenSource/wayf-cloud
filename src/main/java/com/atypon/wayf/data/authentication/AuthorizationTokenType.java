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

package com.atypon.wayf.data.authentication;

import java.util.HashMap;
import java.util.Map;

public enum AuthorizationTokenType {
    API_TOKEN("Token"),
    JWT("Bearer");

    private static final Map<String, AuthorizationTokenType> PREFIX_TO_TYPE_MAP = new HashMap<>();

    static {
        for (AuthorizationTokenType type : AuthorizationTokenType.values()) {
            PREFIX_TO_TYPE_MAP.put(type.getPrefix(), type);
        }
    }

    private String prefix;

    AuthorizationTokenType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public static AuthorizationTokenType fromPrefix(String prefix) {
        return PREFIX_TO_TYPE_MAP.get(prefix);
    }
}
