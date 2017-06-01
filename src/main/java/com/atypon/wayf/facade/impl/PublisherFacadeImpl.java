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

package com.atypon.wayf.facade.impl;

import com.atypon.wayf.dao.PublisherDao;
import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.data.publisher.PublisherQuery;
import com.atypon.wayf.data.publisher.PublisherStatus;
import com.atypon.wayf.facade.PublisherFacade;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.apache.http.HttpStatus;

import static com.atypon.wayf.reactivex.FacadePolicies.singleOrException;

@Singleton
public class PublisherFacadeImpl implements PublisherFacade {

    @Inject
    private PublisherDao publisherDao;

    public PublisherFacadeImpl() {
    }

    @Override
    public Single<Publisher> create(Publisher publisher) {
        publisher.setStatus(PublisherStatus.ACTIVE);

        return publisherDao.create(publisher);
    }

    @Override
    public Single<Publisher> read(Long id) {
        return singleOrException(publisherDao.read(id), HttpStatus.SC_NOT_FOUND, "Invalid Publisher ID");
    }

    @Override
    public Observable<Publisher> filter(PublisherQuery filter) {
        return publisherDao.filter(filter);
    }
}
