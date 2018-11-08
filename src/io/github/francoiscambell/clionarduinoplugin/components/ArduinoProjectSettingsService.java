package io.github.francoiscambell.clionarduinoplugin.components;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import io.github.francoiscambell.clionarduinoplugin.generators.ArduinoProjectSettings;
import org.jetbrains.annotations.NotNull;

@State(name = "ArduinoProject",
        storages = @Storage("arduino-settings.xml")
)
public class ArduinoProjectSettingsService implements PersistentStateComponent<ArduinoProjectSettings> {
    private ArduinoProjectSettings state;

    public ArduinoProjectSettingsService(@NotNull Project project) {
        state = new ArduinoProjectSettings();
    }

    @NotNull
    public static ArduinoProjectSettingsService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, ArduinoProjectSettingsService.class);
    }

    public static ArduinoProjectSettingsService getInstance() {
        return ServiceManager.getService(ArduinoProjectSettingsService.class);
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
