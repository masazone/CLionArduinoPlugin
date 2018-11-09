# <img src="resources/META-INF/pluginIcon.svg" alt="pluginIcon.svg" width="60" align="absmiddle"/> Arduino Support

A JetBrains CLion plugin to integrate
[Arduino CMake](https://github.com/francoiscampbell/arduino-cmake) into the IDE.

[JetBrains Plugin Page](https://plugins.jetbrains.com/plugin/11301-arduino-support)

Create an Arduino CMake project in one click with new project wizard types.

* Adds `Arduino Sketch` and `Arduino Library` project types to new project wizard, with Options
  to select board type, cpu, programmer and port

* Adds New File Action: `New Arduino Sketch`

![Screenshot_NewProject.png](assets/images/Screenshot_NewProject.png)

## Status

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
  * [ ] User selectable boards.txt and programmers.txt location. Currently, the plugin contains
        a copy of these from Arduino IDE 1.8.6
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

