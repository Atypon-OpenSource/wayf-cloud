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

import com.atypon.wayf.data.publisher.PublisherSession;
import com.atypon.wayf.facade.PublisherSessionFacade;
import com.atypon.wayf.facade.impl.PublisherSessionFacadeImpl;
import com.atypon.wayf.request.RequestReader;
import com.atypon.wayf.verticle.WayfRequestHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PublisherSessionRouting implements RoutingProvider {
    private static final Logger LOG = LoggerFactory.getLogger(PublisherSessionRouting.class);

    private static final String PUBLISHER_SESSION_BASE_URL = "/1/publisherSession";
    private static final String PUBLISHER_SESSION_ID_PARAM_NAME = "id";
    private static final String PUBLISHER_SESSION_ID_PARAM = ":" + PUBLISHER_SESSION_ID_PARAM_NAME;

    private static final String CREATE_PUBLISHER_SESSION = PUBLISHER_SESSION_BASE_URL;
    private static final String READ_PUBLISHER_SESSION = PUBLISHER_SESSION_BASE_URL + "/" +  PUBLISHER_SESSION_ID_PARAM;
    private static final String UPDATE_PUBLISHER_SESSION = PUBLISHER_SESSION_BASE_URL + "/" +  PUBLISHER_SESSION_ID_PARAM;
    private static final String DELETE_PUBLISHER_SESSION = PUBLISHER_SESSION_BASE_URL + "/" +  PUBLISHER_SESSION_ID_PARAM;

    private PublisherSessionFacade publisherSessionFacade;

    @Inject
    public PublisherSessionRouting(PublisherSessionFacade publisherSessionFacade) {
        this.publisherSessionFacade = publisherSessionFacade;
    }

    public void addRoutings(Router router) {
        router.route(PUBLISHER_SESSION_BASE_URL + "*").handler(BodyHandler.create());
        router.post(CREATE_PUBLISHER_SESSION).handler(WayfRequestHandler.single((rc) -> createPublisherSession(rc)));
        router.get(READ_PUBLISHER_SESSION).handler(WayfRequestHandler.single((rc) -> readPublisherSession(rc)));
        router.put(UPDATE_PUBLISHER_SESSION).handler(WayfRequestHandler.single((rc) -> updatePublisherSession(rc)));
        router.delete(DELETE_PUBLISHER_SESSION).handler(WayfRequestHandler.completable((rc) -> deletePublisherSession(rc)));
    }

    public Single<PublisherSession> createPublisherSession(RoutingContext routingContext) {
        LOG.debug("Received create PublisherSession request");

        return Single.just(routingContext)
                .flatMap((rc) -> RequestReader.readRequestBody(rc, PublisherSession.class))
                .flatMap((requestPublisherSession) -> publisherSessionFacade.create(requestPublisherSession));
    }

    public Single<PublisherSession> readPublisherSession(RoutingContext routingContext) {
        LOG.debug("Received read PublisherSession request");

        return Single.just(routingContext)
                .flatMap((rc) -> RequestReader.readPathArgument(rc, PUBLISHER_SESSION_ID_PARAM_NAME))
                .flatMap((publisherSessionId) -> publisherSessionFacade.read(publisherSessionId));
    }

    public Single<PublisherSession> updatePublisherSession(RoutingContext routingContext) {
        LOG.debug("Received update PublisherSession request");

        return Single.just(routingContext)
                .flatMap((rc) -> RequestReader.readRequestBody(rc, PublisherSession.class))
                .flatMap((requestPublisherSession) -> publisherSessionFacade.update(requestPublisherSession));
    }

    public Completable deletePublisherSession(RoutingContext routingContext) {
        LOG.debug("Received delete PublisherSession request");

        return Single.just(routingContext)
                .flatMap((rc) -> RequestReader.readPathArgument(rc, PUBLISHER_SESSION_ID_PARAM_NAME))
                .flatMapCompletable((publisherSessionId) -> publisherSessionFacade.delete(publisherSessionId));
    }
}
