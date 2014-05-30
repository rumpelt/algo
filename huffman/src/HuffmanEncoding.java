import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.lang.Exception;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;


/**
 * This is implementation of Huffman Encoding as per https://en.wikipedia.org/wiki/Huffman_coding
 */
public class HuffmanEncoding {

    /**
     * The text stream which we will read in to create encoding dictionary
     */
    private String text; 

    /**
     * map from character to its corresponding encoding computed using above text field.
     */
    private Map<Character, String> encodingDict; 

    /**
     * Construct a huffman encoding dictionary from text
     */
    public HuffmanEncoding(String text) {
        this.text = text; 
        this.encodingDict  = new HashMap<Character, String>();
        this.createEncodingDict();   // creates the encoding dictionary
    }

    /**
     * Returns the text from which encoding dictionary was created.
     */
    public String getText() {
        return this.text;
    }

    
    /**
     * Given a leaf node, we traverse the binary tree upwards to find the encoding for the leaf node
     */
    public String getEncoding(Node leafNode) {
        if(leafNode.getSymbol() == null)
            return null; // this is not a leaf node.. A leaf node will always have a symbol
        StringBuilder encoding = new StringBuilder();
        Node node = leafNode;
        while(node != null) {
            Node parent = node.getParent();
            if(parent != null)
                encoding.insert(0, node.getSign() ? "1" : "0"); // True sign mean right child and 0 mean left child, insert operation is always at offset 0 because we are prepending.
            node = node.getParent(); // move to parent
        }
        return encoding.toString(); 
    }

    private PriorityQueue<Node> makePriorityQueue() {
        PriorityQueue<Node> pq = new PriorityQueue<Node>(1, this.getProbabilityComparator());
        HashMap<Character, Integer> freqCount = new HashMap<Character, Integer>();
        for(int index = 0; index < this.text.length(); index++) {
            char charT = this.text.charAt(index);
            Integer freq = freqCount.get(charT);
            if(freq == null) {
                freqCount.put(charT, 1);
            }
            else {
                freqCount.put(charT, freq + 1);
            }
        }

        int textLength = this.text.length();
        for(Map.Entry<Character,Integer> entry : freqCount.entrySet()) {
            Node symbolNode = new Node(entry.getKey());
            symbolNode.setProbability((entry.getValue() * 1.0)/ textLength);
            pq.offer(symbolNode);
        }
        return pq;
    }

    /**
     * routine which constructs the encoding dictionary
     * This O(n log n) operation due to creation of priority queue
     */
    public void createEncodingDict() {
        PriorityQueue<Node> pq = this.makePriorityQueue(); // returns a PriorityQueue of nodes with node with lowest probability at the head of queue
        List<Node> leafNodes = new ArrayList<Node>(); // List of leaf nodes. We will add leaf nodes as we construct the tree
        this.makeEncodingTree(pq, leafNodes); // Create the huffman binary tree
        for(Node leaf : leafNodes) {
            this.encodingDict.put(leaf.getSymbol(), this.getEncoding(leaf)); // populate the encoding dictionary
        } 
    }

    /**
     * Creates the huffman binary tree as per https://en.wikipedia.org/wiki/Huffman_coding
     * See section "Basic Technique" and subsection "Compression". This is first algo in that section
     * nodeQ : A priority queue of leaf nodes sorted on probability of nodes
     * leafNodes: This will contain point to all leaf nodes from which we can traverse back to the root.
     */   
    private void makeEncodingTree(PriorityQueue<Node> nodeQ, List<Node> leafNodes) {
        // Following is fringe case when we have only one symbol in the queue. This logic is separate to avoid cluttering the main code.
        if(nodeQ.size() == 1) {
            Node node = nodeQ.poll();
            node.setSign(false);
            Node root = new Node();
            node.setParent(root);
            leafNodes.add(node);
            return;
        }

        while(nodeQ.size() > 1) {
            Node left = nodeQ.poll();
            if(left.getSymbol() != null) {
                leafNodes.add(left);
            }
            left.setSign(false); // left child
            
            Node right = nodeQ.poll();
            if(right.getSymbol() != null) {
                leafNodes.add(right);
            }
            right.setSign(true); // right child

            Node parent = new Node(); // create a parent node
            parent.setProbability(left.getProbability() + right.getProbability());
            left.setParent(parent);
            right.setParent(parent);

            nodeQ.offer(parent); // insert to priority queue.
               
        }
    }

    /**
     * Return a Comparator on Node which will compare on probability values of node.
     */
    public Comparator<Node> getProbabilityComparator() {
        return new Comparator<Node>() {
            public int compare(Node fNode, Node sNode) {
                return fNode.getProbability() < sNode.getProbability() ? -1 : 1; 
            }
        };
    }

    /**
     * Given a plain text string returns the huffman encoding for it.
     * it refers the encodingDict for encoding of each character
     */
    public String getHuffmanEncodedString(String plainText) throws Exception{
        StringBuilder sb = new StringBuilder();
        for(int index = 0; index < plainText.length(); index++) {
            char charT = plainText.charAt(index);
            String code = this.encodingDict.get(charT);
            if(code == null)
                throw new Exception("Encoding not found for character "+charT+ " ");
            sb.append(code);
        } 
        return sb.toString();
    }

    /**
     * Prints the encoding dictionary we created
     */
    public void printEncodingDict() {
        for(Map.Entry<Character,String> entry : this.encodingDict.entrySet()) {
            System.out.println(entry.getKey()+ " : "+ entry.getValue()); 
        } 
    }

    public static boolean testEncoding(HuffmanEncoding hfe, String input, String expected) {
        try {
            return hfe.getHuffmanEncodedString(input).equals(expected);
        } catch(Exception e) {
            System.err.print(e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) throws IOException, Exception, FileNotFoundException {
        InputStreamReader isr  = new InputStreamReader(System.in);
        int symbol;
        StringBuilder sb = new StringBuilder();
        /**
         * Read the stream form console untill end of file. Encoding dictionary is created from terminal
         */
        while((symbol = isr.read()) != -1) {
            if(!Character.isSpaceChar(symbol)) { // we ignore the space character in input
                sb.appendCodePoint(symbol);
            }
        }

        HuffmanEncoding hfe = new HuffmanEncoding(sb.toString()); // Constructe the Huffman Encoding Dictionary from the text stream
        System.out.println("Huffman Encoding for input stream");
        System.out.println(hfe.getHuffmanEncodedString(hfe.getText()));
        System.out.println("Encoding Dictionary");
        hfe.printEncodingDict();

        /**
         * This is simple test case when the input string is argument 1 and expected encoding is argument 2
         */
        if (args.length == 2) {
            String expected = args[1];
            String got = hfe.getHuffmanEncodedString(args[0]);
            System.out.println("InputString :" + args[0] + " ExpectedCode: " + expected + " Got : "+got + " TestResult: "+ expected.equals(got));
        }
 
        /**
         * This is when you want to have bunch of test cases in a file
         * Each line in the file will input string and expected encoding.
         */
        if(args.length == 1) {
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            String line = null;
            while((line = br.readLine()) != null) {
                String[] test  = line.split("\\s+");
                String expected = test[1].trim();
                String got = hfe.getHuffmanEncodedString(test[0].trim());
                System.out.println("InputString :" + test[0] + " ExpectedCode: " + expected + " Got : "+got + " TestResult: "+ expected.equals(got));
            }
        }
    }    

}