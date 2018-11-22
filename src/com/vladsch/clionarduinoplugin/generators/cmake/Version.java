package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.clionarduinoplugin.util.Utils;
import org.jetbrains.annotations.NotNull;

import static java.lang.Integer.parseUnsignedInt;

public class Version implements Comparable<Version> {
    private final Integer major;
    private final Integer minor;
    private final Integer patch;
    private final Integer tweak;

    public Version(CharSequence version) {
        String text = String.valueOf(version);
        String[] parts = text.split("\\.");
        major = Utils.parseIntOrNull(parts[0]);
        minor = parts.length > 1 ? Utils.parseIntOrNull(parts[1]) : null;
        patch = parts.length > 2 ? Utils.parseIntOrNull(parts[2]) : null;
        tweak = parts.length > 3 ? Utils.parseIntOrNull(parts[3]) : null;
    }

    @Override
    public int compareTo(@NotNull final Version o) {
        int val = Utils.compareNullable(major, o.major);
        if (val == 0) {
            val = Utils.compareNullable(minor, o.minor);
            if (val == 0) {
                val = Utils.compareNullable(patch, o.patch);
                if (val == 0) {
                    val = Utils.compareNullable(tweak, o.tweak);
                }
            }
        }
        return val;
    }

    public int compareTo(@NotNull final String text) {
        Version o = new Version(text);
        return compareTo(o);
    }
}
