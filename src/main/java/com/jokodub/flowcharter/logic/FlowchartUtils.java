package com.jokodub.flowcharter.logic;

import java.util.HashSet;
import java.util.Set;
import com.jokodub.flowcharter.model.classes.*;

public class FlowchartUtils 
{
    //Used as a library, disallow instances
    private FlowchartUtils(){}

    /* Inserts a node into the Flowchart with connections to the specified ins and outs. 
     * This will intercept existing connections, disconnecting an edge from an in to an out, if it exists.
     * The node's height is set to be just below its lowest inbound.
     * The outbound nodes' heights may be updated, to push them below the node. 
     * @param f as Flowchart to operate on
     * @param newNode as node to be inserted
     * @param in as Set of nodes to point to this node
     * @param out as Set of nodes this node will point to
     */
    public static void insertNode(Flowchart f, Node newNode, Set<Node> in, Set<Node> out)
    {
        Node top = f.getTop();
        Node bottom = f.getBottom();

        //Although this is done in f.addNode, we need to keep track here as well
        if(in.isEmpty()) in.add(top);
        if(out.isEmpty()) out.add(bottom);
        if(in.contains(newNode)) out.add(newNode);
        if(out.contains(newNode)) in.add(newNode);

        f.addNode(newNode, in, out); //Place node with all of its connections into the graph

        //Height = max height of parents + 1
        int max = 0;
        for(Node i : in)
            if(f.getHeight(i) > max) max = f.getHeight(i);
        f.setHeight(newNode, max + 1);

        /*  Remove connections that newNode has replaced.  */
        
        //Inbounds can't be connected to bottom anymore, they have newNode as a child.
        //Outbounds can't be connected to top anymore, they have newNode as a parent.
        boolean topDisconnectFlag = false;
        boolean bottomDisconnectFlag = false;
        for(Node i : in)
            if(f.hasEdge(i, bottom)) 
            {
                f.removeEdge(i, bottom); 
                bottomDisconnectFlag = true; 
            }
        for(Node o : out)
            if(f.hasEdge(top, o))
            {
                f.removeEdge(top, o);
                topDisconnectFlag = true;
            }


        /* All upstream references to bottom were removed in the previous pass,
         * so make newNode the new leaf unless it has downstream of its own.
         * 
         * We use the uniquely outbound set since if a node is attached to its parent it
         * will not gain bottom as an out at the beginning of this method.
         *
         * If newNode has some downstream nodes (that don't immediately point to newNode),
         * we cannot guarantee bottom was not disconnected later in the chain (ex. loop of 3 nodes).
         * Recursively DFS the tree for bottom is unfortunately the only way I can resolve it.
         * 
         * Vice versa with top references downstream of newNode. 
         */

        //Remove top and bottom from sets to skip unnecessary recursion
        Set<Node> uniqueOuts = uniqueOutboundSet(f, newNode);
        uniqueOuts.remove(bottom);
        Set<Node> uniqueIns = uniqueInboundSet(f, newNode);
        uniqueIns.remove(top);

        if(uniqueOuts.isEmpty()) //newNode is a leaf
        {
            f.addEdge(newNode, bottom);
            out.add(bottom); 
        }
        else if(bottomDisconnectFlag) //Guarantee bottom exists somewhere downstream
        {
            System.out.println("DFS");
            bottomConnector(f, newNode);
        }

        if(uniqueIns.isEmpty()) //newNode is a root
        {
            f.addEdge(top, newNode);
            in.add(top); 
        }
        else if(topDisconnectFlag) //Guarantee top exists somewhere upstream
        {
            System.out.println("DFS");
            topConnector(f, newNode);
        }

        //Disconnect the edges that newNode is placed inbetween
        //Iterate over every pair (excluding newNode itself in looping connections)
        for(Node i : in)
        {
            if(i.equals(newNode)) continue;

            for(Node o : out)
            {
                if(o.equals(newNode)) continue;

                if(!i.equals(o) && f.hasEdge(i, o))
                    f.removeEdge(i, o);
            }
        }

        /* Fix the heights now that connections have been sorted out. */

        Set<Node> visited = new HashSet<>();
        visited.add(newNode);

        for(Node o : out)
        {
            if(f.getHeight(o) <= f.getHeight(newNode) && !visited.contains(o))
            {
                f.updateHeight(o, f.getHeight(newNode) - f.getHeight(o) + 1, visited, false);
            }
        }

        ////////
        //Prepare for height recursion later
        //Set<Node> allUpstream = new HashSet<>(); //Stores every node upstream of this, saves repetitive traversal
        //Set<Node> visited = new HashSet<>(); //Prevent updating same node's height twice
        //visited.add(newNode); //Already set this node's height in stone

        /*
            //Update heights if this node needs space, but not if it leads back here (causes height gaps)
            if(o.getHeight() <= newNode.getHeight() 
            && !allUpstream.contains(o) //Shortcut if we visited already
            && !newNode.hasEventualInbound(o, new HashSet<>(), allUpstream)) //Must check if we haven't seen it before
            {
                o.updateHeight(newNode.getHeight()-o.getHeight()+1, visited, f); //Change height to just below newNode
            }
        
        */

    }

