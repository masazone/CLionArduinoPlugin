package com.vladsch.clionarduinoplugin.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.Alarm;
import com.vladsch.clionarduinoplugin.components.ArduinoApplicationSettings;
import com.vladsch.clionarduinoplugin.util.ApplicationSettingsListener;
import com.vladsch.clionarduinoplugin.util.RecursionGuard;
import com.vladsch.clionarduinoplugin.util.ui.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class NewProjectSettingsForm extends FormParams<ArduinoApplicationSettings> implements Disposable, ApplicationSettingsListener, SettableForm<ArduinoApplicationSettings> {
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
    JTextField myLibraryDisplayName;
    JTextField myAuthorName;
    JTextField myAuthorEMail;
    private JLabel myLibraryCategoryLabel;
    private JLabel myLibraryTypeLabel;
    private JLabel myAuthorNameLabel;
    private JLabel myAuthorEMailLabel;
    private JComboBox myBaudRate;
    private JLabel myLibraryDisplayNameLabel;

    EnumLike<SerialPortNames> mySerialPortNames;
    EnumLike<LanguageVersionNames> myLanguageVersionNames;
    EnumLike<LibraryTypeNames> myLibraryTypeNames;
    EnumLike<LibraryCategoryNames> myLibraryCategoryNames;
    EnumLike<BoardNames> myBoardNames;
    EnumLike<CpuNames> myCpuNames;
    EnumLike<ProgrammerNames> myProgrammerNames;

    final Set<Object> myPendingUpdates;

    private Alarm myUpdate;
    RecursionGuard myRecursionGuard = new RecursionGuard();

    private final SettingsComponents<ArduinoApplicationSettings> components;
    private final boolean myIsLibrary;
    private final boolean myIsImmediateUpdate;

    public NewProjectSettingsForm(ArduinoApplicationSettings settings, boolean isLibrary, boolean immediateUpdate) {
        super(settings);

        myIsLibrary = isLibrary;
        myIsImmediateUpdate = immediateUpdate;
        myUpdate = new Alarm(this);
        myPendingUpdates = new LinkedHashSet<>();

        components = new SettingsComponents<ArduinoApplicationSettings>() {
            @Override
            protected Settable[] createComponents(ArduinoApplicationSettings i) {
                if (isLibrary) {
                    return new Settable[] {
                            componentString(mySerialPortNames.ADAPTER, myPort, i::getPort, i::setPort),
                            component(SerialBaudRates.ADAPTER, myBaudRate, i::getBaudRate, i::setBaudRate),
                            componentString(myBoardNames.ADAPTER, myBoards, i::getBoardName, i::setBoardName),
                            componentString(myCpuNames.ADAPTER, myCpus, i::getCpuName, i::setCpuName),
                            componentString(myProgrammerNames.ADAPTER, myProgrammers, i::getProgrammerName, i::setProgrammerName),
                            component(myAddLibraryDirectory, i::isAddLibraryDirectory, i::setAddLibraryDirectory),
                            component(myLibraryDirectory, i::getLibraryDirectory, i::setLibraryDirectory),
                            component(myLibraryDisplayName, i::getLibraryDisplayName, i::setLibraryDisplayName),
                            component(myVerbose, i::isVerbose, i::setVerbose),
                            componentString(myLanguageVersionNames.ADAPTER, myLanguageVersion, i::getLanguageVersion, i::setLanguageVersion),

                            // library only
                            componentString(myLibraryTypeNames.ADAPTER, myLibraryType, i::getLibraryType, i::setLibraryType),
                            component(myAuthorName, i::getAuthorName, i::setAuthorName),
                            component(myAuthorEMail, i::getAuthorEMail, i::setAuthorEMail),
                    };
                } else {
                    return new Settable[] {
                            componentString(mySerialPortNames.ADAPTER, myPort, i::getPort, i::setPort),
                            component(SerialBaudRates.ADAPTER, myBaudRate, i::getBaudRate, i::setBaudRate),
                            componentString(myBoardNames.ADAPTER, myBoards, i::getBoardName, i::setBoardName),
                            componentString(myCpuNames.ADAPTER, myCpus, i::getCpuName, i::setCpuName),
                            componentString(myProgrammerNames.ADAPTER, myProgrammers, i::getProgrammerName, i::setProgrammerName),
                            component(myAddLibraryDirectory, i::isAddLibraryDirectory, i::setAddLibraryDirectory),
                            component(myLibraryDirectory, i::getLibraryDirectory, i::setLibraryDirectory),
                            component(myVerbose, i::isVerbose, i::setVerbose),
                            componentString(myLanguageVersionNames.ADAPTER, myLanguageVersion, i::getLanguageVersion, i::setLanguageVersion),
                    };
                }
            }
        };

        ApplicationManager.getApplication().getMessageBus().connect(this).subscribe(ApplicationSettingsListener.TOPIC, this);

        ItemListener itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    applyChanges(e.getSource());
                }
            }
        };

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                applyChanges(e.getSource());
            }
        };

        components.forAllComponents(mySettings, settable -> {
            Object target = settable.getComponent();
            if (target instanceof AbstractButton) {
                ((AbstractButton) target).addActionListener(actionListener);
            } else {
                if (target instanceof ItemSelectable) {
                    ((ItemSelectable) target).addItemListener(itemListener);
                }

                if (target instanceof JTextComponent) {
                    ((JTextComponent) target).getDocument().addDocumentListener(new DocumentAdapter() {
                        @Override
                        protected void textChanged(@NotNull final DocumentEvent e) {
                            applyChanges(target);
                        }
                    });
                }
            }
        });

        if (immediateUpdate) {
            reset(mySettings);
        } else {
            updateOptions(true);
        }
    }

    @Override
    public void dispose() {
        myUpdate = null;
        components.dispose();
        myPendingUpdates.clear();
    }

    static final int RESET_UPDATE = 5;
    static final int COMPONENT_UPDATE = 4;
    static final int SETTINGS_UPDATE = 0;

    void applyChanges(Object changed) {
        // don't update on every keystroke for text components
        if (!(changed instanceof JTextComponent)) {
            boolean updateCpus = changed == myBoards;
            updateOptions(updateCpus);
        }

        if (myIsImmediateUpdate) {
            if (myUpdate.isDisposed()) {
                myPendingUpdates.clear();
                return;
            }

            myRecursionGuard.enter(SETTINGS_UPDATE, () -> {
                Collections.addAll(myPendingUpdates, changed);

                myUpdate.cancelAllRequests();
                myUpdate.addRequest(() -> {
                    mySettings.groupChanges(() -> {
                        Object[] targets = myPendingUpdates.toArray();
                        myPendingUpdates.clear();
                        components.apply(mySettings, targets);
                    });
                }, 250, ModalityState.any());
            });
        }
    }

    public void updatePort(final @NotNull ArduinoApplicationSettings settings) {
        ArrayList<String> ports = mySerialPortNames.getDisplayNames();
        myPort.setHistorySize(-1);
        myPort.setHistory(ports);
        myPort.setText(settings.getPort());
    }

    void updateOptions(boolean updateCpus) {
        myRecursionGuard.enter(COMPONENT_UPDATE, () -> {
            boolean isArduinoLibrary = myIsLibrary && ArduinoApplicationSettings.ARDUINO_LIB_TYPE.equals(myLibraryType.getSelectedItem());
            myLibraryTypeLabel.setVisible(myIsLibrary);
            myLibraryType.setVisible(myIsLibrary);

            myLibraryCategoryLabel.setVisible(isArduinoLibrary);
            myLibraryCategories.setVisible(isArduinoLibrary);

            myAuthorNameLabel.setVisible(isArduinoLibrary);
            myAuthorName.setVisible(isArduinoLibrary);

            myAuthorEMailLabel.setVisible(isArduinoLibrary);
            myAuthorEMail.setVisible(isArduinoLibrary);
            myLibraryDisplayName.setVisible(isArduinoLibrary);
            myLibraryDisplayNameLabel.setVisible(isArduinoLibrary);

            myLibraryDirectory.setEnabled(myAddLibraryDirectory.isSelected());

            if (updateCpus) {
                updateCpus();
            }
        });
    }

    void updateCpus() {
        updateCpuEnum();

        myCpuNames.ADAPTER.fillComboBox(myCpus, ComboBoxAdaptable.EMPTY);
        myCpus.setSelectedItem(mySettings.getCpuName());

        myCpuLabel.setEnabled(myCpuNames.values.length > 1);
        myCpus.setEnabled(myCpuNames.values.length > 1);
    }

    void updateEnums() {
        mySerialPortNames = SerialPortNames.createEnum(true);
        myBoardNames = BoardNames.createEnum(mySettings.getBoardNames());
        myProgrammerNames = ProgrammerNames.createEnum(mySettings.getProgrammerNames());
        myLibraryCategoryNames = LibraryCategoryNames.createEnum();
    }

    void updateCpuEnum() {
        myCpuNames = CpuNames.createEnum(mySettings.getBoardCpuNames((String) myBoards.getSelectedItem()));
    }

    private void createUIComponents() {
        updateEnums();

        myLanguageVersionNames = LanguageVersionNames.createEnum();
        myLibraryTypeNames = LibraryTypeNames.createEnum();

        myLanguageVersion = myLanguageVersionNames.ADAPTER.createComboBox();
        myLibraryType = myLibraryTypeNames.ADAPTER.createComboBox();

        myBoards = myBoardNames.ADAPTER.createComboBox();
        myBaudRate = SerialBaudRates.ADAPTER.createComboBox();

        updateCpuEnum();
        myCpus = myCpuNames.ADAPTER.createComboBox();

        myProgrammers = myProgrammerNames.ADAPTER.createComboBox();
        myLibraryCategories = myLibraryCategoryNames.ADAPTER.createComboBox();
    }

    @Override
    public void onSettingsChanged() {
        reset(mySettings);
    }

    public boolean isModified(@NotNull ArduinoApplicationSettings settings) {
        return components.isModified(settings);
    }

    public void apply(@NotNull ArduinoApplicationSettings settings) {
        if (isModified(settings)) {
            myRecursionGuard.enter(SETTINGS_UPDATE, () -> {
                settings.groupChanges(() -> {
                    components.apply(settings);
                });
            });
        }
    }

    public void reset(@NotNull ArduinoApplicationSettings settings) {
        // always set to settings, but prevent callbacks to modify settings
        if (myRecursionGuard.enter(RESET_UPDATE, () -> {
            components.reset(settings);

            updateEnums();

            myCpuLabel.setText(mySettings.getCpuLabel());

            myBoardNames.ADAPTER.fillComboBox(myBoards, ComboBoxAdaptable.EMPTY);
            myBoards.setSelectedItem(settings.getBoardName());

            updateCpus();

            myProgrammerNames.ADAPTER.fillComboBox(myProgrammers, ComboBoxAdaptable.EMPTY);
            myProgrammers.setSelectedItem(settings.getProgrammerName());

            myLibraryCategoryNames.ADAPTER.fillComboBox(myLibraryCategories, ComboBoxAdaptable.EMPTY);

            updatePort(settings);
        })) {
            // after reset we update options
            updateOptions(false);
        }
    }

    public JComponent getComponent() {
        return myMainPanel;
    }
}
