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

import com.atypon.wayf.data.authentication.AuthenticatedEntity;
import com.atypon.wayf.data.identity.IdentityProvider;
import com.atypon.wayf.data.identity.IdentityProviderQuery;
import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.facade.DeviceFacade;
import com.atypon.wayf.facade.IdentityProviderFacade;
import com.atypon.wayf.request.RequestContextAccessor;
import com.atypon.wayf.request.RequestParamMapper;
import com.atypon.wayf.request.RequestReader;
import com.atypon.wayf.verticle.WayfRequestHandlerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Completable;
import io.reactivex.Observable;
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
    private static final String IDENTITY_PROVIDER_IDS_PARAM_NAME = "ids";

    private static final String LOCAL_ID_PARAM_NAME = "localId";
    private static final String IDP_ID_PARAM_NAME = "idpId";

    private static final String CREATE_IDENTITY_PROVIDER = "/1/identityProvider";
    private static final String READ_IDENTITY_PROVIDER = "/1/identityProvider/:id";
    private static final String FILTER_IDENTITY_PROVIDERS = "/1/identityProviders";
    private static final String ADD_IDP_TO_DEVICE = "/1/device/:localId/history/idp";
    private static final String REMOVE_IDP_FROM_DEVICE = "/1/device/:localId/history/idp/:idpId";
    private static final String REMOVE_IDP_FROM_MY_DEVICE = "/1/mydevice/history/idp/:idpId";

    private static final String LOCAL_ID_ARG_DESCRIPTION = "Local ID";
    private static final String IDP_ID_ARG_DESCRIPTION = "Identity Provider ID";
    private static final String IDP_IDS_ARG_DESCRIPTION = "Identity Provider IDs";


    @Inject
    private IdentityProviderFacade identityProviderFacade;

    @Inject
    private DeviceFacade deviceFacade;

    @Inject
    private WayfRequestHandlerFactory handlerFactory;

    public IdentityProviderRouting() {
    }

    public void addRoutings(Router router) {
        router.route("/1/identityProvider*").handler(BodyHandler.create());
        router.route("/1/device*").handler(BodyHandler.create());
        router.get(READ_IDENTITY_PROVIDER).handler(handlerFactory.single((rc) -> readIdentityProvider(rc)));
        router.get(FILTER_IDENTITY_PROVIDERS).handler(handlerFactory.observable((rc) -> filterIdentityProviders(rc)));
        router.post(ADD_IDP_TO_DEVICE).handler(handlerFactory.single((rc) -> addIdentityProviderToDevice(rc)));
        router.delete(REMOVE_IDP_FROM_DEVICE).handler(handlerFactory.completable((rc) -> removeIdentityProviderFromDevice(rc)));
        router.delete(REMOVE_IDP_FROM_MY_DEVICE).handler(handlerFactory.completable((rc) -> removeIdentityProviderFromMyDevice(rc)));

    }

    public Single<IdentityProvider> readIdentityProvider(RoutingContext routingContext) {
        LOG.debug("Received read IdentityProvider request");

        Long identityProviderId = Long.valueOf(RequestReader.readRequiredPathParameter(routingContext, IDENTITY_PROVIDER_ID_PARAM_NAME, IDP_ID_ARG_DESCRIPTION));

        return identityProviderFacade.read(identityProviderId);
    }

    public Observable<IdentityProvider> filterIdentityProviders(RoutingContext routingContext) {
        LOG.debug("Received read IdentityProvider request");

        IdentityProviderQuery query = new IdentityProviderQuery();

        RequestParamMapper.mapParams(routingContext, query);

        return identityProviderFacade.filter(query);
    }

    public Single<IdentityProvider> addIdentityProviderToDevice(RoutingContext routingContext) {
        LOG.debug("Received request to add IDP to device");

        String localId = RequestReader.readRequiredPathParameter(routingContext, LOCAL_ID_PARAM_NAME, LOCAL_ID_ARG_DESCRIPTION);
        IdentityProvider body = RequestReader.readRequestBody(routingContext, IdentityProvider.class).blockingGet();

        Publisher publisher = AuthenticatedEntity.authenticatedAsPublisher(RequestContextAccessor.get().getAuthenticated());
        String hashedLocalId = deviceFacade.encryptLocalId(publisher.getId(), localId);

        return identityProviderFacade.recordIdentityProviderUse(hashedLocalId, body);
    }

    public Completable removeIdentityProviderFromDevice(RoutingContext routingContext) {
        LOG.debug("Received request to add IDP to device");

        String localId = RequestReader.readRequiredPathParameter(routingContext, LOCAL_ID_PARAM_NAME, LOCAL_ID_ARG_DESCRIPTION);

        Publisher publisher = AuthenticatedEntity.authenticatedAsPublisher(RequestContextAccessor.get().getAuthenticated());
        String hashedLocalId = deviceFacade.encryptLocalId(publisher.getId(), localId);

        Long idpId = Long.valueOf(RequestReader.readRequiredPathParameter(routingContext, IDP_ID_PARAM_NAME, IDP_ID_ARG_DESCRIPTION));

        return identityProviderFacade.blockIdentityProviderForLocalId(hashedLocalId, idpId);
    }

    public Completable removeIdentityProviderFromMyDevice(RoutingContext routingContext) {
        LOG.debug("Received request to add IDP to device");

        String globalId = RequestReader.getCookieValue(routingContext, RequestReader.DEVICE_ID);

        Long idpId = Long.valueOf(RequestReader.readRequiredPathParameter(routingContext, IDP_ID_PARAM_NAME, IDP_ID_ARG_DESCRIPTION));

        return identityProviderFacade.blockIdentityProviderForGlobalId(globalId, idpId);
    }
}
