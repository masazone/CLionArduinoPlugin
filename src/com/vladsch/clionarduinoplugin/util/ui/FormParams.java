package com.vladsch.clionarduinoplugin.util.ui;

public class FormParams<T> {
    protected final T mySettings;

    public FormParams(final T settings) {
        this.mySettings = settings;
    }

    public T getSettings() {
        return mySettings;
    }
}
