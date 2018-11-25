package com.vladsch.clionarduinoplugin.generators.cmake;

import org.junit.runners.Suite;

@org.junit.runner.RunWith(Suite.class)
@Suite.SuiteClasses({
        CMakeParserPatternTest.class,
        CMakeParserSpecTest.class,
        CMakeFormatterSpecTest.class,
        CMakeBuilderSpecTest.class,
        ArduinoCMakeBuilderTest.class,
})
public class CMakeTestSuite {
}
