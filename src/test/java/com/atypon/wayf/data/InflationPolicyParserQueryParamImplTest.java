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

package com.atypon.wayf.data;

import com.google.common.collect.Sets;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InflationPolicyParserQueryParamImplTest {

    @Test
    public void testParseSimple() {
        String input = "device,user,session";

        InflationPolicy policy = new InflationPolicyParserQueryParamImpl().parse(input);
        assertEquals(Sets.newHashSet("device", "user", "session"), policy.getChildFields());
    }

    @Test
    public void testIgnoreWhitespace() {
        String input = "device, user, session";

        InflationPolicy policy = new InflationPolicyParserQueryParamImpl().parse(input);
        assertEquals(Sets.newHashSet("device", "user", "session"), policy.getChildFields());
    }

    @Test
    public void testParseComplexNested() {
        String input = "device,sessions{publisher{platform},authenticatedBy}";

        InflationPolicy policy = new InflationPolicyParserQueryParamImpl().parse(input);
        assertEquals(Sets.newHashSet("device", "sessions"), policy.getChildFields());
        assertEquals(Sets.newHashSet("publisher", "authenticatedBy"), policy.getChildPolicy("sessions").getChildFields());
        assertEquals(Sets.newHashSet("platform"), policy.getChildPolicy("sessions").getChildPolicy("publisher").getChildFields());
    }
}
