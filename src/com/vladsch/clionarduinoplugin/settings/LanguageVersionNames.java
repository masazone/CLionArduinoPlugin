package com.vladsch.clionarduinoplugin.settings;

import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import com.vladsch.plugin.util.ui.DynamicListAdaptable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComboBox;
import java.util.List;

public class LanguageVersionNames extends DynamicListAdaptable<LanguageVersionNames> {
    public LanguageVersionNames(final int intValue, @NotNull final String displayName) {
        super(intValue, displayName);
    }

    final public static LanguageVersionNames EMPTY = new LanguageVersionNames(0, "");
    public static DynamicListAdaptable[] values = new DynamicListAdaptable[0];
    final public static Static<DynamicListAdaptable<LanguageVersionNames>> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(EMPTY));

    public static void updateValues(@Nullable JComboBox<String> comboBox, LanguageVersionNames... exclude) {
        values = DynamicListAdaptable.updateValues(EMPTY, asList(ArduinoApplicationSettings.LANGUAGE_VERSIONS), false, LanguageVersionNames::new);
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
    public ComboBoxAdapter<DynamicListAdaptable<LanguageVersionNames>> getAdapter() {
        return ADAPTER;
    }

    @NotNull
    @Override
    public DynamicListAdaptable<LanguageVersionNames>[] getValues() {
        //noinspection unchecked
        return values;
    }
}
