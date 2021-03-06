cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)
set(CMAKE_PROJECT_NAME sketch_2)

set(${CMAKE_PROJECT_NAME}_BOARD uno)
# set(ARDUINO_CPU)
project(${CMAKE_PROJECT_NAME})

# Define the source code for cpp files or default arduino sketch files
# set(${CMAKE_PROJECT_NAME}_SRCS)
set(${CMAKE_PROJECT_NAME}_HDRS User_Setup.h)

### Additional static libraries to include in the target.
set(${CMAKE_PROJECT_NAME}_LIBS)

### Main sketch file
set(${CMAKE_PROJECT_NAME}_SKETCH sketch_2.ino)

### Add project directories into the build
# add_subdirectory()

### Additional settings to add non-standard or your own Arduino libraries.
# For this example (libs will contain additional arduino libraries)
# An Arduino library my_lib will contain files in libs/my_lib/: my_lib.h, my_lib.cpp + any other cpp files
# link_directories(${CMAKE_CURRENT_SOURCE_DIR}/libs)
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/libs)

# For nested library sources replace ${LIB_NAME} with library name for each library
# set(${LIB_NAME}_RECURSE true)

#### Additional settings for programmer. From programmers.txt
set(${CMAKE_PROJECT_NAME}_PROGRAMMER usbtinyisp)
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.Bluetooth-Incoming-Port)
# set(uno.upload.speed 9600)

## Verbose build process
# set(${CMAKE_PROJECT_NAME}_AFLAGS -v)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})


