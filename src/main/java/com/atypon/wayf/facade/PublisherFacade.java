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

package com.atypon.wayf.facade;

import com.atypon.wayf.data.publisher.Publisher;
import com.atypon.wayf.data.publisher.PublisherQuery;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface PublisherFacade {
    Single<Publisher> create(Publisher publisher);
    Single<Publisher> read(Long id);
    Observable<Publisher> filter(PublisherQuery filter);

    Single<Publisher> lookupCode(String publisherCode);

    String getPublishersSalt(Long publisherId);
}
