package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.clionarduinoplugin.generators.cmake.ast.CMakeFile;
import com.vladsch.flexmark.IRender;
import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.formatter.RenderPurpose;
import com.vladsch.flexmark.formatter.TranslatingSpanRender;
import com.vladsch.flexmark.formatter.TranslationPlaceholderGenerator;
import com.vladsch.flexmark.formatter.internal.*;
import com.vladsch.flexmark.formatter.internal.Formatter;
import com.vladsch.flexmark.util.collection.DynamicDefaultKey;
import com.vladsch.flexmark.util.collection.NodeCollectingVisitor;
import com.vladsch.flexmark.util.collection.SubClassingBag;
import com.vladsch.flexmark.util.options.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.vladsch.flexmark.formatter.internal.Formatter.NULL_ITERABLE;

public class CMakeFormatter implements IRender {

    // syntax options
    final static public DataKey<Set<String>> SPACE_AFTER_COMMAND_NAME = new DynamicDefaultKey<Set<String>>("SPACE_AFTER_COMMAND_NAME", (dataHolder)->{
        return new HashSet<>(Arrays.asList(
                "if",
                "elseif",
                //"else",
                //"endif",
                "foreach",
                //"endforeach",
                "while"
                //"endwhile"
        ));
    });

    final static public DataKey<Integer> INDENT_SPACES = new DataKey<>("INDENT_SPACES", 4);
    final static public DataKey<String> ARGUMENT_LIST_PREFIX = new DataKey<>("ARGUMENT_LIST_PREFIX", " ");
    final static public DataKey<String> ARGUMENT_LIST_SUFFIX = new DataKey<>("ARGUMENT_LIST_SUFFIX", " ");
    final static public DataKey<String> ARGUMENT_SEPARATOR = new DataKey<>("ARGUMENT_SEPARATOR", " ");
    final static public DataKey<String> ARGUMENT_PARENS_SEPARATOR = new DataKey<>("ARGUMENT_PARENS_SEPARATOR", " ");
    final static public DataKey<Integer> ARGUMENT_LIST_MAX_LINE = new DataKey<>("ARGUMENT_LIST_MAX_LINE", 80);
    final static public DataKey<Boolean> COLLAPSE_COMMENT_WHITESPACE = new DataKey<>("COLLAPSE_COMMENT_WHITESPACE", false);
    final static public DataKey<Boolean> PRESERVE_COMMENT_WHITESPACE = new DataKey<>("PRESERVE_COMMENT_WHITESPACE", true);

    // convenience copies
    final static public DataKey<Integer> FORMAT_FLAGS = Formatter.FORMAT_FLAGS;
    final static public DataKey<Integer> MAX_BLANK_LINES = Formatter.MAX_BLANK_LINES;
    final static public DataKey<Integer> MAX_TRAILING_BLANK_LINES = Formatter.MAX_TRAILING_BLANK_LINES;

    final private DataHolder options;
    final private CMakeFormatterOptions formatterOptions;

    private CMakeFormatter(final DataHolder options) {
        this.options = options == null ? new DataSet():new DataSet(options);
        this.formatterOptions = new CMakeFormatterOptions(options);
    }

    @Override
    public IRender withOptions(final DataHolder options) {
        final MutableDataSet mutableDataSet = new MutableDataSet(this.options);
        if (options != null) mutableDataSet.setAll(options);
        return new CMakeFormatter(mutableDataSet);
    }

    public static CMakeFormatter build(@Nullable DataHolder options) {
        return new CMakeFormatter(options);
    }

    public String render(@NotNull final Node node) {
        StringBuilder sb = new StringBuilder();
        render(node,sb);
        return sb.toString();
    }

    @Override
    public void render(final Node node, final Appendable output) {
        CMakeFormatterContext renderer = new CMakeFormatterContext(options, new MarkdownWriter(output, formatterOptions.formatFlags), (CMakeFile) node.getDocument());
        renderer.render(node);
        renderer.flush(formatterOptions.maxTrailingBlankLines);
    }

