package com.jokodub.flowcharter.model.classes;

import java.util.Set;

public class LabelNode extends VisualNode 
{
    private String label;

    public LabelNode(String s)
    {
        super();
        label = s;
    }

    public LabelNode(String s, Set<Node> in, Set<Node> out)
    {
        super(in, out);
        label = s;
    }

    public String getLabel() { return label; }
    public void setLabel(String s) { label = s; }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("'"+label+"' ");
        sb.append(super.toString());

        return sb.toString();
    }
}
