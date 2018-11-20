package com.vladsch.clionarduinoplugin.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.components.JBCheckBox;
import com.vladsch.clionarduinoplugin.components.ArduinoApplicationSettings;
import com.vladsch.clionarduinoplugin.util.ApplicationSettingsListener;
import com.vladsch.clionarduinoplugin.util.ui.ComboBoxAdaptable;
import com.vladsch.clionarduinoplugin.util.ui.EnumLike;
import com.vladsch.clionarduinoplugin.util.ui.Settable;
import com.vladsch.clionarduinoplugin.util.ui.SettingsComponents;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;

public class NewProjectSettingsForm implements Disposable, ApplicationSettingsListener {
    private JPanel myMainPanel;
    JComboBox myLanguageVersion;
    JComboBox myLibraryType;
    JComboBox myBoards;
    private JLabel myCpuLabel;
    JComboBox myCpus;
    JComboBox myProgrammers;
    JBCheckBox myAddLibraryDirectory;
    JBCheckBox myVerbose;
    TextFieldWithHistory myPort;

    JComboBox myLibraryCategories;
    JTextField myLibraryDirectory;
    JTextField myAuthorName;
    JTextField myAuthorEMail;
    private JLabel myLibraryCategoryLabel;
    private JLabel myLibraryTypeLabel;
    private JLabel myAuthorNameLabel;
    private JLabel myAuthorEMailLabel;

    boolean inUpdate = false;

    EnumLike<SerialPortNames> mySerialPortNames;
    EnumLike<LanguageVersionNames> myLanguageVersionNames;
    EnumLike<LibraryTypeNames> myLibraryTypeNames;
    EnumLike<LibraryCategoryNames> myLibraryCategoryNames;
    EnumLike<BoardNames> myBoardNames;
    EnumLike<CpuNames> myCpuNames;
    EnumLike<ProgrammerNames> myProgrammerNames;

    public JComponent getComponent() {
        return myMainPanel;
    }

    final ArduinoApplicationSettings mySettings;

    private final SettingsComponents<ArduinoApplicationSettings> components;

    public NewProjectSettingsForm(boolean isLibrary, boolean immediateUpdate) {
        mySettings = ServiceManager.getService(ArduinoApplicationSettings.class);

        components = new SettingsComponents<ArduinoApplicationSettings>() {
            @Override
            protected Settable[] getComponents(ArduinoApplicationSettings i) {
                return new Settable[] {
                        componentString(mySerialPortNames.ADAPTER, myPort, i::getPort, i::setPort),
                        componentString(myLanguageVersionNames.ADAPTER, myLanguageVersion, i::getPort, i::setPort),
                        componentString(myLibraryTypeNames.ADAPTER, myLibraryType, i::getPort, i::setPort),
                        componentString(myBoardNames.ADAPTER, myBoards, i::getPort, i::setPort),
                        componentString(myCpuNames.ADAPTER, myCpus, i::getPort, i::setPort),
                        componentString(myProgrammerNames.ADAPTER, myProgrammers, i::getPort, i::setPort),
                        component(myAuthorName, i::getAuthorName, i::setAuthorName),
                        component(myAuthorEMail, i::getAuthorEMail, i::setAuthorEMail),
                        //component(myBundledBoardsTxt, i::isBundledBoardsTxt, i::setBundledBoardsTxt),
                        //component(myBundledProgrammersTxt, i::isBundledProgrammersTxt, i::setBundledProgrammersTxt),
                        //component(myBoardsTxtPath.getTextField(), i::getBoardsTxtPath, i::setBoardsTxtPath),
                        //component(myProgrammersTxtPath.getTextField(), i::getProgrammersTxtPath, i::setProgrammersTxtPath),
                };
            }
        };

        ApplicationManager.getApplication().getMessageBus().connect(this).subscribe(ApplicationSettingsListener.TOPIC, this);

        if (immediateUpdate) {
            // TODO: setup a timer of 1 second interval to check if modified then update settings

        }

        myCpuLabel.setText(mySettings.getCpuLabel());
        setVisibility(isLibrary);
    }