    static class CMakeFormatterContext extends NodeFormatterSubContext {
        // inner stuff
        private final Map<Class<?>, NodeFormattingHandler> renderers;
        final private CMakeParserOptions formatterOptions;
        private final DataHolder options;
        private final CMakeFile document;
        private final SubClassingBag<Node> collectedNodes;
        private final Set<FormattingPhase> renderingPhases;
        private final List<PhasedNodeFormatter> phasedFormatters;
        private FormattingPhase phase;

        CMakeFormatterContext(@Nullable DataHolder options, @NotNull MarkdownWriter out, final CMakeFile document) {
            super(out);
            this.options = options == null ? new MutableDataSet() : new MutableDataSet(options);
            this.document = document;
            this.formatterOptions = new CMakeParserOptions(options);
            this.renderers = new HashMap<Class<?>, NodeFormattingHandler>(32);
            NodeFormatter nodeFormatter = new CMakeNodeFormatter(this.options);
            final Set<Class> collectNodeTypes = new HashSet<Class>(20);
            this.renderingPhases = new HashSet<FormattingPhase>(FormattingPhase.values().length);
            this.phasedFormatters = new ArrayList<PhasedNodeFormatter>(1);

            out.setContext(this);

            final Set<NodeFormattingHandler<?>> formattingHandlers = nodeFormatter.getNodeFormattingHandlers();
            for (NodeFormattingHandler nodeType : formattingHandlers) {
                // Overwrite existing renderer
                renderers.put(nodeType.getNodeType(), nodeType);
            }

            // get nodes of interest
            Set<Class<?>> nodeClasses = nodeFormatter.getNodeClasses();
            if (nodeClasses != null) {
                collectNodeTypes.addAll(nodeClasses);
            }

            if (nodeFormatter instanceof PhasedNodeFormatter) {
                Set<FormattingPhase> phases = ((PhasedNodeFormatter) nodeFormatter).getFormattingPhases();
                if (phases != null) {
                    if (phases.isEmpty()) throw new IllegalStateException("PhasedNodeFormatter with empty Phases");
                    this.renderingPhases.addAll(phases);
                    this.phasedFormatters.add((PhasedNodeFormatter) nodeFormatter);
                } else {
                    throw new IllegalStateException("PhasedNodeFormatter with null Phases");
                }
            }

            // collect nodes of interest from document
            if (!collectNodeTypes.isEmpty()) {
                NodeCollectingVisitor collectingVisitor = new NodeCollectingVisitor(collectNodeTypes);
                collectingVisitor.collect(document);
                collectedNodes = collectingVisitor.getSubClassingBag();
            } else {
                collectedNodes = null;
            }
        }

        @Override
        public final Iterable<? extends Node> nodesOfType(final Class<?>[] classes) {
            return collectedNodes == null ? NULL_ITERABLE : collectedNodes.itemsOfType(Node.class, classes);
        }

        @Override
        public final Iterable<? extends Node> nodesOfType(final Collection<Class<?>> classes) {
            //noinspection unchecked
            return collectedNodes == null ? NULL_ITERABLE : collectedNodes.itemsOfType(Node.class, classes);
        }

        @Override
        public final Iterable<? extends Node> reversedNodesOfType(final Class<?>[] classes) {
            return collectedNodes == null ? NULL_ITERABLE : collectedNodes.reversedItemsOfType(Node.class, classes);
        }

        @Override
        public final Iterable<? extends Node> reversedNodesOfType(final Collection<Class<?>> classes) {
            //noinspection unchecked
            return collectedNodes == null ? NULL_ITERABLE : collectedNodes.reversedItemsOfType(Node.class, classes);
        }

        @Override
        public NodeFormatterContext getSubContext(Appendable out) {
            //MarkdownWriter writer = new MarkdownWriter(out, getMarkdown().getOptions());
            //writer.setContext(this);
            ////noinspection ReturnOfInnerClass
            //return new SubNodeFormatter(this, writer);
            return null;
        }

