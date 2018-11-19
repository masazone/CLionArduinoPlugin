package com.vladsch.clionarduinoplugin.generators.cmake;

/**
 * Class for creating, reading and modifying simple CMakeLists.txt
 *
 * Not a spec CMake parser, only intended for Arduino Support created CMakeLists.txt files
 * bracket comments are not supported
 *
 * Multi-line command arguments are supported
 * Quoted and un-quoted arguments are supported
 *
 *
 * cmake_minimum_required(VERSION 2.8.4)
 * set(CMAKE_TOOLCHAIN_FILE ${CMAKE_SOURCE_DIR}/cmake/ArduinoToolchain.cmake)
 * set(PROJECT_NAME tft_life)
 *
 * ## This must be set before project call
 * set(${CMAKE_PROJECT_NAME}_BOARD pro)
 * set(ARDUINO_CPU 8MHzatmega328)
 *
 * project(${PROJECT_NAME})
 *
 * # Define the source code
 * set(${PROJECT_NAME}_SRCS tft_life.cpp)
 * #set(${CMAKE_PROJECT_NAME}_SKETCH tft_life.cpp)
 * link_directories(${CMAKE_CURRENT_SOURCE_DIR}/..)
 *
 * #### Uncomment below additional settings as needed.
 * set(${CMAKE_PROJECT_NAME}_PROGRAMMER avrispmkii)
 * set(${CMAKE_PROJECT_NAME}_PORT /dev/cu.usbserial-00000000)
 * set(${CMAKE_PROJECT_NAME}_AFLAGS -v)
 * # set(pro.upload.speed 57600)
 *
 * generate_arduino_firmware(${CMAKE_PROJECT_NAME})
 *
 */
public class CMakeListsBuilder {


}
