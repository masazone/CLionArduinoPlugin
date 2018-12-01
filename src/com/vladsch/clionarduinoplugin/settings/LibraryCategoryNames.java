package com.vladsch.clionarduinoplugin.settings;

import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import com.vladsch.plugin.util.ui.DynamicListAdaptable;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComboBox;
import java.util.List;

public class LibraryCategoryNames extends DynamicListAdaptable<LibraryCategoryNames> {
    public LibraryCategoryNames(final int intValue, @NotNull final String displayName) {
        super(intValue, displayName);
    }

    final public static LibraryCategoryNames EMPTY = new LibraryCategoryNames(0, "");
    public static DynamicListAdaptable[] values = new DynamicListAdaptable[0];
    final public static Static<DynamicListAdaptable<LibraryCategoryNames>> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(EMPTY));

    public static void updateValues(JComboBox comboBox, LibraryCategoryNames... exclude) {
        values = DynamicListAdaptable.updateValues(EMPTY, asList(ArduinoProjectFileSettings.LIBRARY_CATEGORIES), false, LibraryCategoryNames::new);
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
    public ComboBoxAdapter<DynamicListAdaptable<LibraryCategoryNames>> getAdapter() {
        return ADAPTER;
    }

    @Override
    public DynamicListAdaptable<LibraryCategoryNames>[] getValues() {
        //noinspection unchecked
        return values;
    }
}
