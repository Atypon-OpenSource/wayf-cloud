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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

public class InflationPolicyParserQueryParamImpl implements InflationPolicyParser<String> {
    private static final Logger LOG = LoggerFactory.getLogger(InflationPolicyParserQueryParamImpl.class);

    private static final char SPACE = ' ';
    private static final char TAB = '\t';
    private static final char DELIMITER = ',';
    private static final char POLICY_START = '{';
    private static final char POLICY_END = '}';

    public InflationPolicy parse(String fieldsQueryParam) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fieldsQueryParam.getBytes());
        return parse(inputStream);
    }

    private InflationPolicy parse(ByteArrayInputStream inputStream) {
        InflationPolicy inflationPolicy = new InflationPolicy();

        StringBuffer fieldBuffer = new StringBuffer();

        int b = 0;
        while((b = inputStream.read()) != -1) {
            char c = (char) b;

            switch (c) {
                case POLICY_END:
                    if (fieldBuffer.length() > 0) {
                        inflationPolicy.addChildPolicy(getAndEmptyBuffer(fieldBuffer), null);
                    }

                    return inflationPolicy;
                case DELIMITER:
                    if (fieldBuffer.length() > 0) {
                        inflationPolicy.addChildPolicy(getAndEmptyBuffer(fieldBuffer), null);
                    }

                    break;
                case POLICY_START:
                    inflationPolicy.addChildPolicy(getAndEmptyBuffer(fieldBuffer), parse(inputStream));
                    break;
                case SPACE:
                case TAB:
                    break;
                default:
                    fieldBuffer.append(c);
                    break;
            }
        }

        if (fieldBuffer.length() > 0) {
            inflationPolicy.addChildPolicy(getAndEmptyBuffer(fieldBuffer), null);
        }

        return inflationPolicy;
    }


    private String getAndEmptyBuffer(StringBuffer buffer) {
        String value = buffer.toString();
        buffer.setLength(0);
        return value;
    }
}
