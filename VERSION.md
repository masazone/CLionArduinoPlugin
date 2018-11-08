## CLionArduinoPlugin Version Notes

[TOC levels=3,4]: # "Version History"

### Version History
- [1.3.4](#134)
- [1.3.2](#132)
- [1.3.0](#130)
- [1.2.4](#124)
- [1.2.3](#123)
- [1.2.2](#122)
- [1.2.1](#121)
- [1.2.0](#120)
- [1.1.0](#110)
- [1.0.2](#102)
- [1.0.1](#101)
- [1.0](#10)


### 1.3.4

* Fix: change plugin name for JetBrains requirements
* Fix: reload CMakeLists.txt after project creation otherwise generated files don't have the
  `-mmcu` set.
* Fix: add port drop down using jssc https://github.com/scream3r/java-simple-serial-connector,
  with `SerialPorts` Patched for Arduino by Cristian Maglie
  https://raw.githubusercontent.com/arduino/Arduino/master/arduino-core/src/processing/app/SerialPortList.java
* Fix: change source packages to match plugin id.

### 1.3.2

* Fix: no cpus showing up until board selection is changed
* Fix: add library sub-directory would be set on form opening.

### 1.3.0

* Fix: add project types to `New Project` wizard, only compatible with 2018.3 and above.
* Fix: remove arduino new project action
* Add: arduino library. Adds .cpp and .h file named as project directory,
* Add: boards project option and cpu option (from boards.txt stored in resources). TODO: add
  config for boards.txt path
* Add: programmers (from boards.txt stored in resources). TODO: add config for boards.txt path
* Add: Port option, for now manually set. TODO: add code to scan available ports
* Add: verbose build option
* Add: local library directory option
* Add: icons for sketch file, library and project
* Add: persistence of project creation options, last cpu per board is saved.

### 1.2.4

* Fix: exceptions when run on CLion 2018.
* Add: `CMakeList.txt` reloading on creation (for 2018 or above)
* Add: comments to CMakeList.txt to help startup with non-mega boards
* Fix: change directory layout and plugin.xml to be more compatible with IntelliJ plugins for
  error checking.
* Add: IntelliJ project files to git

### 1.2.3

* Fixed to run on CLion 2016.3.2 and 2017.2.1
* Updating plugin xml to create a branch that can be uploaded to Jetbrains plugin repo.

### 1.2.2

* Fixed .ino and .pde files not refactorable. Increased compatibility for Servo library

### 1.2.1

* Re-compiled for Java 6

### 1.2.0

* Added new project creation to Welcome Screen and File menu

### 1.1.0

* Compatiblity with Arduino SDK 1.6 on Mac OS X

### 1.0.2

* Removed Groovy runtime, no longer necessary

### 1.0.1

* Fixed organization

### 1.0

* Convert a project to Arduino CMake. This replaces CMakeLists.txt with a default one, deletes
  the default main.cpp file, copies in the Arduino CMake toolchain files, and deletes the build
  direcory to start fresh
* Associates .ino and .pde files as C++ source, so you get syntax highlighting and prediction,
  etc.
* Create a new sketch file in any directory. If you omit the extension, it will add .ino
  automatically
* Adds import for Arduino.h to all newly created sketch files to enable code completion
* Compiled with Java 6 for compatibility with OS X out of the box
