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

import com.vladsch.flexmark.util.ValueRunnable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ListenersRunner<L> {
    final private LinkedHashSet<WeakReference<L>> myListeners = new LinkedHashSet<>();

    public ListenersRunner() {
        
    }

    public void fire(ValueRunnable<L> runnable) {
        myListeners.removeIf(reference -> reference.get() == null);

        for (WeakReference<L> listener : myListeners) {
            L l = listener.get();
            if (l != null) {
                runnable.run(l);
            }
        }
    }

    public void addListener(L listener) {
        myListeners.add(new WeakReference<>(listener));
        myListeners.removeIf(reference -> reference.get() == null);
    }

    public void removeListener(L listener) {
        myListeners.removeIf(reference -> reference.get() == null || reference.get() == listener);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListenersRunner)) return false;

        ListenersRunner runner = (ListenersRunner) o;

        return myListeners.equals(runner.myListeners);
    }

    @Override
    public int hashCode() {
        return myListeners.hashCode();
    }
}
