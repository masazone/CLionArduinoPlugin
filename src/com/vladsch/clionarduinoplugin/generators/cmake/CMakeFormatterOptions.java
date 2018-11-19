package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataHolder;
import com.vladsch.flexmark.util.options.MutableDataSetter;

import java.util.Set;

public class CMakeFormatterOptions implements MutableDataSetter {
    public int indentSpaces;
    public String argumentListPrefix;
    public String argumentListSuffix;
    public String argumentSeparator;
    public String argumentParensSeparator;
    public int argumentListMaxLine;
    public final int formatFlags;
    public final int maxBlankLines;
    public final int maxTrailingBlankLines;
    public final Set<String> spaceAfterCommandName;
    public boolean collapseWhitespace;
    public boolean preserveWhitespace;
    public boolean preserveArgumentSeparator;
    public boolean preserveLineBreaks;

    CMakeFormatterOptions() {
        this((DataHolder) null);
    }

    CMakeFormatterOptions(CMakeFormatterOptions other) {
        indentSpaces = other.indentSpaces;
        argumentListPrefix = other.argumentListPrefix;
        argumentListSuffix = other.argumentListSuffix;
        argumentSeparator = other.argumentSeparator;
        argumentParensSeparator = other.argumentParensSeparator;
        argumentListMaxLine = other.argumentListMaxLine;
        formatFlags = other.formatFlags;
        maxBlankLines = other.maxBlankLines;
        maxTrailingBlankLines = other.maxTrailingBlankLines;
        spaceAfterCommandName = other.spaceAfterCommandName;
        collapseWhitespace = other.collapseWhitespace;
        preserveWhitespace = other.preserveWhitespace;
        preserveArgumentSeparator = other.preserveArgumentSeparator;
        preserveLineBreaks = other.preserveLineBreaks;
    }

    CMakeFormatterOptions(DataHolder options) {
        indentSpaces = CMakeFormatter.INDENT_SPACES.getFrom(options);
        argumentListPrefix = CMakeFormatter.ARGUMENT_LIST_PREFIX.getFrom(options);
        argumentListSuffix = CMakeFormatter.ARGUMENT_LIST_SUFFIX.getFrom(options);
        argumentSeparator = CMakeFormatter.ARGUMENT_SEPARATOR.getFrom(options);
        argumentParensSeparator = CMakeFormatter.ARGUMENT_PARENS_SEPARATOR.getFrom(options);
        argumentListMaxLine = CMakeFormatter.ARGUMENT_LIST_MAX_LINE.getFrom(options);
        formatFlags = CMakeFormatter.FORMAT_FLAGS.getFrom(options);
        maxBlankLines = CMakeFormatter.MAX_BLANK_LINES.getFrom(options);
        maxTrailingBlankLines = CMakeFormatter.MAX_TRAILING_BLANK_LINES.getFrom(options);
        spaceAfterCommandName = CMakeFormatter.SPACE_AFTER_COMMAND_NAME.getFrom(options);
        collapseWhitespace = CMakeFormatter.COLLAPSE_WHITESPACE.getFrom(options);
        preserveWhitespace = CMakeFormatter.PRESERVE_WHITESPACE.getFrom(options);
        preserveArgumentSeparator = CMakeFormatter.PRESERVE_ARGUMENT_SEPARATOR.getFrom(options);
        preserveLineBreaks = CMakeFormatter.PRESERVE_LINE_BREAKS.getFrom(options);
    }

    @Override
    public MutableDataHolder setIn(final MutableDataHolder dataHolder) {
        dataHolder.set(CMakeFormatter.INDENT_SPACES, indentSpaces);
        dataHolder.set(CMakeFormatter.ARGUMENT_LIST_PREFIX, argumentListPrefix);
        dataHolder.set(CMakeFormatter.ARGUMENT_LIST_SUFFIX, argumentListSuffix);
        dataHolder.set(CMakeFormatter.ARGUMENT_SEPARATOR, argumentSeparator);
        dataHolder.set(CMakeFormatter.ARGUMENT_PARENS_SEPARATOR, argumentParensSeparator);
        dataHolder.set(CMakeFormatter.ARGUMENT_LIST_MAX_LINE, argumentListMaxLine);
        dataHolder.set(CMakeFormatter.FORMAT_FLAGS, formatFlags);
        dataHolder.set(CMakeFormatter.MAX_BLANK_LINES, maxBlankLines);
        dataHolder.set(CMakeFormatter.MAX_TRAILING_BLANK_LINES, maxTrailingBlankLines);
        dataHolder.set(CMakeFormatter.SPACE_AFTER_COMMAND_NAME, spaceAfterCommandName);
        dataHolder.set(CMakeFormatter.COLLAPSE_WHITESPACE, collapseWhitespace);
        dataHolder.set(CMakeFormatter.PRESERVE_WHITESPACE, preserveWhitespace);
        dataHolder.set(CMakeFormatter.PRESERVE_ARGUMENT_SEPARATOR, preserveArgumentSeparator);
        dataHolder.set(CMakeFormatter.PRESERVE_LINE_BREAKS, preserveLineBreaks);
        return dataHolder;
    }
}
