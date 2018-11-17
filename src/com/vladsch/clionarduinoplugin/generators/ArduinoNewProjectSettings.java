package com.vladsch.clionarduinoplugin.generators;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.XCollection;
import com.intellij.util.xmlb.annotations.XMap;
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.settings.CMakeProjectSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ArduinoNewProjectSettings extends CMakeProjectSettings {
    protected String languageVersion;
    protected String libraryType;
    protected boolean addLibraryDirectory;
    protected String libraryDirectory;
    protected String board;
    protected String cpu;
    protected String programmer;
    protected String port;
    protected String libraryCategory;
    protected String authorName;
    protected String authorEMail;
    protected boolean verbose;
    protected HashMap<String, String> boardCpu;
    protected LinkedHashSet<String> portHistory;
    protected boolean nestedLibrarySources;

    @NotNull
    @Override
    public String getEnabledProjectLanguages() {
        return "";
    }

    @NotNull
    @Override
    public String getLanguageVersionLineForCMake() {
        return CppLanguageVersions.fromDisplayString(languageVersion);
    }

    public ArduinoNewProjectSettings(
            final String languageVersion,
            final String libraryType,
            final boolean addLibraryDirectory,
            final String libraryDirectory,
            String board,
            String cpu,
            String programmer,
            String port,
            String libraryCategory,
            String authorName,
            String authorEMail,
            boolean verbose,
            @Nullable Map<String, String> boardCpu,
            @Nullable Collection<String> portHistory,
            boolean nestedLibrarySources
    ) {
        this.languageVersion = languageVersion;
        this.libraryType = libraryType;
        this.addLibraryDirectory = addLibraryDirectory;
        this.libraryDirectory = libraryDirectory;
        this.board = board;
        this.cpu = cpu;
        this.programmer = programmer;
        this.port = port;
        this.libraryCategory = libraryCategory;
        this.authorName = authorName;
        this.authorEMail = authorEMail;
        this.verbose = verbose;
        this.boardCpu = boardCpu == null ? new HashMap<>() : new HashMap<>(boardCpu);
        setPortHistory(portHistory == null ? Collections.EMPTY_LIST : portHistory);
        this.nestedLibrarySources = nestedLibrarySources;
    }

    public ArduinoNewProjectSettings() {
        this("", "", false, "", "", "", "", "", "", "", "", false, null, null, false);
    }

    public ArduinoNewProjectSettings(ArduinoNewProjectSettings other) {
        this(other.languageVersion,
                other.libraryType,
                other.addLibraryDirectory,
                other.libraryDirectory,
                other.board,
                other.cpu,
                other.programmer,
                other.port,
                other.libraryCategory,
                other.authorName,
                other.authorEMail,
                other.verbose,
                other.boardCpu,
                other.portHistory,
                other.nestedLibrarySources
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

    public boolean isNestedLibrarySources() {
        return nestedLibrarySources;
    }

    public void setNestedLibrarySources(final boolean nestedLibrarySources) {
        this.nestedLibrarySources = nestedLibrarySources;
    }

    public String getLibraryCategory() {
        return libraryCategory;
    }

    public void setLibraryCategory(final String category) {
        this.libraryCategory = category;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(final String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEMail() {
        return authorEMail;
    }

    public void setAuthorEMail(final String authorEMail) {
        this.authorEMail = authorEMail;
    }
}
