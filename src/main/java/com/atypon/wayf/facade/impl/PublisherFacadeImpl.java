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
import com.atypon.wayf.facade.AuthenticationFacade;
import com.atypon.wayf.facade.PublisherFacade;
import com.atypon.wayf.facade.ClientJsFacade;
import com.atypon.wayf.reactivex.DaoPolicies;
import com.atypon.wayf.reactivex.FacadePolicies;
import com.google.common.collect.Lists;
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

    @Inject
    private AuthenticationFacade authenticationFacade;

    @Inject
    private ClientJsFacade clientJsFacade;

    public PublisherFacadeImpl() {
    }

    @Override
    public Single<Publisher> create(Publisher publisher) {
        publisher.setStatus(PublisherStatus.ACTIVE);

        return publisherDao.create(publisher) // Create the publisher
                .flatMap((createdPublisher) ->

                        Single.zip(
                                // Create an authorization token for the newly created publisher
                                authenticationFacade.createToken(createdPublisher).compose(single -> FacadePolicies.applySingle(single)),

                                // Generate the publisher specific Javascript widget
                                clientJsFacade.generateWidgetForPublisher(createdPublisher).compose(single -> FacadePolicies.applySingle(single)),

                                // Combine the results with the previously created publisher
                                (token, filename) -> {
                                    createdPublisher.setToken(token);
                                    createdPublisher.setWidgetLocation(filename);
                                    return createdPublisher;
                                }
                        )
                );
    }

    @Override
    public Single<Publisher> read(Long id) {
        return singleOrException(publisherDao.read(id), HttpStatus.SC_NOT_FOUND, "Invalid Publisher ID");
    }

    @Override
    public Observable<Publisher> filter(PublisherQuery filter) {
        return publisherDao.filter(filter);
    }

    @Override
    public Single<Publisher> lookupCode(String publisherCode) {
        PublisherQuery query = new PublisherQuery().setCodes(Lists.newArrayList(publisherCode));

        return singleOrException(filter(query), HttpStatus.SC_BAD_REQUEST, "Could not find publisher for code [{}]", publisherCode);
    }
}
