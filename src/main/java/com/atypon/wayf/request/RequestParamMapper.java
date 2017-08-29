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
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.http.HttpStatus;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class RequestParamMapper {

    private static BeanUtilsBean enumConverterUtilsBean = new BeanUtilsBean(new ConvertUtilsBean() {
        @Override
        public Object convert(String value, Class clazz) {
            if (clazz.isEnum()) {
                return Enum.valueOf(clazz, value);
            }

            return super.convert(value, clazz);
        }

        @Override
        public Object convert(Object value, Class clazz) {
            if (clazz.isArray() && String.class.isAssignableFrom(value.getClass())) {
                String[] tokenizedValues = ((String)value).split(",");
                return convert(tokenizedValues, clazz);
            }

            return super.convert(value, clazz);
        }

        @Override
        public Object convert(String[] values, Class<?> clazz) {
            if (clazz.isArray() && clazz.getComponentType().isEnum()) {
                Object enumValues = Array.newInstance(clazz.getComponentType(), values.length);

                for (int i = 0; i < values.length; i++) {
                    Array.set(enumValues, i, Enum.valueOf((Class)clazz.getComponentType(), values[i]));
                }

                return enumValues;
            }

            return super.convert(values, clazz);
        }
    });

    public static void mapParams(RoutingContext routingContext, Object pojo) {
        MultiMap params = routingContext.request().params();

        for (Map.Entry<String, String> queryParam : params.entries()) {
            if (enumConverterUtilsBean.getPropertyUtils().isWriteable(pojo, queryParam.getKey())) {
                try {
                    enumConverterUtilsBean.setProperty(pojo, queryParam.getKey(), queryParam.getValue());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new ServiceException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Could not process query param [" + queryParam.getKey() + "]");
                }
            }
        }
    }
}
