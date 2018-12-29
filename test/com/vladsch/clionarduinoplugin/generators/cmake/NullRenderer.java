package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.flexmark.util.IRender;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.spec.IRenderBase;
import com.vladsch.flexmark.util.options.DataHolder;

class NullRenderer extends IRenderBase {
    public NullRenderer() {
        this(null);
    }

    public NullRenderer(DataHolder options) {
        super(options);
    }

    @Override
    public void render(Node node, Appendable output) {

    }

    @Override
    public IRender withOptions(DataHolder options) {
        return new NullRenderer(options);
    }
}
