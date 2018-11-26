package com.vladsch.clionarduinoplugin.generators.cmake.ast;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class LineComment extends Node {
    private BasedSequence leadingSpaces = BasedSequence.NULL;
    private BasedSequence openingMarker = BasedSequence.NULL;
    private BasedSequence text = BasedSequence.NULL;

    public LineComment(final BasedSequence chars) {
        super(chars);
        BasedSequence leading = chars.trimmedStart();
        BasedSequence trimmed = chars.trimStart();
        
        if (!leading.isEmpty()) leadingSpaces = leading;

        openingMarker = chars.subSequence(0, 1);
        text = chars.subSequence(1);
    }

    public LineComment(final BasedSequence chars, final BasedSequence openingMarker, final BasedSequence text) {
        super(chars);
        this.openingMarker = openingMarker;
        this.text = text;
    }

    @Override
    public BasedSequence[] getSegments() {
        return new BasedSequence[] { leadingSpaces, openingMarker, text };
    }

    @Override
    public void getAstExtra(StringBuilder out) {
        segmentSpanChars(out, leadingSpaces, "spaces");
        segmentSpanChars(out, openingMarker, "open");
        segmentSpanChars(out, text, "text");
    }

    public BasedSequence getLeadingSpaces() {
        return leadingSpaces;
    }

    public void setLeadingSpaces(final BasedSequence leadingSpaces) {
        this.leadingSpaces = leadingSpaces.isEmpty() ? BasedSequence.NULL : leadingSpaces;
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
