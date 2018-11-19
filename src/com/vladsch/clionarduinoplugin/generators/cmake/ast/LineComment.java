package com.vladsch.clionarduinoplugin.generators.cmake.ast;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class LineComment extends Node {
    private BasedSequence openingMarker = BasedSequence.NULL;
    private BasedSequence text = BasedSequence.NULL;

    public LineComment(final BasedSequence chars) {
        super(chars);
        this.openingMarker = chars.subSequence(0, 1);
        this.text = chars.subSequence(1);
    }

    public LineComment(final BasedSequence chars, final BasedSequence openingMarker, final BasedSequence text) {
        super(chars);
        this.openingMarker = openingMarker;
        this.text = text;
    }

    @Override
    public BasedSequence[] getSegments() {
        return new BasedSequence[] { openingMarker, text };
    }

    @Override
    public void getAstExtra(StringBuilder out) {
        segmentSpanChars(out, openingMarker, "open");
        segmentSpanChars(out, text, "text");
    }

    public BasedSequence getOpeningMarker() {
        return openingMarker;
    }

    public void setOpeningMarker(final BasedSequence openingMarker) {
        this.openingMarker = openingMarker;
    }

    public BasedSequence getText() {
        return text;
    }

    public void setText(final BasedSequence text) {
        this.text = text;
    }
}
