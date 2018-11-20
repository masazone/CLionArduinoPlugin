package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.flexmark.IParse;
import com.vladsch.flexmark.IRender;
import com.vladsch.flexmark.spec.IParseBase;
import com.vladsch.flexmark.spec.SpecExample;
import com.vladsch.flexmark.spec.SpecReader;
import com.vladsch.flexmark.test.ComboSpecTestCase;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CMakeBuilderSpecTest extends ComboSpecTestCase {
    private static final String SPEC_RESOURCE = "/com/vladsch/clionarduinoplugin/generators/cmake/cmake_builder_spec.md";
    private static final DataHolder OPTIONS = new MutableDataSet()
            .set(CMakeParser.AUTO_CONFIG, true)
            .set(CMakeParser.AST_LINE_END_EOL, true)
            .set(CMakeParser.AST_COMMENTS, true)
            .set(CMakeParser.AST_BLANK_LINES, true)
            .set(CMakeParser.AST_ARGUMENT_SEPARATORS, true)
            //.set(ExtraRenderer.DUMP_OPTIONS, true)
    ;

    protected static final Map<String, DataHolder> optionsMap = new HashMap<String, DataHolder>();

    static Map<String, String> valueSet(String... values) {
        assert (values.length & 1) == 0;
        int i = values.length;
        HashMap<String, String> valueSet = new HashMap<>();

        while (i > 1) {
            String value = values[--i];
            String key = (String) values[--i];
            valueSet.put(key, value);
        }
        return valueSet;
    }

    static {
        optionsMap.put("board-pro", new MutableDataSet().set(BuilderRenderer.VALUE_SET, valueSet("SET_BOARD", "pro", "SET_CPU", "8MHzatmega328")));
        optionsMap.put("change-all", new MutableDataSet().set(BuilderRenderer.VALUE_SET, valueSet(
                "SET_CMAKE_TOOLCHAIN_FILE", "setCmakeToolchainFile",
                "SET_PROJECT_NAME", "setProjectName",
                "SET_BOARD", "setBoard",
                "SET_CPU", "setCpu",
                "SET_SKETCH", "setSketch",
                "SET_PROGRAMMER", "setProgrammer",
                "SET_PORT", "setPort",
                "SET_AFLAGS", "setAflags",
                "SET_HDRS", "setHdrs",
                "SET_SRCS", "setSrcs",
                "SET_UPLOAD_SPEED", "setUploadSpeed",
                "SET_LIB_NAME_RECURSE", "setLibNameRecurse",
                "LIB_NAME", "libName",
                "SET_UPLOAD_SPEED", "setUploadSpeed",
                "LINK_DIRECTORIES", "linkDirectories"
        )));
        optionsMap.put("dump-options", new MutableDataSet().set(ExtraRenderer.DUMP_OPTIONS, true));
    }

    private static final IParseBase PARSER = new CMakeIParser(OPTIONS);
    // The spec says URL-escaping is optional, but the examples assume that it's enabled.
    private static final IRender RENDERER = new BuilderRenderer(OPTIONS);

    private static DataHolder optionsSet(String optionSet) {
        return optionsMap.get(optionSet);
    }

    public CMakeBuilderSpecTest(SpecExample example) {
        super(example);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        List<SpecExample> examples = SpecReader.readExamples(SPEC_RESOURCE);
        List<Object[]> data = new ArrayList<Object[]>();

        // NULL example runs full spec test
        data.add(new Object[] { SpecExample.NULL });

        for (SpecExample example : examples) {
            data.add(new Object[] { example });
        }
        return data;
    }

    @Override
    public DataHolder options(String optionSet) {
        return optionsSet(optionSet);
    }

    @Override
    public String getSpecResourceName() {
        return SPEC_RESOURCE;
    }

    @Override
    public IParse parser() {
        return PARSER;
    }

    @Override
    public IRender renderer() {
        return RENDERER;
    }
}
