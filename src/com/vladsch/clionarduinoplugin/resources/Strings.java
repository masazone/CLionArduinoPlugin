package com.vladsch.clionarduinoplugin.resources;

public class Strings {
    public static final String CMAKE_LISTS_FILENAME = "CMakeLists.txt";
    public static final String CPP_EXT = "cpp";
    public static final String DOT_CPP_EXT = "." + CPP_EXT;
    public static final String H_EXT = "h";
    public static final String DOT_H_EXT = "." + H_EXT;

    // done
    public static final String DEFAULT_ARDUINO_SKETCH_CONTENTS = "" +
            "#include <Arduino.h>\n" +
            "#include \"User_Setup.h\"\n" +
            "\n" +
            "void setup() {\n" +
            "\n" +
            "}\n" +
            "\n" +
            "void loop() {\n" +
            "\n" +
            "}\n" +
            "";
    public static final String INO_EXT = "ino";
    public static final String DOT_INO_EXT = "." + INO_EXT;
    public static final String MAIN_CPP_FILENAME = "main.cpp";
    public static final String PDE_EXT = "PDE";
    public static final String DOT_PDE_EXT = "." + PDE_EXT;

    //done
    public static final String DEFAULT_ARDUINO_LIBRARY_CPP_CONTENTS = "" +
            "#include \"<$PROJECT_NAME$>.h\"\n" +
            "";

    //done
    public static final String DEFAULT_ARDUINO_USER_SETUP_H_CONTENTS = "" +
            "#ifdef _<$FILE_NAME$>_USER_SETUP_H_\n" +
            "#define _<$FILE_NAME$>_USER_SETUP_H_\n" +
            "\n" +
            "#endif //_<$FILE_NAME$>_USER_SETUP_H_\n" +
            "";

    // done
    public static final String DEFAULT_ARDUINO_LIBRARY_TEST_CONTENTS = "" +
            "#include <Arduino.h>\n" +
            "#include \"<$PROJECT_NAME$>.h\"\n" +
            "\n" +
            "void setup() {\n" +
            "\n" +
            "}\n" +
            "\n" +
            "void loop() {\n" +
            "\n" +
            "}\n" +
            "";

    // done
    public static final String DEFAULT_ARDUINO_LIBRARY_H_CONTENTS = "" +
            "#ifdef _<$FILE_NAME$>_H_\n" +
            "#define _<$FILE_NAME$>_H_\n" +
            "\n" +
            "#endif //_<$FILE_NAME$>_H_\n" +
            "";

    // done
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

    // done
    public static final String DEFAULT_ARDUINO_LIBRARY_PROPERTIES_CONTENTS = "" +
            "name=<$PROJECT_NAME$>\n" +
            "version=0.0.0\n" +
            "author=<$USER_NAME$>\n" +
            "maintainer=<$USER_NAME$> <$E_MAIL$>\n" +
            "sentence=Summary\n" +
            "paragraph=Description\n" +
            "category=<$LIBRARY_CATEGORY$>\n" +
            "url=URL\n" +
            "architectures=*\n" +
            "";
}
