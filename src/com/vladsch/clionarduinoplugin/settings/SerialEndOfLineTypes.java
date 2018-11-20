package com.vladsch.clionarduinoplugin.settings;

import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.clionarduinoplugin.util.ui.ComboBoxAdaptable;
import com.vladsch.clionarduinoplugin.util.ui.ComboBoxAdapter;
import com.vladsch.clionarduinoplugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum SerialEndOfLineTypes implements ComboBoxAdaptable<SerialEndOfLineTypes> {
    NONE(0, "", Bundle.message("settings.eol.none")),
    LF(1, "\n", Bundle.message("settings.eol.lf")),
    CR(2, "\r", Bundle.message("settings.eol.cr")),
    CR_LF(3, "\r\n", Bundle.message("settings.eol.crlf")),
    ;

    public final int intValue;
    public final @NotNull String displayName;
    public final @NotNull String eol;

    SerialEndOfLineTypes(int intValue, @NotNull String eol, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.eol = eol;
    }

    public static Static<SerialEndOfLineTypes> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(LF));

    @Override
    public ComboBoxAdapter<SerialEndOfLineTypes> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public SerialEndOfLineTypes[] getValues() { return values(); }
}
