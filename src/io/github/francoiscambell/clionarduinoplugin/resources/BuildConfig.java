package io.github.francoiscambell.clionarduinoplugin.resources;

import org.gradle.launcher.daemon.protocol.Build;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildConfig {
    public static final String MENU_CPU = "menu.cpu";
    public static final String PROCESSOR = "Processor";

    public static String getBuildTxtString() {
        return ResourceUtils.getResourceFileContent(BuildConfig.class,"config/boards.txt");
    }

    public static String getProgrammersTxtString() {
        return ResourceUtils.getResourceFileContent(BuildConfig.class, "config/programmers.txt");
    }

    public static class Board {
        final public String id;
        final public String name;
        @Nullable final public HashMap<String, String> cpuList; // id => name
        private HashMap<String, String> nameCpuMap;

        public Board(final String id, final String name, @Nullable HashMap<String, String> cpuList) {
            this.id = id;
            this.name = name;
            this.cpuList = cpuList;
        }

        public String cpuFromName(String cpuName) {
            if (nameCpuMap == null) {
                nameCpuMap = new HashMap<>();
                for (Map.Entry<String, String> entry : cpuList.entrySet()) {
                    nameCpuMap.put(entry.getValue(), entry.getKey());
                }
            }
            return nameCpuMap.get(cpuName);
        }
    }

    public static class Programmer {
        final public String id;
        final public String name;

        public Programmer(final String id, final String name) {
            this.id = id;
            this.name = name;
        }
    }

    private String cpuMenu = PROCESSOR;
    private HashMap<String, Board> boards;
    private HashMap<String, Programmer> programmers;
    private HashMap<String, Board> nameBoardMap;
    private HashMap<String, Programmer> nameProgrammerMap;

    public BuildConfig(String buildTxt, String programmersTxt) {
        parseBoards(buildTxt);
        parseProgrammers(programmersTxt);
    }

    public Board boardFromName(String name) {
        if (nameBoardMap == null) {
            nameBoardMap = new HashMap<>();
            for (Map.Entry<String, Board> entry : boards.entrySet()) {
                nameBoardMap.put(entry.getValue().name, entry.getValue());
            }
        }
        return nameBoardMap.get(name);
    }

    public Programmer programmerFromName(String name) {
        if (nameProgrammerMap == null) {
            nameProgrammerMap = new HashMap<>();
            for (Map.Entry<String, Programmer> entry : programmers.entrySet()) {
                nameProgrammerMap.put(entry.getValue().name, entry.getValue());
            }
        }
        return nameProgrammerMap.get(name);
    }

    public String getCpuMenu() {
        return cpuMenu;
    }

    public HashMap<String, Board> getBoards() {
        return boards;
    }

    public HashMap<String, Programmer> getProgrammers() {
        return programmers;
    }

    public void parseBoards(String buildText) {
        boards = new HashMap<>();
        String[] lines = buildText.split("\n");
        Pattern keyEntry = Pattern.compile("([^=]+)=(.*)");

        for (String line : lines) {
            String l = line.trim();
            if (l.isEmpty() || l.charAt(0) == '#') continue;
            Matcher matcher = keyEntry.matcher(l);
            if (matcher.matches()) {
                String key = matcher.group(1);
                String value = matcher.group(2);
                if (value == null) value = "";

                if (key.equals(MENU_CPU)) {
                    cpuMenu = value;
                } else {
                    String[] keyParts = key.split("\\.");

                    if (keyParts.length == 2 && keyParts[1].equals("name")) {
                        // have a board name
                        if (!boards.containsKey(keyParts[0])) {
                            // add the board
                            Board board = new Board(keyParts[0], value, null);
                            boards.put(board.id, board);
                        }
                    }
                    else if (keyParts.length == 4 && keyParts[1].equals("menu") && keyParts[2].equals("cpu")) {
                        // have a board cpu
                        if (boards.containsKey(keyParts[0])) {
                            // add the board
                            Board board = boards.get(keyParts[0]);
                            if (board != null) {
                                if (board.cpuList == null) {
                                    board = new Board(board.id, board.name, new HashMap<>());
                                    boards.put(board.id, board);
                                }
                                board.cpuList.put(keyParts[3], value);
                            }
                        }
                    }
                    //// diecimila.menu.cpu.atmega328.build.mcu
                    //else if (keyParts.length == 6 && keyParts[1].equals("menu") && keyParts[2].equals("cpu") && keyParts[4].equals("build") && keyParts[5].equals("mcu")) {
                    //    // have a board cpu
                    //    if (boards.containsKey(keyParts[0])) {
                    //        // add the board
                    //        Board board = boards.get(keyParts[0]);
                    //        if (board != null) {
                    //            board.cpuList.put(keyParts[3], value);
                    //        }
                    //    }
                    //}
                }
            }
        }
    }

    public void parseProgrammers(String programmersText) {
        programmers = new HashMap<>();
        String[] lines = programmersText.split("\n");
        Pattern keyEntry = Pattern.compile("([^=]+)=(.*)");

        for (String line : lines) {
            String l = line.trim();
            if (l.isEmpty() || l.charAt(0) == '#') continue;
            Matcher matcher = keyEntry.matcher(l);
            if (matcher.matches()) {
                String key = matcher.group(1);
                String value = matcher.group(2);

                String[] keyParts = key.split("\\.");

                if (keyParts.length == 2 && keyParts[1].equals("name")) {
                    // have a programmer name
                    if (!programmers.containsKey(keyParts[0])) {
                        // add the board
                        Programmer programmer = new Programmer(keyParts[0], value);
                        programmers.put(programmer.id, programmer);
                    }
                }
            }
        }
    }
}
