package com.vladsch.clionarduinoplugin.components;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.mac.foundation.Foundation;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.XCollection;
import com.intellij.util.xmlb.annotations.XMap;
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.CMakeProjectGenerator;
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.settings.CMakeProjectSettings;
import com.vladsch.clionarduinoplugin.generators.CppLanguageVersions;
import com.vladsch.clionarduinoplugin.resources.BuildConfig;
import com.vladsch.clionarduinoplugin.resources.ResourceUtils;
import com.vladsch.clionarduinoplugin.util.ApplicationSettingsListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@State(name = "Arduino Support Settings",
        storages = @Storage("arduino-plugin-settings.xml")
)
public class ArduinoApplicationSettings extends CMakeProjectSettings implements PersistentStateComponent<ArduinoApplicationSettings> {
    public static final String[] EMPTY = new String[0];
    public static final @NotNull String[] LANGUAGE_VERSIONS = ContainerUtil.map2Array(CppLanguageVersions.values(), String.class, CppLanguageVersions::getDisplayString);
    private @NotNull String languageVersion = LANGUAGE_VERSIONS[0];
    public static final String ARDUINO_LIB_TYPE = "arduino";

    public static final String TEXT_DELIMITER = "|";
    public static final String TEXT_SPLIT_REGEX = "\\s*\\" + TEXT_DELIMITER + "\\s*";
    public static final String STATIC_LIB_TYPE = "static";
    public static final String[] LIBRARY_TYPES = { ARDUINO_LIB_TYPE, CMakeProjectGenerator.STATIC_LIB_TYPE };
    public static final String[] LIBRARY_CATEGORIES = new String[] {
            "",
            "Communications",
            "Data Processing",
            "Data Storage",
            "Device Control",
            "Display",
            "Other",
            "Sensors",
            "Signal Input/Output",
            "Timing",
    };

    private int myGroupCount = 0;

    private @NotNull String libraryType = ARDUINO_LIB_TYPE;
    private boolean addLibraryDirectory = false;
    private @NotNull String libraryDirectory = "";
    private @NotNull String board = "uno";
    private @NotNull String cpu = "";
    private @NotNull String programmer = "";
    private @NotNull String port = "";
    private int baudRate = 9600;
    private @NotNull String libraryCategory = "";
    private @NotNull String authorName = "";
    private @NotNull String authorEMail = "";
    private boolean verbose = false;
    private @NotNull HashMap<String, String> boardCpu = new HashMap<>();
    private @NotNull LinkedHashSet<String> portHistory = new LinkedHashSet<>();
    private boolean nestedLibrarySources = false;
    private @NotNull String boardsTxtPath = "";
    private @NotNull String programmersTxtPath = "";
    private boolean bundledBoardsTxt = true;
    private boolean bundledProgrammersTxt = true;

    private BuildConfig myBuildConfig = null;

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

    public void setLanguageVersion(@NotNull String languageVersion) {
        this.languageVersion = languageVersion;
    }

    public void setLibraryType(@NotNull String libraryType) {
        this.libraryType = libraryType;
    }

    @NotNull
    public String getLanguageVersion() {
        return languageVersion;
    }

    @NotNull
    public String getLibraryType() {
        return libraryType;
    }

    @NotNull
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

    @NotNull
    public String getBoard() {
        return board;
    }

    public void setBoard(@NotNull final String board) {
        this.board = board;
        this.cpu = getBoardCpu().get(board);
    }

    public void setBoardFromName(@NotNull final String boardName) {
        this.board = getBoardFromName(boardName).id;
        this.cpu = getBoardCpu().get(board);
    }

    @NotNull
    public String getCpu() {
        return cpu;
    }

    public void setCpu(@NotNull final String cpu) {
        this.cpu = cpu;
        setBoardCpu(board, cpu);
    }

    @NotNull
    public String getProgrammer() {
        return programmer;
    }

    public void setProgrammer(@NotNull final String programmer) {
        this.programmer = programmer;
    }

    @NotNull
    public String getPort() {
        return port;
    }

