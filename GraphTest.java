import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.NumberFormatException;
import java.util.Queue;
import java.util.LinkedList;
/**
 * Constructs a graph using adjacency list from a simple graph file.
 * The firt line in the graph should contain number of vertices,number of edges and "d/u " for directed/undirected graph
 * Each line afterwards represent  an edge. 
 * An e.g
 * 3 3  u
 * 0 1
 * 0 2
 * 1 2
 * construct the graph, prints graph, breadth first search
 * To compile : javac GraphTest.java
 * To run : cat <graph file> | java GraphTest
 */
public class GraphTest {
    Integer[] degree;
    EdgeNode[] edgeList;
    int vertices;
    int edges;
    boolean directed;

    public static class EdgeNode {
        int id;
        int weight;
        EdgeNode next;
        public EdgeNode(int id) {
            this.id = id;
            this.next = null;
        }
    }

    /**
     * prints the graph
     */
    public void printGraph() {
        for(int index = 0; index < this.edgeList.length; index++) {
            EdgeNode en = this.edgeList[index];
            System.out.print("Start node: " + en.id);
            EdgeNode next = en.next;
            while(next != null) {
                System.out.print(" "+next.id);
                next = next.next;
            }
            System.out.println();
        }
    }



    public void bfs() {
        Boolean[] discovered = new Boolean[this.edgeList.length];
        for(int index=0; index < discovered.length; index++)
            discovered[index] = false;

        EdgeNode ed = this.edgeList[0];
        if(ed == null)
            return;
        discovered[ed.id] = true;
        Queue<EdgeNode> q = new LinkedList<EdgeNode>();
        q.offer(ed);
        EdgeNode head = null;
        while((head = q.poll()) != null) {
            int id = head.id;
            System.out.println(id);
            EdgeNode child = head.next;
            while(child != null) {
                if(!discovered[child.id]) {
                    discovered[child.id]  = true;
                    q.offer(this.edgeList[child.id]);
                }
                child = child.next;
            }
        }
    }

    public void init(int vertices, int edges, boolean directed) {
        this.vertices = vertices;
        this.edgeList = new EdgeNode[this.vertices];
        for(int index = 0; index < edgeList.length ; index++)
            this.edgeList[index] = null;
        this.degree = new Integer[this.vertices];
        for(int index=0; index < this.degree.length; index++)
            this.degree[index] = 0;
        this.edges = edges;
        this.directed = directed;
    }

    /**
     * Does not if en edge is already present. this can screw up 
     */
    public void addEdge(int start, int end, int weight, boolean directed) {
        EdgeNode source = edgeList[start];
        if(source == null) {
            source = new EdgeNode(start);
            this.edgeList[start] = source;
        }
        EdgeNode dest = new EdgeNode(end);
        dest.weight = weight;
        
        EdgeNode edge = source.next;
        while(edge != null) {
            if(edge.id == end)  {
                System.out.println("edge alread present");
                return;
            }
            edge = edge.next;
        }

        dest.next = source.next;
        source.next = dest;
        this.degree[start] = this.degree[start] + 1;
        if(!directed)
            this.addEdge(end, start, weight, true); // true, so that it doesn't go into infinite loop.
    }

    public static void main(String[] args) throws IOException, NumberFormatException {
        InputStreamReader ir = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(ir);
        String line = br.readLine();
        String[] splitter = line.split("\\s+");

        int vertices = Integer.parseInt(splitter[0]);
        int edges  = Integer.parseInt(splitter[1]);
        boolean directed = splitter[2].equalsIgnoreCase("d");
        GraphTest gt = new GraphTest();
        gt.init(vertices, edges, directed);
        while((line = br.readLine()) != null) {
            splitter = line.split("\\s+");
            gt.addEdge(Integer.parseInt(splitter[0]), Integer.parseInt(splitter[1]), 1, false);
        }
        gt.printGraph();
        gt.bfs();
        br.close();   
    }
}