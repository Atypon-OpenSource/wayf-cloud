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

package com.atypon.wayf.dao.neo4j;

import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;

class MockDriver implements Driver {
    private MockSession mockSession = new MockSession();
    public MockDriver() {
    }

    @Override
    public boolean isEncrypted() {
        return false;
    }

    @Override
    public Session session() {
        return mockSession;
    }

    @Override
    public Session session(AccessMode accessMode) {
        return null;
    }

    @Override
    public Session session(String s) {
        return null;
    }

    @Override
    public Session session(AccessMode accessMode, String s) {
        return null;
    }

    @Override
    public void close() {

    }
}