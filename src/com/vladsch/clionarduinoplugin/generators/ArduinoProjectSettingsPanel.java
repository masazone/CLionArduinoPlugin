package com.vladsch.clionarduinoplugin.generators;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.settings.ui.CMakeSettingsPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.util.List;

public class ArduinoProjectSettingsPanel extends CMakeSettingsPanel {
    ComboBox<String> myLanguageVersionComboBox;
    ComboBox<String> myLibraryTypeComboBox;
    ComboBox<String> myBoardsComboBox;
    ComboBox<String> myCpusComboBox;
    ComboBox<String> myProgrammersComboBox;
    JBCheckBox myAddLibraryDirectory;
    JBCheckBox myVerbose;

    //TextFieldWithBrowseButton myLibraryDirectory;
    JTextField myLibraryDirectory;
    TextFieldWithHistory myPort;

    boolean inUpdate = false;

    public ArduinoProjectSettingsPanel(@NotNull ArduinoProjectGeneratorBase projectGenerator) {
        super(projectGenerator);
        init(projectGenerator);
    }

    public void init(@NotNull final ArduinoProjectGeneratorBase projectGenerator) {
        setLayout(new BorderLayout());
        boolean isLibrary = projectGenerator.addLibrarySettingsPanel();
        String[] boards = projectGenerator.getBoardNames();
        String[] programmers = projectGenerator.getProgrammerNames();

        int rows = (isLibrary ? 6 : 6) + (boards != null ? 2 : 0) + (programmers != null ? 2 : 0);
        GridLayoutManager layoutManager = new GridLayoutManager(rows, 2);
        JPanel panel = new JPanel(layoutManager);

        int row = 0;

        GridConstraints gridConstraints = setConstraints(row, 0);
        JLabel label = new JLabel("Language standard:");
        panel.add(label, gridConstraints);
        myLanguageVersionComboBox = new ComboBox<>(projectGenerator.getLanguageVersions());
        String languageVersion = projectGenerator.getLanguageVersion();
        if (languageVersion != null) {
            myLanguageVersionComboBox.setSelectedItem(languageVersion);
        }

        myLanguageVersionComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                projectGenerator.setLanguageVersion((String) e.getItem());
            }
        });

        gridConstraints = setConstraints(row++, 1);
        panel.add(myLanguageVersionComboBox, gridConstraints);
        label.setDisplayedMnemonic('s');
        label.setLabelFor(myLanguageVersionComboBox);

        if (boards != null) {
            gridConstraints = setConstraints(row, 0);
            JLabel labelBoard = new JLabel("Board:");
            panel.add(labelBoard, gridConstraints);
            myBoardsComboBox = new ComboBox<>(boards);
            String board = projectGenerator.getBoard();
            if (board != null && !board.isEmpty()) {
                myBoardsComboBox.setSelectedItem(board);
            } else {
                board = (String) myBoardsComboBox.getSelectedItem();
            }

            gridConstraints = setConstraints(row++, 1);
            gridConstraints.setFill(GridConstraints.FILL_HORIZONTAL);
            panel.add(myBoardsComboBox, gridConstraints);
            gridConstraints.setFill(GridConstraints.FILL_NONE);
            labelBoard.setDisplayedMnemonic('b');
            labelBoard.setLabelFor(myBoardsComboBox);

            // create CPU dropdown
            String[] cpus = projectGenerator.getBoardCpuNames(board);
            gridConstraints = setConstraints(row, 0);
            JLabel labelCpu = new JLabel(projectGenerator.getCpuLabel());
            panel.add(labelCpu, gridConstraints);
            myCpusComboBox = new ComboBox<>(cpus == null ? new String[0] : cpus);
            String cpu = projectGenerator.getCpu();
            if (cpu == null && cpus != null && cpus.length > 0) {
                // need to initialize the cpu
                cpu = (String) myCpusComboBox.getSelectedItem();
                if (cpu == null) cpu = cpus[0];
                projectGenerator.setCpu(cpu);
            }

            if (cpu != null) {
                myCpusComboBox.setSelectedItem(cpu);
            }

            myCpusComboBox.addItemListener(e -> {
                if (!inUpdate && e.getStateChange() == ItemEvent.SELECTED) {
                    inUpdate = true;
                    projectGenerator.setCpu((String) e.getItem());
                    inUpdate = false;
                }
            });

            myBoardsComboBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    projectGenerator.setBoard((String) e.getItem());

                    String[] cpus1 = projectGenerator.getBoardCpuNames(projectGenerator.getBoard());
                    if (cpus1 != null) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            inUpdate = true;
                            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) myCpusComboBox.getModel();
                            model.removeAllElements();
                            for (String cpu1 : cpus1) {
                                model.addElement(cpu1);
                            }

                            String cpu1 = projectGenerator.getCpu();
                            if (cpu1 == null) {
                                // need to initialize the cpu
                                cpu1 = (String) myCpusComboBox.getSelectedItem();
                                if (cpu1 == null) cpu1 = cpus1[0];
                                projectGenerator.setCpu(cpu1);
                            }
                            myCpusComboBox.setSelectedItem(cpu1);

                            inUpdate = false;

                            myCpusComboBox.setVisible(true);
                            labelCpu.setVisible(true);
                        }, ModalityState.any());
                    } else {
                        myCpusComboBox.setVisible(false);
                        labelCpu.setVisible(false);
                    }
                }
            });

            gridConstraints = setConstraints(row++, 1);
            gridConstraints.setFill(GridConstraints.FILL_HORIZONTAL);
            panel.add(myCpusComboBox, gridConstraints);
            gridConstraints.setFill(GridConstraints.FILL_NONE);
            labelCpu.setDisplayedMnemonic('c');
            labelCpu.setLabelFor(myCpusComboBox);
        }

        if (programmers != null) {
            gridConstraints = setConstraints(row, 0);
            JLabel labelProg = new JLabel("Programmer:");
            panel.add(labelProg, gridConstraints);
            myProgrammersComboBox = new ComboBox<>(programmers);
            String programmer = projectGenerator.getProgrammer();
            if (programmer != null) {
                myProgrammersComboBox.setSelectedItem(programmer);
            }

            myProgrammersComboBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    projectGenerator.setProgrammer((String) e.getItem());
                }
            });

            gridConstraints = setConstraints(row++, 1);
            gridConstraints.setFill(GridConstraints.FILL_HORIZONTAL);
            panel.add(myProgrammersComboBox, gridConstraints);
            gridConstraints.setFill(GridConstraints.FILL_NONE);
            labelProg.setDisplayedMnemonic('g');
            labelProg.setLabelFor(myProgrammersComboBox);

            gridConstraints = setConstraints(row, 0);
            JLabel labelPort = new JLabel("Port:");
            panel.add(labelPort, gridConstraints);
            //myPort = new JTextField(30);
            //String port = projectGenerator.getPort();
            //if (port != null) {
            //    myPort.setText(port);
            //}
            //
            ////myPort.getTextField().getDocument().addDocumentListener(new DocumentListener() {
            //myPort.getDocument().addDocumentListener(new DocumentListener() {
            //    @Override
            //    public void insertUpdate(final DocumentEvent e) {
            //        projectGenerator.setPort(myPort.getText());
            //    }
            //
            //    @Override
            //    public void removeUpdate(final DocumentEvent e) {
            //        projectGenerator.setPort(myPort.getText());
            //    }
            //
            //    @Override
            //    public void changedUpdate(final DocumentEvent e) {
            //        projectGenerator.setPort(myPort.getText());
            //    }
            //});

            myPort = new TextFieldWithHistory();
            @Nullable List<String> ports = projectGenerator.getPorts();
            String port = projectGenerator.getPort();

            myPort.setHistorySize(-1);
            if (ports != null) {
                myPort.setHistory(ports);
            }

            if (port != null && !port.isEmpty()) {
                myPort.setText(port);
            }

            myPort.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    projectGenerator.setPort((String) e.getItem());
                }
            });

            myPort.addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(final DocumentEvent e) {
                    projectGenerator.setPort(myPort.getText());
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    projectGenerator.setPort(myPort.getText());
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    projectGenerator.setPort(myPort.getText());
                }
            });

            gridConstraints = setConstraints(row++, 1);
            gridConstraints.setFill(GridConstraints.FILL_HORIZONTAL);
            panel.add(myPort, gridConstraints);
            gridConstraints.setFill(GridConstraints.FILL_NONE);
        }

        gridConstraints = setConstraints(row++, 0);
        myVerbose = new JBCheckBox("Verbose Build Process");
        panel.add(myVerbose, gridConstraints);

        myVerbose.setSelected(projectGenerator.isVerbose());

        myVerbose.addItemListener(e -> {
            projectGenerator.setVerbose(myVerbose.isSelected());
        });

        if (isLibrary) {
            gridConstraints = setConstraints(row, 0);
            JLabel libraryLabel = new JLabel("Library type:");
            panel.add(libraryLabel, gridConstraints);
            myLibraryTypeComboBox = new ComboBox<>(projectGenerator.getLibraryTypes());
            String libraryType = projectGenerator.getLibraryType();
            if (libraryType != null) {
                myLibraryTypeComboBox.setSelectedItem(libraryType);
            }

            myLibraryTypeComboBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    projectGenerator.setLibraryType((String) e.getItem());
                }
            });

            gridConstraints = setConstraints(row++, 1);
            panel.add(myLibraryTypeComboBox, gridConstraints);
            libraryLabel.setDisplayedMnemonic('i');
            libraryLabel.setLabelFor(myLibraryTypeComboBox);
        } else {
            gridConstraints = setConstraints(row, 0);
            myAddLibraryDirectory = new JBCheckBox("Add library sub-directory:");
            panel.add(myAddLibraryDirectory, gridConstraints);

            myAddLibraryDirectory.setSelected(projectGenerator.isAddLibraryDirectory());

            myAddLibraryDirectory.addItemListener(e -> {
                projectGenerator.setAddLibraryDirectory(myAddLibraryDirectory.isSelected());
                myLibraryDirectory.setEnabled(myAddLibraryDirectory.isSelected());
            });

            //myLibraryDirectory = new TextFieldWithBrowseButton(
            //        new JTextField(
            //                "",
            //                20)
            //        , new ActionListener() {
            //            @Override
            //            public void actionPerformed(ActionEvent e) {
            //                int tmp = 0;
            //            }
            //        }
            //);
            //
            //myLibraryDirectory.addBrowseFolderListener(
            //        "Select Library Sub-Directory",
            //        null,
            //        null,
            //        new FileChooserDescriptor(false,
            //                true,
            //                false,
            //                false,
            //                false,
            //                false));

            myLibraryDirectory = new JTextField(30);
            String libraryDirectory = projectGenerator.getLibraryDirectory();
            if (libraryDirectory == null) {
                myAddLibraryDirectory.setSelected(false);
                myLibraryDirectory.setEnabled(false);
            } else {
                myLibraryDirectory.setEnabled(myAddLibraryDirectory.isSelected());
                myLibraryDirectory.setText(libraryDirectory);
            }

            //myLibraryDirectory.getTextField().getDocument().addDocumentListener(new DocumentListener() {
            myLibraryDirectory.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(final DocumentEvent e) {
                    projectGenerator.setLibraryDirectory(myLibraryDirectory.getText());
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    projectGenerator.setLibraryDirectory(myLibraryDirectory.getText());
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    projectGenerator.setLibraryDirectory(myLibraryDirectory.getText());
                }
            });

            gridConstraints = setConstraints(row++, 1);
            panel.add(myLibraryDirectory, gridConstraints);
        }

        add(panel, "West");
    }

    @NotNull
    private static GridConstraints setConstraints(int row, int column) {
        GridConstraints constraints = new GridConstraints();
        constraints.setRow(row);
        constraints.setColumn(column);
        constraints.setAnchor(8);
        return constraints;
    }
}
