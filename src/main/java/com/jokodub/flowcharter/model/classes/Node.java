package com.jokodub.flowcharter.model.classes;

import java.util.Objects;

public class Node 
{    
    // === Instance Variables ===

    private static int nodeCount = -2; //First real node should be 1, after Top and Bottom
    private final int id;
    private final String name;

    // === Constructors ===

    public Node()
    {
        this("");
    }

    public Node(String s)
    {
        id = ++nodeCount;
        name = s;
    }

    // === Get-Set ===

    public int getId() { return id; }
    public String getName() { return name; }

    // === Node Methods ===

    @Override
    public String toString()
    {
        if(name.isEmpty()) 
            return ""+id;
        else
            return name;
    }

    @Override
    public int hashCode() 
    {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) 
    {
        if(obj == this)
            return true;
        if(!(obj instanceof Node))
            return false;
        
        Node other = (Node) obj;
        return this.id == other.id; //Two nodes cannot share id
    }
}

/*
 * //Travel upstream looking for searchNode
    public boolean hasEventualInbound(Node searchNode, Set<Node> visitedNodes) 
    { 
        return hasEventualInbound(searchNode, visitedNodes, null); 
    }
    /* Recursively return true if this node has a parental chain to searchNode
     * @param searchNode as Node which could be on the inbound chain of this node
     * @param visitedNodes as all nodes we've visited in this recursion
     * @param upstreamNodes as all nodes visited ever
     * @return true if searchNode is found
    
    public boolean hasEventualInbound(Node searchNode, Set<Node> visitedNodes, Set<Node> upstreamNodes)
    {   
        if(upstreamNodes != null) upstreamNodes.add(this);

        //If we've been here before and recursion hasn't ended, it cannot be on this branch and we've looped
        if(visitedNodes.contains(this)) 
            return false;
        else 
            visitedNodes.add(this);

        if(this == searchNode) return true; //Reached searchNode
        if(this.height < 1) return false; //Reached top of tree, not here

        //Recurse further up the tree
        for(Node i : inbound)
            if(i.hasEventualInbound(searchNode, visitedNodes, upstreamNodes))
                return true;
        return false;
    }
*/