package com.vladsch.clionarduinoplugin.generators.cmake.ast;

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class UnrecognizedInput extends Node {
    public UnrecognizedInput(final BasedSequence chars) {
        super(chars);
    }

    @Override
    public BasedSequence[] getSegments() {
        return EMPTY_SEGMENTS;
    }
}