        @Override
        public void render(final Node node) {
            renderNode(node, this);
        }

        void renderNode(Node node, NodeFormatterSubContext subContext) {
            if (node instanceof Document) {
                // here we render multiple phases
                for (FormattingPhase phase : FormattingPhase.values()) {
                    if (phase != FormattingPhase.DOCUMENT && !renderingPhases.contains(phase)) { continue; }
                    this.phase = phase;
                    // here we render multiple phases
                    if (this.phase == FormattingPhase.DOCUMENT) {
                        NodeFormattingHandler nodeRenderer = renderers.get(node.getClass());
                        if (nodeRenderer != null) {
                            subContext.setRenderingNode(node);
                            nodeRenderer.render(node, subContext, subContext.getMarkdown());
                            subContext.setRenderingNode(null);
                        }
                    } else {
                        // go through all renderers that want this phase
                        for (PhasedNodeFormatter phasedFormatter : phasedFormatters) {
                            if (phasedFormatter.getFormattingPhases().contains(phase)) {
                                subContext.setRenderingNode(node);
                                phasedFormatter.renderDocument(subContext, subContext.getMarkdown(), (Document) node, phase);
                                subContext.setRenderingNode(null);
                            }
                        }
                    }
                }
            } else {
                NodeFormattingHandler nodeRenderer = renderers.get(node.getClass());

                if (nodeRenderer == null) {
                    nodeRenderer = renderers.get(Node.class);
                }

                if (nodeRenderer != null) {
                    Node oldNode = this.getRenderingNode();
                    subContext.setRenderingNode(node);
                    nodeRenderer.render(node, subContext, subContext.getMarkdown());
                    subContext.setRenderingNode(oldNode);
                } else {
                    // default behavior is controlled by generic Node.class that is implemented in CoreNodeFormatter
                    throw new IllegalStateException("Core Node Formatter should implement generic Node renderer");
                }
            }
        }

        public void renderChildren(Node parent) {
            renderChildrenNode(parent, this);
        }

        @SuppressWarnings("WeakerAccess")
        protected void renderChildrenNode(Node parent, NodeFormatterSubContext subContext) {
            Node node = parent.getFirstChild();
            while (node != null) {
                Node next = node.getNext();
                renderNode(node, subContext);
                node = next;
            }
        }

        @Override
        public FormattingPhase getFormattingPhase() {
            return FormattingPhase.DOCUMENT;
        }

        @Override
        public DataHolder getOptions() {
            return options;
        }

        @Override
        public FormatterOptions getFormatterOptions() {
            return null;
        }

        public CMakeParserOptions getCMakeOptions() {
            return formatterOptions;
        }

        @Override
        public Document getDocument() {
            return document;
        }

        @Override
        public Node getCurrentNode() {
            return getRenderingNode();
        }

        // Unused

        @Override
        public RenderPurpose getRenderPurpose() {
            return null;
        }

        @Override
        public MutableDataHolder getTranslationStore() {
            return null;
        }

        @Override
        public boolean isTransformingText() {
            return false;
        }

        @Override
        public CharSequence transformNonTranslating(final CharSequence prefix, final CharSequence nonTranslatingText, final CharSequence suffix, final CharSequence suffix2) {
            return null;
        }

        @Override
        public CharSequence transformTranslating(final CharSequence prefix, final CharSequence translatingText, final CharSequence suffix, final CharSequence suffix2) {
            return null;
        }

        @Override
        public CharSequence transformAnchorRef(final CharSequence pageRef, final CharSequence anchorRef) {
            return null;
        }

        @Override
        public void translatingSpan(final TranslatingSpanRender render) {

        }

        @Override
        public void nonTranslatingSpan(final TranslatingSpanRender render) {

        }

        @Override
        public void translatingRefTargetSpan(final Node target, final TranslatingSpanRender render) {

        }

        @Override
        public void customPlaceholderFormat(final TranslationPlaceholderGenerator generator, final TranslatingSpanRender render) {

        }
    }
}
