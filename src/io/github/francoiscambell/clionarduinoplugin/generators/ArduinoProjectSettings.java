package io.github.francoiscambell.clionarduinoplugin.generators;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

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

    public ArduinoProjectSettings(final String languageVersion, final String libraryType, final boolean addLibraryDirectory, final String libraryDirectory, String board, String cpu, String programmer, String port, boolean verbose, @Nullable Map<String, String> boardCpu) {
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
    }

    public ArduinoProjectSettings() {
        this("", "", false, "", "", "", "", "", false, null);
    }

    public ArduinoProjectSettings(ArduinoProjectSettings other) {
         this(other.languageVersion, other.libraryType, other.addLibraryDirectory, other.libraryDirectory, other.board, other.cpu, other.programmer, other.port, other.verbose, other.boardCpu);
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

    public HashMap<String, String> getBoardCpu() {
        return boardCpu;
    }

    public void setBoardCpu(final Map<String, String> boardCpu) {
        this.boardCpu = new HashMap<>(boardCpu);
    }

    public void setBoardCpu(String board, String cpu) {
        this.boardCpu.put(board, cpu);
    }
}
