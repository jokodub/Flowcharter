package com.jokodub.flowcharter.logic;

import java.util.HashSet;
import java.util.Set;
import com.jokodub.flowcharter.model.classes.*;

public class Flowcharts 
{
    public static void insertNode(Flowchart f, Node newNode, Set<Node> in, Set<Node> out)
    {
        Node top = f.getTop();
        Node bottom = f.getBottom();

        //Automatically add invisible nodes where connections are missing
        if(in.isEmpty()) in.add(top);
        if(out.isEmpty()) out.add(bottom);

        f.register(newNode);

        //Height = max height of parents + 1
        int max = 0;
        for(Node i : in)
            if(i.getHeight() > max) max = i.getHeight();
        newNode.setHeight(max + 1);

        //Prepare for height recursion later
        Set<Node> visited = new HashSet<>(); //Prevent updating same node twice
        visited.add(newNode);
        Set<Node> allUpstream = new HashSet<>(); //Stores every node upstream of this, saves repetitive traversal

        //Connect all inbound nodes
        for(Node i : in)
        {
            f.connect(i, newNode);
            
            if(i.hasOutbound(bottom) && i.getHeight() <= newNode.getHeight()) //&& is unnecessary i think
            {
                out.add(bottom);
                f.connect(newNode, bottom);
                f.unconnect(i, bottom);
            }
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

            if(o.hasInbound(top) && o.getHeight() >= newNode.getHeight())
            {
                in.add(top);
                f.connect(top, newNode);
                f.unconnect(top, o);
            }
        }

    }

    /*
    public static void optimize(Flowchart f)
    {

    }
    
    

    public static void removeNode(Flowchart f, int id)
    {
        
    }
    */
}
