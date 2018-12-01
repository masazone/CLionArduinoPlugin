package com.vladsch.clionarduinoplugin.settings;

import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import com.vladsch.plugin.util.ui.DynamicListAdaptable;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComboBox;
import java.util.List;

public class BoardNames extends DynamicListAdaptable<BoardNames> {
    public BoardNames(final int intValue, @NotNull final String displayName) {
        super(intValue, displayName);
    }

    final public static BoardNames EMPTY = new BoardNames(0, "");
    public static DynamicListAdaptable[] values = new DynamicListAdaptable[0];
    final public static Static<DynamicListAdaptable<BoardNames>> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(EMPTY));

    public static void updateValues(String[] valueList, final boolean addEmpty, JComboBox comboBox, BoardNames... exclude) {
        updateValues(asList(valueList), addEmpty, comboBox, exclude);
    }

    public static void updateValues(Iterable<String> valueList, final boolean addEmpty, JComboBox comboBox, BoardNames... exclude) {
        values = DynamicListAdaptable.updateValues(EMPTY, valueList, addEmpty, BoardNames::new);
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
    public ComboBoxAdapter<DynamicListAdaptable<BoardNames>> getAdapter() {
        return ADAPTER;
    }

    @Override
    public DynamicListAdaptable<BoardNames>[] getValues() {
        //noinspection unchecked
        return values;
    }
}
