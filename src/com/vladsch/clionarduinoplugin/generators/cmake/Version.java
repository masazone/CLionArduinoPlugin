package com.vladsch.clionarduinoplugin.generators.cmake;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.Integer.parseUnsignedInt;

public class Version implements Comparable<Version> {
    private final Integer major;
    private final Integer minor;
    private final Integer patch;
    private final Integer tweak;

    public Version(CharSequence version) {
        String text = String.valueOf(version);
        String[] parts = text.split("\\.");
        major = parseInt(parts[0]);
        minor = parts.length > 1 ? parseInt(parts[1]) : null;
        patch = parts.length > 2 ? parseInt(parts[2]) : null;
        tweak = parts.length > 3 ? parseInt(parts[3]) : null;
    }

    @Override
    public int compareTo(@NotNull final Version o) {
        int val = compareNullable(major, o.major);
        if (val == 0) {
            val = compareNullable(minor, o.minor);
            if (val == 0) {
                val = compareNullable(patch, o.patch);
                if (val == 0) {
                    val = compareNullable(tweak, o.tweak);
                }
            }
        }
        return val;
    }

    public int compareTo(@NotNull final String text) {
        Version o = new Version(text);
        return compareTo(o);
    }

    static public int compareNullable(Integer i1, Integer i2) {
        if (i1 == null || i2 == null) return 0;
        else return Integer.compare(i1, i2);
    }

    @Nullable
    public static Integer parseInt(String text) {
        try {
            return parseUnsignedInt(text);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
