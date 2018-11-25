package com.vladsch.clionarduinoplugin.generators;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum CppLanguageVersions {
    CPP98("98"),
    CPP11("11"),
    CPP14("14"),
    CPP17("17");

    @NotNull
    private final String version;

    private CppLanguageVersions(@NotNull String version) {
        this.version = version;
    }

    @NotNull
    public String getVersion() {
        return this.version;
    }

    @NotNull
    public String getDisplayString() {
        return "C++ " + this.version;
    }

    @NotNull
    public static String fromDisplayString(@NotNull String displayString) {
        return StringUtil.trimStart(displayString, "C++");
    }

    @Nullable
    public static CppLanguageVersions valueOrNull(@NotNull String value) {
        for (CppLanguageVersions version : values()) {
            if (version.version.equals(value)) {
                return version;
            }
        }
        return null;
    }
}
