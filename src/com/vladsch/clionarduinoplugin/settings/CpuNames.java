package com.vladsch.clionarduinoplugin.settings;

import com.vladsch.clionarduinoplugin.util.ui.ComboBoxAdaptable;
import com.vladsch.clionarduinoplugin.util.ui.ComboBoxAdapter;
import com.vladsch.clionarduinoplugin.util.ui.EnumLike;
import org.jetbrains.annotations.NotNull;

public class CpuNames implements ComboBoxAdaptable<CpuNames> {
    public final EnumLike parent;
    public final int intValue;
    public final @NotNull String displayName;

    public static EnumLike<CpuNames> createEnum(@NotNull String[] cpuNames) {
        return new EnumLike<>(cpuNames, CpuNames::new, true);
    }

    public CpuNames(final EnumLike parent, final int intValue, @NotNull final String displayName) {
        this.parent = parent;
        this.intValue = intValue;
        this.displayName = displayName;
    }

    @Override
    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String name() {
        return displayName;
    }

    @Override
    public int getIntValue() {
        return intValue;
    }

    @Override
    public ComboBoxAdapter<CpuNames> getAdapter() {
        //noinspection unchecked
        return parent.ADAPTER;
    }

    @Override
    public CpuNames[] getValues() {
        return (CpuNames[]) parent.values;
    }
}
