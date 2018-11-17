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

From previous experience, when I see an issue open for a couple of years I can see that the
maintainer is no longer motivated by the project. I can understand but I am not motivated to
report suggestions or go to the trouble of a PR.

I opt out for another solution or implement it myself. At least with the latter I am limited by
my available time and motivation to get the functionality the way I like it. Until I too loose
interest. Then maybe someone else will pick up the torch at take it further.

## Wish List

* [x] Convert to project wizard in new project as Arduino Sketch Project, instead of file menu
      item.
* [x] Add Configuration options:
  * [x] Board selection
  * [x] CPU selection
  * [x] Port selection using jssc https://github.com/scream3r/java-simple-serial-connector
        Patched for Arduino by Cristian Maglie
        https://raw.githubusercontent.com/arduino/Arduino/master/arduino-core/src/processing/app/SerialPortList.java
  * [x] Persistence for new project options, since these are most likely to be re-used.
* [x] Add: Serial Terminal tool window, current
      [`Serial Monitor` plugin](https://plugins.jetbrains.com/plugin/8031-serial-port-monitor)
      is functional but prevents JSSC native libraries from being used to list available ports,
      is not actively developed and lacks creature comforts:
  * [x] auto disconnect on project build so update build can connect to the board,
  * [x] reconnect after successful build is done
  * [x] recognize the enter key as send
  * [ ] option to send individual keys as they are typed, with or without local echo, simulating
        a real serial
  * [ ] Add Hex pane in addition to text view. Either one or both could be displayed. With
        coordinated highlighting carets between the two. That way text view is not mangled into
        a block but naturally flows as you would expect, while hex view can be either a block or
        flow to match the text.
  * [ ] Display of ascii codes without resorting to hex display.
* [ ] User selectable boards.txt and programmers.txt location. Currently, the plugin contains
  a copy of these from Arduino IDE 1.8.6
* [ ] Add UI for changing existing CMakeLists.txt configuration
  * [ ] Change board, cpu, programmer, port, etc. All options which were available on project
        creation.
  * [ ] Load Additional Libraries from URLs
  * [ ] Allow additional Library directories
* [ ] Refactoring support which CLion does not handle:
  * [ ] Add file to sources or headers (CLion cannot make sense of the Arduino project file)
  * [ ] Add updating of `keywords.txt` when identifiers in source or headers are renamed.
  * [ ] Add reload of `CMakeLists.txt` when library includes are added.
* [ ] Add Import Arduino IDE config, sketches and libraries
* [ ] Add: `Generate keywords.txt` action to generate keywords.txt based on contained project
      classes and functions.

## Release notes

[CLionArduinoPlugin Version Notes](VERSION.md)

## History

This plugin is a fork of
[rjuang/CLionArduinoPlugin](https://github.com/rjuang/CLionArduinoPlugin) which in turn is a
fork of [Original CLionArduinoPlugin](https://github.com/francoiscampbell/CLionArduinoPlugin)
written by Francois Campbell.

## Notes

For serial port list and functionality the plugin uses
[jSSC-2.8.0](https://github.com/scream3r/java-simple-serial-connector), Licensed under
[GNU Lesser GPL](http://www.gnu.org/licenses/lgpl.html)

