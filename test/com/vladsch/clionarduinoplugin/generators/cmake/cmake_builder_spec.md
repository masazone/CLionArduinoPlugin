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
set(CMAKE_CXX_STANDARD 98)
set(CMAKE_PROJECT_NAME tft_life)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD uno)
set(ARDUINO_CPU none)

project(${CMAKE_PROJECT_NAME})

# Define the source code
set(${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp)
#set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

### Add project sub-directories into the build
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set (${CMAKE_PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 57600)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)
set(CMAKE_PROJECT_NAME tft_life)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD pro)
set(ARDUINO_CPU 8MHzatmega328)

project(${CMAKE_PROJECT_NAME})

# Define the source code
set(${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp)
#set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

### Add project sub-directories into the build
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set(${CMAKE_PROJECT_NAME}_AFLAGS -v)
# set(pro.upload.speed 57600)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 851]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  LineEnding[37, 38]
  Command[38, 114] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 113, "CMAKE_TOOLCHAIN_FILE \"${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake\""] close:[113, 114, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 113] open:[63, 64, "\""] text:[64, 112, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[112, 113, "\""]
  LineEnding[114, 115]
  Command[115, 141] text:[115, 118, "set"] open:[118, 119, "("] arguments:[119, 140, "CMAKE_CXX_STANDARD 98"] close:[140, 141, ")"]
    Argument[119, 137] text:[119, 137, "CMAKE_CXX_STANDARD"]
    Argument[138, 140] text:[138, 140, "98"]
  LineEnding[141, 142]
  Command[142, 174] text:[142, 145, "set"] open:[145, 146, "("] arguments:[146, 173, "CMAKE_PROJECT_NAME tft_life"] close:[173, 174, ")"]
    Argument[146, 164] text:[146, 164, "CMAKE_PROJECT_NAME"]
    Argument[165, 173] text:[165, 173, "tft_life"]
  LineEnding[174, 175]
  BlankLine[175, 176]
  LineComment[176, 216] open:[176, 177, "#"] text:[177, 216, "# This must be set before project call\n"]
  Command[216, 252] text:[216, 219, "set"] open:[219, 220, "("] arguments:[220, 251, "${CMAKE_PROJECT_NAME}_BOARD uno"] close:[251, 252, ")"]
    Argument[220, 247] text:[220, 247, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[248, 251] text:[248, 251, "uno"]
  LineEnding[252, 253]
  Command[253, 274] text:[253, 256, "set"] open:[256, 257, "("] arguments:[257, 273, "ARDUINO_CPU none"] close:[273, 274, ")"]
    Argument[257, 268] text:[257, 268, "ARDUINO_CPU"]
    Argument[269, 273] text:[269, 273, "none"]
  LineEnding[274, 275]
  BlankLine[275, 276]
  Command[276, 306] text:[276, 283, "project"] open:[283, 284, "("] arguments:[284, 305, "${CMAKE_PROJECT_NAME}"] close:[305, 306, ")"]
    Argument[284, 305] text:[284, 305, "${CMAKE_PROJECT_NAME}"]
  LineEnding[306, 307]
  BlankLine[307, 308]
  LineComment[308, 333] open:[308, 309, "#"] text:[309, 333, " Define the source code\n"]
  Command[333, 377] text:[333, 336, "set"] open:[336, 337, "("] arguments:[337, 376, "${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp"] close:[376, 377, ")"]
    Argument[337, 363] text:[337, 363, "${CMAKE_PROJECT_NAME}_SRCS"]
    Argument[364, 376] text:[364, 376, "tft_life.cpp"]
  LineEnding[377, 378]
  LineComment[378, 426] open:[378, 379, "#"] text:[379, 426, "set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)\n"]
  Command[426, 474] text:[426, 442, "link_directories"] open:[442, 443, "("] arguments:[443, 473, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[473, 474, ")"]
    Argument[443, 473] text:[443, 473, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
  LineEnding[474, 475]
  BlankLine[475, 476]
  LineComment[476, 523] open:[476, 477, "#"] text:[477, 523, "## Add project sub-directories into the build\n"]
  Command[523, 572] text:[523, 539, "add_subdirectory"] open:[539, 540, "("] arguments:[540, 571, "${CMAKE_CURRENT_SOURCE_DIR}/sub"] close:[571, 572, ")"]
    Argument[540, 571] text:[540, 571, "${CMAKE_CURRENT_SOURCE_DIR}/sub"]
  LineEnding[572, 573]
  BlankLine[573, 574]
  LineComment[574, 626] open:[574, 575, "#"] text:[575, 626, "### Uncomment below additional settings as needed.\n"]
  Command[626, 674] text:[626, 629, "set"] open:[629, 630, "("] arguments:[630, 673, "${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[673, 674, ")"]
    Argument[630, 662] text:[630, 662, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[663, 673] text:[663, 673, "avrispmkii"]
  LineEnding[674, 675]
  Command[675, 733] text:[675, 678, "set"] open:[678, 679, "("] arguments:[679, 732, "${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[732, 733, ")"]
    Argument[679, 705] text:[679, 705, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[706, 732] text:[706, 732, "/dev/cu.usbserial-00000000"]
  LineEnding[733, 734]
  Command[734, 771] text:[734, 737, "set"] open:[738, 739, "("] arguments:[739, 770, "${CMAKE_PROJECT_NAME}_AFLAGS -v"] close:[770, 771, ")"]
    Argument[739, 767] text:[739, 767, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[768, 770] text:[768, 770, "-v"]
  LineEnding[771, 772]
  LineComment[772, 802] open:[772, 773, "#"] text:[773, 802, " set(pro.upload.speed 57600)\n"]
  BlankLine[802, 803]
  Command[803, 851] text:[803, 828, "generate_arduino_firmware"] open:[828, 829, "("] arguments:[829, 850, "${CMAKE_PROJECT_NAME}"] close:[850, 851, ")"]
    Argument[829, 850] text:[829, 850, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


actual file

```````````````````````````````` example(Arduno: 2) options(change-all)
cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake")
set(CMAKE_CXX_STANDARD 98)
set(CMAKE_PROJECT_NAME tft_life)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD uno)
set(ARDUINO_CPU none)

project(${CMAKE_PROJECT_NAME})

# Define the source code
set(${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp)
set(${CMAKE_PROJECT_NAME}_HDRS tft_life.h)
set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)

### Add project sub-directories into the build
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)

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
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
set(CMAKE_PROJECT_NAME tft_life)

## This must be set before project call
set(${CMAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)

project(${CMAKE_PROJECT_NAME})

# Define the source code
set(${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp setSrcs)
set(${CMAKE_PROJECT_NAME}_HDRS tft_life.h setHdrs)
set(${CMAKE_PROJECT_NAME}_SKETCH setSketch)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/.. linkDirectories)

### Add project sub-directories into the build
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)
add_subdirectory(addSubdirectory)

#### Uncomment below additional settings as needed.
set(${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${CMAKE_PROJECT_NAME}_PORT setPort)
set(${CMAKE_PROJECT_NAME}_AFLAGS -v setAflags)

set(setBoard.upload.speed setUploadSpeed)
set(libName_RECURSE setLibNameRecurse)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 926]
  Command[0, 37] text:[0, 22, "cmake_minimum_required"] open:[22, 23, "("] arguments:[23, 36, "VERSION 2.8.4"] close:[36, 37, ")"]
    Argument[23, 30] text:[23, 30, "VERSION"]
    Argument[31, 36] text:[31, 36, "2.8.4"]
  LineEnding[37, 38]
  Command[38, 114] text:[38, 41, "set"] open:[41, 42, "("] arguments:[42, 113, "CMAKE_TOOLCHAIN_FILE \"${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake\""] close:[113, 114, ")"]
    Argument[42, 62] text:[42, 62, "CMAKE_TOOLCHAIN_FILE"]
    Argument[63, 113] open:[63, 64, "\""] text:[64, 112, "${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake"] close:[112, 113, "\""]
  LineEnding[114, 115]
  Command[115, 141] text:[115, 118, "set"] open:[118, 119, "("] arguments:[119, 140, "CMAKE_CXX_STANDARD 98"] close:[140, 141, ")"]
    Argument[119, 137] text:[119, 137, "CMAKE_CXX_STANDARD"]
    Argument[138, 140] text:[138, 140, "98"]
  LineEnding[141, 142]
  Command[142, 174] text:[142, 145, "set"] open:[145, 146, "("] arguments:[146, 173, "CMAKE_PROJECT_NAME tft_life"] close:[173, 174, ")"]
    Argument[146, 164] text:[146, 164, "CMAKE_PROJECT_NAME"]
    Argument[165, 173] text:[165, 173, "tft_life"]
  LineEnding[174, 175]
  BlankLine[175, 176]
  LineComment[176, 216] open:[176, 177, "#"] text:[177, 216, "# This must be set before project call\n"]
  Command[216, 252] text:[216, 219, "set"] open:[219, 220, "("] arguments:[220, 251, "${CMAKE_PROJECT_NAME}_BOARD uno"] close:[251, 252, ")"]
    Argument[220, 247] text:[220, 247, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[248, 251] text:[248, 251, "uno"]
  LineEnding[252, 253]
  Command[253, 274] text:[253, 256, "set"] open:[256, 257, "("] arguments:[257, 273, "ARDUINO_CPU none"] close:[273, 274, ")"]
    Argument[257, 268] text:[257, 268, "ARDUINO_CPU"]
    Argument[269, 273] text:[269, 273, "none"]
  LineEnding[274, 275]
  BlankLine[275, 276]
  Command[276, 306] text:[276, 283, "project"] open:[283, 284, "("] arguments:[284, 305, "${CMAKE_PROJECT_NAME}"] close:[305, 306, ")"]
    Argument[284, 305] text:[284, 305, "${CMAKE_PROJECT_NAME}"]
  LineEnding[306, 307]
  BlankLine[307, 308]
  LineComment[308, 333] open:[308, 309, "#"] text:[309, 333, " Define the source code\n"]
  Command[333, 377] text:[333, 336, "set"] open:[336, 337, "("] arguments:[337, 376, "${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp"] close:[376, 377, ")"]
    Argument[337, 363] text:[337, 363, "${CMAKE_PROJECT_NAME}_SRCS"]
    Argument[364, 376] text:[364, 376, "tft_life.cpp"]
  LineEnding[377, 378]
  Command[378, 420] text:[378, 381, "set"] open:[381, 382, "("] arguments:[382, 419, "${CMAKE_PROJECT_NAME}_HDRS tft_life.h"] close:[419, 420, ")"]
    Argument[382, 408] text:[382, 408, "${CMAKE_PROJECT_NAME}_HDRS"]
    Argument[409, 419] text:[409, 419, "tft_life.h"]
  LineEnding[420, 421]
  Command[421, 467] text:[421, 424, "set"] open:[424, 425, "("] arguments:[425, 466, "${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp"] close:[466, 467, ")"]
    Argument[425, 453] text:[425, 453, "${CMAKE_PROJECT_NAME}_SKETCH"]
    Argument[454, 466] text:[454, 466, "tft_life.cpp"]
  LineEnding[467, 468]
  Command[468, 516] text:[468, 484, "link_directories"] open:[484, 485, "("] arguments:[485, 515, "${CMAKE_CURRENT_SOURCE_DIR}/.."] close:[515, 516, ")"]
    Argument[485, 515] text:[485, 515, "${CMAKE_CURRENT_SOURCE_DIR}/.."]
  LineEnding[516, 517]
  BlankLine[517, 518]
  LineComment[518, 565] open:[518, 519, "#"] text:[519, 565, "## Add project sub-directories into the build\n"]
  Command[565, 614] text:[565, 581, "add_subdirectory"] open:[581, 582, "("] arguments:[582, 613, "${CMAKE_CURRENT_SOURCE_DIR}/sub"] close:[613, 614, ")"]
    Argument[582, 613] text:[582, 613, "${CMAKE_CURRENT_SOURCE_DIR}/sub"]
  LineEnding[614, 615]
  BlankLine[615, 616]
  LineComment[616, 668] open:[616, 617, "#"] text:[617, 668, "### Uncomment below additional settings as needed.\n"]
  Command[668, 716] text:[668, 671, "set"] open:[671, 672, "("] arguments:[672, 715, "${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii"] close:[715, 716, ")"]
    Argument[672, 704] text:[672, 704, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[705, 715] text:[705, 715, "avrispmkii"]
  LineEnding[720, 721]
  Command[721, 779] text:[721, 724, "set"] open:[724, 725, "("] arguments:[725, 778, "${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000"] close:[778, 779, ")"]
    Argument[725, 751] text:[725, 751, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[752, 778] text:[752, 778, "/dev/cu.usbserial-00000000"]
  LineEnding[780, 781]
  Command[781, 818] text:[781, 784, "set"] open:[785, 786, "("] arguments:[786, 817, "${CMAKE_PROJECT_NAME}_AFLAGS -v"] close:[817, 818, ")"]
    Argument[786, 814] text:[786, 814, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[815, 817] text:[815, 817, "-v"]
  LineEnding[819, 820]
  BlankLine[820, 821]
  Command[821, 848] text:[821, 824, "set"] open:[824, 825, "("] arguments:[825, 847, "uno.upload.speed 57600"] close:[847, 848, ")"]
    Argument[825, 841] text:[825, 841, "uno.upload.speed"]
    Argument[842, 847] text:[842, 847, "57600"]
  LineEnding[848, 849]
  Command[849, 875] text:[849, 852, "set"] open:[852, 853, "("] arguments:[853, 874, "libName_RECURSE false"] close:[874, 875, ")"]
    Argument[853, 868] text:[853, 868, "libName_RECURSE"]
    Argument[869, 874] text:[869, 874, "false"]
  LineEnding[876, 877]
  BlankLine[877, 878]
  Command[878, 926] text:[878, 903, "generate_arduino_firmware"] open:[903, 904, "("] arguments:[904, 925, "${CMAKE_PROJECT_NAME}"] close:[925, 926, ")"]
    Argument[904, 925] text:[904, 925, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


test case

```````````````````````````````` example(Arduno: 3) options(change-all)
set(${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp)
set(${CMAKE_PROJECT_NAME}_HDRS tft_life.h)
.
set(${CMAKE_PROJECT_NAME}_SRCS tft_life.cpp setSrcs)
set(${CMAKE_PROJECT_NAME}_HDRS tft_life.h setHdrs)
````````````````````````````````


test case

```````````````````````````````` example(Arduno: 4) options(change-all)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/.. ${CMAKE_CURRENT_SOURCE_DIR}/sub)
.
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/.. ${CMAKE_CURRENT_SOURCE_DIR}/sub linkDirectories)
````````````````````````````````


test case

```````````````````````````````` example(Arduno: 5) options(change-all)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/.. linkDirectories)
.
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/.. linkDirectories)
````````````````````````````````


test case

```````````````````````````````` example(Arduno: 6) options(change-all)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)
.
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)
add_subdirectory(addSubdirectory)
````````````````````````````````


test case

```````````````````````````````` example(Arduno: 7) options(change-all)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)
.
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)
add_subdirectory(addSubdirectory)
````````````````````````````````


