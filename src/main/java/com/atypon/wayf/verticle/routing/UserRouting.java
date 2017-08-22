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

import com.atypon.wayf.data.authentication.PasswordCredentials;
import com.atypon.wayf.data.user.User;
import com.atypon.wayf.data.user.UserQuery;
import com.atypon.wayf.facade.PasswordCredentialsFacade;
import com.atypon.wayf.facade.UserFacade;
import com.atypon.wayf.request.RequestParamMapper;
import com.atypon.wayf.request.RequestReader;
import com.atypon.wayf.verticle.WayfRequestHandlerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.impl.CookieImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class UserRouting implements RoutingProvider {
    private static final Logger LOG = LoggerFactory.getLogger(UserRouting.class);

    private static final String USER_BASE_URL = "/1/user";
    private static final String USER_ID_PARAM_NAME = "id";
    private static final String USER_ID_PARAM = ":" + USER_ID_PARAM_NAME;

    private static final String READ_USER = USER_BASE_URL + "/" + USER_ID_PARAM;
    private static final String FILTER_USERS = USER_BASE_URL + "s";
    private static final String LOGIN_URL = USER_BASE_URL + "/credentials";
    private static final String CHANGE_PASSWORD_URL = USER_BASE_URL + "/" + USER_ID_PARAM + "/credentials";
    private static final String DELETE_USER = USER_BASE_URL + "/" + USER_ID_PARAM;

    private static final String USER_ID_ARG_DESCRIPTION = "User ID";

    @Inject
    private UserFacade userFacade;

    @Inject
    private PasswordCredentialsFacade passwordCredentialsFacade;

    @Inject
    private WayfRequestHandlerFactory handlerFactory;

    @Inject
    @Named("wayf.domain")
    private String wayfDomain;

    public UserRouting() {
    }

    public void addRoutings(Router router) {
        router.route(USER_BASE_URL + "*").handler(BodyHandler.create());
        router.post(USER_BASE_URL).handler(handlerFactory.single((rc) -> createUser(rc)));
        router.get(READ_USER).handler(handlerFactory.single((rc) -> readUser(rc)));
        router.get(FILTER_USERS).handler(handlerFactory.observable((rc) -> filterUsers(rc)));
        router.post(LOGIN_URL).handler(handlerFactory.completable((rc) -> login(rc)));
        router.delete(DELETE_USER).handler(handlerFactory.completable((rc) -> deleteUser(rc)));
        router.put(CHANGE_PASSWORD_URL).handler(handlerFactory.completable((rc) -> resetPassword(rc)));
    }

    public Single<User> createUser(RoutingContext routingContext) {
        LOG.debug("Received read User request");

        return RequestReader.readRequestBody(routingContext, User.class)
                .flatMap((user) -> userFacade.create(user));
    }

    public Single<User> readUser(RoutingContext routingContext) {
        LOG.debug("Received read User request");

        Long userId = Long.valueOf(RequestReader.readRequiredPathParameter(routingContext, USER_ID_PARAM_NAME, USER_ID_ARG_DESCRIPTION));

        return userFacade.read(userId);
    }

    public Observable<User> filterUsers(RoutingContext routingContext) {
        LOG.debug("Received filter User request");

        UserQuery userQuery = new UserQuery();
        RequestParamMapper.mapParams(routingContext, userQuery);

        return userFacade.filter(userQuery);
    }

    public Completable login(RoutingContext routingContext) {
        return RequestReader.readRequestBody(routingContext, PasswordCredentials.class)
                .flatMap((credentials) -> passwordCredentialsFacade.generateSessionToken(credentials))
                .flatMapCompletable((authorizationToken) ->
                        Completable.fromAction(() -> {
                            Cookie cookie = new CookieImpl("adminToken", authorizationToken.getValue())
                                    .setDomain(wayfDomain)
                                    .setMaxAge(Math.toIntExact(authorizationToken.getValidUntil().getTime() / 1000L))
                                    .setPath("/");

                            routingContext.addCookie(cookie);
                        })
                );
    }

    public Completable deleteUser(RoutingContext routingContext) {
        Long userId = Long.valueOf(RequestReader.readRequiredPathParameter(routingContext, USER_ID_PARAM_NAME, USER_ID_ARG_DESCRIPTION));

        return userFacade.delete(userId);
    }

    public Completable resetPassword(RoutingContext routingContext) {
        Long userId = Long.valueOf(RequestReader.readRequiredPathParameter(routingContext, USER_ID_PARAM_NAME, USER_ID_ARG_DESCRIPTION));

        return RequestReader.readRequestBody(routingContext, PasswordCredentials.class)
                .flatMapCompletable((passwordCredentials) -> passwordCredentialsFacade.resetPassword(userId, passwordCredentials));

    }
}
