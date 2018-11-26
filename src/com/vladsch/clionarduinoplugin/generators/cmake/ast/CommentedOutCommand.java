package com.vladsch.clionarduinoplugin.generators.cmake.ast;

import com.vladsch.flexmark.util.sequence.BasedSequence;

public class CommentedOutCommand extends Command {
    private BasedSequence commentMarker = BasedSequence.NULL;

    public CommentedOutCommand() {
    }

    public CommentedOutCommand(final BasedSequence chars) {
        super(chars);
    }

    @Override
    public BasedSequence[] getSegments() {
        return new BasedSequence[] { leadingSpaces, commentMarker, command, openingMarker, arguments, closingMarker };
    }

    @Override
    public void getAstExtra(StringBuilder out) {
        segmentSpanChars(out, leadingSpaces, "spaces");
        segmentSpanChars(out, commentMarker, "comment");
        segmentSpanChars(out, command, "text");
        segmentSpanChars(out, openingMarker, "open");
        if (arguments.isNotNull()) segmentSpanChars(out, arguments, "arguments");
        segmentSpanChars(out, closingMarker, "close");
    }

    public BasedSequence getCommentMarker() {
        return commentMarker;
    }

    public void setCommentMarker(final BasedSequence commentMarker) {
        this.commentMarker = commentMarker;
    }
}
