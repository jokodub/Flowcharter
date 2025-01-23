package com.jokodub.flowcharter.logic;

import java.util.Set;
import java.util.HashSet;

import com.jokodub.flowcharter.model.classes.*;

import com.jokodub.flowcharter.model.classes.Flowchart;

public class FlowchartTester 
{
    public static void main(String[] args)
    {
        Flowchart myChart = new Flowchart();

        System.out.println("~~ John's Flowchart Prototype ~~");

        Node a = new Node("a");
        Node b = new Node("b");
        Node c = new Node("c");
        Node d = new Node("d");
        Node e = new Node("e");
        Node f = new Node("f");

        
        //Make a circle
        FlowchartUtils.insertNode(myChart, a, newSet(), newSet());
        System.out.println(myChart.toString());

        FlowchartUtils.insertNode(myChart, b, newSet(a), newSet(a));
        System.out.println(myChart.toString());

        FlowchartUtils.insertNode(myChart, c, newSet(b), newSet(a));
        System.out.println(myChart.toString());

        FlowchartUtils.insertNode(myChart, d, newSet(c), newSet(a));
        System.out.println(myChart.toString());

        FlowchartUtils.insertNode(myChart, e, newSet(d), newSet(a));
        System.out.println(myChart.toString());

        FlowchartUtils.insertNode(myChart, f, newSet(e), newSet(a));
        System.out.println(myChart.toString());
        
        /* 
        FlowchartUtils.insertNode(myChart, a, newSet(), newSet());
        System.out.println(myChart.toString());
        FlowchartUtils.insertNode(myChart, b, newSet(a), newSet());
        System.out.println(myChart.toString());
        FlowchartUtils.insertNode(myChart, c, newSet(a), newSet(b));
        System.out.println(myChart.toString());
        */
    }

    private static Set<Node> newSet(Node... nodes)
    {
        Set<Node> mySet = new HashSet<>();
        for(Node n : nodes)
            mySet.add(n);
        return mySet;
    }

    public static void menu()
    {
        System.out.println("What to do?");
        System.out.println("N: Add a Node");
        System.out.println("L: Height-link two Nodes");
        System.out.println("Q: Quit");
        System.out.print("> ");
    }

}