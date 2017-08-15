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

package com.atypon.wayf.integration.admin;

import com.atypon.wayf.verticle.routing.BaseHttpTest;
import org.junit.Test;

import java.util.UUID;

public class AdminUserIntegrationTest extends BaseHttpTest {
    private static final String HTTP_LOGGING_FILE = "device_admin_integration_test";

    private static final String BASE_ADMIN_USER_FILE_PATH = "json_files/admin_user_integration/";

    private static final String CREATE_ADMIN_REQUEST_JSON = getFileAsString(BASE_ADMIN_USER_FILE_PATH + "user/create_admin_user_request.json");
    private static final String CREATE_ADMIN_RESPONSE_JSON = getFileAsString(BASE_ADMIN_USER_FILE_PATH + "user/create_admin_user_response.json");
    private static final String CREATE_ADMIN_NO_TOKEN_RESPONSE_JSON = getFileAsString(BASE_ADMIN_USER_FILE_PATH + "user/create_admin_user_no_token_response.json");

    private static final String CREATE_PUBLISHER_REQUEST_JSON = getFileAsString(BASE_ADMIN_USER_FILE_PATH + "publisher/create_publisher_a_request.json");
    private static final String CREATE_PUBLISHER_RESPONSE_JSON = getFileAsString(BASE_ADMIN_USER_FILE_PATH + "publisher/create_publisher_a_response.json");
    private static final String CREATE_PUBLISHER_NO_TOKEN_RESPONSE_JSON = getFileAsString(BASE_ADMIN_USER_FILE_PATH + "publisher/create_publisher_no_token_response.json");
    private static final String CREATE_PUBLISHER_BAD_TOKEN_RESPONSE_JSON = getFileAsString(BASE_ADMIN_USER_FILE_PATH + "publisher/create_publisher_bad_token_response.json");

    private static final String LOGIN_REQUEST_JSON = getFileAsString(BASE_ADMIN_USER_FILE_PATH + "user/admin_user_login.json");

    public AdminUserIntegrationTest() {
        super(HTTP_LOGGING_FILE);
    }

    @Test
    public void testCreateAdmin() {
        String credentialsEmail = UUID.randomUUID().toString() + "@atypon.com";
        userTestUtil.testCreateUser(credentialsEmail, CREATE_ADMIN_REQUEST_JSON, CREATE_ADMIN_RESPONSE_JSON);
    }

    @Test
    public void testCreateAdminNoToken() {
        String credentialsEmail = UUID.randomUUID().toString() + "@atypon.com";
        userTestUtil.testCreateUserNoToken(credentialsEmail, CREATE_ADMIN_REQUEST_JSON, CREATE_ADMIN_NO_TOKEN_RESPONSE_JSON);
    }

    @Test
    public void testCreateAndLogin() {
        String credentialsEmail = UUID.randomUUID().toString() + "@atypon.com";
        userTestUtil.testCreateUser(credentialsEmail, CREATE_ADMIN_REQUEST_JSON, CREATE_ADMIN_RESPONSE_JSON);

        String adminToken = userTestUtil.testLogin(credentialsEmail, LOGIN_REQUEST_JSON);
    }

    @Test
    public void testCreatePublisherNoToken() {
        publisherTestUtil.testCreatePublisherNoToken(CREATE_PUBLISHER_REQUEST_JSON, CREATE_PUBLISHER_NO_TOKEN_RESPONSE_JSON);
    }

    @Test
    public void testPublisherWithToken() {
        String credentialsEmail = UUID.randomUUID().toString() + "@atypon.com";
        userTestUtil.testCreateUser(credentialsEmail, CREATE_ADMIN_REQUEST_JSON, CREATE_ADMIN_RESPONSE_JSON);

        String adminToken = userTestUtil.testLogin(credentialsEmail, LOGIN_REQUEST_JSON);

        publisherTestUtil.testCreatePublisher(adminToken, CREATE_PUBLISHER_REQUEST_JSON, CREATE_PUBLISHER_RESPONSE_JSON);
    }

    @Test
    public void testOnlyOneActiveToken() {
        String credentialsEmail = UUID.randomUUID().toString() + "@atypon.com";
        userTestUtil.testCreateUser(credentialsEmail, CREATE_ADMIN_REQUEST_JSON, CREATE_ADMIN_RESPONSE_JSON);

        String firstToken = userTestUtil.testLogin(credentialsEmail, LOGIN_REQUEST_JSON);
        String secondToken = userTestUtil.testLogin(credentialsEmail, LOGIN_REQUEST_JSON);

        publisherTestUtil.testCreatePublisher(secondToken, CREATE_PUBLISHER_REQUEST_JSON, CREATE_PUBLISHER_RESPONSE_JSON);
        publisherTestUtil.testCreatePublisherBadToken(firstToken, CREATE_PUBLISHER_REQUEST_JSON, CREATE_PUBLISHER_BAD_TOKEN_RESPONSE_JSON);

    }
}
