package com.vladsch.clionarduinoplugin.components;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.vladsch.clionarduinoplugin.generators.ArduinoNewProjectSettings;
import org.jetbrains.annotations.NotNull;

@State(name = "Arduino Support Settings",
        storages = @Storage("arduino-plugin-settings.xml")
)
public class ArduinoApplicationSettingsService implements PersistentStateComponent<ArduinoNewProjectSettings> {
    final private ArduinoNewProjectSettings state;

    public ArduinoApplicationSettingsService() {
        state = new ArduinoNewProjectSettings();
    }

    public static ArduinoApplicationSettingsService getInstance() {
        return ServiceManager.getService(ArduinoApplicationSettingsService.class);
    }

    @NotNull
    @Override
    public ArduinoNewProjectSettings getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull ArduinoNewProjectSettings configurationState) {
        state.copyFrom(configurationState);
    }

    //@Override
    //public void noStateLoaded() {
    //    state = new ArduinoNewProjectSettings();
    //}
}
