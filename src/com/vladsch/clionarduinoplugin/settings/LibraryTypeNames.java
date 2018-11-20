package com.vladsch.clionarduinoplugin.settings;

import com.vladsch.clionarduinoplugin.components.ArduinoApplicationSettings;
import com.vladsch.clionarduinoplugin.util.ui.ComboBoxAdaptable;
import com.vladsch.clionarduinoplugin.util.ui.ComboBoxAdapter;
import com.vladsch.clionarduinoplugin.util.ui.EnumLike;
import org.jetbrains.annotations.NotNull;

public class LibraryTypeNames implements ComboBoxAdaptable<LibraryTypeNames> {
    public final EnumLike parent;
    public final int intValue;
    public final @NotNull String displayName;

    public static EnumLike<LibraryTypeNames> createEnum() {
        return new EnumLike<>(ArduinoApplicationSettings.LIBRARY_TYPES, LibraryTypeNames::new, false);
    }

    public LibraryTypeNames(final EnumLike parent, final int intValue, @NotNull final String displayName) {
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
    public ComboBoxAdapter<LibraryTypeNames> getAdapter() {
        //noinspection unchecked
        return parent.ADAPTER;
    }

    @Override
    public LibraryTypeNames[] getValues() {
        return (LibraryTypeNames[]) parent.values;
    }
}
