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

    /* Links the heights of two nodes so they will
     * always be on the same height level for visual clarity.
     * @param a,b as nodes to link heights
     */
    public void addLink(Node a, Node b)
    {
        //make sure all heights are synced
        /*
    /* Height Link: Two indirectly linked nodes will be forced to the same height, a style 
     * choice for visual clarity. The Nodes may or may not be also directly connected.
     * @param a, b as Nodes to height-link. 
     
    public void link(Node a, Node b)
    {
        //First, move highest node down to be level
        //Add the other node to the already-visited set so both are not updated if connected
        if(a.getHeight() > b.getHeight()) //b needs to be moved down
            b.updateHeight(a.getHeight()-b.getHeight(), createNodeSet(a), this);

        else if( a.getHeight() < b.getHeight()) //a needs to be moved down
            a.updateHeight(b.getHeight()-a.getHeight(), createNodeSet(b), this);

        //Second, register link
        links.putIfAbsent(a, new HashSet<>());
        links.putIfAbsent(b, new HashSet<>());
        links.get(a).add(b);
        links.get(b).add(a);
    }
    */
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

    /*
     * 
     */
    public void updateHeight(Node n, int delta)
    {

    }
}






    
    
