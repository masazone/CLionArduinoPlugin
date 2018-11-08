package com.vladsch.clionarduinoplugin.components;

import com.intellij.openapi.components.*;
import com.vladsch.clionarduinoplugin.generators.ArduinoProjectSettings;
import org.jetbrains.annotations.NotNull;

@State(name = "ArduinoPlugin",
        storages = @Storage("arduino-plugin-settings.xml")
)
public class ArduinoApplicationSettingsService implements PersistentStateComponent<ArduinoProjectSettings> {
    private ArduinoProjectSettings state;

    public static ArduinoApplicationSettingsService getInstance() {
        return ServiceManager.getService(ArduinoApplicationSettingsService.class);
    }

    @NotNull
    @Override
    public ArduinoProjectSettings getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull ArduinoProjectSettings configurationState) {
        state = configurationState;
    }

    @Override
    public void noStateLoaded() {
        state = new ArduinoProjectSettings();
    }
}
