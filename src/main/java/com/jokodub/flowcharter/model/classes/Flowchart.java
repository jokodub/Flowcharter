package com.jokodub.flowcharter.model.classes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Flowchart 
{   
    // === Instance Variables ===

    private String title;
    private Map<Node, List<Integer>> nodePositions; //[Height(vertical), Rank(horizontal)]
    private Map<Node, List<Set<Node>>> nodeConnections; //[Inbound, Outbound, Links, Mentions]
    private final Node top;
    private final Node bottom;

    // === Constructors ===

    public Flowchart(String title)
    {
        this.title = title; 

        //Initialize maps of node positions and connections
        nodePositions = new HashMap<>();
        nodeConnections = new HashMap<>();

        //Initialize invisible top and bottom nodes for traversal
        top = new Node("Top");
        bottom = new Node("Bot");
        this.registerNode(top);
        this.registerNode(bottom);
        this.addEdge(top, bottom);
        this.setHeight(bottom, 1);
    }

    public Flowchart()
    {
        this("Flowchart");
    }

    // === Get-Set ===

    public int getHeight(Node n) { return nodePositions.get(n).get(0); }
    public int getRank(Node n) { return nodePositions.get(n).get(1); }
    public void setHeight(Node n, int h) { nodePositions.get(n).set(0, h); }
    public void setRank(Node n, int r) { nodePositions.get(n).set(1, r); }

    public Set<Node> getAllNodes() { return nodeConnections.keySet(); }
    public Set<Node> getInboundSet(Node n) { return nodeConnections.get(n).get(0); }
    public Set<Node> getOutboundSet(Node n) { return nodeConnections.get(n).get(1); }
    public Set<Node> getLinkSet(Node n) { return nodeConnections.get(n).get(2); }
    public Set<Node> getMentionSet(Node n) { return nodeConnections.get(n).get(3); }

    public Node getTop() { return top; }
    public Node getBottom() { return bottom; }
    
    public String getTitle() { return title; }
    public void setTitle(String t) { title = t; }

    // === Graph Methods ===

    public boolean hasEdge(Node src, Node dest) { return getOutboundSet(src).contains(dest); }

    /* Searches all nodes for one matching id
     * @param id as id to search for
     * @return first matching node (should be only one that exists)
     */
    public Node getNodeById(int id)
    {
        for(Node n : getAllNodes())
            if(n.getId() == id)
                return n;
        return null; //else, Node not a part of this flowchart
    }

    /* Creates an entry for this Node in the Flowchart's Maps,
     * which means that Node is an element of it. 
     * Will do nothing if entry already exists.
     * @param n as Node to create entry for
     */
    private void registerNode(Node n)
    {
        //Initialize a list of 4 sets,
        //  [Inbound connections, Outbound connections, Height links, Mentions]
        //Null check here as to not create 4 more Sets if entry exists. 
        if(nodeConnections.putIfAbsent(n, new ArrayList<>(4)) == null)
            for(int i = 0; i < 4; i++)
                nodeConnections.get(n).add(new HashSet<>());
        
        //Initialize ist of 2 integers, [height, rank]
        nodePositions.putIfAbsent(n, new ArrayList<>(Arrays.asList(0, 0)));
    }

    /* Deletes a Node from the Flowchart's Maps,
     * meaning all references to it will be removed.
     * Node does not need to be registered previously, this will do nothing. 
     * @param n as Node to remove
     */
    private void unregisterNode(Node n)
    {   
        //Remove all connections to n in other entries
        for(List<Set<Node>> list : nodeConnections.values())
            for(Set<Node> set : list)
                set.remove(n);

        //Remove n's own entries
        nodeConnections.remove(n); 
        nodePositions.remove(n);
    }

    /* Places a node into the graph with some connections.
     * A node missing connections to and from it will be connected to an
     * invisible Top and Bottom, used for traversal and sorting.
     * @param n as Node to add into the Flowchart
     * @param inbound as Set of nodes to point to n
     * @param outbound as Set of nodes n points to
     */
    public void addNode(Node n, Set<Node> inbound, Set<Node> outbound)
    {
        registerNode(n);

        //Ensure node with no connections is traverseable
        if(inbound.isEmpty()) inbound.add(top);
        if(outbound.isEmpty()) outbound.add(bottom);

        //If n loops, ensure it is both parent AND child of itself
        if(inbound.contains(n)) outbound.add(n);
        if(outbound.contains(n)) inbound.add(n);

        //Draw all the edges to n
        for(Node in : inbound)
            addEdge(in, n);

        for(Node out : outbound)
            addEdge(n, out);
    }
    
    public void addNode(Node n)
    {
        addNode(n, new HashSet<>(), new HashSet<>());
    }

    /* Removes a node from the graph, severing all connections
     * Logically identical to removing all record of it.
     */
    public void removeNode(Node n)
    {
        unregisterNode(n);
    }

    /* Directionally connects two nodes. 
     * @param src as start of connection
     * @param dest as end of connection
     */
    public void addEdge(Node src, Node dest)
    {
        getOutboundSet(src).add(dest);
        getInboundSet(dest).add(src);
    }

    /* Removes a directional connection, if it exists.
     * @param src as start of connection to remove
     * @param dest as end of connection to remove
     */
    public void removeEdge(Node src, Node dest)
    {
        getOutboundSet(src).remove(dest);
        getInboundSet(dest).remove(src);
    }

    /* Summarizes the Flowchart by listing every node's
     * position and connection sets.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getTitle() + "\n");

        //Display a summary of each node
        for(Node n : getAllNodes())
        {   
            //Display node's name and placement
            sb.append(n.toString());
            sb.append(" (" + getHeight(n) + "," + getRank(n) + ") ");
            sb.append("\t");

            //Display node's sets of connections
            sb.append(" In: {"+getInboundSet(n).toString()+"} ");
            sb.append("Out: {"+getOutboundSet(n).toString()+"} ");
            sb.append("Lnk: {"+getLinkSet(n).toString()+"} ");
            sb.append("Mnt: {"+getMentionSet(n).toString()+"} ");

            sb.append("\n");
        } 

        return sb.toString();
    }
    

    // === Link Methods ===

    public boolean hasLink(Node n, Node query) { return getLinkSet(n).contains(query); } 

    /* Links the heights of two nodes so they will
     * always be on the same height level for visual clarity.
     * @param a,b as nodes to link heights
     */
    public void addLink(Node a, Node b)
    {
        //When recursing in the next step, avoid recursing to the one we don't want to move.
        Set<Node> abSet = new HashSet<>();
        abSet.add(a);
        abSet.add(b);

        //Move highest node down to be level, easier than dragging upward
        if(getHeight(a) > getHeight(b)) //b needs to be moved down
            updateHeight(b, getHeight(a) - getHeight(b), abSet, false);
        else if(getHeight(a) < getHeight(b))
            updateHeight(a, getHeight(b) - getHeight(a), abSet, false);

        //Acknowledge link from now on
        getLinkSet(a).add(b);
        getLinkSet(b).add(a);
    }

    /* Removes the height link between two nodes.
     * Does not reset heights to what they were before the link.
     * @param a,b as nodes to unlink
     */
    public void removeLink(Node a, Node b)
    {
        getLinkSet(a).remove(b);
        getLinkSet(b).remove(a);
    }

    // === Mention Methods ===

    public boolean hasMention(Node n, Node query) { return getMentionSet(n).contains(query); }

    /* Adds a superficial directional connection of two nodes
     * that acts like a footnote. Does not affect structure.
     * @param src as node to have the note of dest
     * @param dest as node to be referenced
     */
    public void addMention(Node src, Node dest)
    {
        getMentionSet(src).add(dest);
    }

    /* Removes src's footnote mention of dest, if it exists
     * @param src as the source of the mention
     * @param dest as the target of the mention
     */
    public void removeMention(Node src, Node dest)
    {
        getMentionSet(src).remove(dest);
    }

    /* Collects all mentions of n into a Set.
     * Iterates over the connections map because nodes do
     * not track mentions both ways (intended)
     * @param n as node to search for mentions of
     */
    public Set<Node> allMentionsTo(Node n)
    {
        Set<Node> mentionsN = new HashSet<>(); 

        //Iterate over all sets of mentions to find references to n
        for(Node i : getAllNodes())
            if(getMentionSet(i).contains(n))
                mentionsN.add(i);
        
        return mentionsN;
    }
    
    // === Height Methods ===

    /* Recursively update the height of this node and all of its children,
     * keeping them packed together. 
     * @param start as the Node whose children and itself will be updated
     * @param delta as how much to change height by, positive or negative.
     */
    public void updateHeight(Node start, int delta)
    {
        recUpdateHeight(start, delta, new HashSet<>(), true);
    }

    /* Overload to support multiple recursions without updating shared children
     * @param previousVisited as a set to be defined outside this call across multiple recursions
     * @param allowUpwardRecursion to allow recursion to travel to nodes higher than this one
     */
    public void updateHeight(Node start, int delta, Set<Node> previousVisited, boolean allowUpwardRecursion)
    {
        recUpdateHeight(start, delta, previousVisited, allowUpwardRecursion);
    }

    private void recUpdateHeight(Node cur, int delta, Set<Node> visited, boolean allowUpwardRecursion)
    {
        visited.add(cur); //Prevent updating this node again

        //Recurse to all children not yet visited
        //Only recurse upward if allowed
        for(Node child : getOutboundSet(cur))
            if(!visited.contains(child) && (!(getHeight(child) < getHeight(cur)) || allowUpwardRecursion))
                recUpdateHeight(child, delta, visited, allowUpwardRecursion);

        //Recurse to all height-links not yet visited
        for(Node link : getLinkSet(cur))
            if(!visited.contains(link))
                recUpdateHeight(cur, delta, visited, allowUpwardRecursion);

        //Recursion has reached every node needed, change heights now.
        setHeight(cur, getHeight(cur) + delta);
    }
}






    
    
