package com.vladsch.clionarduinoplugin;

import com.vladsch.clionarduinoplugin.generators.cmake.CMakeTestSuite;
import com.vladsch.clionarduinoplugin.resources.TemplateResolverTest;
import org.junit.runners.Suite;

@org.junit.runner.RunWith(Suite.class)
@Suite.SuiteClasses({
        CMakeTestSuite.class,
        TemplateResolverTest.class,
})
public class PluginTestSuite {
}
