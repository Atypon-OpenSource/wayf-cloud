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

package com.atypon.wayf.dao;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ResultSetProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(ResultSetProcessor.class);

    private static final String DELIMITER = ".";
    private static final String REGEX_DELIMITER = "\\.";

    private static BeanUtilsBean beanUtilsBean = new BeanUtilsBean(new ConvertUtilsBean() {
        @Override
        public Object convert(String value, Class clazz) {
            if (clazz.isEnum()){
                return Enum.valueOf(clazz, value);
            } else {
                return super.convert(value, clazz);
            }
        }
    });

    public <T> T processRow(Map<String, Object> row, Class<T> type) throws Exception {
        T bean = type.newInstance();

        for (String key : row.keySet()) {
            if (key.contains(DELIMITER)) {
                String[] pathFields = key.split(REGEX_DELIMITER);

                handleNestedValue(bean, pathFields, 0, row.get(key));
            } else {
                beanUtilsBean.setProperty(bean, key, row.get(key));
            }
        }

        return bean;
    }

    private Object handleNestedValue(Object bean, String[] path, int index, Object value) throws Exception {
        LOG.debug("Handling nested value for bean[{}] path[{}] index[{}] value[{}]", bean, path, index, value);

        String fieldName = path[index];

        if (index == path.length - 1) {
            LOG.debug("Setting bean[{}] field[{}] to value[{}]", bean, fieldName, value);

            beanUtilsBean.setProperty(bean, fieldName, value);
        } else {
            Object childBean = beanUtilsBean.getPropertyUtils().getProperty(bean, fieldName);

            if (childBean == null) {
                childBean = beanUtilsBean.getPropertyUtils().getPropertyType(bean, fieldName).newInstance();
            }

            LOG.debug("Recursing for childBean bean[{}] field[{}]", childBean, fieldName);

            handleNestedValue(childBean, path, ++index, value);
            beanUtilsBean.setProperty(bean, fieldName, childBean);
        }

        return bean;
    }
}
