import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * A fixed size priority queue using binary heap.
 * Fixed size priority queue will always contain fixed set of high priority objects.
 * Currently this is not generic queue (only integer values). But can be easily converted to by make Node class generic.
 * Compile javac PriorityQueueSorted
 * Can't explain the code at this point. U can write to me thought. 
 */
public class PriorityQueueSorted {
    Node[] queue;
    int  freeIndex;    
    int leafIndex;
    int size;
    public PriorityQueueSorted(int size) {
        this.queue = new Node[size];
        this.size = this.queue.length;
        this.freeIndex = 0;
        this.leafIndex = this.queue.length / 2;
    }
    
    public int leafIndex() {
        return (this.freeIndex)/ 2;
    }

    public Node peek() {
        Node node = this.queue[0];
        return node;
    }

    public int findMaxLeafIndex() {
        int leafIndex = this.leafIndex();
        Node maxNode = this.queue[leafIndex];
        int maxIndex = leafIndex;
        for(int index = leafIndex; index < this.freeIndex; index++) {
            Node node  = this.queue[index];
            if(node.value > maxNode.value) {
                maxNode = node;
                maxIndex = index;
            }
        }
        return maxIndex;
    }

    public void bubbleDown(int index) {
        if(index >= ((this.freeIndex)/ 2))
            return;
        Node node = this.queue[index];
        int minIndex = index;
        Node minNode = node;
        for(int start = ((index + 1)*2 - 1); start < this.freeIndex; start++) {
            Node cNode = this.queue[start];
            if(cNode.value < minNode.value) {
                minIndex = start;
                minNode = cNode;
            }
        }
        if(minIndex != index) {
            this.queue[index] = this.queue[minIndex];
            this.queue[minIndex] = node;
            this.bubbleDown(minIndex);
        }
    }

    public void bubbleUp(int index) {
        if(index < 1) 
            return;
        
        //index = index + 1;  // converting to index from 1  
        int parentIndex = ((index + 1) /2) - 1;
        Node parent = this.queue[parentIndex];
        Node node = this.queue[index];
        //        System.out.println(parentIndex+ " "  + index + " " + node + " " + parent );
        if(parent.value > node.value) {
            this.queue[parentIndex] = node;
            this.queue[index] = parent;
            this.bubbleUp(parentIndex);
        }
    }

    public Node insertTop(Node node) {
        Node min = this.queue[0];
        this.queue[0] = node;
        return min;
    }

    public void addNode(Node node) {
        if(this.freeIndex < this.queue.length) {
            this.queue[this.freeIndex] = node;
            //  System.out.println("index " + this.freeIndex+ " " + this.queue[this.freeIndex].value);
            this.bubbleUp(this.freeIndex);
            this.freeIndex++;
        }
        else {
            Node min = this.peek();
            if(min.value > node.value) {
                node = this.insertTop(node);              
            }
            int maxIndex = this.findMaxLeafIndex();
            Node max = this.queue[maxIndex];
            if(max.value > node.value) {
                this.queue[maxIndex] = node;
                this.bubbleUp(maxIndex);
            }
        }
    }

    public Node extractMin() {
        if(this.freeIndex == 0)
            return null;
        Node node = this.queue[0];
        this.queue[0] = this.queue[this.freeIndex -1];
        this.freeIndex = this.freeIndex - 1;
        this.bubbleDown(0); 
        return node;
    }

    public void addNodeValue(String id , int value) {
        Node node = new Node(id, value);
        this.addNode(node);
    }

    public  static class Node {
        String nodeId;
        int value;
        Node(String id, int value) {
            this.nodeId = id;
            this.value = value;
        }
    };
    
    public static void main(String args[]) throws IOException{
        InputStreamReader in = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(in);
        String line = null;
        PriorityQueueSorted pq = new PriorityQueueSorted(5);
        int count = 1;
        while((line = br.readLine()) != null) {
            String[] values = line.split("\\s+");
            int priority = Integer.parseInt(values[1]);
            if( (count % 3) == 0) {
                Node node = pq.extractMin();
                System.out.println("Online modification " + node.nodeId + " " + node.value);
            }
            pq.addNodeValue(values[0], priority);
            count++;
        }
        Node node = pq.extractMin();
        while(node != null) {
            System.out.println(node.nodeId + " " + node.value);
            node = pq.extractMin();
            
        }
         //System.out.println(pq.extractMin().value); 
    }
}