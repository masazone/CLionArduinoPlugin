---
title: CMake Builder Spec Test
author: Vladimir Schneider
version: 0.1
date: '2018-11-18'
license: '[CC-BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)'
...

---

## Arduno

actual file

```````````````````````````````` example(Arduno: 1) options(board-pro)
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake")
set(PROJECT_NAME tft_life)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD uno)
set(ARDUINO_CPU none)

project(${PROJECT_NAME})

# Define the source code
set(${PROJECT_NAME}_SRCS tft_life.cpp)
#set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set (${CMAKE_PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 57600)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(PROJECT_NAME tft_life)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)

project(${PROJECT_NAME})

# Define the source code
set(${PROJECT_NAME}_SRCS tft_life.cpp)
#set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set(${CMAKE_PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 57600)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 708]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  LineEnding[37, 38]
  Command[38, 114] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 113, "CMAKE_TOOLCHAIN_FILE \"${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake\""] close:[113, 114, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 113] open:[63, 64, "\""] text:[64, 112, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[112, 113, "\""]
  LineEnding[114, 115]
  Command[115, 141] text:[115, 118, "set"] open:[118, 119, "("] arguments:[119, 140, "PROJECT_NAME tft_life"] close:[140, 141, ")"]
    Argument[119, 131] text:[119, 131, "PROJECT_NAME"]
    Argument[132, 140] text:[132, 140, "tft_life"]
  LineEnding[141, 142]
  BlankLine[142, 143]
  LineComment[143, 183] open:[143, 144, "#"] text:[144, 183, "# This must be set before project call\n"]
  Command[183, 219] text:[183, 186, "set"] open:[186, 187, "("] arguments:[187, 218, "${CMAKE_PROJECT_NAME}_BOARD uno"] close:[218, 219, ")"]
    Argument[187, 214] text:[187, 214, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[215, 218] text:[215, 218, "uno"]
  LineEnding[219, 220]
  Command[220, 241] text:[220, 223, "set"] open:[223, 224, "("] arguments:[224, 240, "ARDUINO_CPU none"] close:[240, 241, ")"]
    Argument[224, 235] text:[224, 235, "ARDUINO_CPU"]
    Argument[236, 240] text:[236, 240, "none"]
  LineEnding[241, 242]
  BlankLine[242, 243]
  Command[243, 267] text:[243, 250, "project"] open:[250, 251, "("] arguments:[251, 266, "${PROJECT_NAME}"] close:[266, 267, ")"]
    Argument[251, 266] text:[251, 266, "${PROJECT_NAME}"]
  LineEnding[267, 268]
  BlankLine[268, 269]
  LineComment[269, 294] open:[269, 270, "#"] text:[270, 294, " Define the source code\n"]
  Command[294, 332] text:[294, 297, "set"] open:[297, 298, "("] arguments:[298, 331, "${PROJECT_NAME}_SRCS tft_life.cpp"] close:[331, 332, ")"]
    Argument[298, 318] text:[298, 318, "${PROJECT_NAME}_SRCS"]
    Argument[319, 331] text:[319, 331, "tft_life.cpp"]
  LineEnding[332, 333]
  LineComment[333, 381] open:[333, 334, "#"] text:[334, 381, "set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)\n"]
  Command[381, 429] text:[381, 397, "link_directories"] open:[397, 398, "("] arguments:[398, 428, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[428, 429, ")"]
    Argument[398, 428] text:[398, 428, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
  LineEnding[429, 430]
  BlankLine[430, 431]
  LineComment[431, 483] open:[431, 432, "#"] text:[432, 483, "### Uncomment below additional settings as needed.\n"]
  Command[483, 531] text:[483, 486, "set"] open:[486, 487, "("] arguments:[487, 530, "${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[530, 531, ")"]
    Argument[487, 519] text:[487, 519, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[520, 530] text:[520, 530, "avrispmkii"]
  LineEnding[531, 532]
  Command[532, 590] text:[532, 535, "set"] open:[535, 536, "("] arguments:[536, 589, "${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[589, 590, ")"]
    Argument[536, 562] text:[536, 562, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[563, 589] text:[563, 589, "/dev/cu.usbserial-00000000"]
  LineEnding[590, 591]
  Command[591, 628] text:[591, 594, "set"] open:[595, 596, "("] arguments:[596, 627, "${CMAKE_PROJECT_NAME}_AFLAGS -v"] close:[627, 628, ")"]
    Argument[596, 624] text:[596, 624, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[625, 627] text:[625, 627, "-v"]
  LineEnding[628, 629]
  LineComment[629, 659] open:[629, 630, "#"] text:[630, 659, " set(pro.upload.speed 57600)\n"]
  BlankLine[659, 660]
  Command[660, 708] text:[660, 685, "generate_arduino_firmware"] open:[685, 686, "("] arguments:[686, 707, "${CMAKE_PROJECT_NAME}"] close:[707, 708, ")"]
    Argument[686, 707] text:[686, 707, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


actual file

```````````````````````````````` example(Arduno: 2) options(change-all)
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake")
set(PROJECT_NAME tft_life)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD uno)
set(ARDUINO_CPU none)

project(${PROJECT_NAME})

# Define the source code
set(${PROJECT_NAME}_SRCS tft_life.cpp)
set(${PROJECT_NAME}_HDRS tft_life.h)
set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)    
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000) 
set (${CMAKE_PROJECT_NAME}_AFLAGS -v) 

set(uno.upload.speed 57600)
set(libName_RECURSE false) 

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(PROJECT_NAME setProjectName)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)

project(${PROJECT_NAME})

# Define the source code
set(${PROJECT_NAME}_SRCS tft_life.cpp setSrcs)
set(${PROJECT_NAME}_HDRS tft_life.h setHdrs)
set(${CMAKE_PROJECT_NAME}_SKETCH setSketch)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/.. linkDirectories)

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${CMAKE_PROJECT_NAME}_PORT setPort)
set(${CMAKE_PROJECT_NAME}_AFLAGS setAflags)

set(setBoard.upload.speed setUploadSpeed)
set(libName_RECURSE setLibNameRecurse)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 777]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  LineEnding[37, 38]
  Command[38, 114] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 113, "CMAKE_TOOLCHAIN_FILE \"${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake\""] close:[113, 114, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 113] open:[63, 64, "\""] text:[64, 112, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[112, 113, "\""]
  LineEnding[114, 115]
  Command[115, 141] text:[115, 118, "set"] open:[118, 119, "("] arguments:[119, 140, "PROJECT_NAME tft_life"] close:[140, 141, ")"]
    Argument[119, 131] text:[119, 131, "PROJECT_NAME"]
    Argument[132, 140] text:[132, 140, "tft_life"]
  LineEnding[141, 142]
  BlankLine[142, 143]
  LineComment[143, 183] open:[143, 144, "#"] text:[144, 183, "# This must be set before project call\n"]
  Command[183, 219] text:[183, 186, "set"] open:[186, 187, "("] arguments:[187, 218, "${CMAKE_PROJECT_NAME}_BOARD uno"] close:[218, 219, ")"]
    Argument[187, 214] text:[187, 214, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[215, 218] text:[215, 218, "uno"]
  LineEnding[219, 220]
  Command[220, 241] text:[220, 223, "set"] open:[223, 224, "("] arguments:[224, 240, "ARDUINO_CPU none"] close:[240, 241, ")"]
    Argument[224, 235] text:[224, 235, "ARDUINO_CPU"]
    Argument[236, 240] text:[236, 240, "none"]
  LineEnding[241, 242]
  BlankLine[242, 243]
  Command[243, 267] text:[243, 250, "project"] open:[250, 251, "("] arguments:[251, 266, "${PROJECT_NAME}"] close:[266, 267, ")"]
    Argument[251, 266] text:[251, 266, "${PROJECT_NAME}"]
  LineEnding[267, 268]
  BlankLine[268, 269]
  LineComment[269, 294] open:[269, 270, "#"] text:[270, 294, " Define the source code\n"]
  Command[294, 332] text:[294, 297, "set"] open:[297, 298, "("] arguments:[298, 331, "${PROJECT_NAME}_SRCS tft_life.cpp"] close:[331, 332, ")"]
    Argument[298, 318] text:[298, 318, "${PROJECT_NAME}_SRCS"]
    Argument[319, 331] text:[319, 331, "tft_life.cpp"]
  LineEnding[332, 333]
  Command[333, 369] text:[333, 336, "set"] open:[336, 337, "("] arguments:[337, 368, "${PROJECT_NAME}_HDRS tft_life.h"] close:[368, 369, ")"]
    Argument[337, 357] text:[337, 357, "${PROJECT_NAME}_HDRS"]
    Argument[358, 368] text:[358, 368, "tft_life.h"]
  LineEnding[369, 370]
  Command[370, 416] text:[370, 373, "set"] open:[373, 374, "("] arguments:[374, 415, "${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp"] close:[415, 416, ")"]
    Argument[374, 402] text:[374, 402, "${CMAKE_PROJECT_NAME}_SKETCH"]
    Argument[403, 415] text:[403, 415, "tft_life.cpp"]
  LineEnding[416, 417]
  Command[417, 465] text:[417, 433, "link_directories"] open:[433, 434, "("] arguments:[434, 464, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[464, 465, ")"]
    Argument[434, 464] text:[434, 464, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
  LineEnding[465, 466]
  BlankLine[466, 467]
  LineComment[467, 519] open:[467, 468, "#"] text:[468, 519, "### Uncomment below additional settings as needed.\n"]
  Command[519, 567] text:[519, 522, "set"] open:[522, 523, "("] arguments:[523, 566, "${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[566, 567, ")"]
    Argument[523, 555] text:[523, 555, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[556, 566] text:[556, 566, "avrispmkii"]
  LineEnding[571, 572]
  Command[572, 630] text:[572, 575, "set"] open:[575, 576, "("] arguments:[576, 629, "${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[629, 630, ")"]
    Argument[576, 602] text:[576, 602, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[603, 629] text:[603, 629, "/dev/cu.usbserial-00000000"]
  LineEnding[631, 632]
  Command[632, 669] text:[632, 635, "set"] open:[636, 637, "("] arguments:[637, 668, "${CMAKE_PROJECT_NAME}_AFLAGS -v"] close:[668, 669, ")"]
    Argument[637, 665] text:[637, 665, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[666, 668] text:[666, 668, "-v"]
  LineEnding[670, 671]
  BlankLine[671, 672]
  Command[672, 699] text:[672, 675, "set"] open:[675, 676, "("] arguments:[676, 698, "uno.upload.speed 57600"] close:[698, 699, ")"]
    Argument[676, 692] text:[676, 692, "uno.upload.speed"]
    Argument[693, 698] text:[693, 698, "57600"]
  LineEnding[699, 700]
  Command[700, 726] text:[700, 703, "set"] open:[703, 704, "("] arguments:[704, 725, "libName_RECURSE false"] close:[725, 726, ")"]
    Argument[704, 719] text:[704, 719, "libName_RECURSE"]
    Argument[720, 725] text:[720, 725, "false"]
  LineEnding[727, 728]
  BlankLine[728, 729]
  Command[729, 777] text:[729, 754, "generate_arduino_firmware"] open:[754, 755, "("] arguments:[755, 776, "${CMAKE_PROJECT_NAME}"] close:[776, 777, ")"]
    Argument[755, 776] text:[755, 776, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


test case

```````````````````````````````` example(Arduno: 3) options(change-all)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)
.
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/.. linkDirectories)
````````````````````````````````


test case

```````````````````````````````` example(Arduno: 4) options(change-all)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/.. linkDirectories)
.
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/.. linkDirectories)
````````````````````````````````


