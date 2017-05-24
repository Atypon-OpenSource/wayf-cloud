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

import com.atypon.wayf.data.identity.IdentityProvider;
import com.atypon.wayf.facade.IdentityProviderFacade;
import com.atypon.wayf.request.RequestReader;
import com.atypon.wayf.verticle.WayfRequestHandlerFactory;
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
public class IdentityProviderRouting implements RoutingProvider {
    private static final Logger LOG = LoggerFactory.getLogger(IdentityProviderRouting.class);

    private static final String IDENTITY_PROVIDER_ID_PARAM_NAME = "id";
    private static final String LOCAL_ID_PARAM_NAME = "localId";
    private static final String IDP_ID_PARAM_NAME = "idpId";

    private static final String CREATE_IDENTITY_PROVIDER = "/1/identityProvider";
    private static final String READ_IDENTITY_PROVIDER = "/1/identityProvider/:id";
    private static final String ADD_IDP_TO_DEVICE = "/1/device/:localId/history/idp";
    private static final String REMOVE_IDP_FROM_DEVICE = "/1/device/:localId/history/idp/:idpId";

    @Inject
    private IdentityProviderFacade identityProviderFacade;

    @Inject
    private WayfRequestHandlerFactory handlerFactory;

    public IdentityProviderRouting() {
    }

    public void addRoutings(Router router) {
        router.route("/1/identityProvider*").handler(BodyHandler.create());
        router.route("/1/device*").handler(BodyHandler.create());
        router.post(CREATE_IDENTITY_PROVIDER).handler(handlerFactory.single((rc) -> createIdentityProvider(rc)));
        router.get(READ_IDENTITY_PROVIDER).handler(handlerFactory.single((rc) -> readIdentityProvider(rc)));
        router.post(ADD_IDP_TO_DEVICE).handler(handlerFactory.single((rc) -> addIdentityProviderToDevice(rc)));
        router.delete(REMOVE_IDP_FROM_DEVICE).handler(handlerFactory.completable((rc) -> removeIdentityProviderFromDevice(rc)));
    }

    public Single<IdentityProvider> createIdentityProvider(RoutingContext routingContext) {
        LOG.debug("Received create IdentityProvider request");

        IdentityProvider requestBody = RequestReader.readRequestBody(routingContext, IdentityProvider.class).blockingGet();

        return identityProviderFacade.create(requestBody);
    }

    public Single<IdentityProvider> readIdentityProvider(RoutingContext routingContext) {
        LOG.debug("Received read IdentityProvider request");

        Long identityProviderId = Long.valueOf(RequestReader.readPathArgument(routingContext, IDENTITY_PROVIDER_ID_PARAM_NAME));

        return identityProviderFacade.read(identityProviderId);
    }

    public Single<IdentityProvider> addIdentityProviderToDevice(RoutingContext routingContext) {
        LOG.debug("Received request to add IDP to device");

        String localId = RequestReader.readPathArgument(routingContext, LOCAL_ID_PARAM_NAME);
        IdentityProvider body = RequestReader.readRequestBody(routingContext, IdentityProvider.class).blockingGet();

        return identityProviderFacade.recordIdentityProviderUse(localId, body);
    }

    public Completable removeIdentityProviderFromDevice(RoutingContext routingContext) {
        LOG.debug("Received request to add IDP to device");

        String localId = RequestReader.readPathArgument(routingContext, LOCAL_ID_PARAM_NAME);
        Long idpId = Long.valueOf(RequestReader.readPathArgument(routingContext, IDP_ID_PARAM_NAME));

        return identityProviderFacade.blockIdentityProviderForDevice(localId, idpId);
    }
}
