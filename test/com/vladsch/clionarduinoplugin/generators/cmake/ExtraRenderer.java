package com.vladsch.clionarduinoplugin.generators.cmake;

import com.intellij.openapi.util.Comparing;
import com.vladsch.clionarduinoplugin.generators.cmake.ast.CMakeFile;
import com.vladsch.flexmark.util.IRender;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.spec.IRenderBase;
import com.vladsch.flexmark.util.Pair;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.DataKey;
import com.vladsch.flexmark.util.options.MutableDataSet;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ExtraRenderer extends IRenderBase {
    public ExtraRenderer() {
        this(null);
    }

    final static public DataKey<Boolean> DUMP_OPTIONS = new DataKey<>("DUMP_OPTIONS", false);

    public ExtraRenderer(DataHolder options) {
        super(options);
    }

    @Override
    public void render(Node node, Appendable output) {
        assert node instanceof CMakeFile;

        List<Pair<String, BasedSequence>> errors = ((CMakeFile) node).getErrors();
        try {
            for (Pair<String, BasedSequence> pair : errors) {
                String message = pair.getFirst();
                BasedSequence location = pair.getSecond();
                Pair<Integer, Integer> lineColumnAtIndex = node.getChars().getLineColumnAtIndex(location.getStartOffset());
                int line = lineColumnAtIndex.getFirst();
                int col = lineColumnAtIndex.getSecond();

                output.append(message).append(": ")
                        .append(Integer.toString(line)).append(":").append(Integer.toString(col)).append(": ")
                        .append(location);

                if (location.length() > 0 && location.charAt(location.length()-1) != '\n') output.append('\n');
            }

            if (DUMP_OPTIONS.getFrom(getOptions())) {
                // dump the options
                Map<DataKey, Object> all = ((CMakeFile) node).getAll();
                ArrayList<DataKey> keys = new ArrayList<>(all.keySet());
                keys.sort((o1,o2)->Comparing.compare(o1.getName(),o2.getName()));

                for (DataKey key:keys) {
                    output.append(key.getName()).append("->").append(String.valueOf(all.get(key))).append("\n");
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
        return new ExtraRenderer(mutableDataSet);
    }
}
