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

package com.atypon.wayf.dao.impl;

import com.atypon.wayf.dao.ErrorLoggerDao;
import com.atypon.wayf.data.ErrorLogEntry;
import com.atypon.wayf.database.DbExecutor;
import com.atypon.wayf.reactivex.DaoPolicies;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.reactivex.Completable;

@Singleton
public class ErrorLoggerDaoDbImpl implements ErrorLoggerDao {

    @Inject
    @Named("error-logger.dao.db.create")
    private String create;

    @Inject
    private DbExecutor dbExecutor;

    @Override
    public Completable logError(ErrorLogEntry logEntry) {
        return dbExecutor.executeUpdate(create, logEntry).compose((single) -> DaoPolicies.applySingle(single)).toCompletable();
    }
}
