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

package com.atypon.wayf.data.cache;

import io.reactivex.Maybe;

public class CascadingCache<K, V> {

    private KeyValueCache<K, V> l1;
    private KeyValueCache<K, V> l2;

    public CascadingCache(KeyValueCache<K, V> l1, KeyValueCache<K, V> l2) {
        this.l1 = l1;
        this.l2 = l2;
    }
    public Maybe<V> get(K key) {
        return Maybe.concat(
                l1.get(key),
                l2.get(key).doOnSuccess((value) -> l1.put(key, value).subscribe())
        ).firstElement();
    }
}
