cmake_minimum_required(VERSION 2.8.4)
set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
set(CMAKE_CXX_STANDARD 98)

set(PROJECT_NAME project_name)
set(${CMAKE_PROJECT_NAME}_BOARD board)
set(ARDUINO_CPU cpu)
project(${PROJECT_NAME})

# Define the source code for cpp files or default arduino sketch files
set(${PROJECT_NAME}_SRCS)
set(${PROJECT_NAME}_HDRS User_Setup.h)

### Additional static libraries to include in the target.
set(${CMAKE_PROJECT_NAME}_LIBS)

set(${CMAKE_PROJECT_NAME}_SKETCH sketch_name.ino)

### Additional settings to add non-standard or your own Arduino libraries.
# For this example (libs will contain additional arduino libraries)
# An Arduino library my_lib will contain files in libs/my_lib/: my_lib.h, my_lib.cpp + any other cpp files
# link_directories(${CMAKE_CURRENT_SOURCE_DIR}/libs)

# For nested library sources replace ${LIB_NAME} with library name for each library
# set(${LIB_NAME}_RECURSE true)

#### Additional settings for programmer. From programmers.txt
set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
set(board.upload.speed baud)

## Verbose build process
set(${CMAKE_PROJECT_NAME}_AFLAGS -v)

generate_arduino_firmware(${CMAKE_PROJECT_NAME})
