package com.vladsch.clionarduinoplugin.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.Alarm;
import com.vladsch.clionarduinoplugin.util.ApplicationSettingsListener;
import com.vladsch.clionarduinoplugin.util.RecursionGuard;
import com.vladsch.clionarduinoplugin.util.ui.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    JBCheckBox myCommentOutUnusedSettings;

    JComboBox myLibraryCategory;
    JTextField myLibraryDirectory;
    JTextField myLibraryDisplayName;
    JTextField myAuthorName;
    JTextField myAuthorEMail;
    private JLabel myLibraryCategoryLabel;
    private JLabel myLibraryTypeLabel;
    private JLabel myAuthorNameLabel;
    private JLabel myAuthorEMailLabel;
    JComboBox myBaudRate;
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
    final boolean myIsLibrary;
    private final boolean isImmediateUpdate;
    private final boolean isLimitedConfig;
    Runnable myRunnable = null;

    public enum ErrorComp {
        CPU,
        BOARD,
        LIB_DIR,
    }

    @Nullable
    public JComponent getErrorComponent(ErrorComp comp) {
        switch (comp) {
            case CPU:
                return myCpus;
            case BOARD:
                return myBoards;
            case LIB_DIR:
                return myLibraryDirectory;

            default:
                return null;
        }
    }

    @Nullable
    public JComponent getPreferredFocusedComponent() {
        return myBoards;
    }

    public NewProjectSettingsForm(ArduinoApplicationSettingsProxy settings, boolean immediateUpdate, final boolean limitedConfig) {
        super(settings.getApplicationSettings());

        myIsLibrary = settings.isLibrary();
        isImmediateUpdate = immediateUpdate;
        isLimitedConfig = limitedConfig;

        myUpdate = new Alarm(this);
        myPendingUpdates = new LinkedHashSet<>();

        components = new SettingsComponents<ArduinoApplicationSettings>() {
            @Override
            protected Settable[] createComponents(ArduinoApplicationSettings i) {
                if (myIsLibrary) {
                    return new Settable[] {
                            componentString(mySerialPortNames.ADAPTER, myPort, i::getPort, i::setPort),
                            component(SerialBaudRates.ADAPTER, myBaudRate, i::getBaudRate, i::setBaudRate),
                            componentString(myBoardNames.ADAPTER, myBoards, i::getBoardName, i::setBoardName),
                            componentString(myCpuNames.ADAPTER, myCpus, i::getCpuName, i::setCpuName),
                            componentString(myProgrammerNames.ADAPTER, myProgrammers, i::getProgrammerName, i::setProgrammerName),
                            component(myAddLibraryDirectory, i::isAddLibraryDirectory, i::setAddLibraryDirectory),
                            component(myLibraryDirectory, i::getLibraryDirectory, i::setLibraryDirectory),
                            component(myVerbose, i::isVerbose, i::setVerbose),
                            component(myCommentOutUnusedSettings, i::isCommentUnusedSettings, i::setCommentUnusedSettings),
                            componentString(myLanguageVersionNames.ADAPTER, myLanguageVersion, i::getLanguageVersionName, i::setLanguageVersionName),

                            // library only
                            component(myLibraryDisplayName, i::getLibraryDisplayName, i::setLibraryDisplayName),
                            componentString(myLibraryTypeNames.ADAPTER, myLibraryType, i::getLibraryType, i::setLibraryType),
                            componentString(myLibraryCategoryNames.ADAPTER, myLibraryCategory, i::getLibraryCategory, i::setLibraryCategory),
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
                            component(myCommentOutUnusedSettings, i::isCommentUnusedSettings, i::setCommentUnusedSettings),
                            componentString(myLanguageVersionNames.ADAPTER, myLanguageVersion, i::getLanguageVersionName, i::setLanguageVersionName),
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

        reset(mySettings);
        updateOptions(true);
    }

    public void setRunnable(final Runnable myRunnable) {
        this.myRunnable = myRunnable;
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
        if (!(changed instanceof JTextComponent)) {
            boolean updateCpus = changed == myBoards;
            updateOptions(updateCpus);
        }

        if (isImmediateUpdate) {
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
        } else {
            if (myUpdate.isDisposed()) {
                return;
            }

            myUpdate.cancelAllRequests();
            if (myRunnable != null) {
                myUpdate.addRequest(() -> {
                    myRunnable.run();
                }, 250, ModalityState.any());
            }
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
            boolean isArduinoLibrary = myIsLibrary && ArduinoProjectFileSettings.ARDUINO_LIB_TYPE.equals(myLibraryType.getSelectedItem());
            myLibraryTypeLabel.setVisible(myIsLibrary && !isLimitedConfig);
            myLibraryType.setVisible(myIsLibrary && !isLimitedConfig);

            myLibraryCategoryLabel.setVisible(isArduinoLibrary && !isLimitedConfig);
            myLibraryCategory.setVisible(isArduinoLibrary && !isLimitedConfig);

            myAuthorNameLabel.setVisible(isArduinoLibrary && !isLimitedConfig);
            myAuthorName.setVisible(isArduinoLibrary && !isLimitedConfig);

            myAuthorEMailLabel.setVisible(isArduinoLibrary && !isLimitedConfig);
            myAuthorEMail.setVisible(isArduinoLibrary && !isLimitedConfig);

            myLibraryDisplayName.setVisible(isArduinoLibrary && !isLimitedConfig);
            myLibraryDisplayNameLabel.setVisible(isArduinoLibrary && !isLimitedConfig);

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
        
        myLibraryCategoryNames = LibraryCategoryNames.createEnum();
        myLibraryCategory = myLibraryCategoryNames.ADAPTER.createComboBox();
    }

    @Override
    public void onSettingsChanged(ArduinoApplicationSettings settings) {
        if (mySettings == settings) {
            reset(mySettings);
        }
    }

    public boolean isModified(@NotNull ArduinoApplicationSettings settings) {
        return components.isModified(settings);
    }

    public void apply(@NotNull ArduinoApplicationSettings settings) {
        if (isModified(settings)) {
            if (settings == mySettings) {
                myRecursionGuard.enter(SETTINGS_UPDATE, () -> {
                    settings.groupChanges(() -> {
                        components.apply(settings);
                    });
                });
            } else {
                settings.groupChanges(() -> {
                    components.apply(settings);
                });
            }
        }
    }

    public void reset(@NotNull ArduinoApplicationSettings settings) {
        // always set to settings, but prevent callbacks to modify settings
        if (myRecursionGuard.enter(RESET_UPDATE, () -> {
            unguardedReset(settings);
        })) {
            // after reset we update options
            updateOptions(false);
        }
    }

    void unguardedReset(final @NotNull ArduinoApplicationSettings settings) {
        components.reset(settings);

        updateEnums();

        myCpuLabel.setText(mySettings.getCpuLabel());

        myBoardNames.ADAPTER.fillComboBox(myBoards, ComboBoxAdaptable.EMPTY);
        myBoards.setSelectedItem(settings.getBoardName());

        updateCpus();

        myProgrammerNames.ADAPTER.fillComboBox(myProgrammers, ComboBoxAdaptable.EMPTY);
        myProgrammers.setSelectedItem(settings.getProgrammerName());

        myLibraryCategoryNames.ADAPTER.fillComboBox(myLibraryCategory, ComboBoxAdaptable.EMPTY);
        myLibraryCategory.setSelectedItem(settings.getLibraryCategory());

        updatePort(settings);
    }

    public JComponent getComponent() {
        return myMainPanel;
    }
}
