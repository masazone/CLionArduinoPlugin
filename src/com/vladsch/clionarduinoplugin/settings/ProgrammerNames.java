package com.vladsch.clionarduinoplugin.settings;

import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import com.vladsch.plugin.util.ui.DynamicListAdaptable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComboBox;
import java.util.List;

public class ProgrammerNames extends DynamicListAdaptable<ProgrammerNames> {
    public ProgrammerNames(final int intValue, @NotNull final String displayName) {
        super(intValue, displayName);
    }

    final public static ProgrammerNames EMPTY = new ProgrammerNames(0,"");
    public static DynamicListAdaptable[] values = new DynamicListAdaptable[0];
    final public static Static<DynamicListAdaptable<ProgrammerNames>> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(EMPTY));

    public static void updateValues(@NotNull String[] valueList, final boolean addEmpty, @Nullable JComboBox<String> comboBox, ProgrammerNames... exclude) {
        updateValues(asList(valueList), addEmpty, comboBox, exclude);
    }
    
    public static void updateValues(List<String> valueList, final boolean addEmpty, @Nullable JComboBox<String> comboBox, ProgrammerNames... exclude) {
        values = DynamicListAdaptable.updateValues(EMPTY, valueList, addEmpty, ProgrammerNames::new);
        //noinspection unchecked
        ADAPTER.setDefaultValue(values[0]);

        if (comboBox != null) {
            ADAPTER.fillComboBox(comboBox, exclude);
        }
    }

    public static List<String> getDisplayNames() {
        return getDisplayNames(values);
    }

    @Override
    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    @NotNull
    @Override
    public String name() {
        return displayName;
    }

    @Override
    public int getIntValue() {
        return intValue;
    }

    @NotNull
    @Override
    public ComboBoxAdapter<DynamicListAdaptable<ProgrammerNames>> getAdapter() {
        return ADAPTER;
    }

    @NotNull
    @Override
    public DynamicListAdaptable<ProgrammerNames>[] getValues() {
        //noinspection unchecked
        return values;
    }
}
