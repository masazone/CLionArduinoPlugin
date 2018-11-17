package com.vladsch.clionarduinoplugin.settings;

import com.intellij.openapi.Disposable;
import com.intellij.ui.components.JBCheckBox;
import com.vladsch.clionarduinoplugin.components.ArduinoProjectSettings;
import com.vladsch.clionarduinoplugin.util.ui.Settable;
import com.vladsch.clionarduinoplugin.util.ui.SettingsComponents;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SendSettingsForm implements Disposable {

    private JPanel myMainPanel;
    JComboBox mySerialEndOfLineTypes;
    JBCheckBox myLogSentText;
    JBCheckBox myIsImmediateSend;
    Runnable myChangeMonitor = null;
    boolean myInChangeUpdate = false;

    public JComponent getComponent() {
        return myMainPanel;
    }

    private final SettingsComponents<ArduinoProjectSettings> components;

    public SendSettingsForm() {
        components = new SettingsComponents<ArduinoProjectSettings>() {
            @Override
            protected Settable[] getComponents(ArduinoProjectSettings i) {
                return new Settable[] {
                        //componentString(mySerialPortNames.ADAPTER, myPort, i::getPort, i::setPort),
                        component(SerialEndOfLineTypes.ADAPTER, mySerialEndOfLineTypes, i::getSerialEndOfLine, i::setSerialEndOfLine),
                        component(myLogSentText, i::isLogSentText, i::setLogSentText),
                        component(myIsImmediateSend, i::isImmediateSend, i::setImmediateSend),
                };
            }
        };

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (myChangeMonitor != null && !myInChangeUpdate) {
                    myInChangeUpdate = true;
                    myChangeMonitor.run();
                    myInChangeUpdate = false;
                }
            }
        };

        mySerialEndOfLineTypes.addActionListener(listener);
        myLogSentText.addActionListener(listener);
        myIsImmediateSend.addActionListener(listener);
    }

    public void setChangeMonitor(Runnable changeMonitor) {
        myChangeMonitor = changeMonitor;
    }

    void updateOptions(boolean onInit) {

    }

    private void createUIComponents() {
        final ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {updateOptions(false);}
        };

        mySerialEndOfLineTypes = SerialEndOfLineTypes.ADAPTER.createComboBox();
    }

    public boolean isModified(@NotNull ArduinoProjectSettings settings) {
        return components.isModified(settings);
    }

    public void apply(@NotNull ArduinoProjectSettings settings) {
        settings.groupChanges(()->{
            components.apply(settings);
        });
    }

    public void reset(@NotNull ArduinoProjectSettings settings) {
        boolean inChange = myInChangeUpdate;
        myInChangeUpdate = true;
        components.reset(settings);
        myInChangeUpdate = inChange;
    }

    @Override
    public void dispose() {

    }
}
