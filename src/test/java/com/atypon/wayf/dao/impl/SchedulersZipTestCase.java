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

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;

public class SchedulersZipTestCase {

    @Test
    public void runTest() {
        Single.zip(
                Single.just("abc").map((a) -> { System.out.println(Thread.currentThread().getName());return a;}).subscribeOn(Schedulers.io()),
                Single.just("abc").map((a) -> { System.out.println(Thread.currentThread().getName());return a;}).subscribeOn(Schedulers.io()),
                Single.just("abc").map((a) -> { System.out.println(Thread.currentThread().getName());return a;}).subscribeOn(Schedulers.io()),

                (a,b,c) -> {System.out.println("Finished"); return Single.just("");}
        ).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .blockingGet();


    }
}
