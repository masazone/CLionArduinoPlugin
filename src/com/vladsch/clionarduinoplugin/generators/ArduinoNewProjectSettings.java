package com.vladsch.clionarduinoplugin.generators;

import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.mac.foundation.Foundation;
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
    protected String boardsTxtPath = "";
    protected String programmersTxtPath = "";
    protected boolean bundledBoardsTxt = true;
    protected boolean bundledProgrammersTxt = true;

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
            boolean nestedLibrarySources,
            String boardsTxtPath,
            String programmersTxtPath,
            boolean bundledBoardsTxt,
            boolean bundledProgrammersTxt
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
        this.boardsTxtPath = boardsTxtPath;
        this.programmersTxtPath = programmersTxtPath;
        this.bundledBoardsTxt = bundledBoardsTxt;
        this.bundledProgrammersTxt = bundledProgrammersTxt;
    }

    public void copyFrom(ArduinoNewProjectSettings other) {
        this.languageVersion = other.languageVersion;
        this.libraryType = other.libraryType;
        this.addLibraryDirectory = other.addLibraryDirectory;
        this.libraryDirectory = other.libraryDirectory;
        this.board = other.board;
        this.cpu = other.cpu;
        this.programmer = other.programmer;
        this.port = other.port;
        this.libraryCategory = other.libraryCategory;
        this.authorName = other.authorName;
        this.authorEMail = other.authorEMail;
        this.verbose = other.verbose;
        this.boardCpu = other.boardCpu == null ? new HashMap<>() : new HashMap<>(other.boardCpu);
        setPortHistory(other.portHistory == null ? Collections.EMPTY_LIST : other.portHistory);
        this.nestedLibrarySources = other.nestedLibrarySources;
        this.boardsTxtPath = other.boardsTxtPath;
        this.programmersTxtPath = other.programmersTxtPath;
        this.bundledBoardsTxt = other.bundledBoardsTxt;
        this.bundledProgrammersTxt = other.bundledProgrammersTxt;
    }

    public ArduinoNewProjectSettings() {
        this("",
                "",
                false,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                false,
                null,
                null,
                false,
                "",
                "",
                true,
                true);
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
                other.nestedLibrarySources,
                other.boardsTxtPath,
                other.programmersTxtPath,
                other.bundledBoardsTxt,
                other.bundledProgrammersTxt
        );
    }

    public boolean isBundledBoardsTxt() {
        return bundledBoardsTxt;
    }

    public void setBundledBoardsTxt(final boolean bundledBoardsTxt) {
        this.bundledBoardsTxt = bundledBoardsTxt;
    }

    public boolean isBundledProgrammersTxt() {
        return bundledProgrammersTxt;
    }

    public void setBundledProgrammersTxt(final boolean bundledProgrammersTxt) {
        this.bundledProgrammersTxt = bundledProgrammersTxt;
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
        return getAuthorName(this);
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

    public String getBoardsTxtPath() {
        return boardsTxtPath;
    }

    public void setBoardsTxtPath(final String boardsTxtPath) {
        this.boardsTxtPath = boardsTxtPath;
    }

    public String getProgrammersTxtPath() {
        return programmersTxtPath;
    }

    public void setProgrammersTxtPath(final String programmersTxtPath) {
        this.programmersTxtPath = programmersTxtPath;
    }

    @SuppressWarnings("NonFinalFieldReferenceInEquals")
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ArduinoNewProjectSettings)) return false;

        ArduinoNewProjectSettings settings = (ArduinoNewProjectSettings) o;

        if (addLibraryDirectory != settings.addLibraryDirectory) return false;
        if (verbose != settings.verbose) return false;
        if (nestedLibrarySources != settings.nestedLibrarySources) return false;
        if (!Objects.equals(languageVersion, settings.languageVersion)) return false;
        if (!Objects.equals(libraryType, settings.libraryType)) return false;
        if (!Objects.equals(libraryDirectory, settings.libraryDirectory)) return false;
        if (!Objects.equals(board, settings.board)) return false;
        if (!Objects.equals(cpu, settings.cpu)) return false;
        if (!Objects.equals(programmer, settings.programmer)) return false;
        if (!Objects.equals(port, settings.port)) return false;
        if (!Objects.equals(libraryCategory, settings.libraryCategory)) return false;
        if (!Objects.equals(authorName, settings.authorName)) return false;
        if (!Objects.equals(authorEMail, settings.authorEMail)) return false;
        if (!Objects.equals(boardCpu, settings.boardCpu)) return false;
        if (!Objects.equals(portHistory, settings.portHistory)) return false;
        if (!Objects.equals(boardsTxtPath, settings.boardsTxtPath)) return false;
        if (!Objects.equals(programmersTxtPath, settings.programmersTxtPath)) return false;
        if (!Objects.equals(bundledBoardsTxt, settings.bundledBoardsTxt)) return false;
        if (!Objects.equals(bundledProgrammersTxt, settings.bundledProgrammersTxt)) return false;
        return false;
    }

    @SuppressWarnings("NonFinalFieldReferencedInHashCode")
    @Override
    public int hashCode() {
        int result = languageVersion != null ? languageVersion.hashCode() : 0;
        result = 31 * result + (libraryType != null ? libraryType.hashCode() : 0);
        result = 31 * result + (addLibraryDirectory ? 1 : 0);
        result = 31 * result + (libraryDirectory != null ? libraryDirectory.hashCode() : 0);
        result = 31 * result + (board != null ? board.hashCode() : 0);
        result = 31 * result + (cpu != null ? cpu.hashCode() : 0);
        result = 31 * result + (programmer != null ? programmer.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (libraryCategory != null ? libraryCategory.hashCode() : 0);
        result = 31 * result + (authorName != null ? authorName.hashCode() : 0);
        result = 31 * result + (authorEMail != null ? authorEMail.hashCode() : 0);
        result = 31 * result + (verbose ? 1 : 0);
        result = 31 * result + (boardCpu != null ? boardCpu.hashCode() : 0);
        result = 31 * result + (portHistory != null ? portHistory.hashCode() : 0);
        result = 31 * result + (nestedLibrarySources ? 1 : 0);
        result = 31 * result + (boardsTxtPath != null ? boardsTxtPath.hashCode() : 0);
        result = 31 * result + (programmersTxtPath != null ? programmersTxtPath.hashCode() : 0);
        result = 31 * result + (bundledBoardsTxt ? 1 : 0);
        result = 31 * result + (bundledProgrammersTxt ? 1 : 0);
        return result;
    }

    @NotNull
    public static String getAuthorName(final ArduinoNewProjectSettings settings) {
        if (settings.authorName == null || settings.authorName.isEmpty()) {
            String userName = System.getProperty("user.name");

            if (SystemInfo.isMac) {
                String fullUserName = Foundation.fullUserName();
                if (fullUserName != null && !fullUserName.isEmpty()) {
                    userName = fullUserName;
                }
            }

            if (userName == null) {
                userName = "";
            }

            settings.authorName = userName;
        }

        return settings.authorName;
    }
}
