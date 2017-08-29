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

package com.atypon.wayf.integration.publisher;

import com.atypon.wayf.verticle.routing.BaseHttpTest;
import org.junit.Test;

import static com.atypon.wayf.integration.HttpTestUtil.setField;

public class PublisherRegistrationIntegrationTest extends BaseHttpTest {

    private static final String HTTP_LOGGING_FILE = "publisher_registration_integration_test";

    private static final String BASE_FILE_PATH = "json_files/publisher_registration_integration/";

    private static final String CREATE_PUBLISHER_REGISTRATION_REQUEST = getFileAsString(BASE_FILE_PATH + "registration/create_publisher_registration_request.json");
    private static final String CREATE_PUBLISHER_REGISTRATION_RESPONSE = getFileAsString(BASE_FILE_PATH + "registration/create_publisher_registration_response.json");

    private static final String APPROVE_REGISTRATION_RESPONSE = getFileAsString(BASE_FILE_PATH + "admin/approve_registration_response.json");
    private static final String DENIED_REGISTRATION_RESPONSE = getFileAsString(BASE_FILE_PATH + "admin/denied_registration_response.json");
    private static final String FIND_PENDING_REGISTRATIONS_RESPONSE = getFileAsString(BASE_FILE_PATH + "admin/pending_registrations_response.json");

    public PublisherRegistrationIntegrationTest() {
        super(HTTP_LOGGING_FILE);
    }

    @Test
    public void testRegistrationAsPublisher() {
        publisherRegistrationTestUtil.testPublisherRegistration(CREATE_PUBLISHER_REGISTRATION_REQUEST, CREATE_PUBLISHER_REGISTRATION_RESPONSE);
    }

    @Test
    public void testRegistrationRetrieval() {
        Long registrationId = publisherRegistrationTestUtil.testPublisherRegistration(CREATE_PUBLISHER_REGISTRATION_REQUEST, CREATE_PUBLISHER_REGISTRATION_RESPONSE);
        publisherRegistrationTestUtil.readRegistration(registrationId, CREATE_PUBLISHER_REGISTRATION_RESPONSE);
    }

    @Test
    public void testRegistrationApproval() {
        Long registrationId = publisherRegistrationTestUtil.testPublisherRegistration(CREATE_PUBLISHER_REGISTRATION_REQUEST, CREATE_PUBLISHER_REGISTRATION_RESPONSE);
        String registration = publisherRegistrationTestUtil.readRegistration(registrationId, CREATE_PUBLISHER_REGISTRATION_RESPONSE);

        String approvedRegistration = setField(registration, "$.status", "APPROVED");
        publisherRegistrationTestUtil.updateRegistrationStatus(Boolean.TRUE, registrationId, approvedRegistration, APPROVE_REGISTRATION_RESPONSE);
    }

    @Test
    public void testRegistrationDenial() {
        Long registrationId = publisherRegistrationTestUtil.testPublisherRegistration(CREATE_PUBLISHER_REGISTRATION_REQUEST, CREATE_PUBLISHER_REGISTRATION_RESPONSE);
        String registration = publisherRegistrationTestUtil.readRegistration(registrationId, CREATE_PUBLISHER_REGISTRATION_RESPONSE);

        String approvedRegistration = setField(registration, "$.status", "DENIED");
        publisherRegistrationTestUtil.updateRegistrationStatus(Boolean.FALSE, registrationId, approvedRegistration, DENIED_REGISTRATION_RESPONSE);
    }

    @Test
    public void testFindPendingRegistrations() {
        Long registrationId = publisherRegistrationTestUtil.testPublisherRegistration(CREATE_PUBLISHER_REGISTRATION_REQUEST, CREATE_PUBLISHER_REGISTRATION_RESPONSE);
        publisherRegistrationTestUtil.findPendingRegistrations(registrationId, FIND_PENDING_REGISTRATIONS_RESPONSE);
    }
}
