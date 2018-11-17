package com.vladsch.clionarduinoplugin.settings;

import com.vladsch.clionarduinoplugin.util.Utils;
import com.vladsch.clionarduinoplugin.util.ui.ComboBoxAdaptable;
import com.vladsch.clionarduinoplugin.util.ui.ComboBoxAdapter;
import com.vladsch.clionarduinoplugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SerialPortNames implements ComboBoxAdaptable<SerialPortNames> {
    public final EnumLike parent;
    public final int intValue;
    public final @NotNull String displayName;

    public SerialPortNames(final EnumLike parent, final int intValue, @NotNull final String displayName) {
        this.parent = parent;
        this.intValue = intValue;
        this.displayName = displayName;
    }

    @Override
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

    static class EnumLike {
        final SerialPortNames[] values;
        final public ComboBoxAdaptable.Static<SerialPortNames> ADAPTER;

        public EnumLike(boolean filtered) {
            ArrayList<String> ports = Utils.getSerialPorts(filtered);
            values = new SerialPortNames[ports.size() + 1];

            int i = 0;
            values[i] = new SerialPortNames(this, i, "");
            i++;

            for (String port : ports) {
                values[i] = new SerialPortNames(this, i, port);
                i++;
            }

            ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(values[0]));
        }
    }

    @Override
    public ComboBoxAdapter<SerialPortNames> getAdapter() {
        return parent.ADAPTER;
    }

    @Override
    public SerialPortNames[] getValues() {
        return parent.values;
    }
}
