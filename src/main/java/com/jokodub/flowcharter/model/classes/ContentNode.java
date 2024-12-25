package com.jokodub.flowcharter.model.classes;

import java.util.Set;

public class ContentNode extends VisualNode
{
    private String label;
    private String content;

    public ContentNode(String l, String c)
    {
        super();
        label = l;
        content = c;
    }

    public ContentNode(String l, String c, Set<Node> in, Set<Node> out)
    {
        super(in, out);
        label = l;
        content = c;
    }

    public ContentNode(String l, Set<Node> in, Set<Node> out)
    {
        this(l, "", in, out);
    }

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    public String getContent() { return content; }
    public void setContent(String s) { content = s; }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("'"+label+"'\n");
        sb.append(content+"\n");
        sb.append(super.toString());

        return sb.toString();
    }
    
}
