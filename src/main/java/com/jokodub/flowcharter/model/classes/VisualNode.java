package com.jokodub.flowcharter.model.classes;

import java.util.Set;

public abstract class VisualNode extends Node
{
    private double xPos, yPos;
    private int xDim, yDim;

    public VisualNode(Set<Node> in, Set<Node> out)
    {
        this(0, 0, in, out);
    }

    public VisualNode(double x, double y, Set<Node> in, Set<Node> out)
    {
        super(in, out);
        xPos = x;
        yPos = y;
        xDim = 100;
        yDim = 100;
    }
    //NOTE: Add another constructor to change size of a node. 

    public double getXPos() { return xPos; }
    public double getYPos() { return yPos; }
    public void setXPos(double x) { xPos = x; }
    public void setYPos(double y) { yPos = y; }

    public int getXDim() { return xDim; }
    public int getYDim() { return yDim; }
    public void setXDim(int x) { xDim = x; }
    public void setYDim(int y) { yDim = y; }
}
