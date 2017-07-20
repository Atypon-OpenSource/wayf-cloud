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

import com.atypon.wayf.data.AuthorizationTokenType;
import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.facade.ClientJsFacade;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Single;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Pattern;

@Singleton
public class ClientJsFacadeImpl implements ClientJsFacade {
    private static final String WIDGET_OUTPUT_PATH_PREFIX = "public/widget_";
    private static final String WIDGET_OUPUT_SUFFIX = ".js";
    private static final String TEMPLATE_FILE = "wayf_widget_template.js";
    private static final Pattern TOKEN_PATTERN = Pattern.compile("@PUBLISHER_JWT@", Pattern.DOTALL);

    private static String templateFile;

    @Inject
    @Named("jwtSecret")
    private String jwtSecret;

    public void initTemplateFile() {
        if (templateFile == null) {
            try {
                templateFile = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(TEMPLATE_FILE), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new ServiceException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Could not read widget tempalte file", e);
            }
        }
    }

    @Override
    public Single<String> generateWidgetForPublisher(Publisher publisher) {
        return Single.fromCallable(() -> {
            initTemplateFile();

            String jwtToken = generateJwt(publisher.getCode());

            String customFile = TOKEN_PATTERN.matcher(templateFile).replaceAll(jwtToken);

            String outputFileName = WIDGET_OUTPUT_PATH_PREFIX + UUID.randomUUID().toString() + WIDGET_OUPUT_SUFFIX;

            try {
                FileUtils.write(new File(outputFileName), customFile, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new ServiceException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Could not write to custom widget file", e);
            }

            return outputFileName;
        });
    }

    private String generateJwt(String publisherCode) {
        Algorithm algorithm = null;

        try {
            algorithm = Algorithm.HMAC256(jwtSecret);

            String token = JWT.create()
                    .withClaim(PUBLISHER_CODE_KEY, publisherCode)
                    .sign(algorithm);

            return AuthorizationTokenType.JWT.getPrefix() + " " + token;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
