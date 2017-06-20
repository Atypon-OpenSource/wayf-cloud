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

import com.atypon.wayf.data.AuthorizationToken;
import com.atypon.wayf.data.InflationPolicyParser;
import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.device.DeviceQuery;
import com.atypon.wayf.facade.ClientJsFacade;
import com.atypon.wayf.facade.DeviceFacade;
import com.atypon.wayf.request.RequestContextAccessor;
import com.atypon.wayf.request.RequestReader;
import com.atypon.wayf.request.ResponseWriter;
import com.atypon.wayf.verticle.WayfRequestHandlerFactory;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DeviceRoutingProvider implements RoutingProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceRoutingProvider.class);

    private static final String DEVICE_ID_PARAM_NAME = "id";
    private static final String LOCAL_ID_PARAM = "localId";

    private static final String READ_DEVICE = "/1/device/:id";
    private static final String FILTER_DEVICE = "/1/devices";
    private static final String ADD_DEVICE_PUBLISHER_RELATIONSHIP = "/1/device/:localId";

    @Inject
    private ResponseWriter responseWriter;

    @Inject
    private DeviceFacade deviceFacade;

    @Inject
    private WayfRequestHandlerFactory handlerFactory;

    @Inject
    private InflationPolicyParser<String> inflationPolicyParser;

    @Inject
    @Named("jwtSecret")
    private String jwtSecret;

    public DeviceRoutingProvider() {
    }

    public void addRoutings(Router router) {
        router.route("/1/device*").handler(BodyHandler.create());
        router.get(READ_DEVICE).handler(handlerFactory.single((rc) -> readDevice(rc)));
        router.get(FILTER_DEVICE).handler(handlerFactory.observable((rc) -> filterDevice(rc)));
        router.post(ADD_DEVICE_PUBLISHER_RELATIONSHIP).handler(handlerFactory.completable((rc) -> registerLocalId(rc)));
        router.patch(ADD_DEVICE_PUBLISHER_RELATIONSHIP).handler(handlerFactory.single((rc) -> createPublisherDeviceRelationship(rc)));
    }

    public Single<Device> readDevice(RoutingContext routingContext) {
        LOG.debug("Received read Device request");

        DeviceQuery query = buildQuery(routingContext);

        return deviceFacade.read(query);
    }

    public Observable<Device> filterDevice(RoutingContext routingContext) {
        LOG.debug("Received read Device request");

        DeviceQuery query = buildQuery(routingContext);

        return deviceFacade.filter(query);
    }

    public Completable registerLocalId(RoutingContext routingContext) {
        String localId = RequestReader.readPathArgument(routingContext, LOCAL_ID_PARAM);

        return deviceFacade.registerLocalId(localId);
    }

    public Single<Device> createPublisherDeviceRelationship(RoutingContext routingContext) {
        LOG.debug("Received request to create publisher/device relationship");

        String localId = RequestReader.readPathArgument(routingContext, LOCAL_ID_PARAM);

        AuthorizationToken token = RequestContextAccessor.get().getAuthorizationToken();
        if (token == null) {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "An Authorization token is required");
        }

        LOG.debug("Token value [{}]", token.getValue());

        String publisherCode = null;

        try {
            Algorithm.HMAC256(jwtSecret);
            DecodedJWT jwt = JWT.decode(token.getValue());

            publisherCode = jwt.getClaim(ClientJsFacade.PUBLISHER_CODE_KEY).asString();
        } catch (Exception e) {
            throw new ServiceException(HttpStatus.SC_UNAUTHORIZED, "Invalid Authorization header", e);
        }

        LOG.debug("Publisher code {}", publisherCode);

        return deviceFacade.relateLocalIdToDevice(publisherCode, localId)
                .map((device) -> {
                    String globalId = device.getGlobalId();

                    responseWriter.setDeviceIdHeader(routingContext, globalId);

                    device.setGlobalId(null);

                    return device;
                });
    }

    private DeviceQuery buildQuery(RoutingContext routingContext) {
        DeviceQuery query = new DeviceQuery();

        String fields = RequestReader.getQueryValue(routingContext, "fields");
        if (fields != null) {
            query.setInflationPolicy(inflationPolicyParser.parse(fields));
        }

        String id = RequestReader.readPathArgument(routingContext, DEVICE_ID_PARAM_NAME);
        query.setGlobalId(id);

        String ids = RequestReader.getQueryValue(routingContext, "ids");
        if (ids != null) {
            String[] idArray = ids.split(",");
            query.setGlobalIds(Lists.newArrayList(idArray));
        }

        return query;

    }
}
