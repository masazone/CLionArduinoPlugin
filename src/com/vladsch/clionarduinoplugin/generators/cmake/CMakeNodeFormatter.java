package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.clionarduinoplugin.generators.cmake.ast.*;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.formatter.CustomNodeFormatter;
import com.vladsch.flexmark.formatter.internal.MarkdownWriter;
import com.vladsch.flexmark.formatter.internal.NodeFormatter;
import com.vladsch.flexmark.formatter.internal.NodeFormatterContext;
import com.vladsch.flexmark.formatter.internal.NodeFormattingHandler;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.RepeatedCharSequence;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CMakeNodeFormatter implements NodeFormatter {

    private final CMakeFormatterOptions formatterOptions;
    private int blankLines;

    public CMakeNodeFormatter(DataHolder options) {
        this.formatterOptions = new CMakeFormatterOptions(options);
        blankLines = 0;
    }

    @Override
    public Set<Class<?>> getNodeClasses() {
        return null;
    }

    private void render(final Node node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        markdown.append(node.getChars());
    }

    private void render(final BlankLine node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        markdown.blankLine();
    }

    private void render(final Command node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        markdown.append(node.getCommand());
        if (formatterOptions.spaceAfterCommandName.contains(node.getCommand().toString())) {
            markdown.append(' ');
        }
        markdown.append(node.getOpeningMarker());
        markdown.pushPrefix();
        if (formatterOptions.indentSpaces > 0) markdown.addPrefix(RepeatedCharSequence.of(' ', formatterOptions.indentSpaces));
        context.renderChildren(node);
        markdown.popPrefix();
        markdown.append(node.getClosingMarker()).line();
    }

    private void render(final Argument node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        boolean hadLine = false;
        Node prevArg = node.getPreviousAny(Argument.class);
        Node nextArg = node.getNextAny(Argument.class);

        if (prevArg == null) {
            // first arg
            markdown.append(formatterOptions.argumentListPrefix);
        }

        if (formatterOptions.argumentListMaxLine > 0 && formatterOptions.argumentListMaxLine < 1000) {
            int col = markdown.columnWith(node.getChars(), 0, node.getChars().length());
            if (col > formatterOptions.argumentListMaxLine) {
                // we break, parent should have setup indent prefix
                markdown.line();
                hadLine = true;
            }
        }

        if (!hadLine && prevArg != null) {
            // add separator
            if (prevArg.getChars().equals("(") || node.getChars().equals(")")) {
                markdown.append(formatterOptions.argumentParensSeparator);
            } else {
                markdown.append(formatterOptions.argumentSeparator);
            }
        }

        // prevent prefixes from getting into quoted or bracketed params
        markdown.flushWhitespaces(); // save any space arg list prefix
        markdown.openPreFormatted(true); // leading spaces allowed
        markdown.append(node.getChars());
        markdown.closePreFormatted();

        if (nextArg == null) {
            // last arg
            markdown.append(formatterOptions.argumentListSuffix);
        }
    }

    private void render(final BracketComment node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        markdown.append(node.getChars());

        if (formatterOptions.preserveCommentWhitespace || formatterOptions.collapseCommentWhitespace) {
            Node next = node.getNext();
            if (next != null) {
                BasedSequence sequence = node.getChars().baseSubSequence(node.getEndOffset(), next.getStartOffset());
                if (!sequence.isEmpty() && sequence.isBlank()) {
                    if (!formatterOptions.preserveCommentWhitespace) {
                        if (sequence.indexOfAny(BasedSequence.EOL_CHARS) != -1) {
                            markdown.append('\n');
                        } else {
                            markdown.append(' ');
                        }
                    } else {
                        markdown.append(sequence);
                    }
                }
            }
        }
    }

    private void render(final CMakeFile node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        context.renderChildren(node);
    }

    private void render(final LineComment node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        // need to preserve whitespace between us and previous node
        if (formatterOptions.preserveCommentWhitespace || formatterOptions.collapseCommentWhitespace) {
            Node prev = node.getPrevious();
            if (prev != null) {
                BasedSequence sequence = node.getChars().baseSubSequence(prev.getEndOffset(), node.getStartOffset());
                if (!sequence.isEmpty() && sequence.isBlank()) {
                    if (!formatterOptions.preserveCommentWhitespace) {
                        if (sequence.indexOfAny(BasedSequence.EOL_CHARS) != -1) {
                            markdown.append('\n');
                        } else {
                            markdown.append(' ');
                        }
                    } else {
                        markdown.append(sequence);
                    }
                }
            }
        }

        markdown.append(node.getChars());
    }

    private void render(final LineEnding node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        markdown.append(node.getChars());
    }

    private void render(final Separator node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        //markdown.append(node.getChars());
    }

    private void render(final UnrecognizedInput node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        markdown.append(node.getChars());
    }

    private void render(final VariableReference node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        markdown.append(node.getChars());
    }

    @Override
    public Set<NodeFormattingHandler<?>> getNodeFormattingHandlers() {
        return new HashSet<NodeFormattingHandler<? extends Node>>(Arrays.asList(
                // Generic unknown node formatter
                new NodeFormattingHandler<Node>(Node.class, new CustomNodeFormatter<Node>() {
                    @Override
                    public void render(Node node, NodeFormatterContext context, MarkdownWriter markdown) {
                        CMakeNodeFormatter.this.render(node, context, markdown);
                    }
                }),
                new NodeFormattingHandler<BlankLine>(BlankLine.class, new CustomNodeFormatter<BlankLine>() {
                    @Override
                    public void render(BlankLine node, NodeFormatterContext context, MarkdownWriter markdown) {
                        CMakeNodeFormatter.this.render(node, context, markdown);
                    }
                }),
                new NodeFormattingHandler<Command>(Command.class, new CustomNodeFormatter<Command>() {
                    @Override
                    public void render(Command node, NodeFormatterContext context, MarkdownWriter markdown) {
                        CMakeNodeFormatter.this.render(node, context, markdown);
                    }
                }),
                new NodeFormattingHandler<Argument>(Argument.class, new CustomNodeFormatter<Argument>() {
                    @Override
                    public void render(Argument node, NodeFormatterContext context, MarkdownWriter markdown) {
                        CMakeNodeFormatter.this.render(node, context, markdown);
                    }
                }),
                new NodeFormattingHandler<BracketComment>(BracketComment.class, new CustomNodeFormatter<BracketComment>() {
                    @Override
                    public void render(BracketComment node, NodeFormatterContext context, MarkdownWriter markdown) {
                        CMakeNodeFormatter.this.render(node, context, markdown);
                    }
                }),
                new NodeFormattingHandler<CMakeFile>(CMakeFile.class, new CustomNodeFormatter<CMakeFile>() {
                    @Override
                    public void render(CMakeFile node, NodeFormatterContext context, MarkdownWriter markdown) {
                        CMakeNodeFormatter.this.render(node, context, markdown);
                    }
                }),
                new NodeFormattingHandler<LineComment>(LineComment.class, new CustomNodeFormatter<LineComment>() {
                    @Override
                    public void render(LineComment node, NodeFormatterContext context, MarkdownWriter markdown) {
                        CMakeNodeFormatter.this.render(node, context, markdown);
                    }
                }),
                new NodeFormattingHandler<LineEnding>(LineEnding.class, new CustomNodeFormatter<LineEnding>() {
                    @Override
                    public void render(LineEnding node, NodeFormatterContext context, MarkdownWriter markdown) {
                        CMakeNodeFormatter.this.render(node, context, markdown);
                    }
                }),
                new NodeFormattingHandler<Separator>(Separator.class, new CustomNodeFormatter<Separator>() {
                    @Override
                    public void render(Separator node, NodeFormatterContext context, MarkdownWriter markdown) {
                        CMakeNodeFormatter.this.render(node, context, markdown);
                    }
                }),
                new NodeFormattingHandler<UnrecognizedInput>(UnrecognizedInput.class, new CustomNodeFormatter<UnrecognizedInput>() {
                    @Override
                    public void render(UnrecognizedInput node, NodeFormatterContext context, MarkdownWriter markdown) {
                        CMakeNodeFormatter.this.render(node, context, markdown);
                    }
                }),
                new NodeFormattingHandler<VariableReference>(VariableReference.class, new CustomNodeFormatter<VariableReference>() {
                    @Override
                    public void render(VariableReference node, NodeFormatterContext context, MarkdownWriter markdown) {
                        CMakeNodeFormatter.this.render(node, context, markdown);
                    }
                })
        ));
    }
}
