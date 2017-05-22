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

import com.atypon.wayf.data.InflationPolicyParser;
import com.atypon.wayf.data.device.Device;
import com.atypon.wayf.data.device.DeviceQuery;
import com.atypon.wayf.facade.DeviceFacade;
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
public class DeviceRoutingProvider implements RoutingProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceRoutingProvider.class);

    private static final String DEVICE_BASE_URL = "/1/device";
    private static final String DEVICE_ID_PARAM_NAME = "id";
    private static final String DEVICE_ID_PARAM = ":" + DEVICE_ID_PARAM_NAME;

    private static final String CREATE_DEVICE = DEVICE_BASE_URL;
    private static final String READ_DEVICE = DEVICE_BASE_URL + "/" +  DEVICE_ID_PARAM;
    private static final String UPDATE_DEVICE = DEVICE_BASE_URL + "/" +  DEVICE_ID_PARAM;
    private static final String DELETE_DEVICE = DEVICE_BASE_URL + "/" +  DEVICE_ID_PARAM;
    private static final String FILTER_DEVICE = DEVICE_BASE_URL + "s";

    @Inject
    private DeviceFacade deviceFacade;

    @Inject
    private WayfRequestHandlerFactory handlerFactory;

    @Inject
    private InflationPolicyParser<String> inflationPolicyParser;

    public DeviceRoutingProvider() {
    }

    public void addRoutings(Router router) {
        router.route(DEVICE_BASE_URL + "*").handler(BodyHandler.create());
        router.post(CREATE_DEVICE).handler(handlerFactory.single((rc) -> createDevice(rc)));
        router.get(READ_DEVICE).handler(handlerFactory.single((rc) -> readDevice(rc)));
        router.get(FILTER_DEVICE).handler(handlerFactory.observable((rc) -> filterDevice(rc)));
    }

    public Single<Device> createDevice(RoutingContext routingContext) {
        LOG.debug("Received create Device request");

        return Single.just(routingContext)
                .flatMap((rc) -> RequestReader.readRequestBody(rc, Device.class))
                .flatMap((requestDevice) -> deviceFacade.create(requestDevice));
    }

    public Single<Device> readDevice(RoutingContext routingContext) {
        LOG.debug("Received read Device request");

        return Single.just(routingContext)
                .flatMap((deviceId) -> deviceFacade.read(buildQuery(routingContext)));
    }

    public Observable<Device> filterDevice(RoutingContext routingContext) {
        LOG.debug("Received read Device request");

        return deviceFacade.filter(buildQuery(routingContext));
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
