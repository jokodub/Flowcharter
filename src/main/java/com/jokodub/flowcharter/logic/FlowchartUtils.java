package com.jokodub.flowcharter.logic;

import java.util.HashSet;
import java.util.Set;
import com.jokodub.flowcharter.model.classes.*;

public class FlowchartUtils 
{
    /* Inserts a node into the Flowchart with connections to the specified ins and outs. 
     * The node's height is reset to be just below its lowest inbound.
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

        //Automatically add invisible nodes where connections are missing
        if(in.isEmpty()) in.add(top);
        if(out.isEmpty()) out.add(bottom);

        f.register(newNode); //Acknowledge node's existence if brand new

        //Height = max height of parents + 1
        int max = 0;
        for(Node i : in)
            if(i.getHeight() > max) max = i.getHeight();
        newNode.setHeight(max + 1);

        //Prepare for height recursion later
        Set<Node> allUpstream = new HashSet<>(); //Stores every node upstream of this, saves repetitive traversal
        Set<Node> visited = new HashSet<>(); //Prevent updating same node's height twice
        visited.add(newNode); //Already set this node's height in stone
        

        //Connect all inbound nodes
        for(Node i : in)
        {
            f.connect(i, newNode);
            
            //No longer lowest node, disconnect from Bottom
            if(i.hasOutbound(bottom) && i.getHeight() <= newNode.getHeight()) //&& is unnecessary i think
                f.unconnect(i, bottom);
        }

        //Connect all outbound nodes
        for(Node o : out)
        {
            f.connect(newNode, o);

            //If any inbound and outbound were already connected, newnode intercepts it.
            for(Node i : in)
                if(o.hasInbound(i))
                    f.unconnect(i, o);

            //Update heights if this node needs space, but not if it leads back here (causes height gaps)
            if(o.getHeight() <= newNode.getHeight() 
            && !allUpstream.contains(o) //Shortcut if we visited already
            && !newNode.hasEventualInbound(o, new HashSet<>(), allUpstream)) //Must check if we haven't seen it before
            {
                o.updateHeight(newNode.getHeight()-o.getHeight()+1, visited, f); //Change height to just below newNode
            }

            //No longer highest node, disconnect from Top
            if(o.hasInbound(top) && o.getHeight() >= newNode.getHeight()) 
                f.unconnect(top, o);
        }

    }
    

    /* 
     * 
     */
    public static void deleteNode(Flowchart f, Node delNode)
    {
        Node top = f.getTop();
        Node bottom = f.getBottom();
        Set<Node> in = delNode.getInboundSet();
        Set<Node> out = delNode.getOutboundSet();

        
        //Special handling of top and bottom
        if(out.contains(bottom))
        {
            for(Node i : in)
            {
                if(true)
                {
                    
                }

            }

            out.remove(bottom);
        }

        if(in.contains(top))
        {
            for(Node o : out)
            {

            }

            in.remove(top);
        }

        //Patch the hole from deletion, connect everything across the gap
        for(Node i : in)
            for(Node o : out)
                if(!i.equals(o))
                    f.connect(i, o);
        
        f.unregister(delNode); //Consider it handled, boss.
    }


    /*
     * 
     */
    public static void swapNodes(Node a, Node b)
    {

    }
    
    
}
