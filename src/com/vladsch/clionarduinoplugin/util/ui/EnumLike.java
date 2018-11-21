package com.vladsch.clionarduinoplugin.util.ui;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class EnumLike<E extends ComboBoxAdaptable<E>> {
    final public E[] values;
    final public ComboBoxAdaptable.Static<E> ADAPTER;

    @NotNull
    public ArrayList<String> getDisplayNames() {
        ArrayList<String> displayNames = new ArrayList<>();
        for (E value : values) {
            displayNames.add(value.getDisplayName());
        }
        return displayNames;
    }

    public static interface Factory<F> {
        F create(@NotNull EnumLike parent, int intValue, @NotNull String displayName);
    }

    @SuppressWarnings("ThisEscapedInObjectConstruction")
    public EnumLike(String[] valueList, Factory<E> factory, final boolean addEmpty) {
        E dummy = factory.create(this, 0, "");

        //noinspection unchecked
        values = (E[]) Array.newInstance(dummy.getClass(), valueList.length + (addEmpty ? 1 : 0));

        int i = 0;
        if (addEmpty) {
            values[i] = dummy;
            i++;
        }

        for (String port : valueList) {
            values[i] = factory.create(this, i, port);
            i++;
        }

        ADAPTER = new ComboBoxAdaptable.Static<E>(new ComboBoxAdapterImpl<>(values[0]));
    }

    public EnumLike(List<String> valueList, Factory<E> factory, final boolean addEmpty) {
        E dummy = factory.create(this, 0, "");

        //noinspection unchecked
        values = (E[]) Array.newInstance(dummy.getClass(), valueList.size() + (addEmpty ? 1 : 0));

        int i = 0;
        if (addEmpty) {
            values[i] = dummy;
            i++;
        }

        for (String port : valueList) {
            values[i] = factory.create(this, i, port);
            i++;
        }

        ADAPTER = new ComboBoxAdaptable.Static<>(new ComboBoxAdapterImpl<>(values[0]));
    }
}
