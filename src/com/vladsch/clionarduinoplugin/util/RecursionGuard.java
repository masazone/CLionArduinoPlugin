/*
 * Copyright (c) 2016-2018 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.vladsch.clionarduinoplugin.util;

/**
 * Entry handled by flag value.
 * entry will be allowed if the passed flag is > than the flag of one already inside
 * flag 0 will enter only if no one is inside
 * <p>
 * Not intended for thread safety but for UI recursion when processing events that modify components
 */
public class RecursionGuard {
    private int myInside = 0;

    public boolean enter(int rank, Runnable runnable) {
        if (rank > myInside || myInside == 0) {
            int saved = myInside;
            myInside = rank > 0 ? rank : 1;

            try {
                runnable.run();
            } finally {
                myInside = saved;
            }
            return true;
        }
        return false;
    }
}
