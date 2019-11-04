/*************************************************************************
*  A directed graph implemented using adjacency lists.
*
*************************************************************************/

import java.util.*;
import java.io.*;

public class AirlineSystem {
  private String [] cityNames;
  private Digraph G;
  /**
  * Test client.
  */
  public static void main(String[] args) throws IOException {
    AirlineSystem airline = new AirlineSystem();
    airline.readGraph();
    airline.printGraph();
  }

  public void readGraph() throws IOException {
    Scanner inScan = new Scanner(System.in);
    System.out.println("Please enter graph filename:");
    String fileName = inScan.nextLine();
    Scanner fileScan = new Scanner(new FileInputStream(fileName));
    //TODO: Complete this method to read the graph from the input file
    //      (e.g., data1.txt)
    int number = 0;
    if(fileScan.hasNext()) {
        number = Integer.parseInt(fileScan.nextLine());
    } else {
        System.out.println("Error: First line must be an integer.");
        System.exit(1);
    }
    cityNames = new String[number];
    int i;
    for(i = 0; fileScan.hasNext() && i < number; ++i) {
        cityNames[i] = fileScan.nextLine();
    }
    if(i != number) {
        System.out.println("Was unable to read " + number + " city names from the file.");
        System.exit(1);
    }
    G = new Digraph(number);
    for(i = 0; fileScan.hasNext(); ++i) {
        String line = fileScan.nextLine();
        String[] nums = line.split(" ");
        if(nums.length != 2) {
            System.out.println("Line [" + line + "] does not contain two values delimmeted by a space.");
            System.exit(1);
        }
        DirectedEdge new_edge = new DirectedEdge(Integer.parseInt(nums[0]) - 1, Integer.parseInt(nums[1]) - 1);
        G.addEdge(new_edge);
    }
    fileScan.close();
  }

  public void printGraph() {
    //TODO: Complete this method to print the graph as in the expected output
    //      files (e.g., output1.txt)
    for(int i = 0; i < cityNames.length; ++i) {
        System.out.print(cityNames[i] + ":");
        for(DirectedEdge edge : G.adj(i)) {
            System.out.print(" " + cityNames[edge.to()]);
        }
        System.out.println();
    }
  }

  /**
  *  The <tt>Digraph</tt> class represents an directed graph of vertices
  *  named 0 through v-1. It supports the following operations: add an edge to
  *  the graph, iterate over all of edges leaving a vertex.Self-loops are
  *  permitted.
  */
  private class Digraph {
    private final int v;
    private int e;
    private LinkedList<DirectedEdge>[] adj;

    /**
    * Create an empty digraph with v vertices.
    */
    public Digraph(int v) {
      if (v < 0) throw new RuntimeException("Number of vertices must be nonnegative");
      this.v = v;
      this.e = 0;
      @SuppressWarnings("unchecked")
      LinkedList<DirectedEdge>[] temp =
      (LinkedList<DirectedEdge>[]) new LinkedList[v];
      adj = temp;
      for (int i = 0; i < v; i++)
      adj[i] = new LinkedList<DirectedEdge>();
    }

    /**
    * Add the edge e to this digraph.
    */
    public void addEdge(DirectedEdge edge) {
      int from = edge.from();
      adj[from].add(edge);
      e++;
    }


    /**
    * Return the edges leaving vertex v as an Iterable.
    * To iterate over the edges leaving vertex v, use foreach notation:
    * <tt>for (DirectedEdge e : graph.adj(v))</tt>.
    */
    public Iterable<DirectedEdge> adj(int v) {
      return adj[v];
    }
  }

  /**
  *  The <tt>DirectedEdge</tt> class represents an edge in an directed graph.
  */

  private class DirectedEdge {
    private final int v;
    private final int w;
    /**
    * Create a directed edge from v to w with given weight.
    */
    public DirectedEdge(int v, int w) {
      this.v = v;
      this.w = w;
    }

    public int from(){
      return v;
    }

    public int to(){
      return w;
    }
  }
}
