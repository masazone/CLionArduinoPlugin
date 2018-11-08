# <img src="resources/META-INF/pluginIcon.svg" alt="pluginIcon.svg" width="60" align="absmiddle"/> CLion Arduino Plugin

This is a JetBrains CLion plugin that integrates
[Arduino CMake](https://github.com/francoiscampbell/arduino-cmake) into the IDE.

[JetBrains Plugin Page](https://plugins.jetbrains.com/plugin/11298-clion-arduino-plugin)

The current features are to create an Arduino CMake project in one click, and to create new
sketch files.

![Screenshot_NewProject.png](assets/images/Screenshot_NewProject.png)

For those asking how to upload, you either need to select the port from the dropdown when
crating the project or specify the serial port in the CMakeLists.txt file. For example:

    set(${CMAKE_PROJECT_NAME}_BOARD uno)
    set(${CMAKE_PROJECT_NAME}_PORT /dev/ttys0)

This will create the 'upload' configuration in your Run Configurations and you can click the Run
button or use the keyboard shortcut to upload.

## Update on status:

Forked to fix CLion 2018 issues and decided to add a few creature comforts:

* [x] Convert to project wizard in new project as Arduino Sketch Project, instead of file menu
      item.
* [x] Add Configuration options:
  * [x] Board selection
  * [x] CPU selection
  * [x] Port selection using jssc https://github.com/scream3r/java-simple-serial-connector
        Patched for Arduino by Cristian Maglie
        https://raw.githubusercontent.com/arduino/Arduino/master/arduino-core/src/processing/app/SerialPortList.java
  * [x] Persistence for new project options, since these are most likely to be re-used.
  * [ ] User selectable boards.txt and programmers.txt location, for now the jar contains a copy
        from Arduino IDE 1.8.6
* [ ] Add UI for changing existing CMakeLists.txt configuration
  * [ ] Load Libraries from URLs
  * [ ] Allow additional Library directories
* [ ] Add Import Arduino IDE config, sketches and libraries

## Release notes

[CLionArduinoPlugin Version Notes](VERSION.md)

## History

This plugin is a fork of
[rjuang/CLionArduinoPlugin](https://github.com/rjuang/CLionArduinoPlugin) which in turn is a
fork of [Original CLionArduinoPlugin](https://github.com/francoiscampbell/CLionArduinoPlugin)
written by Francois Campbell.

