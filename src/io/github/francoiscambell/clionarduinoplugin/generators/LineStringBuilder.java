package io.github.francoiscambell.clionarduinoplugin.generators;

import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

public class LineStringBuilder implements Appendable, CharSequence {
    private final StringBuilder sb;
    private String prefix;
    private boolean prefixed;
    private boolean prefixOneLine;

    public LineStringBuilder(StringBuilder stringBuilder) {
        sb = stringBuilder;
        prefix = "";
        prefixed = false;
        prefixOneLine = false;
    }

    public LineStringBuilder() {
        this("");
    }

    public LineStringBuilder(String prefix) {
        sb = new StringBuilder();
        this.prefix = prefix;
        prefixed = false;
        prefixOneLine = false;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    public boolean isPrefixed() {
        return prefixed;
    }

    public void setPrefixed(final boolean prefixed) {
        this.prefixed = prefixed;
    }

    public LineStringBuilder prefix() {
        prefixOneLine = true;
        return this;
    }

    public LineStringBuilder prefix(Object obj) {
        prefixOneLine = obj == null;
        return this;
    }

    public LineStringBuilder prefix(boolean prefixOneLine) {
        this.prefixOneLine = prefixOneLine;
        return this;
    }

    public LineStringBuilder prefixNullOrEmpty(String text) {
        this.prefixOneLine = text == null || text.isEmpty();
        return this;
    }

    public LineStringBuilder line() { sb.append("\n"); return this; }

    public boolean isNewLine() {
        return sb.length() != 0 || sb.charAt(sb.length()-1) == '\n';
    }

    public LineStringBuilder prefixNewLine() {
        if ((prefixed || prefixOneLine) && !prefix.isEmpty() && isNewLine()) {
            sb.append(prefix);
            prefixOneLine = false;
        }
        return this;
    }

    // @formatter:off
    public LineStringBuilder append(final Object obj) { prefixNewLine(); sb.append(obj); return this;}
    public LineStringBuilder append(final String str) { prefixNewLine(); sb.append(str); return this;}
    public LineStringBuilder append(final StringBuffer sb) { prefixNewLine(); this.sb.append(sb); return this;}
    public LineStringBuilder append(final char[] str) { prefixNewLine(); sb.append(str); return this;}
    public LineStringBuilder append(final char[] str,  final int offset, final int len) { prefixNewLine(); sb.append(str, offset, len); return this;}
    public LineStringBuilder append(final boolean b) { prefixNewLine(); sb.append(b); return this;}
    public LineStringBuilder append(final int i) { prefixNewLine(); sb.append(i); return this;}
    public LineStringBuilder append(final long lng) { prefixNewLine(); sb.append(lng); return this;}
    public LineStringBuilder append(final float f) { prefixNewLine(); sb.append(f); return this;}
    public LineStringBuilder append(final double d) { prefixNewLine(); sb.append(d); return this;}

    @Override public LineStringBuilder append(final CharSequence s) { prefixNewLine(); sb.append(s); return this;}
    @Override public LineStringBuilder append(final CharSequence s, final int start, final int end) { prefixNewLine(); sb.append(s, start, end); return this;}
    @Override public LineStringBuilder append(final char c) { prefixNewLine(); sb.append(c); return this;}

    public LineStringBuilder appendln(final Object obj) {  prefixNewLine(); sb.append(obj); line(); return this;}
    public LineStringBuilder appendln(final String str) {  prefixNewLine(); sb.append(str); line(); return this;}
    public LineStringBuilder appendln(final StringBuffer sb) {  prefixNewLine(); this.sb.append(sb); line(); return this;}
    public LineStringBuilder appendln(final CharSequence s) {  prefixNewLine(); sb.append(s); line(); return this;}
    public LineStringBuilder appendln(final CharSequence s,  final int start, final int end) {  prefixNewLine(); sb.append(s, start, end); line(); return this;}
    public LineStringBuilder appendln(final char[] str) {  prefixNewLine(); sb.append(str); line(); return this;}
    public LineStringBuilder appendln(final char[] str,  final int offset, final int len) {  prefixNewLine(); sb.append(str, offset, len); line(); return this;}
    public LineStringBuilder appendln(final boolean b) {  prefixNewLine(); sb.append(b); line(); return this;}
    public LineStringBuilder appendln(final char c) {  prefixNewLine(); sb.append(c); line(); return this;}
    public LineStringBuilder appendln(final int i) {  prefixNewLine(); sb.append(i); line(); return this;}
    public LineStringBuilder appendln(final long lng) {  prefixNewLine(); sb.append(lng); line(); return this;}
    public LineStringBuilder appendln(final float f) {  prefixNewLine(); sb.append(f); line(); return this;}
    public LineStringBuilder appendln(final double d) {  prefixNewLine(); sb.append(d); line(); return this;}

    public LineStringBuilder appendCodePoint(final int codePoint) {sb.appendCodePoint(codePoint); return this;}
    public LineStringBuilder delete(final int start, final int end) {sb.delete(start, end); return this;}
    public LineStringBuilder deleteCharAt(final int index) {sb.deleteCharAt(index); return this;}
    public LineStringBuilder replace(final int start, final int end, final String str) {sb.replace(start, end, str); return this;}
    public LineStringBuilder insert(final int index, final char[] str, final int offset, final int len) {sb.insert(index, str, offset, len); return this;}
    public LineStringBuilder insert(final int offset, final Object obj) {sb.insert(offset, obj); return this;}
    public LineStringBuilder insert(final int offset, final String str) {sb.insert(offset, str); return this;}
    public LineStringBuilder insert(final int offset, final char[] str) {sb.insert(offset, str); return this;}
    public LineStringBuilder insert(final int dstOffset, final CharSequence s) {sb.insert(dstOffset, s); return this;}
    public LineStringBuilder insert(final int dstOffset, final CharSequence s, final int start, final int end) {sb.insert(dstOffset, s, start, end); return this;}
    public LineStringBuilder insert(final int offset, final boolean b) {sb.insert(offset, b); return this;}
    public LineStringBuilder insert(final int offset, final char c) {sb.insert(offset, c); return this;}
    public LineStringBuilder insert(final int offset, final int i) {sb.insert(offset, i); return this;}
    public LineStringBuilder insert(final int offset, final long l) {sb.insert(offset, l); return this;}
    public LineStringBuilder insert(final int offset, final float f) {sb.insert(offset, f); return this;}
    public LineStringBuilder insert(final int offset, final double d) {sb.insert(offset, d); return this;}
    public LineStringBuilder reverse() {return new LineStringBuilder(sb.reverse());}
    // @formatter:on


    public int indexOf(final String str) {return sb.indexOf(str);}

    public int indexOf(final String str, final int fromIndex) {return sb.indexOf(str, fromIndex);}

    public int lastIndexOf(final String str) {return sb.lastIndexOf(str);}

    public int lastIndexOf(final String str, final int fromIndex) {return sb.lastIndexOf(str, fromIndex);}

    @NotNull
    @Override
    public String toString() {return sb.toString();}

    @Override
    public int length() {return sb.length();}

    public int capacity() {return sb.capacity();}

    public void ensureCapacity(final int minimumCapacity) {sb.ensureCapacity(minimumCapacity);}

    public void trimToSize() {sb.trimToSize();}

    public void setLength(final int newLength) {sb.setLength(newLength);}

    @Override
    public char charAt(final int index) {return sb.charAt(index);}

    public int codePointAt(final int index) {return sb.codePointAt(index);}

    public int codePointBefore(final int index) {return sb.codePointBefore(index);}

    public int codePointCount(final int beginIndex, final int endIndex) {return sb.codePointCount(beginIndex, endIndex);}

    public int offsetByCodePoints(final int index, final int codePointOffset) {return sb.offsetByCodePoints(index, codePointOffset);}

    public void getChars(final int srcBegin, final int srcEnd, final char[] dst, final int dstBegin) {sb.getChars(srcBegin, srcEnd, dst, dstBegin);}

    public void setCharAt(final int index, final char ch) {sb.setCharAt(index, ch);}

    public String substring(final int start) {return sb.substring(start);}

    @Override
    public CharSequence subSequence(final int start, final int end) {return sb.subSequence(start, end);}

    public String substring(final int start, final int end) {return sb.substring(start, end);}

    @Override
    public IntStream chars() {return sb.chars();}

    @Override
    public IntStream codePoints() {return sb.codePoints();}
}
