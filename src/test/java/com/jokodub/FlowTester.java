package com.jokodub;

import java.util.Scanner;
import java.util.Set;

import com.jokodub.flowcharter.logic.FlowchartUtils;
import com.jokodub.flowcharter.model.classes.*;

import java.util.HashSet;

public class FlowTester 
{
    public static void main(String[] args)
    {
        Flowchart myChart = new Flowchart();

        System.out.println("~~ John's Flowchart Prototype ~~");
        
        Scanner scan = new Scanner(System.in);
        String input = "";
        while(!input.equals("Q"))
        {
            menu();
            input = scan.nextLine();

            switch(input)
            {
                case "N": //New Node
                    createNode(myChart, scan);
                    myChart.summary();
                    break;
                case "L": //New Label
                    createLabelNode(myChart, scan);
                    myChart.summary();
                    break;
                case "C": //New Content
                    createContentNode(myChart, scan);
                    myChart.summary();
                    break;
                case "LL": //Link
                    myChart.link(getNode(myChart, scan), getNode(myChart, scan));
                    myChart.summary();
                    break;
                case "Q": //Quit
                    System.out.println("Goodbye!");
                    System.out.print(myChart.encode());
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }

        scan.close();
    }

    public static void menu()
    {
        System.out.println("What to do?");
        System.out.println(" N: Add a Node");
        System.out.println(" L: Add a Label Node");
        System.out.println(" C: Add a Content Node");
        System.out.println("LL: Height-link two Nodes");
        System.out.println(" Q: Quit");
        System.out.print("> ");
    }

    public static Node getNode(Flowchart f, Scanner scan)
    {
        System.out.print("Node id: ");
        String input = scan.nextLine();
        return f.getNodeById(Integer.parseInt(input));
    }

    public static void createNode(Flowchart f, Scanner scan)
    {
        String input = "";
        String[] splitInput = {};

        //Get inbound connections
        Set<Node> in = new HashSet<>();
        System.out.print("List inbound node(s): ");
        input = scan.nextLine();
        if(input != "")
        {
            splitInput = input.split(" ");
            for(String s : splitInput)
                in.add(f.getNodeById(Integer.parseInt(s)));
        }

        //Get outbound connections
        Set<Node> out = new HashSet<>();
        System.out.print("List outbound node(s): ");
        input = scan.nextLine();
        if(input != "")
        {
            splitInput = input.split(" ");
            for(String s : splitInput)
                out.add(f.getNodeById(Integer.parseInt(s)));
        }

        FlowchartUtils.insertNode(f, new Node(), in, out);
    }

    public static void createLabelNode(Flowchart f, Scanner scan)
    {
        String input = "";
        String[] splitInput = {};

        //Get label
        System.out.print("Enter label: ");
        String label = scan.nextLine();

        //Get inbound connections
        Set<Node> in = new HashSet<>();
        System.out.print("List inbound node(s): ");
        input = scan.nextLine();
        if(input != "")
        {
            splitInput = input.split(" ");
            for(String s : splitInput)
                in.add(f.getNodeById(Integer.parseInt(s)));
        }

        //Get outbound connections
        Set<Node> out = new HashSet<>();
        System.out.print("List outbound node(s): ");
        input = scan.nextLine();
        if(input != "")
        {
            splitInput = input.split(" ");
            for(String s : splitInput)
                out.add(f.getNodeById(Integer.parseInt(s)));
        }

        FlowchartUtils.insertNode(f, new LabelNode(label), in, out);
    }

    public static void createContentNode(Flowchart f, Scanner scan)
    {
        String input = "";
        String[] splitInput = {};

        //Get label
        System.out.print("Enter label: ");
        String label = scan.nextLine();

        //Get Content
        System.out.print("Enter content: ");
        String content = scan.nextLine();

        //Get inbound connections
        Set<Node> in = new HashSet<>();
        System.out.print("List inbound node(s): ");
        input = scan.nextLine();
        if(input != "")
        {
            splitInput = input.split(" ");
            for(String s : splitInput)
                in.add(f.getNodeById(Integer.parseInt(s)));
        }

        //Get outbound connections
        Set<Node> out = new HashSet<>();
        System.out.print("List outbound node(s): ");
        input = scan.nextLine();
        if(input != "")
        {
            splitInput = input.split(" ");
            for(String s : splitInput)
                out.add(f.getNodeById(Integer.parseInt(s)));
        }

        FlowchartUtils.insertNode(f, new ContentNode(label, content), in, out);
    }

}