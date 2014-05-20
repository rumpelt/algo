import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
/**
 * author : ashwani
 * Programming challenge on Yodle : http://www.yodlecareers.com/puzzles/triangle.html
 */  
public class TriangleMaxSum {
    
    /**
     * An integer value in the triangle
     */
    public static class Node {
        int value; // value this node represents;
        long maxSum;// keeps the maximum sum Value from this path onward
        public Node(int value) {
            this.value  = value;
            this.maxSum = -1;
        }

        public Node() {
            this.maxSum = -1;
        }
    }

    /**
     * Recursivel compute the maximum sum. 
     * triangle : The triangle with values
     * node : the node for which we are calculating the maximum sum
     * triangleIndex: The row representing node
     * index : the index of node in the row
     */
    public static long maxSum(List<Node[]> triangle, Node node, int triangleIndex, int index) {
        if(triangleIndex == triangle.size() - 1)  { // We are at the last row of the triangle
            node.maxSum = node.value; // the maximum sum at this point is the value itself 
            return node.maxSum; // 
        }

        Node[] values =  triangle.get(triangleIndex + 1); // Get the next row

        int firstAdjIndex = index; // the first adjacent index
        Node first = values[firstAdjIndex]; 
        long adjValueOne;
        if(first.maxSum != -1) { // We have already calculated the maximum sum from this point onward
            adjValueOne = first.maxSum;
        } 
        else {
            adjValueOne = maxSum(triangle, first, triangleIndex + 1, index); // recurse to find the maximum sum from this point
        }

        int secondAdjIndex = index + 1; // the second adj index
        Node second = values[secondAdjIndex];
        long adjValueSec;
        if(second.maxSum != -1) {
            adjValueSec = second.maxSum;
        }
        else {
            adjValueSec = maxSum(triangle, second, triangleIndex + 1, index + 1);
        }

        // following updates the maximum sum we have found so far.
        if(adjValueOne > adjValueSec) {
            node.maxSum = node.value + adjValueOne;
        }
        else
            node.maxSum = node.value + adjValueSec;

        return node.maxSum;            
    }

    /**
     * The first argument should be the triangle.txt file name
     */
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        List<Node[]> triangle = new ArrayList<Node[]>();
        String line = null;
        // folloiwng while loop constructs the triangle
        while((line = br.readLine()) != null) {
            String[] stringValues = line.split("\\s+");
            Node[] values  = new Node[stringValues.length];
            for(int idx = 0; idx < values.length; idx++) {
                values[idx] = new Node(Integer.parseInt(stringValues[idx].trim()));
            }
            triangle.add(values);
        }

        long sum = maxSum(triangle, triangle.get(0)[0], 0, 0); // calculate the maximum sum
        System.out.println(sum);
        br.close();
    }
}