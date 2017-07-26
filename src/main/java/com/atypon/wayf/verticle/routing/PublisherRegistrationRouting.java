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

package com.atypon.wayf.verticle.routing;

import com.atypon.wayf.data.publisher.PublisherQuery;
import com.atypon.wayf.data.publisher.registration.PublisherRegistration;
import com.atypon.wayf.data.publisher.registration.PublisherRegistrationQuery;
import com.atypon.wayf.facade.PublisherRegistrationFacade;
import com.atypon.wayf.request.RequestParamMapper;
import com.atypon.wayf.request.RequestReader;
import com.atypon.wayf.verticle.WayfRequestHandlerFactory;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PublisherRegistrationRouting implements RoutingProvider {
    private static final Logger LOG = LoggerFactory.getLogger(PublisherRegistrationRouting.class);

    private static final String PUBLISHER_REGISTRATION_BASE_URL = "/1/publisherRegistration";
    private static final String PUBLISHER_REGISTRATION_ID_PARAM_NAME = "id";
    private static final String PUBLISHER_REGISTRATION_ID_PARAM = ":" + PUBLISHER_REGISTRATION_ID_PARAM_NAME;

    private static final String CREATE_PUBLISHER_REGISTRATION = PUBLISHER_REGISTRATION_BASE_URL;
    private static final String READ_PUBLISHER_REGISTRATION = PUBLISHER_REGISTRATION_BASE_URL + "/" +  PUBLISHER_REGISTRATION_ID_PARAM;
    private static final String FILTER_PUBLISHER_REGISTRATIONS = PUBLISHER_REGISTRATION_BASE_URL + "s";


    private static final String PUBLISHER_REGISTRATION_ID_ARG_DESCRIPTION = "Publisher Registration ID";

    @Inject
    private PublisherRegistrationFacade publisherRegistrationFacade;

    @Inject
    private WayfRequestHandlerFactory handlerFactory;

    public PublisherRegistrationRouting() {
    }

    public void addRoutings(Router router) {
        router.route(PUBLISHER_REGISTRATION_BASE_URL + "*").handler(BodyHandler.create());
        router.post(CREATE_PUBLISHER_REGISTRATION).handler(handlerFactory.single((rc) -> createPublisher(rc)));
        router.get(READ_PUBLISHER_REGISTRATION).handler(handlerFactory.single((rc) -> readPublisherRegistration(rc)));
        router.patch(READ_PUBLISHER_REGISTRATION).handler(handlerFactory.single((rc) -> updatePublisherRegistrationStatus(rc)));
        router.get(FILTER_PUBLISHER_REGISTRATIONS).handler(handlerFactory.observable((rc) -> filterPublisherRegistrations(rc)));
    }

    public Single<PublisherRegistration> createPublisher(RoutingContext routingContext) {
        LOG.debug("Received create PublisherRegistration request");

        return Single.just(routingContext)
                .flatMap((rc) -> RequestReader.readRequestBody(rc, PublisherRegistration.class))
                .flatMap((requestPublisherRegistration) -> publisherRegistrationFacade.create(requestPublisherRegistration));
    }

    public Single<PublisherRegistration> readPublisherRegistration(RoutingContext routingContext) {
        LOG.debug("Received read PublisherSession request");

        Long publisherRegistrationId = Long.valueOf(
                RequestReader.readRequiredPathParameter(routingContext, PUBLISHER_REGISTRATION_ID_PARAM_NAME, PUBLISHER_REGISTRATION_ID_ARG_DESCRIPTION));

        return publisherRegistrationFacade.read(publisherRegistrationId);
    }

    public Single<PublisherRegistration> updatePublisherRegistrationStatus(RoutingContext routingContext) {
        LOG.debug("Received read PublisherSession request");

        Long publisherRegistrationId = Long.valueOf(
                RequestReader.readRequiredPathParameter(routingContext, PUBLISHER_REGISTRATION_ID_PARAM_NAME, PUBLISHER_REGISTRATION_ID_ARG_DESCRIPTION));

        return RequestReader.readRequestBody(routingContext, PublisherRegistration.class)
                .map((publisherRegistration) -> {
                        publisherRegistration.setId(publisherRegistrationId);
                        return publisherRegistration;
                })
                .flatMap((publisherRegistration) -> publisherRegistrationFacade.updateStatus(publisherRegistration));
    }

    public Observable<PublisherRegistration> filterPublisherRegistrations(RoutingContext routingContext) {
        LOG.debug("Received filter PublisherRegistration request");

        PublisherRegistrationQuery query = new PublisherRegistrationQuery();

        RequestParamMapper.mapParams(routingContext, query);

        return publisherRegistrationFacade.filter(query);
    }
}
