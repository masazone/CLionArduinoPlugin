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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class BuilderRenderer extends IRenderBase {
    public BuilderRenderer() {
        this(null);
    }

    public BuilderRenderer(DataHolder options) {
        super(options);
    }

    final static public DataKey<Map<String, String>> VALUE_SET = new DynamicDefaultKey<>("VALUE_SET", (options) -> new HashMap<>());

    @Override
    public void render(Node node, Appendable output) {
        assert node instanceof CMakeFile;
        CMakeFile cMakeFile = (CMakeFile) node;
        Map<String, String> values = getOptions().get(VALUE_SET);
        Map<String, Object> valueSet = new HashMap<>(getOptions().get(VALUE_SET));
        CMakeListsBuilder builder = new CMakeListsBuilder(cMakeFile, valueSet);

        try {
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

                CMakeCommand command = builder.getSetCommand(name);
                if (command != null) {
                    if (command.getCommandType().isNoDupeArgs()) {
                        command.addArg(entry.getValue());
                    } else {
                        command.setArg(index, entry.getValue());
                        valueSet.remove(entry.getKey());
                    }
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
        return new BuilderRenderer(mutableDataSet);
    }
}
