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

package com.vladsch.clionarduinoplugin.util.ui;

import com.intellij.openapi.Disposable;
import com.vladsch.flexmark.util.ValueRunnable;
import org.gradle.internal.impldep.com.google.api.services.storage.model.Objects;
import org.jetbrains.annotations.NotNull;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.text.JTextComponent;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class SettingsComponents<T> implements SettingsConfigurable<T>, Disposable {
    protected Settable<T>[] mySettables;

    public SettingsComponents() {
        mySettables = null;
    }

    @Override
    public void dispose() {
        mySettables = null;
    }

    protected Settable<T>[] getComponents(T i) {
        if (mySettables == null) {
            mySettables = createComponents(i);
        }
        return mySettables;
    }

    protected abstract Settable<T>[] createComponents(T i);

    public Set<Settable<T>> getComponentSet(T i) {
        return new LinkedHashSet<>(Arrays.asList(getComponents(i)));
    }

    public void forAllComponents(T i, ValueRunnable<Settable<T>> runnable) {
        for (Settable<T> settable : getComponents(i)) {
            runnable.run(settable);
        }
    }

    public void forAllTargets(T i, Object[] targets, ValueRunnable<Settable<T>> runnable) {
        Set<Object> targetSet = new LinkedHashSet<>();
        Collections.addAll(targetSet, targets);

        forAllComponents(i, settable -> {
            if (targetSet.contains(settable.getComponent())) runnable.run(settable);
        });
    }

    @Override
    public void reset(T i) {
        forAllComponents(i, Settable::reset);
    }

    @Override
    public T apply(T i) {
        forAllComponents(i, Settable::apply);
        return i;
    }

    @Override
    public boolean isModified(T i) {
        for (Settable<T> settable : getComponents(i)) {
            if (settable.isModified()) return true;
        }
        return false;
    }

    public void apply(T i, Object... targets) {
        forAllTargets(i, targets, Settable::apply);
    }

    public void reset(T i, Object... targets) {
        forAllTargets(i, targets, Settable::reset);
    }

    // @formatter:off
    @NotNull public CheckBoxSetter component(JCheckBox component, Getter<Boolean> getter, Setter<Boolean> setter) { return new CheckBoxSetter(component, getter, setter); }
    @NotNull public RadioButtonSetter component(JRadioButton component, Getter<Boolean> getter, Setter<Boolean> setter) { return new RadioButtonSetter(component, getter, setter); }
    @NotNull public <V> SpinnerSetter<V> component(JSpinner component, Getter<V> getter, Setter<V> setter) { return new SpinnerSetter<V>(component, getter, setter); }
    @NotNull public TextBoxSetter component(JTextComponent component, Getter<String> getter, Setter<String> setter) { return new TextBoxSetter(component, getter, setter); }
    @NotNull public ColorCheckBoxSetter component(CheckBoxWithColorChooser component, Getter<java.awt.Color> getter, Setter<java.awt.Color> setter) { return new ColorCheckBoxSetter(component, getter, setter); }
    @NotNull public ColorCheckBoxEnabledSetter componentEnabled(CheckBoxWithColorChooser component, Getter<Boolean> getter, Setter<Boolean> setter) { return new ColorCheckBoxEnabledSetter(component, getter, setter); }
    // @formatter:on

    @NotNull
    public ComboBoxBooleanSetter component(@NotNull ComboBoxBooleanAdapter adapter, JComboBox component, Getter<Boolean> getter, Setter<Boolean> setter) { return new ComboBoxBooleanSetter(component, adapter, getter, setter); }

    @NotNull
    public ComboBoxSetter component(@NotNull ComboBoxAdapter adapter, JComboBox component, Getter<Integer> getter, Setter<Integer> setter) { return new ComboBoxSetter(component, adapter, getter, setter); }

    @NotNull
    public ComboBoxStringSetter componentString(@NotNull ComboBoxAdapter adapter, JComboBox component, Getter<String> getter, Setter<String> setter) { return new ComboBoxStringSetter(component, adapter, getter, setter); }

    @NotNull
    public JComponentSettableForm<T> component(@NotNull SettableForm<T> component, @NotNull T settings) { return new JComponentSettableForm<>(component, settings);}

    public static class CheckBoxSetter extends JComponentSettable<Boolean> {
        public CheckBoxSetter(@NotNull JCheckBox component, @NotNull Getter<Boolean> getter, @NotNull Setter<Boolean> setter) {
            super(component, component::isSelected, component::setSelected, getter, setter);
        }
    }

    public static class ColorCheckBoxEnabledSetter extends JComponentSettable<Boolean> {
        public ColorCheckBoxEnabledSetter(@NotNull CheckBoxWithColorChooser component, @NotNull Getter<Boolean> getter, @NotNull Setter<Boolean> setter) {
            super(component, component::isSelected, component::setSelected, getter, setter);
        }
    }

    public static class RadioButtonSetter extends JComponentSettable<Boolean> {
        public RadioButtonSetter(@NotNull JRadioButton component, @NotNull Getter<Boolean> getter, @NotNull Setter<Boolean> setter) {
            super(component, component::isSelected, component::setSelected, getter, setter);
        }
    }

    public static class ComboBoxBooleanSetter extends JComponentSettable<Boolean> {
        public ComboBoxBooleanSetter(@NotNull JComboBox component, @NotNull ComboBoxBooleanAdapter adapter, @NotNull Getter<Boolean> getter, @NotNull Setter<Boolean> setter) {
            super(component, () -> adapter.findEnum((String) component.getSelectedItem()) == adapter.getNonDefault(),
                    (value) -> component.setSelectedItem(value ? adapter.getNonDefault().getDisplayName() : adapter.getDefault().getDisplayName()),
                    getter, setter);
        }
    }

    public static class ComboBoxSetter extends JComponentSettable<Integer> {
        public ComboBoxSetter(@NotNull JComboBox component, @NotNull ComboBoxAdapter adapter, @NotNull Getter<Integer> getter, @NotNull Setter<Integer> setter) {
            super(component, () -> adapter.findEnum((String) component.getSelectedItem()).getIntValue(),
                    (value) -> component.setSelectedItem(adapter.findEnum(value).getDisplayName()),
                    getter, setter);
        }
    }

    public static class ComboBoxStringSetter extends JComponentSettable<String> {
        public ComboBoxStringSetter(@NotNull JComboBox component, @NotNull ComboBoxAdapter adapter, @NotNull Getter<String> getter, @NotNull Setter<String> setter) {
            super(component, () -> (String) component.getSelectedItem(),
                    component::setSelectedItem,
                    getter, setter);
        }
    }

    public static class SpinnerSetter<V> extends JComponentSettable<V> {
        public SpinnerSetter(@NotNull JSpinner component, @NotNull Getter<V> getter, @NotNull Setter<V> setter) {
            //noinspection unchecked
            super(component, () -> (V) component.getValue(), component::setValue, getter, setter);
        }
    }

    public static class TextBoxSetter extends JComponentSettable<String> {
        public TextBoxSetter(@NotNull JTextComponent component, @NotNull Getter<String> getter, @NotNull Setter<String> setter) {
            super(component, component::getText, component::setText, getter, setter);
        }
    }

    public static class ColorCheckBoxSetter extends JComponentSettable<java.awt.Color> {
        public ColorCheckBoxSetter(@NotNull CheckBoxWithColorChooser component, @NotNull Getter<java.awt.Color> getter, @NotNull Setter<java.awt.Color> setter) {
            super(component, component::getColor, component::setColor, getter, setter);
        }
    }

    public static class JComponentSettable<V> implements Settable {
        private final @NotNull Object myInstance;
        private final @NotNull Getter<V> myComponentGetter;
        private final @NotNull Setter<V> myComponentSetter;
        private final @NotNull Getter<V> myGetter;
        private final @NotNull Setter<V> mySetter;

        public JComponentSettable(@NotNull final Object instance, @NotNull Getter<V> componentGetter, @NotNull Setter<V> componentSetter, @NotNull Getter<V> getter, @NotNull Setter<V> setter) {
            myInstance = instance;
            myComponentGetter = componentGetter;
            myComponentSetter = componentSetter;
            myGetter = getter;
            mySetter = setter;
        }

        @NotNull
        @Override
        public Object getComponent() {
            return myInstance;
        }

        @Override
        public void reset() {
            //noinspection unchecked
            if (!myGetter.get().equals(myComponentGetter.get())) {
                myComponentSetter.set(myGetter.get());
            }
        }

        @Override
        public void apply() {
            //noinspection unchecked
            if (!myGetter.get().equals(myComponentGetter.get())) {
                mySetter.set(myComponentGetter.get());
            }
        }

        @Override
        public boolean isModified() {
            //noinspection unchecked
            return myGetter.get() == null ? myComponentGetter.get() != null : !myGetter.get().equals(myComponentGetter.get());
        }
    }

    public static class JComponentSettableForm<S> implements Settable<S> {
        private final @NotNull SettableForm<S> myInstance;
        private final @NotNull Runnable myApplier;
        private final @NotNull Runnable myResetter;
        private final @NotNull Getter<Boolean> myIsModified;

        public JComponentSettableForm(@NotNull final SettableForm<S> instance, S settings) {
            myInstance = instance;
            myApplier = new Runnable() {
                @Override
                public void run() {
                    instance.apply(settings);
                }
            };

            myResetter = new Runnable() {
                @Override
                public void run() {
                    instance.reset(settings);
                }
            };

            myIsModified = new Getter<Boolean>() {
                @Override
                public Boolean get() {
                    return instance.isModified(settings);
                }
            };
        }

        @Override
        public void reset() {
            myResetter.run();
        }

        @Override
        public void apply() {
            myApplier.run();
        }

        @Override
        public boolean isModified() {
            return myIsModified.get();
        }

        @Override
        public SettableForm<S> getComponent() {
            return myInstance;
        }
    }
}
