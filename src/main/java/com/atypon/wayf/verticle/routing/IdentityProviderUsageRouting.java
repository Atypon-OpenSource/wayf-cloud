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

import com.atypon.wayf.data.AuthenticatedEntity;
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.device.DeviceQuery;
import com.atypon.wayf.data.identity.IdentityProviderUsage;
import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.facade.DeviceFacade;
import com.atypon.wayf.facade.IdentityProviderUsageFacade;
import com.atypon.wayf.request.RequestContextAccessor;
import com.atypon.wayf.request.RequestReader;
import com.atypon.wayf.verticle.WayfRequestHandlerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Observable;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class IdentityProviderUsageRouting implements RoutingProvider {
    private static final Logger LOG = LoggerFactory.getLogger(IdentityProviderUsageRouting.class);

    private static final String LOCAL_ID_PARAM_NAME = "localId";

    private static final String READ_DEVICE_RECENT_HISTORY = "/1/device/:localId/history";
    private static final String READ_MY_DEVICE_RECENT_HISTORY = "/1/mydevice/history";

    private static final String LOCAL_ID_ARG_DESCRIPTION = "Local ID";

    @Inject
    private IdentityProviderUsageFacade identityProviderUsageFacade;

    @Inject
    private WayfRequestHandlerFactory handlerFactory;

    @Inject
    private DeviceFacade deviceFacade;

    public IdentityProviderUsageRouting() {
    }

    public void addRoutings(Router router) {
        router.get(READ_DEVICE_RECENT_HISTORY).handler(handlerFactory.observable((rc) -> readDeviceLocalHistory(rc)));
        router.get(READ_MY_DEVICE_RECENT_HISTORY).handler(handlerFactory.observable((rc) -> readMyDeviceLocalHistory(rc)));
    }

    public Observable<IdentityProviderUsage> readDeviceLocalHistory(RoutingContext routingContext) {
        LOG.debug("Received create IdentityProvider request");

        String localId = RequestReader.readRequiredPathParameter(routingContext, LOCAL_ID_PARAM_NAME, LOCAL_ID_ARG_DESCRIPTION);

        Publisher publisher = AuthenticatedEntity.entityAsPublisher(RequestContextAccessor.get().getAuthenticated());
        String hashedLocalId = deviceFacade.encryptLocalId(publisher.getId(), localId);

        return identityProviderUsageFacade.buildRecentHistory(hashedLocalId);
    }

    public Observable<IdentityProviderUsage> readMyDeviceLocalHistory(RoutingContext routingContext) {
        LOG.debug("Received create IdentityProvider request");

        String globalId = RequestReader.getCookieValue(routingContext, RequestReader.DEVICE_ID);
        Device device = deviceFacade.read(new DeviceQuery().setGlobalId(globalId)).blockingGet();

        return Observable.fromIterable(identityProviderUsageFacade.buildRecentHistory(device));
    }
}
