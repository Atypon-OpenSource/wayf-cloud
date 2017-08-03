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

import com.atypon.wayf.data.publisher.PublisherQuery;
import com.atypon.wayf.data.publisher.registration.PublisherRegistrationQuery;
import com.atypon.wayf.data.publisher.registration.PublisherRegistrationStatus;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;
import org.junit.Test;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

public class RequestParamMapperTest {

    @Test
    public void testPublisherQueryMapping() {
        Map<String, String> publisherQueryValues = new HashMap<>();
        publisherQueryValues.put("ids", "1,2,3");
        publisherQueryValues.put("codes", "PUB_A,PUB_B");

        RoutingContext publisherQueryRoutingContext = new RoutingContextMock() {
            @Override
            public HttpServerRequest request() {
                return new MockHttpServerRequest(publisherQueryValues);
            }
        };

        PublisherQuery publisherQuery = new PublisherQuery();

        RequestParamMapper paramMapper = new RequestParamMapper();
        paramMapper.mapParams(publisherQueryRoutingContext, publisherQuery);

        Long[] ids  = {1L, 2L, 3L};
        assertNotNull(publisherQuery.getIds());
        assertArrayEquals(ids, publisherQuery.getIds());

        String[] codes = {"PUB_A", "PUB_B"};
        assertNotNull(publisherQuery.getCodes());
        assertArrayEquals(codes, publisherQuery.getCodes());
    }

    @Test
    public void testPublisherRegistrationQueryMapping() {
        Map<String, String> publisherRegistrationValues = new HashMap<>();
        publisherRegistrationValues.put("statuses", "PENDING,APPROVED");

        RoutingContext publisherRegistrationQueryRoutingContext = new RoutingContextMock() {
            @Override
            public HttpServerRequest request() {
                return new MockHttpServerRequest(publisherRegistrationValues);
            }
        };

        PublisherRegistrationQuery publisherRegistrationQuery = new PublisherRegistrationQuery();

        RequestParamMapper paramMapper = new RequestParamMapper();
        paramMapper.mapParams(publisherRegistrationQueryRoutingContext, publisherRegistrationQuery);

        PublisherRegistrationStatus[] statuses  = {PublisherRegistrationStatus.PENDING, PublisherRegistrationStatus.APPROVED};
        assertNotNull(publisherRegistrationQuery.getStatuses());
        assertArrayEquals(statuses, publisherRegistrationQuery.getStatuses());
    }



    private class MockHttpServerRequest implements HttpServerRequest {
        private Map<String, String> params;
        MockHttpServerRequest(Map<String, String> params) {
            this.params = params;
        }
        @Override
        public HttpServerRequest exceptionHandler(Handler<Throwable> handler) {
            return null;
        }

        @Override
        public HttpServerRequest handler(Handler<Buffer> handler) {
            return null;
        }

        @Override
        public HttpServerRequest pause() {
            return null;
        }

        @Override
        public HttpServerRequest resume() {
            return null;
        }

        @Override
        public HttpServerRequest endHandler(Handler<Void> endHandler) {
            return null;
        }

        @Override
        public HttpVersion version() {
            return null;
        }

        @Override
        public HttpMethod method() {
            return null;
        }

        @Override
        public boolean isSSL() {
            return false;
        }

        @Override
        public String uri() {
            return null;
        }

        @Override
        public String path() {
            return null;
        }

        @Override
        public String query() {
            return null;
        }

        @Override
        public HttpServerResponse response() {
            return null;
        }

        @Override
        public MultiMap headers() {
            return null;
        }

        @Override
        public String getHeader(String headerName) {
            return null;
        }

        @Override
        public String getHeader(CharSequence headerName) {
            return null;
        }

        @Override
        public MultiMap params() {
            MultiMap params = MultiMap.caseInsensitiveMultiMap();
            params.addAll(this.params);
            return params;
        }

        @Override
        public String getParam(String paramName) {
            return null;
        }

        @Override
        public SocketAddress remoteAddress() {
            return null;
        }

        @Override
        public SocketAddress localAddress() {
            return null;
        }

        @Override
        public X509Certificate[] peerCertificateChain() throws SSLPeerUnverifiedException {
            return new X509Certificate[0];
        }

        @Override
        public String absoluteURI() {
            return null;
        }

        @Override
        public HttpServerRequest bodyHandler(Handler<Buffer> bodyHandler) {
            return null;
        }

        @Override
        public NetSocket netSocket() {
            return null;
        }

        @Override
        public HttpServerRequest setExpectMultipart(boolean expect) {
            return null;
        }

        @Override
        public boolean isExpectMultipart() {
            return false;
        }

        @Override
        public HttpServerRequest uploadHandler(Handler<HttpServerFileUpload> uploadHandler) {
            return null;
        }

        @Override
        public MultiMap formAttributes() {
            return null;
        }

        @Override
        public String getFormAttribute(String attributeName) {
            return null;
        }

        @Override
        public ServerWebSocket upgrade() {
            return null;
        }

        @Override
        public boolean isEnded() {
            return false;
        }
    }

}
