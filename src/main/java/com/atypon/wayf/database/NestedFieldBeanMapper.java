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

package com.atypon.wayf.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.vertx.core.cli.annotations.Name;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Singleton
class NestedFieldBeanMapper {
    private static final Logger LOG = LoggerFactory.getLogger(NestedFieldBeanMapper.class);

    private static final String DELIMITER = ".";
    private static final String REGEX_DELIMITER = "\\.";

    @Inject
    @Named("beanFactoryMap")
    private Map<Class<?>, BeanFactory<?>> beanFactoryMap;

    private BeanUtilsBean enumConverterUtilsBean = new BeanUtilsBean(new ConvertUtilsBean() {
        @Override
        public Object convert(String value, Class clazz) {
            if (clazz.isEnum()){
                return Enum.valueOf(clazz, value);
            } else {
                return super.convert(value, clazz);
            }
        }
    });

    public NestedFieldBeanMapper() {
    }

    public <T> T map(Map<String, Object> row, Class<T> type) {
        try {
            T bean = null;

            if (beanFactoryMap.get(type) != null) {
                bean = (T) beanFactoryMap.get(type).createInstance(row);
            } else {
                bean = type.newInstance();
            }

            for (String key : row.keySet()) {
                Object value = row.get(key);

                if (value == null) {
                    continue;
                }

                if (key.contains(DELIMITER)) {
                    String[] pathFields = key.split(REGEX_DELIMITER);

                    handleNestedValue(bean, pathFields, 0, value);
                } else {
                    enumConverterUtilsBean.setProperty(bean, key, value);
                }
            }

            return bean;
        } catch (Exception e) {
            throw new RuntimeException("Could not process resultset row [" + row + "] for type [" + type + "]", e);
        }
    }

    private Object handleNestedValue(Object bean, String[] path, int index, Object value) throws Exception {
        LOG.debug("Handling nested value for bean[{}] path[{}] index[{}] value[{}]", bean, path, index, value);

        String fieldName = path[index];

        if (index == path.length - 1) {
            LOG.debug("Setting bean[{}] field[{}] to value[{}]", bean, fieldName, value);

            if (value != null) {
                enumConverterUtilsBean.setProperty(bean, fieldName, value);
            } else {
                return null;
            }
        } else {
            Object childBean = enumConverterUtilsBean.getPropertyUtils().getProperty(bean, fieldName);

            if (childBean == null) {
                childBean = enumConverterUtilsBean.getPropertyUtils().getPropertyType(bean, fieldName).newInstance();
            }

            LOG.debug("Recursing for childBean bean[{}] field[{}]", childBean, fieldName);

            enumConverterUtilsBean.setProperty(bean, fieldName, handleNestedValue(childBean, path, ++index, value));
        }

        return bean;
    }
}
