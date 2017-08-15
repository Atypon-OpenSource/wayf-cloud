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

package com.atypon.wayf.integration;

import com.atypon.wayf.data.AuthorizationToken;
import com.atypon.wayf.data.AuthorizationTokenType;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;

public class AuthorizationTokenTestUtil {
    private static final String SECRET_JWT_KEY = "shh_its_a_secret";
    private static final String DEFAULT_ADMIN_TOKEN = "DEFAULT_PLEASE_CHANGE";

    public static String generateDefaultApiTokenHeaderValue() {
        return AuthorizationTokenType.API_TOKEN.getPrefix() + " " + DEFAULT_ADMIN_TOKEN;
    }

    public static String generateApiTokenHeaderValue(AuthorizationToken authorizationToken) {
        return authorizationToken.getType().getPrefix() + " " + authorizationToken.getValue();
    }

    public static String generateJwtTokenHeaderValue(String publisherCode) {
        Algorithm algorithm = null;

        try {
            algorithm = Algorithm.HMAC256(SECRET_JWT_KEY);

            String token = JWT.create()
                    .withClaim("publisherCode", publisherCode)
                    .sign(algorithm);

            return AuthorizationTokenType.JWT.getPrefix() + " " + token;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
