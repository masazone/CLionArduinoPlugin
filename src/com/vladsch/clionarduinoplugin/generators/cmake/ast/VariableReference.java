package com.vladsch.clionarduinoplugin.generators.cmake.ast;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class VariableReference extends Node {
    private BasedSequence openingMarker = BasedSequence.NULL;
    private BasedSequence text = BasedSequence.NULL;
    private BasedSequence closingMarker = BasedSequence.NULL;

    public VariableReference(final BasedSequence chars) {
        super(chars);
    }

    @Override
    public BasedSequence[] getSegments() {
        return new BasedSequence[] { openingMarker, text, closingMarker };
    }

    @Override
    public void getAstExtra(StringBuilder out) {
        segmentSpanChars(out, openingMarker, "open");
        if (text.isNotNull()) segmentSpanChars(out, text, "text");
        segmentSpanChars(out, closingMarker, "close");
    }
}