    public void setPort(@NotNull final String port) {
        this.port = port;
        addPortHistory(port);
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(final int baudRate) {
        this.baudRate = baudRate;
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

    @NotNull
    public String getLibraryCategory() {
        return libraryCategory;
    }

    public void setLibraryCategory(@NotNull final String category) {
        this.libraryCategory = category;
    }

    public String getAuthorName() {
        return getAuthorName(this);
    }

    public void setAuthorName(@NotNull final String authorName) {
        this.authorName = authorName;
    }

    @NotNull
    public String getAuthorEMail() {
        return authorEMail;
    }

    public void setAuthorEMail(@NotNull final String authorEMail) {
        this.authorEMail = authorEMail;
    }

    @NotNull
    public String getBoardsTxtPath() {
        return boardsTxtPath;
    }

    public void setBoardsTxtPath(@NotNull final String boardsTxtPath) {
        if (!this.boardsTxtPath.equals(boardsTxtPath)) {
            invalidateBuildConfig();
            this.boardsTxtPath = boardsTxtPath;
        }
    }

    public boolean isBundledBoardsTxt() {
        return bundledBoardsTxt;
    }

    public void setBundledBoardsTxt(final boolean bundledBoardsTxt) {
        if (this.bundledBoardsTxt != bundledBoardsTxt) {
            invalidateBuildConfig();
            this.bundledBoardsTxt = bundledBoardsTxt;
        }
    }

    public boolean isBundledProgrammersTxt() {
        return bundledProgrammersTxt;
    }

    public void setBundledProgrammersTxt(final boolean bundledProgrammersTxt) {
        if (this.bundledProgrammersTxt != bundledProgrammersTxt) {
            invalidateBuildConfig();
            this.bundledProgrammersTxt = bundledProgrammersTxt;
        }
    }

    @NotNull
    public String getProgrammersTxtPath() {
        return programmersTxtPath;
    }

    public void setProgrammersTxtPath(@NotNull final String programmersTxtPath) {
        if (!this.programmersTxtPath.equals(programmersTxtPath)) {
            invalidateBuildConfig();
            this.programmersTxtPath = programmersTxtPath;
        }
    }

    private static String getFileContent(String path) {
        if (!path.isEmpty()) {
            File file = new File(path);
            if (!file.exists()) {
            } else if (file.isDirectory()) {
            } else {
                return ResourceUtils.getFileContent(file);
            }
        }
        return null;
    }

    @NotNull
    public String[] getBoardNames() {
        BuildConfig buildConfig = getBuildConfig();
        return ContainerUtil.map2Array(buildConfig.getBoards().values(), String.class, (board) -> board.name);
    }

    @NotNull
    public String[] getProgrammerNames() {
        BuildConfig buildConfig = getBuildConfig();
        return ContainerUtil.map2Array(buildConfig.getBoards().values(), String.class, (board) -> board.name);
    }

    @NotNull
    public String[] getBoardCpuNames(final @NotNull String boardName) {
        BuildConfig.Board board = getBoardFromName(boardName);
        if (board.cpuList != null) {
            return board.cpuList.values().toArray(new String[0]);
        }
        return EMPTY;
    }

    @NotNull
    public String getCpuLabel() {
        return getBuildConfig().getCpuMenu();
    }

    @NotNull
    String getCpuId() {
        if (!board.isEmpty() && !cpu.isEmpty()) {
            BuildConfig.Board board = getBoardFromName(this.board);
            return board.cpuFromName(cpu);
        }
        return "";
    }

    public @NotNull BuildConfig.Board getBoardFromName(final @NotNull String boardName) {
        return getBuildConfig().boardFromName(boardName);
    }

    void invalidateBuildConfig() {
        myBuildConfig = null;
    }

    @NotNull
    public BuildConfig getBuildConfig() {
        if (myBuildConfig == null) {
            String boardsTxt = BuildConfig.getBoardsTxtString();
            String programmersTxt = BuildConfig.getProgrammersTxtString();

            if (!bundledBoardsTxt) {
                String otherBoardsTxt = getFileContent(boardsTxtPath);
                BuildConfig buildConfig = new BuildConfig(otherBoardsTxt, "");
                if (!buildConfig.getBoards().isEmpty()) {
                    boardsTxt = otherBoardsTxt;
                }
            }

            if (!bundledProgrammersTxt) {
                String otherProgrammerTxt = getFileContent(programmersTxtPath);
                BuildConfig buildConfig = new BuildConfig(otherProgrammerTxt, "");
                if (!buildConfig.getProgrammers().isEmpty()) {
                    programmersTxt = otherProgrammerTxt;
                }
            }

            myBuildConfig = new BuildConfig(boardsTxt, programmersTxt);
        }
        return myBuildConfig;
    }

    @SuppressWarnings("NonFinalFieldReferenceInEquals")
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArduinoApplicationSettings settings = (ArduinoApplicationSettings) o;

        if (addLibraryDirectory != settings.addLibraryDirectory) return false;
        if (baudRate != settings.baudRate) return false;
        if (verbose != settings.verbose) return false;
        if (nestedLibrarySources != settings.nestedLibrarySources) return false;
        if (bundledBoardsTxt != settings.bundledBoardsTxt) return false;
        if (bundledProgrammersTxt != settings.bundledProgrammersTxt) return false;
        if (!languageVersion.equals(settings.languageVersion)) return false;
        if (!libraryType.equals(settings.libraryType)) return false;
        if (!libraryDirectory.equals(settings.libraryDirectory)) return false;
        if (!board.equals(settings.board)) return false;
        if (!cpu.equals(settings.cpu)) return false;
        if (!programmer.equals(settings.programmer)) return false;
        if (!port.equals(settings.port)) return false;
        if (!libraryCategory.equals(settings.libraryCategory)) return false;
        if (!authorName.equals(settings.authorName)) return false;
        if (!authorEMail.equals(settings.authorEMail)) return false;
        if (!boardCpu.equals(settings.boardCpu)) return false;
        if (!portHistory.equals(settings.portHistory)) return false;
        if (!boardsTxtPath.equals(settings.boardsTxtPath)) return false;
        return programmersTxtPath.equals(settings.programmersTxtPath);
    }

    @SuppressWarnings("NonFinalFieldReferencedInHashCode")
    @Override
    public int hashCode() {
        int result = languageVersion.hashCode();
        result = 31 * result + libraryType.hashCode();
        result = 31 * result + (addLibraryDirectory ? 1 : 0);
        result = 31 * result + libraryDirectory.hashCode();
        result = 31 * result + board.hashCode();
        result = 31 * result + cpu.hashCode();
        result = 31 * result + programmer.hashCode();
        result = 31 * result + port.hashCode();
        result = 31 * result + baudRate;
        result = 31 * result + libraryCategory.hashCode();
        result = 31 * result + authorName.hashCode();
        result = 31 * result + authorEMail.hashCode();
        result = 31 * result + (verbose ? 1 : 0);
        result = 31 * result + boardCpu.hashCode();
        result = 31 * result + portHistory.hashCode();
        result = 31 * result + (nestedLibrarySources ? 1 : 0);
        result = 31 * result + boardsTxtPath.hashCode();
        result = 31 * result + programmersTxtPath.hashCode();
        result = 31 * result + (bundledBoardsTxt ? 1 : 0);
        result = 31 * result + (bundledProgrammersTxt ? 1 : 0);
        return result;
    }

    @NotNull
    public static String getAuthorName(final ArduinoApplicationSettings settings) {
        if (settings.authorName.isEmpty()) {
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

    public void groupChanges(Runnable grouped) {
        myGroupCount++;
        try {
            grouped.run();
        } finally {
            myGroupCount--;
            if (myGroupCount == 0) {
                fireSettingsChanged();
            }
        }
    }

    private void fireSettingsChanged() {
        if (myGroupCount == 0) {
            ApplicationManager.getApplication().getMessageBus().syncPublisher(ApplicationSettingsListener.TOPIC).onSettingsChanged();
        }
    }

    @NotNull
    public static ArduinoApplicationSettings getInstance() {
        return ServiceManager.getService(ArduinoApplicationSettings.class);
    }

    @NotNull
    @Override
    public ArduinoApplicationSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ArduinoApplicationSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
