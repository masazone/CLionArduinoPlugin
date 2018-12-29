package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.flexmark.util.IParse;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.spec.IParseBase;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;
import com.vladsch.flexmark.util.sequence.BasedSequence;

class CMakeIParser extends IParseBase {
    public CMakeIParser() {
        this(null);
    }

    public CMakeIParser(DataHolder options) {
        super(options);
    }

    @Override
    public Node parse(BasedSequence input) {
        // here we make the lexer parse the input sequence from start to finish and accumulate everything in custom nodes
        CMakeParser parser = new CMakeParser(input, getOptions());
        return parser.getDocument();
    }

    @Override
    public IParse withOptions(DataHolder options) {
        final MutableDataSet mutableDataSet = new MutableDataSet(getOptions());
        if (options != null) mutableDataSet.setAll(options);
        return new CMakeIParser(mutableDataSet);
    }
}
