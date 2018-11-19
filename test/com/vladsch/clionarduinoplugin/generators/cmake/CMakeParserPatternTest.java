package com.vladsch.clionarduinoplugin.generators.cmake;


import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class CMakeParserPatternTest {

    private void assertMatches(Pattern pattern, String arg, String result) {
        Matcher matcher = pattern.matcher(arg);
        assertTrue(matcher.find());
        assertEquals(result, matcher.group());
    }


    private void assertFails(Pattern pattern, String arg) {
        Matcher matcher = pattern.matcher(arg);
        assertFalse(matcher.find());
    }


    @Test
    public void test_bracketArgument() {
        assertMatches(CMakeParser.BRACKET_ARGUMENT, "[[]]", "[[]]");
        assertMatches(CMakeParser.BRACKET_ARGUMENT, "[[ ]]", "[[ ]]");
        assertMatches(CMakeParser.BRACKET_ARGUMENT, "[[ ; \\]]", "[[ ; \\]]");
        assertMatches(CMakeParser.BRACKET_ARGUMENT, "[=[ ; \\]=]", "[=[ ; \\]=]");
        assertMatches(CMakeParser.BRACKET_ARGUMENT, "[==[ ; \\]==]", "[==[ ; \\]==]");
        assertMatches(CMakeParser.BRACKET_ARGUMENT, "[[\n]]", "[[\n]]");
        assertMatches(CMakeParser.BRACKET_ARGUMENT, "[[\r]]", "[[\r]]");
        assertMatches(CMakeParser.BRACKET_ARGUMENT, "[[\r\n]]", "[[\r\n]]");

        assertMatches(CMakeParser.BRACKET_ARGUMENT, "[[]]]", "[[]]");
        assertMatches(CMakeParser.BRACKET_ARGUMENT, "[[ ]]]", "[[ ]]");
        assertMatches(CMakeParser.BRACKET_ARGUMENT, "[[ ; \\]]]", "[[ ; \\]]");
        assertMatches(CMakeParser.BRACKET_ARGUMENT, "[=[ ; \\]=]]", "[=[ ; \\]=]");
        assertMatches(CMakeParser.BRACKET_ARGUMENT, "[==[ ; \\]==]]", "[==[ ; \\]==]");

        assertFails(CMakeParser.BRACKET_ARGUMENT, " [[]]]");
        assertFails(CMakeParser.BRACKET_ARGUMENT, " [[ ]]]");
        assertFails(CMakeParser.BRACKET_ARGUMENT, " [[ ; \\]]]");
        assertFails(CMakeParser.BRACKET_ARGUMENT, " [=[ ; \\]=]]");
        assertFails(CMakeParser.BRACKET_ARGUMENT, " [==[ ; \\]==]]");

        assertFails(CMakeParser.BRACKET_ARGUMENT, "[]]");
        assertFails(CMakeParser.BRACKET_ARGUMENT, "[ ]]");
        assertFails(CMakeParser.BRACKET_ARGUMENT, "[ ; \\]]");
        assertFails(CMakeParser.BRACKET_ARGUMENT, "[[ ; \\]=]");
        assertFails(CMakeParser.BRACKET_ARGUMENT, "[=[ ; \\]==]");

        assertFails(CMakeParser.BRACKET_ARGUMENT, "[[]");
        assertFails(CMakeParser.BRACKET_ARGUMENT, "[[ ]");
        assertFails(CMakeParser.BRACKET_ARGUMENT, "[[ ; \\]");
        assertFails(CMakeParser.BRACKET_ARGUMENT, "[=[ ; \\]]");
        assertFails(CMakeParser.BRACKET_ARGUMENT, "[==[ ; \\]=]");
    }

    @Test
    public void test_quotedArgumentNoCont() {
        assertMatches(CMakeParser.QUOTED_ARGUMENT_NO_LINE_CONT, "\"abc\\\\\ndef\n \"", "\"abc\\\\\ndef\n \"");
        assertMatches(CMakeParser.QUOTED_ARGUMENT_NO_LINE_CONT, "\"abc\\\\\\\\\ndef\n \"", "\"abc\\\\\\\\\ndef\n \"");

        assertFails(CMakeParser.QUOTED_ARGUMENT_NO_LINE_CONT, "\"abc\\\ndef\n \"");
        assertFails(CMakeParser.QUOTED_ARGUMENT_NO_LINE_CONT, "\"abc\\\\\\\ndef\n \"");
    }

    @Test
    public void test_quotedArgumentLineCont() {
        assertMatches(CMakeParser.QUOTED_ARGUMENT_LINE_CONT, "\"abc\ndef\n \"", "\"abc\ndef\n \"");
        assertMatches(CMakeParser.QUOTED_ARGUMENT_LINE_CONT, "\"abc\\\ndef\n \"", "\"abc\\\ndef\n \"");
        assertMatches(CMakeParser.QUOTED_ARGUMENT_LINE_CONT, "\"abc\\\\\ndef\n \"", "\"abc\\\\\ndef\n \"");
        assertMatches(CMakeParser.QUOTED_ARGUMENT_LINE_CONT, "\"abc\\\\\\\ndef\n \"", "\"abc\\\\\\\ndef\n \"");
        assertMatches(CMakeParser.QUOTED_ARGUMENT_LINE_CONT, "\"abc\\\\\\\\\ndef\n \"", "\"abc\\\\\\\\\ndef\n \"");
    }

    @Test
    public void test_unquotedArgument() {
        assertMatches(CMakeParser.UNQUOTED_ARGUMENT, "/","/");
        assertMatches(CMakeParser.UNQUOTED_ARGUMENT, "/abc","/abc");
        assertMatches(CMakeParser.UNQUOTED_ARGUMENT, "/abc\\ def","/abc\\ def");
        assertMatches(CMakeParser.UNQUOTED_ARGUMENT, "abc:def","abc:def");
        assertMatches(CMakeParser.UNQUOTED_ARGUMENT, "/@+-*&!$%^&*=[]|?<>~`","/@+-*&!$%^&*=[]|?<>~`");
        assertMatches(CMakeParser.UNQUOTED_ARGUMENT, "abc;def","abc");

        assertMatches(CMakeParser.UNQUOTED_ARGUMENT, "/ ","/");
        assertMatches(CMakeParser.UNQUOTED_ARGUMENT, "/abc ","/abc");
        assertMatches(CMakeParser.UNQUOTED_ARGUMENT, "abc:def ","abc:def");
        assertMatches(CMakeParser.UNQUOTED_ARGUMENT, "/@+-*&!$%^&*=[]|?<>~` ","/@+-*&!$%^&*=[]|?<>~`");

        assertFails(CMakeParser.UNQUOTED_ARGUMENT, "(/ ");
        assertFails(CMakeParser.UNQUOTED_ARGUMENT, "(/abc ");
        assertFails(CMakeParser.UNQUOTED_ARGUMENT, "(abc:def ");
        assertFails(CMakeParser.UNQUOTED_ARGUMENT, "(/@+-*&!$%^&*=[]|?<>~` ");

        assertFails(CMakeParser.UNQUOTED_ARGUMENT, ")/ ");
        assertFails(CMakeParser.UNQUOTED_ARGUMENT, ")/abc ");
        assertFails(CMakeParser.UNQUOTED_ARGUMENT, ")abc:def ");
        assertFails(CMakeParser.UNQUOTED_ARGUMENT, ")/@+-*&!$%^&*=[]|?<>~` ");

        assertFails(CMakeParser.UNQUOTED_ARGUMENT, "\"/ ");
        assertFails(CMakeParser.UNQUOTED_ARGUMENT, "\"/abc ");
        assertFails(CMakeParser.UNQUOTED_ARGUMENT, "\"abc:def ");
        assertFails(CMakeParser.UNQUOTED_ARGUMENT, "\"/@+-*&!$%^&*=[]|?<>~` ");

    }

    @Test
    public void test_unquotedLegacy() {
        assertMatches(CMakeParser.UNQUOTED_LEGACY, "abc\" \"def","abc\" \"def");
        assertMatches(CMakeParser.UNQUOTED_LEGACY, "-Da=\"b c\"", "-Da=\"b c\"");
        assertMatches(CMakeParser.UNQUOTED_LEGACY, "-Da=$(v)", "-Da=$(v)");
        assertMatches(CMakeParser.UNQUOTED_LEGACY, "a\" \"b\"c\"d", "a\" \"b\"c\"d");
        assertMatches(CMakeParser.UNQUOTED_LEGACY, "abc;def","abc");
        assertMatches(CMakeParser.UNQUOTED_LEGACY, "abc\\;def","abc\\;def");
        assertMatches(CMakeParser.UNQUOTED_LEGACY, "abc\" def", "abc");
        assertMatches(CMakeParser.UNQUOTED_LEGACY, "-Da=\"b c", "-Da=");
        assertMatches(CMakeParser.UNQUOTED_LEGACY, "-Da=$(v)\"", "-Da=$(v)");
        assertMatches(CMakeParser.UNQUOTED_LEGACY, "a\" b\"c\"d", "a\" b\"c");

        assertFails(CMakeParser.UNQUOTED_LEGACY, " abc\" \"def");
        assertFails(CMakeParser.UNQUOTED_LEGACY, " -Da=\"b c\"");
        assertFails(CMakeParser.UNQUOTED_LEGACY, " -Da=$(v)");
        assertFails(CMakeParser.UNQUOTED_LEGACY, " a\" \"b\"c\"d");


    }
}
