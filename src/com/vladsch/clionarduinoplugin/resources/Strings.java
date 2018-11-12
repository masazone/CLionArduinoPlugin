package com.vladsch.clionarduinoplugin.resources;

public class Strings {
    public static final String CMAKE_LISTS_FILENAME = "CMakeLists.txt";
    public static final String CPP_EXT = "cpp";
    public static final String DOT_CPP_EXT = "." + CPP_EXT;
    public static final String H_EXT = "h";
    public static final String DOT_H_EXT = "." + H_EXT;
    public static final String DEFAULT_ARDUINO_SKETCH_CONTENTS = "#include <Arduino.h>\n" +
            "\n" +
            "void setup() {\n" +
            "\n" +
            "}\n" +
            "\n" +
            "void loop() {\n" +
            "\n" +
            "}";
    public static final String ENTER_FILENAME = "Enter filename";
    public static final String ERROR = "Error";
    public static final String FILE_ALREADY_EXISTS = "File Already Exists";
    public static final String INO_EXT = "ino";
    public static final String DOT_INO_EXT = "." + INO_EXT;
    public static final String MAIN_CPP_FILENAME = "main.cpp";
    public static final String PDE_EXT = "PDE";
    public static final String DOT_PDE_EXT = "." + PDE_EXT;
    public static final String QUESTION_OVERWRITE = "Do you wish to overwrite the existing file?";
    public static final String SKETCH_NAME = "Sketch Name";
    public static final String DEFAULT_ARDUINO_LIBRARY_CPP_CONTENTS = "\n";
    public static final String DEFAULT_ARDUINO_LIBRARY_H_CONTENTS = "\n";
    public static final String DEFAULT_ARDUINO_LIBRARY_KEYWORDS_CONTENTS = "" +
            "#######################################\n" +
            "# Syntax Coloring Map For <$PROJECT_NAME$>\n" +
            "#######################################\n" +
            "\n" +
            "#######################################\n" +
            "# Datatypes (KEYWORD1)\n" +
            "#######################################\n" +
            "\n" +
            "#######################################\n" +
            "# Methods and Functions (KEYWORD2)\n" +
            "#######################################\n" +
            "\n" +
            "######################################\n" +
            "# Instances (KEYWORD2)\n" +
            "#######################################\n" +
            "\n" +
            "#######################################\n" +
            "# Constants (LITERAL1)\n" +
            "#######################################\n" +
            "";
    public static final String DEFAULT_ARDUINO_LIBRARY_PROPERTIES_CONTENTS = "" +
            "name=<$PROJECT_NAME$>\n"+
            "version=0.0.0\n"+
            "author=Name\n"+
            "maintainer=Name <name@email.com>\n"+
            "sentence=Summany\n"+
            "paragraph=Description\n"+
            "category=Device Control\n"+
            "url=URL\n"+
            "architectures=*\n"+
            "";
}
