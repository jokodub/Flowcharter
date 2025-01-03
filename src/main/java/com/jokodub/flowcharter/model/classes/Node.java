package com.jokodub.flowcharter.model.classes;

import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

public class Node
{
    private static int nodeCount = -2; //First real node should be 1, after Top and Bottom

    //Instance Variables
    private final int id;
    private int height;
    private Set<Node> inbound;
    private Set<Node> outbound;
    
    //Constructors
    public Node() //Entirely blank node
    {
        id = ++nodeCount;
        inbound = new HashSet<>();
        outbound = new HashSet<>();
        height = 0;
    }

    public Node(Set<Node> in, Set<Node> out)
    {
        id = ++nodeCount;
        inbound = in;
        outbound = out;
        
        //Height = max height of parents + 1
        int max = 0;
        for(Node i : in)
            if(i.getHeight() > max) max = i.getHeight();
        height = max + 1;
    }

    //Standard Get-Set-Boilerplates
    public int getId() { return id; }
    public int getHeight() { return height; }
    public void setHeight(int h) { height = h; }

    public Set<Node> getInboundSet() { return inbound; }
    public Set<Node> getOutboundSet() { return outbound; }

    public void addInbound(Node parent) { inbound.add(parent); }
    public void addOutbound(Node child) { outbound.add(child); }

    public void removeInbound(Node parent) { inbound.remove(parent); }
    public void removeOutbound(Node child) { outbound.remove(child); }

    public int numInbound() { return inbound.size(); }
    public int numOutbound() { return outbound.size(); }

    public boolean hasInbound(Node n) { return inbound.contains(n); }
    public boolean hasOutbound(Node n) { return outbound.contains(n); }

    //Travel upstream looking for searchNode
    public boolean hasEventualInbound(Node searchNode, Set<Node> visitedNodes) 
    { 
        return hasEventualInbound(searchNode, visitedNodes, null); 
    }
    /* Recursively return true if this node has a parental chain to searchNode
     * @param searchNode as Node which could be on the inbound chain of this node
     * @param visitedNodes as all nodes we've visited in this recursion
     * @param upstreamNodes as all nodes visited ever
     * @return true if searchNode is found
     */
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


    /* Update the height of this node and recurse to lower nodes
     * @param change as amount to change height by, can be negative.
     * @param visitedNodes as a Set of nodes to not hit the same node twice
     * @param f as Flowchart this node is a part of to get all height-linked nodes (stored as global map not locally)
     */
    public void updateHeight(int change, Set<Node> visitedNodes, Flowchart f)
    {
        visitedNodes.add(this); //Prevent updating this node again

        //Recurse to all children unless visited before
        for(Node child : outbound)
            if(!visitedNodes.contains(child))
                child.updateHeight(change, visitedNodes, f);

        //Recurse to all height-links unless visited before
        for(Node link : f.getLinks(this))
            if(!visitedNodes.contains(link))
                link.updateHeight(change, visitedNodes, f);

        //System.out.println("Updated "+id+" from h="+height+" to h="+(height+change)); //Debugging
        height += change; //Increment height of all selected nodes after recursion is complete.
    }

    /* Return summary of this node's components.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("id="+id + ", h=" + height + ":\n");

        sb.append("Ins: ");
        for(Node i : inbound)
            sb.append(i.getId() + " ");
        sb.append("\n");

        sb.append("Outs: ");
        for(Node o : outbound)
            sb.append(o.getId() + " ");

        return sb.toString();
    }
    

}