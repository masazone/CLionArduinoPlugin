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
# set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
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
  CommentedOutCommand[379, 425] comment:[378, 379, "#"] text:[379, 382, "set"] open:[382, 383, "("] arguments:[383, 424, "${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp"] close:[424, 425, ")"]
    Argument[383, 411] text:[383, 411, "${CMAKE_PROJECT_NAME}_SKETCH"]
    Argument[412, 424] text:[412, 424, "tft_life.cpp"]
  LineEnding[425, 426]
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
  CommentedOutCommand[774, 801] comment:[772, 773, "#"] text:[774, 777, "set"] open:[777, 778, "("] arguments:[778, 800, "pro.upload.speed 57600"] close:[800, 801, ")"]
    Argument[778, 794] text:[778, 794, "pro.upload.speed"]
    Argument[795, 800] text:[795, 800, "57600"]
  LineEnding[801, 802]
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


test case

```````````````````````````````` example(Arduno: 8) options(change-all, set-or-add)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)
.
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
set(PROJECT_NAME setProjectName)
set(${CMAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
link_directories(linkDirectories)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/sub)
set(${CMAKE_PROJECT_NAME}_SKETCH setSketch)
set(${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${CMAKE_PROJECT_NAME}_PORT setPort)
set(${CMAKE_PROJECT_NAME}_AFLAGS setAflags)
set(${CMAKE_PROJECT_NAME}_HDRS setHdrs)
set(${CMAKE_PROJECT_NAME}_SRCS setSrcs)
add_subdirectory(addSubdirectory)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
generate_arduino_firmware(${CMAKE_PROJECT_NAME})
generate_arduino_library(${CMAKE_PROJECT_NAME})
````````````````````````````````


test case

```````````````````````````````` example(Arduno: 9) options(change-all, set-or-add)
## Top

## Before project
project(${CMAKE_PROJECT_NAME})
## After project

## Bottom
.
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
## Top

## Before project
set(PROJECT_NAME setProjectName)
set(${CMAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
project(${CMAKE_PROJECT_NAME})
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${CMAKE_PROJECT_NAME}_SKETCH setSketch)
set(${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${CMAKE_PROJECT_NAME}_PORT setPort)
set(${CMAKE_PROJECT_NAME}_AFLAGS setAflags)
set(${CMAKE_PROJECT_NAME}_HDRS setHdrs)
set(${CMAKE_PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME})
generate_arduino_library(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 84]
  LineComment[0, 7] open:[0, 1, "#"] text:[1, 7, "# Top\n"]
  BlankLine[7, 8]
  LineComment[8, 26] open:[8, 9, "#"] text:[9, 26, "# Before project\n"]
  Command[26, 56] text:[26, 33, "project"] open:[33, 34, "("] arguments:[34, 55, "${CMAKE_PROJECT_NAME}"] close:[55, 56, ")"]
    Argument[34, 55] text:[34, 55, "${CMAKE_PROJECT_NAME}"]
  LineEnding[56, 57]
  LineComment[57, 74] open:[57, 58, "#"] text:[58, 74, "# After project\n"]
  BlankLine[74, 75]
  LineComment[75, 84] open:[75, 76, "#"] text:[76, 84, "# Bottom"]
````````````````````````````````


```````````````````````````````` example(Arduno: 10) options(add-project, set-or-add)
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
## Top

## Before project
set(${CMAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${CMAKE_PROJECT_NAME}_SKETCH setSketch)
set(${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${CMAKE_PROJECT_NAME}_PORT setPort)
set(${CMAKE_PROJECT_NAME}_AFLAGS setAflags)
set(${CMAKE_PROJECT_NAME}_HDRS setHdrs)
set(${CMAKE_PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME} )
generate_arduino_library(${CMAKE_PROJECT_NAME} )
.
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
## Top

## Before project
set(${CMAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
project(${CMAKE_PROJECT_NAME})
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${CMAKE_PROJECT_NAME}_SKETCH setSketch)
set(${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${CMAKE_PROJECT_NAME}_PORT setPort)
set(${CMAKE_PROJECT_NAME}_AFLAGS setAflags)
set(${CMAKE_PROJECT_NAME}_HDRS setHdrs)
set(${CMAKE_PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME})
generate_arduino_library(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 719]
  Command[0, 47] text:[0, 3, "set"] open:[3, 4, "("] arguments:[4, 46, "CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile"] close:[46, 47, ")"]
    Argument[4, 24] text:[4, 24, "CMAKE_TOOLCHAIN_FILE"]
    Argument[25, 46] text:[25, 46, "setCmakeToolchainFile"]
  LineEnding[47, 48]
  Command[48, 91] text:[48, 51, "set"] open:[51, 52, "("] arguments:[52, 90, "CMAKE_CXX_STANDARD setCmakeCxxStandard"] close:[90, 91, ")"]
    Argument[52, 70] text:[52, 70, "CMAKE_CXX_STANDARD"]
    Argument[71, 90] text:[71, 90, "setCmakeCxxStandard"]
  LineEnding[91, 92]
  LineComment[92, 99] open:[92, 93, "#"] text:[93, 99, "# Top\n"]
  BlankLine[99, 100]
  LineComment[100, 118] open:[100, 101, "#"] text:[101, 118, "# Before project\n"]
  Command[118, 159] text:[118, 121, "set"] open:[121, 122, "("] arguments:[122, 158, "${CMAKE_PROJECT_NAME}_BOARD setBoard"] close:[158, 159, ")"]
    Argument[122, 149] text:[122, 149, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[150, 158] text:[150, 158, "setBoard"]
  LineEnding[159, 160]
  Command[160, 183] text:[160, 163, "set"] open:[163, 164, "("] arguments:[164, 182, "ARDUINO_CPU setCpu"] close:[182, 183, ")"]
    Argument[164, 175] text:[164, 175, "ARDUINO_CPU"]
    Argument[176, 182] text:[176, 182, "setCpu"]
  LineEnding[183, 184]
  Command[184, 217] text:[184, 200, "link_directories"] open:[200, 201, "("] arguments:[201, 216, "linkDirectories"] close:[216, 217, ")"]
    Argument[201, 216] text:[201, 216, "linkDirectories"]
  LineEnding[217, 218]
  Command[218, 251] text:[218, 234, "add_subdirectory"] open:[234, 235, "("] arguments:[235, 250, "addSubdirectory"] close:[250, 251, ")"]
    Argument[235, 250] text:[235, 250, "addSubdirectory"]
  LineEnding[251, 252]
  Command[252, 295] text:[252, 255, "set"] open:[255, 256, "("] arguments:[256, 294, "${CMAKE_PROJECT_NAME}_SKETCH setSketch"] close:[294, 295, ")"]
    Argument[256, 284] text:[256, 284, "${CMAKE_PROJECT_NAME}_SKETCH"]
    Argument[285, 294] text:[285, 294, "setSketch"]
  LineEnding[295, 296]
  Command[296, 347] text:[296, 299, "set"] open:[299, 300, "("] arguments:[300, 346, "${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer"] close:[346, 347, ")"]
    Argument[300, 332] text:[300, 332, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[333, 346] text:[333, 346, "setProgrammer"]
  LineEnding[347, 348]
  Command[348, 387] text:[348, 351, "set"] open:[351, 352, "("] arguments:[352, 386, "${CMAKE_PROJECT_NAME}_PORT setPort"] close:[386, 387, ")"]
    Argument[352, 378] text:[352, 378, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[379, 386] text:[379, 386, "setPort"]
  LineEnding[387, 388]
  Command[388, 431] text:[388, 391, "set"] open:[391, 392, "("] arguments:[392, 430, "${CMAKE_PROJECT_NAME}_AFLAGS setAflags"] close:[430, 431, ")"]
    Argument[392, 420] text:[392, 420, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[421, 430] text:[421, 430, "setAflags"]
  LineEnding[431, 432]
  Command[432, 471] text:[432, 435, "set"] open:[435, 436, "("] arguments:[436, 470, "${CMAKE_PROJECT_NAME}_HDRS setHdrs"] close:[470, 471, ")"]
    Argument[436, 462] text:[436, 462, "${CMAKE_PROJECT_NAME}_HDRS"]
    Argument[463, 470] text:[463, 470, "setHdrs"]
  LineEnding[471, 472]
  Command[472, 511] text:[472, 475, "set"] open:[475, 476, "("] arguments:[476, 510, "${CMAKE_PROJECT_NAME}_SRCS setSrcs"] close:[510, 511, ")"]
    Argument[476, 502] text:[476, 502, "${CMAKE_PROJECT_NAME}_SRCS"]
    Argument[503, 510] text:[503, 510, "setSrcs"]
  LineEnding[511, 512]
  Command[512, 550] text:[512, 515, "set"] open:[515, 516, "("] arguments:[516, 549, "libName_RECURSE setLibNameRecurse"] close:[549, 550, ")"]
    Argument[516, 531] text:[516, 531, "libName_RECURSE"]
    Argument[532, 549] text:[532, 549, "setLibNameRecurse"]
  LineEnding[550, 551]
  Command[551, 592] text:[551, 554, "set"] open:[554, 555, "("] arguments:[555, 591, "setBoard.upload.speed setUploadSpeed"] close:[591, 592, ")"]
    Argument[555, 576] text:[555, 576, "setBoard.upload.speed"]
    Argument[577, 591] text:[577, 591, "setUploadSpeed"]
  LineEnding[592, 593]
  LineComment[593, 610] open:[593, 594, "#"] text:[594, 610, "# After project\n"]
  BlankLine[610, 611]
  LineComment[611, 621] open:[611, 612, "#"] text:[612, 621, "# Bottom\n"]
  Command[621, 670] text:[621, 646, "generate_arduino_firmware"] open:[646, 647, "("] arguments:[647, 669, "${CMAKE_PROJECT_NAME} "] close:[669, 670, ")"]
    Argument[647, 668] text:[647, 668, "${CMAKE_PROJECT_NAME}"]
  LineEnding[670, 671]
  Command[671, 719] text:[671, 695, "generate_arduino_library"] open:[695, 696, "("] arguments:[696, 718, "${CMAKE_PROJECT_NAME} "] close:[718, 719, ")"]
    Argument[696, 717] text:[696, 717, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


test uncomment command

```````````````````````````````` example(Arduno: 11) options(add-project, set-or-add)
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
## Top

## Before project
set(${CMAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${CMAKE_PROJECT_NAME}_SKETCH setSketch)
#set(${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${CMAKE_PROJECT_NAME}_PORT setPort)
set(${CMAKE_PROJECT_NAME}_AFLAGS setAflags)
set(${CMAKE_PROJECT_NAME}_HDRS setHdrs)
set(${CMAKE_PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME} )
generate_arduino_library(${CMAKE_PROJECT_NAME} )
.
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
## Top

## Before project
set(${CMAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
project(${CMAKE_PROJECT_NAME})
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${CMAKE_PROJECT_NAME}_SKETCH setSketch)
set(${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${CMAKE_PROJECT_NAME}_PORT setPort)
set(${CMAKE_PROJECT_NAME}_AFLAGS setAflags)
set(${CMAKE_PROJECT_NAME}_HDRS setHdrs)
set(${CMAKE_PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME})
generate_arduino_library(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 720]
  Command[0, 47] text:[0, 3, "set"] open:[3, 4, "("] arguments:[4, 46, "CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile"] close:[46, 47, ")"]
    Argument[4, 24] text:[4, 24, "CMAKE_TOOLCHAIN_FILE"]
    Argument[25, 46] text:[25, 46, "setCmakeToolchainFile"]
  LineEnding[47, 48]
  Command[48, 91] text:[48, 51, "set"] open:[51, 52, "("] arguments:[52, 90, "CMAKE_CXX_STANDARD setCmakeCxxStandard"] close:[90, 91, ")"]
    Argument[52, 70] text:[52, 70, "CMAKE_CXX_STANDARD"]
    Argument[71, 90] text:[71, 90, "setCmakeCxxStandard"]
  LineEnding[91, 92]
  LineComment[92, 99] open:[92, 93, "#"] text:[93, 99, "# Top\n"]
  BlankLine[99, 100]
  LineComment[100, 118] open:[100, 101, "#"] text:[101, 118, "# Before project\n"]
  Command[118, 159] text:[118, 121, "set"] open:[121, 122, "("] arguments:[122, 158, "${CMAKE_PROJECT_NAME}_BOARD setBoard"] close:[158, 159, ")"]
    Argument[122, 149] text:[122, 149, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[150, 158] text:[150, 158, "setBoard"]
  LineEnding[159, 160]
  Command[160, 183] text:[160, 163, "set"] open:[163, 164, "("] arguments:[164, 182, "ARDUINO_CPU setCpu"] close:[182, 183, ")"]
    Argument[164, 175] text:[164, 175, "ARDUINO_CPU"]
    Argument[176, 182] text:[176, 182, "setCpu"]
  LineEnding[183, 184]
  Command[184, 217] text:[184, 200, "link_directories"] open:[200, 201, "("] arguments:[201, 216, "linkDirectories"] close:[216, 217, ")"]
    Argument[201, 216] text:[201, 216, "linkDirectories"]
  LineEnding[217, 218]
  Command[218, 251] text:[218, 234, "add_subdirectory"] open:[234, 235, "("] arguments:[235, 250, "addSubdirectory"] close:[250, 251, ")"]
    Argument[235, 250] text:[235, 250, "addSubdirectory"]
  LineEnding[251, 252]
  Command[252, 295] text:[252, 255, "set"] open:[255, 256, "("] arguments:[256, 294, "${CMAKE_PROJECT_NAME}_SKETCH setSketch"] close:[294, 295, ")"]
    Argument[256, 284] text:[256, 284, "${CMAKE_PROJECT_NAME}_SKETCH"]
    Argument[285, 294] text:[285, 294, "setSketch"]
  LineEnding[295, 296]
  CommentedOutCommand[297, 348] comment:[296, 297, "#"] text:[297, 300, "set"] open:[300, 301, "("] arguments:[301, 347, "${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer"] close:[347, 348, ")"]
    Argument[301, 333] text:[301, 333, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[334, 347] text:[334, 347, "setProgrammer"]
  LineEnding[348, 349]
  Command[349, 388] text:[349, 352, "set"] open:[352, 353, "("] arguments:[353, 387, "${CMAKE_PROJECT_NAME}_PORT setPort"] close:[387, 388, ")"]
    Argument[353, 379] text:[353, 379, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[380, 387] text:[380, 387, "setPort"]
  LineEnding[388, 389]
  Command[389, 432] text:[389, 392, "set"] open:[392, 393, "("] arguments:[393, 431, "${CMAKE_PROJECT_NAME}_AFLAGS setAflags"] close:[431, 432, ")"]
    Argument[393, 421] text:[393, 421, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[422, 431] text:[422, 431, "setAflags"]
  LineEnding[432, 433]
  Command[433, 472] text:[433, 436, "set"] open:[436, 437, "("] arguments:[437, 471, "${CMAKE_PROJECT_NAME}_HDRS setHdrs"] close:[471, 472, ")"]
    Argument[437, 463] text:[437, 463, "${CMAKE_PROJECT_NAME}_HDRS"]
    Argument[464, 471] text:[464, 471, "setHdrs"]
  LineEnding[472, 473]
  Command[473, 512] text:[473, 476, "set"] open:[476, 477, "("] arguments:[477, 511, "${CMAKE_PROJECT_NAME}_SRCS setSrcs"] close:[511, 512, ")"]
    Argument[477, 503] text:[477, 503, "${CMAKE_PROJECT_NAME}_SRCS"]
    Argument[504, 511] text:[504, 511, "setSrcs"]
  LineEnding[512, 513]
  Command[513, 551] text:[513, 516, "set"] open:[516, 517, "("] arguments:[517, 550, "libName_RECURSE setLibNameRecurse"] close:[550, 551, ")"]
    Argument[517, 532] text:[517, 532, "libName_RECURSE"]
    Argument[533, 550] text:[533, 550, "setLibNameRecurse"]
  LineEnding[551, 552]
  Command[552, 593] text:[552, 555, "set"] open:[555, 556, "("] arguments:[556, 592, "setBoard.upload.speed setUploadSpeed"] close:[592, 593, ")"]
    Argument[556, 577] text:[556, 577, "setBoard.upload.speed"]
    Argument[578, 592] text:[578, 592, "setUploadSpeed"]
  LineEnding[593, 594]
  LineComment[594, 611] open:[594, 595, "#"] text:[595, 611, "# After project\n"]
  BlankLine[611, 612]
  LineComment[612, 622] open:[612, 613, "#"] text:[613, 622, "# Bottom\n"]
  Command[622, 671] text:[622, 647, "generate_arduino_firmware"] open:[647, 648, "("] arguments:[648, 670, "${CMAKE_PROJECT_NAME} "] close:[670, 671, ")"]
    Argument[648, 669] text:[648, 669, "${CMAKE_PROJECT_NAME}"]
  LineEnding[671, 672]
  Command[672, 720] text:[672, 696, "generate_arduino_library"] open:[696, 697, "("] arguments:[697, 719, "${CMAKE_PROJECT_NAME} "] close:[719, 720, ")"]
    Argument[697, 718] text:[697, 718, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


test uncomment command

```````````````````````````````` example(Arduno: 12) options(add-project, set-or-add)
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
## Top

## Before project
set(${CMAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
#project(${CMAKE_PROJECT_NAME})
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${CMAKE_PROJECT_NAME}_SKETCH setSketch)
#set(${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${CMAKE_PROJECT_NAME}_PORT setPort)
set(${CMAKE_PROJECT_NAME}_AFLAGS setAflags)
set(${CMAKE_PROJECT_NAME}_HDRS setHdrs)
set(${CMAKE_PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME} )
generate_arduino_library(${CMAKE_PROJECT_NAME} )
.
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
## Top

## Before project
set(${CMAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
project(${CMAKE_PROJECT_NAME})
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${CMAKE_PROJECT_NAME}_SKETCH setSketch)
set(${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${CMAKE_PROJECT_NAME}_PORT setPort)
set(${CMAKE_PROJECT_NAME}_AFLAGS setAflags)
set(${CMAKE_PROJECT_NAME}_HDRS setHdrs)
set(${CMAKE_PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME})
generate_arduino_library(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 752]
  Command[0, 47] text:[0, 3, "set"] open:[3, 4, "("] arguments:[4, 46, "CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile"] close:[46, 47, ")"]
    Argument[4, 24] text:[4, 24, "CMAKE_TOOLCHAIN_FILE"]
    Argument[25, 46] text:[25, 46, "setCmakeToolchainFile"]
  LineEnding[47, 48]
  Command[48, 91] text:[48, 51, "set"] open:[51, 52, "("] arguments:[52, 90, "CMAKE_CXX_STANDARD setCmakeCxxStandard"] close:[90, 91, ")"]
    Argument[52, 70] text:[52, 70, "CMAKE_CXX_STANDARD"]
    Argument[71, 90] text:[71, 90, "setCmakeCxxStandard"]
  LineEnding[91, 92]
  LineComment[92, 99] open:[92, 93, "#"] text:[93, 99, "# Top\n"]
  BlankLine[99, 100]
  LineComment[100, 118] open:[100, 101, "#"] text:[101, 118, "# Before project\n"]
  Command[118, 159] text:[118, 121, "set"] open:[121, 122, "("] arguments:[122, 158, "${CMAKE_PROJECT_NAME}_BOARD setBoard"] close:[158, 159, ")"]
    Argument[122, 149] text:[122, 149, "${CMAKE_PROJECT_NAME}_BOARD"]
    Argument[150, 158] text:[150, 158, "setBoard"]
  LineEnding[159, 160]
  Command[160, 183] text:[160, 163, "set"] open:[163, 164, "("] arguments:[164, 182, "ARDUINO_CPU setCpu"] close:[182, 183, ")"]
    Argument[164, 175] text:[164, 175, "ARDUINO_CPU"]
    Argument[176, 182] text:[176, 182, "setCpu"]
  LineEnding[183, 184]
  CommentedOutCommand[185, 215] comment:[184, 185, "#"] text:[185, 192, "project"] open:[192, 193, "("] arguments:[193, 214, "${CMAKE_PROJECT_NAME}"] close:[214, 215, ")"]
    Argument[193, 214] text:[193, 214, "${CMAKE_PROJECT_NAME}"]
  LineEnding[215, 216]
  Command[216, 249] text:[216, 232, "link_directories"] open:[232, 233, "("] arguments:[233, 248, "linkDirectories"] close:[248, 249, ")"]
    Argument[233, 248] text:[233, 248, "linkDirectories"]
  LineEnding[249, 250]
  Command[250, 283] text:[250, 266, "add_subdirectory"] open:[266, 267, "("] arguments:[267, 282, "addSubdirectory"] close:[282, 283, ")"]
    Argument[267, 282] text:[267, 282, "addSubdirectory"]
  LineEnding[283, 284]
  Command[284, 327] text:[284, 287, "set"] open:[287, 288, "("] arguments:[288, 326, "${CMAKE_PROJECT_NAME}_SKETCH setSketch"] close:[326, 327, ")"]
    Argument[288, 316] text:[288, 316, "${CMAKE_PROJECT_NAME}_SKETCH"]
    Argument[317, 326] text:[317, 326, "setSketch"]
  LineEnding[327, 328]
  CommentedOutCommand[329, 380] comment:[328, 329, "#"] text:[329, 332, "set"] open:[332, 333, "("] arguments:[333, 379, "${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer"] close:[379, 380, ")"]
    Argument[333, 365] text:[333, 365, "${CMAKE_PROJECT_NAME}_PROGRAMMER"]
    Argument[366, 379] text:[366, 379, "setProgrammer"]
  LineEnding[380, 381]
  Command[381, 420] text:[381, 384, "set"] open:[384, 385, "("] arguments:[385, 419, "${CMAKE_PROJECT_NAME}_PORT setPort"] close:[419, 420, ")"]
    Argument[385, 411] text:[385, 411, "${CMAKE_PROJECT_NAME}_PORT"]
    Argument[412, 419] text:[412, 419, "setPort"]
  LineEnding[420, 421]
  Command[421, 464] text:[421, 424, "set"] open:[424, 425, "("] arguments:[425, 463, "${CMAKE_PROJECT_NAME}_AFLAGS setAflags"] close:[463, 464, ")"]
    Argument[425, 453] text:[425, 453, "${CMAKE_PROJECT_NAME}_AFLAGS"]
    Argument[454, 463] text:[454, 463, "setAflags"]
  LineEnding[464, 465]
  Command[465, 504] text:[465, 468, "set"] open:[468, 469, "("] arguments:[469, 503, "${CMAKE_PROJECT_NAME}_HDRS setHdrs"] close:[503, 504, ")"]
    Argument[469, 495] text:[469, 495, "${CMAKE_PROJECT_NAME}_HDRS"]
    Argument[496, 503] text:[496, 503, "setHdrs"]
  LineEnding[504, 505]
  Command[505, 544] text:[505, 508, "set"] open:[508, 509, "("] arguments:[509, 543, "${CMAKE_PROJECT_NAME}_SRCS setSrcs"] close:[543, 544, ")"]
    Argument[509, 535] text:[509, 535, "${CMAKE_PROJECT_NAME}_SRCS"]
    Argument[536, 543] text:[536, 543, "setSrcs"]
  LineEnding[544, 545]
  Command[545, 583] text:[545, 548, "set"] open:[548, 549, "("] arguments:[549, 582, "libName_RECURSE setLibNameRecurse"] close:[582, 583, ")"]
    Argument[549, 564] text:[549, 564, "libName_RECURSE"]
    Argument[565, 582] text:[565, 582, "setLibNameRecurse"]
  LineEnding[583, 584]
  Command[584, 625] text:[584, 587, "set"] open:[587, 588, "("] arguments:[588, 624, "setBoard.upload.speed setUploadSpeed"] close:[624, 625, ")"]
    Argument[588, 609] text:[588, 609, "setBoard.upload.speed"]
    Argument[610, 624] text:[610, 624, "setUploadSpeed"]
  LineEnding[625, 626]
  LineComment[626, 643] open:[626, 627, "#"] text:[627, 643, "# After project\n"]
  BlankLine[643, 644]
  LineComment[644, 654] open:[644, 645, "#"] text:[645, 654, "# Bottom\n"]
  Command[654, 703] text:[654, 679, "generate_arduino_firmware"] open:[679, 680, "("] arguments:[680, 702, "${CMAKE_PROJECT_NAME} "] close:[702, 703, ")"]
    Argument[680, 701] text:[680, 701, "${CMAKE_PROJECT_NAME}"]
  LineEnding[703, 704]
  Command[704, 752] text:[704, 728, "generate_arduino_library"] open:[728, 729, "("] arguments:[729, 751, "${CMAKE_PROJECT_NAME} "] close:[751, 752, ")"]
    Argument[729, 750] text:[729, 750, "${CMAKE_PROJECT_NAME}"]
````````````````````````````````


test takes uncommented over commented

```````````````````````````````` example(Arduno: 13) options(change-all, set-or-add)
#project(${CMAKE_PROJECT_NAME})
## Top

## Before project
project(${CMAKE_PROJECT_NAME})
## After project

## Bottom
.
set(CMAKE_TOOLCHAIN_FILE setCmakeToolchainFile)
set(CMAKE_CXX_STANDARD setCmakeCxxStandard)
# project(${CMAKE_PROJECT_NAME})
## Top

## Before project
set(PROJECT_NAME setProjectName)
set(${CMAKE_PROJECT_NAME}_BOARD setBoard)
set(ARDUINO_CPU setCpu)
project(${CMAKE_PROJECT_NAME})
link_directories(linkDirectories)
add_subdirectory(addSubdirectory)
set(${CMAKE_PROJECT_NAME}_SKETCH setSketch)
set(${CMAKE_PROJECT_NAME}_PROGRAMMER setProgrammer)
set(${CMAKE_PROJECT_NAME}_PORT setPort)
set(${CMAKE_PROJECT_NAME}_AFLAGS setAflags)
set(${CMAKE_PROJECT_NAME}_HDRS setHdrs)
set(${CMAKE_PROJECT_NAME}_SRCS setSrcs)
set(libName_RECURSE setLibNameRecurse)
set(setBoard.upload.speed setUploadSpeed)
## After project

## Bottom
generate_arduino_firmware(${CMAKE_PROJECT_NAME})
generate_arduino_library(${CMAKE_PROJECT_NAME})
.
CMakeFile[0, 116]
  CommentedOutCommand[1, 31] comment:[0, 1, "#"] text:[1, 8, "project"] open:[8, 9, "("] arguments:[9, 30, "${CMAKE_PROJECT_NAME}"] close:[30, 31, ")"]
    Argument[9, 30] text:[9, 30, "${CMAKE_PROJECT_NAME}"]
  LineEnding[31, 32]
  LineComment[32, 39] open:[32, 33, "#"] text:[33, 39, "# Top\n"]
  BlankLine[39, 40]
  LineComment[40, 58] open:[40, 41, "#"] text:[41, 58, "# Before project\n"]
  Command[58, 88] text:[58, 65, "project"] open:[65, 66, "("] arguments:[66, 87, "${CMAKE_PROJECT_NAME}"] close:[87, 88, ")"]
    Argument[66, 87] text:[66, 87, "${CMAKE_PROJECT_NAME}"]
  LineEnding[88, 89]
  LineComment[89, 106] open:[89, 90, "#"] text:[90, 106, "# After project\n"]
  BlankLine[106, 107]
  LineComment[107, 116] open:[107, 108, "#"] text:[108, 116, "# Bottom"]
````````````````````````````````


