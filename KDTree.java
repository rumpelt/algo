import java.util.Comparator;
import java.util.Arrays;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.NumberFormatException;

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
    };

    public static void main(String[] args)  throws IOException, NumberFormatException {
        FileReader fr = new FileReader(args[0]);
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
        tree.inOrderTraversal(tree.root);
        br.close();    
    }
};