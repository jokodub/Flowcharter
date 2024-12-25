package com.jokodub.flowcharter.model.classes;

import java.util.Scanner;
import java.util.Set;

import com.jokodub.flowcharter.logic.Flowcharts;

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
                case "NN": //New Node
                    createNode(myChart, scan);
                    myChart.summary();
                    break;
                case "NL": //New Label
                    createLabelNode(myChart, scan);
                    myChart.summary();
                    break;
                case "NC": //New Content
                    createContentNode(myChart, scan);
                    myChart.summary();
                    break;
                case "L": //Link
                    myChart.link(getNode(myChart, scan), getNode(myChart, scan));
                    myChart.summary();
                    break;
                case "Q": //Quit
                    System.out.println("Goodbye!");
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
        System.out.println("NN: Add a Node");
        System.out.println("NL: Add a Label Node");
        System.out.println("NC: Add a Content Node");
        System.out.println(" L: Height-link two Nodes");
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

        Flowcharts.insertNode(f, new Node(), in, out);
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

        Flowcharts.insertNode(f, new LabelNode(label), in, out);
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

        Flowcharts.insertNode(f, new ContentNode(label, content), in, out);
    }

}