    /* Returns the set difference (A - B) between two Sets, defined as:
     * x in A ^ x not in B
     */
    private static Set<Node> setDifference(Set<Node> a, Set<Node> b)
    {
        Set<Node> diff = new HashSet<>();
        
        //Discard elements of a that are also in b
        for(Node n : a)
            if(!b.contains(n))
                diff.add(n);

        return diff;
    }

    /* Return a Set of all inbound nodes that are not also outbound nodes.
     * @param f as Flowchart to use
     * @param n as node to get connections of
     */
    private static Set<Node> uniqueInboundSet(Flowchart f, Node n)
    {
        return setDifference(f.getInboundSet(n), f.getOutboundSet(n));
    }

    /* Return a Set of all outbound nodes that are not also inbound nodes.
     * @param f as Flowchart to use
     * @param n as node to get connections of
     */
    private static Set<Node> uniqueOutboundSet(Flowchart f, Node n)
    {
        return setDifference(f.getOutboundSet(n), f.getInboundSet(n));
    }

    /*
     * 
     */
    private static void topConnector(Flowchart f, Node start)
    {
        recTopConnector(f, start, new HashSet<>());
    }

    private static boolean recTopConnector(Flowchart f, Node cur, Set<Node> visited)
    {
        boolean found = false; //Default to not found the top yet

        if(f.hasEdge(f.getTop(), cur)) //Base case: found it!
            return true;

        //Recurse to find bottom (or end of a closed loop)
        for(Node i : uniqueInboundSet(f, cur))
        {
            if(!visited.contains(i)) //Don't recurse to same place twice
            {
                visited.add(i);

                found = recTopConnector(f, i, visited);
                if(found) break; //Top is connected, we can end our search. 
            }
        }
        
        if(!found)
        {
            //Nothing upstream of this node is connected to top, so add the connection here.
            //This should happen at the top of the graph, when no more inbounds exist.
            f.addEdge(f.getTop(), cur);
            found = true;
        }

        return found;
    }

    /* DFS recurse through a Flowchart to find where bottom is or should be attached.
     * By only going down the tree (unique outbounds), we get closer to where bottom should be.
     * If bottom is not at the end of that chain, then it has been disconnected
     * so we reattach at that point.
     */
    private static void bottomConnector(Flowchart f, Node start)
    {
        recBottomConnector(f, start, new HashSet<>());
    }

    private static boolean recBottomConnector(Flowchart f, Node cur, Set<Node> visited)
    {
        boolean found = false; //Default to not found the bottom yet

        if(f.hasEdge(cur, f.getBottom())) //Base case: found it!
            return true;

        //Recurse to find bottom (or end of a closed loop)
        for(Node o : uniqueOutboundSet(f, cur))
        {
            if(!visited.contains(o)) //Don't recurse to same place twice
            {
                visited.add(o);

                found = recBottomConnector(f, o, visited);
                if(found) break; //Bottom is connected, we can end our search. 
            }
        }

        if(!found)
        {
            //Nothing downstream of this node is connected to bottom, so add the connection here.
            //This should happen at the bottom of the tree, when no more outbounds exist.
            f.addEdge(cur, f.getBottom());
            found = true;
        }

        return found;
    }
    

    /* 
     * 
     */
    public static void deleteNode(Flowchart f, Node delNode)
    {
        
    }


    /*
     * 
     */
    public static void swapNodes(Node a, Node b)
    {

    }
    
    
}
