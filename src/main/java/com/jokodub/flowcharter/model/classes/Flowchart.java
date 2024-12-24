package com.jokodub.flowcharter.model.classes;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

public class Flowchart
{   
    // === Instance Variables ===

    private String title;
    private Set<Node> nodes; //Holds Nodes belonging to this flowchart
    private HashMap<Node, Set<Node>> links;
    private HashMap<Node, Set<Node>> references;

    //Nodes placed at top and bottom for organization and sorting.
    private Node top; //Parent of all heads
    private Node bottom; //Child of all leafs

    // === Constructors ===

    public Flowchart(String title)
    {
        this.title = title;
        nodes = new HashSet<>();
        links = new HashMap<>();
        references = new HashMap<>();
        
        top = new Node();
        bottom = new Node(createNodeSet(top), new HashSet<>());
        top.addOutbound(bottom);
        nodes.add(top);
        nodes.add(bottom);
    }

    public Flowchart()
    {   
        this("Flowchart");
    }

    // === Getter-Setters ===

    public String getTitle() { return title; }
    public void setTitle(String s) { title = s; }
    public Set<Node> getAllNodes() { return nodes; }
    public Set<Node> getReferences(Node n) { return references.getOrDefault(n, Collections.emptySet()); }
    public Set<Node> getLinks(Node n) { return links.getOrDefault(n, Collections.emptySet()); }
    public Node getTop() { return top; }
    public Node getBottom() { return bottom; }

    /* Returns of a set of all Nodes that reference this Node. 
     * @param n as Node that is the subject of the references
     * @return a Set of all Nodes pointing to n
     */
    public Set<Node> getReferencedBy(Node n) 
    {
        Set<Node> referencingThis = new HashSet<>();

        for(Map.Entry<Node, Set<Node>> entry : references.entrySet())
            if(entry.getValue().contains(n))
                referencingThis.add(entry.getKey());

        return referencingThis;
    }

    /* Searches all nodes for one matching id
     * @param id as id to search for
     * @return first matching node (should be only one that exists)
     */
    public Node getNodeById(int id)
    {
        for(Node n : nodes)
            if(n.getId() == id)
                return n;
        return null; //else, Node not a part of this flowchart
    }

    // === Adding and Removing Records === 

    /* We maintain a set of all nodes that are in this Flowchart. 
     * This is more efficient than traversing the tree every time. 
     * @param n as Node to add to the Flowchart
     */
    public void register(Node n) 
    {
        nodes.add(n); 
    }

    //Removes this Node's record, and all records containing it. 
    public void unregister(Node n) 
    {
        links.remove(n); //Remove n's record
        for(Map.Entry<Node, Set<Node>> entry : links.entrySet()) //Loop to remove n from all
            if(entry.getValue().contains(n))
                entry.getValue().remove(n);

        references.remove(n);
        for(Map.Entry<Node, Set<Node>> entry : references.entrySet())
            if(entry.getValue().contains(n))
                entry.getValue().remove(n);

        nodes.remove(n); 
    }

    public void unregister(int id) { unregister(getNodeById(id)); }

    /* Direct Connection: Two nodes can have a directional parent-child relationship, 
     * though the connection is not strictly downward, so I use inbound-outbound instead.
     * **This does not update the heights of each node like Insertion does.** 
     * @param src as Node connection originates from
     * @param dest as Node connection leads to
     */
    public void connect(Node src, Node dest)
    {
        src.addOutbound(dest);
        dest.addInbound(src);
    }

    //Disconnects a direct relationship, if it exists. 
    public void unconnect(Node src, Node dest)
    {
        src.removeOutbound(dest);
        dest.removeInbound(src);
    }

    /* Height Link: Two indirectly linked nodes will be forced to the same height, a style 
     * choice for visual clarity. The Nodes may or may not be also directly connected.
     * @param a, b as Nodes to height-link. 
     */
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

    //Disconnects an indirect link, if it exists.
    public void unlink(Node a, Node b)
    {
        if(links.containsKey(a))
            links.get(a).remove(b);
        if(links.containsKey(b))
            links.get(b).remove(a);
    }

    /* Reference: A node may indirectly "reference" another like an appendix. 
     * A reference is directional and neither Node affects the other. 
     * @param src as Node needing a reference
     * @param dest as Node to point to
     */
    public void reference(Node src, Node dest)
    {
        references.putIfAbsent(src, new HashSet<>());
        references.get(src).add(dest);
    }

    //Disconnects an indirect reference, if it exists.
    public void unreference(Node src, Node dest)
    {
        if(references.containsKey(src))
            references.get(src).remove(dest);
    }

    /* Insert a node into an existing connection. 
     * The new node will be placed one step below its lowest parent and move all children to match.
     * If any of the ins and outs were connected before, this node breaks that connection for itself.
     * @param newNode as node with one-way connections to parents and children to be completed. 
     */
    public void insertNode(Node newNode)
    {
        //If no nodes specified for in and out, they actually link to hidden top and bottom
        if(newNode.numInbound() == 0) newNode.addInbound(top);
        if(newNode.numOutbound() == 0) newNode.addOutbound(bottom);

        register(newNode); //Register node as a part of this flowchart

        //Connect parents downward
        for(Node i : newNode.getInboundSet())
        {
            i.addOutbound(newNode);
            
            //If any in and out were already connected, this node intercepts it.
            for(Node o : newNode.getOutboundSet())
                if(i.hasOutbound(o))
                    unconnect(i, o);
        }

        //Finalize connection to new node and update heights
        Set<Node> visited = createNodeSet(newNode);
        for(Node o : newNode.getOutboundSet())
        {
            o.addInbound(newNode);

            //This node is no longer a head because it the parent of newNode, unconnect from Top
            if(top.hasOutbound(o))
                unconnect(top, o);

            //If child is above this node, need to bring it down
            if(o.getHeight() <= newNode.getHeight()) 
                o.updateHeight(newNode.getHeight()-o.getHeight()+1, visited, this); //Change height to just below newNode
        }
    }

    // === Helper Methods ===

    /* Creates a HashSet of nodes from a vararg
     * @param as many Nodes as you need
     * @return a HashSet initialized with those Nodes.
     */
    public static HashSet<Node> createNodeSet(Node... nodes)
    {
        HashSet<Node> s = new HashSet<>();
        for(Node n : nodes)
            s.add(n);
        return s;
    }

    public void summary()
    {
        System.out.println("--- Summary of "+title+" ---");
        for(Node n : nodes)
        {
            System.out.println(n.toString());
            System.out.println("----------");
        }
    }

    
}
