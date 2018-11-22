package com.vladsch.clionarduinoplugin.generators.cmake;

import com.intellij.openapi.util.Comparing;
import com.vladsch.clionarduinoplugin.generators.cmake.ast.CMakeFile;
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommand;
import com.vladsch.flexmark.IRender;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.spec.IRenderBase;
import com.vladsch.flexmark.util.collection.DynamicDefaultKey;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.DataKey;
import com.vladsch.flexmark.util.options.MutableDataSet;

import java.io.IOException;
import java.util.*;

class ArduinoCMakeListsTxtBuilderRenderer extends IRenderBase {
    public ArduinoCMakeListsTxtBuilderRenderer() {
        this(null);
    }

    public ArduinoCMakeListsTxtBuilderRenderer(DataHolder options) {
        super(options);
    }

    final static public DataKey<Map<String, String>> VALUE_SET = new DynamicDefaultKey<>("VALUE_SET", (options) -> new HashMap<>());

    @Override
    public void render(Node node, Appendable output) {
        assert node instanceof CMakeFile;
        CMakeFile cMakeFile = (CMakeFile) node;
        Map<String, String> values = getOptions().get(VALUE_SET);
        Map<String, Object> valueSet = new HashMap<>(getOptions().get(VALUE_SET));
        CMakeListsTxtBuilder builder = new ArduinoCMakeListsTxtBuilder(cMakeFile, valueSet);

        try {
            LinkedHashMap<String, ArrayList<String>> commandArgs = new LinkedHashMap<>();

            // prepare all argument lists for the commands
            for (Map.Entry<String, String> entry : values.entrySet()) {
                String name = entry.getKey();
                int index = 0;
                int pos = name.indexOf('[');
                if (pos != -1) {
                    try {
                        index = Integer.parseUnsignedInt(name.substring(pos + 1, name.length() - 1));
                    } catch (NumberFormatException ignored) {
                    }
                    name = name.substring(0, pos);
                }

                ArrayList<String> args = commandArgs.computeIfAbsent(name, (n) -> new ArrayList<>());
                while (index >= args.size()) args.add(null);
                args.set(index, entry.getValue());
            }

            for (Map.Entry<String, ArrayList<String>> entry : commandArgs.entrySet()) {
                String name = entry.getKey();
                ArrayList<String> args = entry.getValue();

                CMakeCommand command = builder.addCommand(name, args);
                //noinspection VariableNotUsedInsideIf
                if (command != null) {
                    valueSet.remove(entry.getKey());
                }
            }

            String contents = builder.getCMakeContents(valueSet);
            output.append(contents);

            if (ExtraRenderer.DUMP_OPTIONS.getFrom(cMakeFile)) {
                // dump the options
                Map<DataKey, Object> all = ((CMakeFile) node).getAll();
                ArrayList<DataKey> keys = new ArrayList<>(all.keySet());
                keys.sort((o1, o2) -> Comparing.compare(o1.getName(), o2.getName()));

                for (DataKey key : keys) {
                    output.append("# ").append(key.getName()).append("->").append(String.valueOf(all.get(key))).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IRender withOptions(DataHolder options) {
        final MutableDataSet mutableDataSet = new MutableDataSet(getOptions());
        if (options != null) mutableDataSet.setAll(options);
        return new ArduinoCMakeListsTxtBuilderRenderer(mutableDataSet);
    }
}
