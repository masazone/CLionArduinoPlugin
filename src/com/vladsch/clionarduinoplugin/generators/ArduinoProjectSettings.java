package com.vladsch.clionarduinoplugin.generators;

import com.intellij.util.xmlb.annotations.XCollection;
import com.intellij.util.xmlb.annotations.XMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ArduinoProjectSettings {
    protected String languageVersion;
    protected String libraryType;
    protected boolean addLibraryDirectory;
    protected String libraryDirectory;
    protected String board;
    protected String cpu;
    protected String programmer;
    protected String port;
    protected boolean verbose;
    protected HashMap<String, String> boardCpu;
    protected LinkedHashSet<String> portHistory;

    public ArduinoProjectSettings(
            final String languageVersion,
            final String libraryType,
            final boolean addLibraryDirectory,
            final String libraryDirectory,
            String board,
            String cpu,
            String programmer,
            String port,
            boolean verbose,
            @Nullable Map<String, String> boardCpu,
            @Nullable Collection<String> portHistory
    ) {
        this.languageVersion = languageVersion;
        this.libraryType = libraryType;
        this.addLibraryDirectory = addLibraryDirectory;
        this.libraryDirectory = libraryDirectory;
        this.board = board;
        this.cpu = cpu;
        this.programmer = programmer;
        this.port = port;
        this.verbose = verbose;
        this.boardCpu = boardCpu == null ? new HashMap<>() : new HashMap<>(boardCpu);
        setPortHistory(portHistory == null ? Collections.EMPTY_LIST : portHistory);
    }

    public ArduinoProjectSettings() {
        this("", "", false, "", "", "", "", "", false, null, null);
    }

    public ArduinoProjectSettings(ArduinoProjectSettings other) {
        this(other.languageVersion,
                other.libraryType,
                other.addLibraryDirectory,
                other.libraryDirectory,
                other.board,
                other.cpu,
                other.programmer,
                other.port,
                other.verbose,
                other.boardCpu,
                other.portHistory
        );
    }

    public void setLanguageVersion(@NotNull String languageVersion) {
        this.languageVersion = languageVersion;
    }

    public void setLibraryType(@NotNull String libraryType) {
        this.libraryType = libraryType;
    }

    public String getLanguageVersion() {
        return languageVersion;
    }

    public String getLibraryType() {
        return libraryType;
    }

    public String getLibraryDirectory() {
        return libraryDirectory;
    }

    public boolean isAddLibraryDirectory() {
        return addLibraryDirectory;
    }

    public void setAddLibraryDirectory(final boolean addLibraryDirectory) {
        this.addLibraryDirectory = addLibraryDirectory;
    }

    public void setLibraryDirectory(@NotNull String libraryDirectory) {
        this.libraryDirectory = libraryDirectory;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(final String board) {
        this.board = board;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(final String cpu) {
        this.cpu = cpu;
    }

    public String getProgrammer() {
        return programmer;
    }

    public void setProgrammer(final String programmer) {
        this.programmer = programmer;
    }

    public String getPort() {
        return port;
    }

    public void setPort(final String port) {
        this.port = port;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    @XMap
    public Map<String, String> getBoardCpu() {
        return boardCpu;
    }

    @XMap
    public void setBoardCpu(final Map<String, String> boardCpu) {
        this.boardCpu = new HashMap<>(boardCpu);
    }

    @XCollection
    public Collection<String> getPortHistory() {
        return portHistory;
    }

    @XCollection
    public void setPortHistory(final Collection<String> portHistory) {
        this.portHistory = new LinkedHashSet<>();
        for (String port : portHistory) {
            if (port != null && !port.isEmpty()) {
                this.portHistory.add(port);
            }
        }
    }

    public void addPortHistory(@Nullable final String port) {
        if (port != null && !port.isEmpty()) {
            LinkedHashSet<String> portHistory = new LinkedHashSet<>();
            portHistory.add(port);
            portHistory.addAll(this.portHistory);
            this.portHistory = portHistory;
        }
    }

    public void setBoardCpu(String board, String cpu) {
        this.boardCpu.put(board, cpu);
    }
}
