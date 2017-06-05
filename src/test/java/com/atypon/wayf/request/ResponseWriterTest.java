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

package com.atypon.wayf.request;

import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.facade.ErrorLoggerFacade;
import com.atypon.wayf.facade.impl.ErrorLoggerFacadeMockImpl;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.*;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ResponseWriterTest {

    private ResponseWriterMock responseWriter;
    private ErrorLoggerFacadeMockImpl errorLoggerFacade;

    @Before
    public void setup() {
        ResponseWriterMock responseWriterMock = new ResponseWriterMock();
        errorLoggerFacade = new ErrorLoggerFacadeMockImpl();
        responseWriterMock.setErrorLoggerFacade(errorLoggerFacade);
        this.responseWriter = responseWriterMock;
    }

    @After
    public void teardown() {
        RequestContextAccessor.set(null);
    }

    @Test
    public void testHasMore() {
        RequestContextAccessor.set(new RequestContext().setRequestUrl("http://localhost:8080/list?param1=1&limit=30&offset=0").setOffset(0).setLimit(30).setHasAnotherDbPage(Boolean.TRUE));
        String link = responseWriter._getLinkHeaderValue();
        assertEquals("<http://localhost:8080/list?param1=1&limit=30&offset=30>; rel=\"next\"", link);
    }

    @Test
    public void testHasNoMore() {
        RequestContextAccessor.set(new RequestContext().setRequestUrl("/list").setOffset(0).setLimit(30).setHasAnotherDbPage(Boolean.FALSE));
        String link = responseWriter._getLinkHeaderValue();
        assertEquals("", link);
    }

    @Test
    public void testLogsError() {
        responseWriter.buildFailure(new RoutingContextMock());

        int statusCode = errorLoggerFacade.getStatusCode();
        Throwable exception = errorLoggerFacade.getException();

        assertEquals(HttpStatus.SC_FAILED_DEPENDENCY, statusCode);
        assertEquals(ServiceException.class, exception.getClass());
    }

    private class ResponseWriterMock extends ResponseWriter {
        public ResponseWriterMock() {
        }

        public void setErrorLoggerFacade(ErrorLoggerFacade errorLoggerFacade) {
            super.errorLoggerFacade = errorLoggerFacade;
        }

        public String _getLinkHeaderValue() {
            return super.buildLinkHeaderValue();
        }
    }

    private class RoutingContextMock implements RoutingContext {
        @Override
        public HttpServerRequest request() {
            return null;
        }

        @Override
        public HttpServerResponse response() {
            return null;
        }

        @Override
        public void next() {

        }

        @Override
        public void fail(int statusCode) {

        }

        @Override
        public void fail(Throwable throwable) {

        }

        @Override
        public RoutingContext put(String key, Object obj) {
            return null;
        }

        @Override
        public <T> T get(String key) {
            return null;
        }

        @Override
        public Map<String, Object> data() {
            return null;
        }

        @Override
        public Vertx vertx() {
            return null;
        }

        @Override
        public String mountPoint() {
            return null;
        }

        @Override
        public Route currentRoute() {
            return null;
        }

        @Override
        public String normalisedPath() {
            return null;
        }

        @Override
        public Cookie getCookie(String name) {
            return null;
        }

        @Override
        public RoutingContext addCookie(Cookie cookie) {
            return null;
        }

        @Override
        public Cookie removeCookie(String name) {
            return null;
        }

        @Override
        public int cookieCount() {
            return 0;
        }

        @Override
        public Set<Cookie> cookies() {
            return null;
        }

        @Override
        public String getBodyAsString() {
            return null;
        }

        @Override
        public String getBodyAsString(String encoding) {
            return null;
        }

        @Override
        public JsonObject getBodyAsJson() {
            return null;
        }

        @Override
        public JsonArray getBodyAsJsonArray() {
            return null;
        }

        @Override
        public Buffer getBody() {
            return null;
        }

        @Override
        public Set<FileUpload> fileUploads() {
            return null;
        }

        @Override
        public Session session() {
            return null;
        }

        @Override
        public User user() {
            return null;
        }

        @Override
        public Throwable failure() {
            return new ServiceException(HttpStatus.SC_FAILED_DEPENDENCY, "test");
        }

        @Override
        public int statusCode() {
            return 0;
        }

        @Override
        public String getAcceptableContentType() {
            return null;
        }

        @Override
        public int addHeadersEndHandler(Handler<Void> handler) {
            return 0;
        }

        @Override
        public boolean removeHeadersEndHandler(int handlerID) {
            return false;
        }

        @Override
        public int addBodyEndHandler(Handler<Void> handler) {
            return 0;
        }

        @Override
        public boolean removeBodyEndHandler(int handlerID) {
            return false;
        }

        @Override
        public boolean failed() {
            return false;
        }

        @Override
        public void setBody(Buffer body) {

        }

        @Override
        public void setSession(Session session) {

        }

        @Override
        public void setUser(User user) {

        }

        @Override
        public void clearUser() {

        }

        @Override
        public void setAcceptableContentType(String contentType) {

        }

        @Override
        public void reroute(HttpMethod method, String path) {

        }

        @Override
        public List<Locale> acceptableLocales() {
            return null;
        }

        @Override
        public void reroute(String path) {

        }

        @Override
        public Locale preferredLocale() {
            return null;
        }
    }
}