    void setVisibility(boolean isLibrary) {
        myLibraryCategories.setVisible(isLibrary);
        myLibraryDirectory.setVisible(isLibrary);
        myAuthorName.setVisible(isLibrary);
        myAuthorEMail.setVisible(isLibrary);
        myLibraryCategoryLabel.setVisible(isLibrary);
        myLibraryTypeLabel.setVisible(isLibrary);
        myAuthorNameLabel.setVisible(isLibrary);
        myAuthorEMailLabel.setVisible(isLibrary);
    }

    void guard(Runnable runnable) {
        if (!inUpdate) {
            try {
                inUpdate = true;
                runnable.run();
            } finally {
                inUpdate = false;
            }
        }
    }

    void updateEnums() {
        mySerialPortNames = SerialPortNames.createEnum(true);
        myBoardNames = BoardNames.createEnum();
        myCpuNames = CpuNames.createEnum();
        myProgrammerNames = ProgrammerNames.createEnum();
        myLibraryCategoryNames = LibraryCategoryNames.createEnum();
    }

    private void createUIComponents() {
        updateEnums();

        myLanguageVersionNames = LanguageVersionNames.createEnum();
        myLibraryTypeNames = LibraryTypeNames.createEnum();

        myLanguageVersion = myLanguageVersionNames.ADAPTER.createComboBox();
        myLibraryType = myLibraryTypeNames.ADAPTER.createComboBox();
        myBoards = myBoardNames.ADAPTER.createComboBox();
        myCpus = myCpuNames.ADAPTER.createComboBox();
        myProgrammers = myProgrammerNames.ADAPTER.createComboBox();
        myLibraryCategories = myLibraryCategoryNames.ADAPTER.createComboBox();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void onSettingsChanged() {
        guard(() -> {
            reset(mySettings);
        });
    }

    public boolean isModified(@NotNull ArduinoApplicationSettings settings) {
        return components.isModified(settings);
    }

    public void apply(@NotNull ArduinoApplicationSettings settings) {
        guard(() -> {
            settings.groupChanges(() -> {
                components.apply(settings);
            });
        });
    }

    public void reset(@NotNull ArduinoApplicationSettings settings) {
        guard(() -> {
            components.reset(settings);

            updateEnums();

            myCpuLabel.setText(mySettings.getCpuLabel());

            myBoards.removeAllItems();
            myBoardNames.ADAPTER.fillComboBox(myBoards, ComboBoxAdaptable.EMPTY);
            myBoards.setSelectedItem(settings.getBoard());
            if (!myBoards.getSelectedItem().equals(settings.getBoard())) {
                settings.setBoard((String)myBoards.getSelectedItem());

                // relaod CPUs
                myCpuNames = CpuNames.createEnum();
            }

            if (myCpuNames.values.length == 0) {

            }
            myCpus.removeAllItems();
            myCpuNames.ADAPTER.fillComboBox(myCpus, ComboBoxAdaptable.EMPTY);
            myCpus.setSelectedItem(settings.getCpu());
            if (!myCpus.getSelectedItem().equals(settings.getCpu())) {
                settings.setCpu((String)myCpus.getSelectedItem());
            }

            myProgrammers.removeAllItems();
            myProgrammerNames.ADAPTER.fillComboBox(myProgrammers, ComboBoxAdaptable.EMPTY);

            myLibraryCategories.removeAllItems();
            myLibraryCategoryNames.ADAPTER.fillComboBox(myLibraryCategories, ComboBoxAdaptable.EMPTY);

            ArrayList<String> ports = mySerialPortNames.getDisplayNames();
            String port = settings.getPort();

            if (!port.isEmpty()) {
                ports.add(0, port);
            }

            myPort.setHistorySize(-1);
            myPort.setHistory(ports);
            myPort.setText(port);
        });
    }
}
