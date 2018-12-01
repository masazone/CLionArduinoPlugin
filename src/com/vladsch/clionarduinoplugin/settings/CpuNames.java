package com.vladsch.clionarduinoplugin.settings;

import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import com.vladsch.plugin.util.ui.DynamicListAdaptable;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComboBox;
import java.util.List;

public class CpuNames extends DynamicListAdaptable<CpuNames> {
    public CpuNames(final int intValue, @NotNull final String displayName) {
        super(intValue, displayName);
    }

    final public static CpuNames EMPTY = new CpuNames(0,"");
    public static DynamicListAdaptable[] values = new DynamicListAdaptable[0];
    final public static Static<DynamicListAdaptable<CpuNames>> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(EMPTY));

    public static void updateValues(String[] valueList, final boolean addEmpty, JComboBox comboBox, CpuNames... exclude) {
        updateValues(asList(valueList), addEmpty, comboBox, exclude);
    }
    
    public static void updateValues(Iterable<String> valueList, final boolean addEmpty, JComboBox comboBox, CpuNames... exclude) {
        values = DynamicListAdaptable.updateValues(EMPTY, valueList, addEmpty, CpuNames::new);
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

    @Override
    public String name() {
        return displayName;
    }

    @Override
    public int getIntValue() {
        return intValue;
    }

    @Override
    public ComboBoxAdapter<DynamicListAdaptable<CpuNames>> getAdapter() {
        return ADAPTER;
    }

    @Override
    public DynamicListAdaptable<CpuNames>[] getValues() {
        //noinspection unchecked
        return values;
    }
}
