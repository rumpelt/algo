import java.util.Comparator;
import java.util.Arrays;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.lang.NumberFormatException;
/**
 * A simple KD tree constrcution and nearest negibor search.
 * First input should input file containing geo codes in integer format. A point per line as shown following
 * 0 0
 * 1 2
 * 3 5
 * Second argument should be x co ordinate of  the point to search. 
 * third argument should be y coordinate of point to search
 * It will print the nearest neighbor form geo codes in file.
 * Implemented as per https://en.wikipedia.org/wiki/Kd-tree
 */
public class KDTree {
    Node root;

    KDTree() {
        root = null;
    }

    DepthComparator dc = new DepthComparator();

    public static class DepthComparator  implements Comparator<Node>{

        int depth=0;
        public void setDepth(int depth) {
            this.depth = depth;
        }
        public int compare(Node nodeOne, Node nodeTwo) {
            if((this.depth % 2) == 0) {
                if(nodeOne.x < nodeTwo.x) {
                    return -1;
                }
                else if (nodeOne.x == nodeTwo.x) 
                    return 0;
                else
                    return 1;
            } 
            else {
                if(nodeOne.y < nodeTwo.y)
                    return -1;
                else if(nodeOne.y == nodeTwo.y)
                    return 0;
                else
                    return 1;
            }
        }        
    };

    public void inOrderTraversal(Node node) {
        if(node == null)
            return;
        inOrderTraversal(node.left);
        System.out.println("Depth "+node.depth + " " + node.x + " "+ node.y);
        inOrderTraversal(node.right);
    }


    public Node findLeaf(Node head, Node search) {
        if(head.left == null && head.right == null)
            return head;
        Node selectNode = null;
        if((head.depth % 2) == 0) {
            if(search.x < head.x) 
                selectNode = (head.left != null)? head.left : head.right;
            else 
                selectNode = (head.right != null)? head.right : head.left;            
        }
        else {
            if(search.y < head.y) 
                selectNode = (head.left != null)? head.left : head.right; 
            else
               selectNode = (head.right != null)? head.right : head.left;
        }
        
        return findLeaf(selectNode, search);
    }

    public double distance(Node one, Node two) {
        return Math.sqrt(Math.pow(one.x - two.x, 2) + Math.pow(one.y - two.y, 2));
    }

    public double distanceAxis(Node axis, Node node) {
        if((axis.depth % 2) == 0)
            return Math.abs(axis.x - node.x);
        else
            return Math.abs(axis.y - node.y);
    }

    public Node sibling(Node parent, Node child) {
        if(parent.left == child)
            return parent.right;
        else if(parent.right == child)
            return parent.left;
        else
            return null; 
    }

    public Node nearestNeighbor(Node current, Node search, Node best, double minDistance,  boolean dfs , Set<Node> visited) {
        if(current ==  null) {
            return best; // we have reached back to head
        }
        
        if(dfs) {
            current = this.findLeaf(current, search);
        }
     
        if(best == null) {
            best = current;
            minDistance = this.distance(current, search);  
        }
        
        double distanceCurr = distance(current, search);
        //        System.out.println(current+ " "+distanceCurr);
        if(distanceCurr < minDistance) {
            best = current;
            minDistance = distanceCurr;
            //           System.out.println(best+ " " + minDistance);
        }
        
        double axisDistance = distanceAxis(current, search);
        
        if(axisDistance < minDistance) {
            Node unvisited = this.getNotVisitedNode(visited, current);
            if(unvisited != null) {
                return nearestNeighbor(unvisited, search, best, minDistance, true, visited); 
            }
        }

        visited.add(current);
        Node parent = current.parent;
        return nearestNeighbor(parent, search, best, minDistance, false, visited);
    }
    
    public Node getNotVisitedNode(Set<Node> visited, Node node) {
        if(!visited.contains(node.right))
            return node.right;
        if(!visited.contains(node.left))
            return node.left;
        return null;
    }

    public Node constructKD(Node[] nodes, Node parent,  int depth) {
       
        this.dc.setDepth(depth);
        Arrays.sort(nodes, dc);
        int median = (nodes.length) / 2 ;
        Node medNode = nodes[median];
        medNode.depth = depth;
        medNode.parent = parent;

        if(parent == null) {
            this.root = medNode;
        } 

        if(nodes.length == 1) {
            medNode.left = null;
            medNode.right = null;
            return medNode;
        }

        Node[] leftNodes;
        
        if(median > 0)
            leftNodes  = Arrays.copyOfRange(nodes, 0 , median);
        else
            leftNodes = null;

        Node[] rightNodes = null;
        if(median < nodes.length - 1)
            rightNodes = Arrays.copyOfRange(nodes, median + 1 , nodes.length);
         
        
        //System.out.println("Total node "+ nodes.length + " left "+  leftNodes.length : 0 + " right " + rightNodes != null? rightNodes.length:0 + " " +median);

        if(leftNodes != null) {
            //System.out.println("total " + nodes.length +" lefNodes "+leftNodes.length);
            medNode.left = constructKD(leftNodes, medNode, depth + 1);
        }

        if(rightNodes != null) {
            //System.out.println(" total" + nodes.length +" rightNodes "+ rightNodes.length);
            medNode.right = constructKD(rightNodes, medNode, depth + 1);
        }
        return medNode;
    }

    public static class Node {
        int x;
        int y;
        int depth;
        Node parent;
        Node left;
        Node right;
        boolean mark = false;
        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Node() {
        }
        
        public String toString() {
            return "("+this.x+","+this.y+")";
        }
        public boolean equals(Node two) {
            return this.x == two.x && this.y == two.y; 
        }

    };

    public static void main(String[] args)  throws IOException, NumberFormatException {
        FileReader fr = new FileReader(args[0]);
        Node searchNode  = new Node(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        BufferedReader br = new BufferedReader(fr);
        ArrayList<Node> points = new ArrayList<Node>();
        String line;
        while((line = br.readLine()) != null) {
            String[] split = line.split("\\s+");
            Node node = new Node();
            node.x = Integer.parseInt(split[0]);
            node.y = Integer.parseInt(split[1]); 
            points.add(node);
        }

        KDTree tree = new KDTree();
        Node[] nodes = points.toArray(new Node[0]);
        tree.constructKD(nodes, null, 0);
        //       tree.inOrderTraversal(tree.root);

        Node result = tree.nearestNeighbor(tree.root,searchNode, null, 999999, true, new HashSet<Node>());
        System.out.println(result);
        br.close();    
    }
};