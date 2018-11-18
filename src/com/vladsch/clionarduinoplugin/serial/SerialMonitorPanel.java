package com.vladsch.clionarduinoplugin.serial;

import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBTextField;
import com.vladsch.clionarduinoplugin.actions.ConnectAction;
import com.vladsch.clionarduinoplugin.actions.EditSettingsAction;
import com.vladsch.clionarduinoplugin.actions.ShowSendOptionsAction;
import com.vladsch.clionarduinoplugin.components.ArduinoProjectSettings;
import com.vladsch.clionarduinoplugin.settings.SendSettingsForm;
import com.vladsch.clionarduinoplugin.util.ProjectSettingsListener;
import jssc.SerialPortEvent;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SerialMonitorPanel implements Disposable, SerialPortListener, ProjectSettingsListener {
    private JPanel myPanel;
    private JButton mySendButton;
    private JPanel myConsoleHolder;
    private JPanel myToolbarPanel;
    JBTextField mySendText;
    SendSettingsForm mySendSettings;
    private ConsoleView myConsoleView;

    @NotNull final Project myProject;
    final ArduinoProjectSettings myProjectSettings;

    public SerialMonitorPanel(@NotNull Project project) {
        myProject = project;
        createConsole();

        myProjectSettings = ArduinoProjectSettings.getInstance(myProject);
        mySendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                // send the text
                handleEnter();
            }
        });

        mySendText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    handleEnter();
                } else {
                    super.keyTyped(e);
                    if (myProjectSettings.isImmediateSend()) {
                        SerialProjectComponent.getInstance(myProject).send(String.valueOf(e.getKeyChar()).getBytes());
                    }
                }
            }
        });

        mySendText.setEnabled(false);
        mySendButton.setEnabled(false);

        project.getMessageBus().connect(this).subscribe(ProjectSettingsListener.TOPIC, this);

        mySendSettings.getComponent().setVisible(myProjectSettings.isShowSendOptions());
        mySendSettings.setChangeMonitor(()->{
            mySendSettings.apply(myProjectSettings);
        });

        mySendSettings.reset(myProjectSettings);
    }

    void handleEnter() {
        String eol = myProjectSettings.getSerialEndOfLineType().eol;
        if (myProjectSettings.isImmediateSend()) {
            SerialProjectComponent.getInstance(myProject).send(eol.getBytes());
        } else {
            SerialProjectComponent.getInstance(myProject).send((mySendText.getText() + eol).getBytes());
        }
        mySendText.setText("");
    }

    public ConsoleView getConsoleView() {
        return myConsoleView;
    }

    @Override
    public void onSettingsChanged() {
        mySendSettings.reset(myProjectSettings);
        mySendSettings.getComponent().setVisible(myProjectSettings.isShowSendOptions());

        //mySendButton.setEnabled(settings.isImmediateSend());
    }

    private void createConsole() {
        TextConsoleBuilderImpl consoleBuilder = new TextConsoleBuilderImpl(myProject);
        consoleBuilder.setViewer(true);

        myConsoleView = consoleBuilder.getConsole();
        JComponent consoleComponent = myConsoleView.getComponent();
        myConsoleHolder.add(consoleComponent, BorderLayout.CENTER);

        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new ConnectAction());
        actionGroup.add(new EditSettingsAction());
        actionGroup.add(new ShowSendOptionsAction());
        actionGroup.addAll(filterActions(myConsoleView.createConsoleActions()));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("com.vladsch.clionarduinoplugin.SerialMonitor", actionGroup, false);
        toolbar.setTargetComponent(consoleComponent);
        myToolbarPanel.add(toolbar.getComponent(), BorderLayout.WEST);

        SerialProjectComponent.getInstance(myProject).addPortListener(this);
    }

    @Override
    public void onReceive(final byte[] buf) {
        String s = new String(buf).replaceAll("\r", "");
        myConsoleView.print(s, ConsoleViewContentType.NORMAL_OUTPUT);
    }

    @Override
    public void onSent(final byte[] buf) {
        if (myProjectSettings.isLogSentText()) {
            String s = new String(buf).replaceAll("\r", "");
            myConsoleView.print(s, ConsoleViewContentType.USER_INPUT);
        }
    }

    @Override
    public void onConnect(final String portName, final int baudRate) {
        mySendText.setEnabled(true);
        mySendButton.setEnabled(true);

        if (myProjectSettings.isLogConnectDisconnect()) {
            myConsoleView.print("--------------- Connected " + portName + " @ " + baudRate + " ----------------\n", ConsoleViewContentType.SYSTEM_OUTPUT);
        }
    }

    @Override
    public void onDisconnect(final String portName, final int baudRate) {
        mySendText.setEnabled(false);
        mySendButton.setEnabled(false);

        if (myProjectSettings.isLogConnectDisconnect()) {
            myConsoleView.print("-------------- Disconnected " + portName + " @ " + baudRate + " --------------\n", ConsoleViewContentType.SYSTEM_OUTPUT);
        }
    }

    @Override
    public void onEvent(final SerialPortEvent event) {

    }

    private List<AnAction> filterActions(AnAction[] actions) {
        ArrayList<AnAction> filteredActions = new ArrayList<>();
        addIfFoundAction(filteredActions, actions, ToggleUseSoftWrapsToolbarAction.class);
        addIfFoundAction(filteredActions, actions, ScrollToTheEndToolbarAction.class);
        addIfFoundAction(filteredActions, actions, ActionManager.getInstance().getAction("Print").getClass());
        addIfFoundAction(filteredActions, actions, ConsoleViewImpl.ClearAllAction.class);

        return filteredActions;
    }

    private <T extends AnAction> void addIfFoundAction(ArrayList<AnAction> list, AnAction[] actions, Class<T> actionClass) {
        for (AnAction action : actions) {
            if (actionClass.isInstance(action)) {
                list.add(action);
            }
        }
    }

    public JComponent getComponent() {
        return myPanel;
    }

    @Override
    public void dispose() {
        if (myConsoleView != null) {
            Disposer.dispose(myConsoleView);
            myConsoleView = null;
        }
        //mySerialService.removePortStateListener(portStateListener);
        //mySerialService.removeDataListener(dataListener);
    }
}
