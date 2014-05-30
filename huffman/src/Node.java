/**
 * Refer https://en.wikipedia.org/wiki/Huffman_coding section Compression with the first algorithm using O(n log n) operation
 * If this node is left child then it is represented by "0" else "1"
 */
public class Node {
    /**
     * Points to its parent. A Root node will point to null.
     */
    private Node parent;

    /**
     * the probability of occurence of this node.
     */
    private double probability;

    /**
     * The character this node represent. For non leaf node this will be set to null;
     */
     private final Character symbol;
 
    /**
     * A flag to indicate if this node is left child or right child of the node.
     * We can avoid this by maintaing two separate pointer to left child or right child.
     * But this is sufficient for our purpose.
     * True value for sign represents this is right child of parent.
     * False value represents this to be left child of parent.
     * This flag is required while we are traversing from leaf to root in the tree and the
     * traversal will determine the encoding for that character.
     */
    private boolean sign;

    /**
     * Constrcutor for non leaf node.
     */
    public Node() {
        this.symbol = null;
        this.parent = null;
        this.probability = 0;
    }

    /**
     * A leaf node will always have symbol.
     */
    public Node(char symbol) {
        this.symbol = symbol;
        this.parent = null;
        this.probability = 0;
    }
    
    /**
     * Returns the parent node
     */
    public Node getParent() {
        return this.parent;
    }

    /**
     * Set the parent node
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }
    /**
     * The probability of this node.
     */
    public double getProbability() {
        return this.probability;
    }

    /**
     * Set the probability of this node
     */
    public void setProbability(double probability) {
        this.probability = probability;
    }

    /**
     * The symbol this node represents
     */
    public Character getSymbol() {
        return this.symbol; 
    }
        
    /**
     * the sign of this node(left or right child)
     */
    public  boolean getSign() {
        return this.sign;
    }
         
    /**
     * Is this left or right child of parent?
     */
    public void setSign(boolean sign) {
        this.sign = sign;
    }

    /**
     * Override the default equals method. Two nodes are equal only if they have same symbol
     */
    public boolean equals(Object object) {
        if(!(object instanceof Node))
            return false;
        Node node =(Node) object;
        if(node.getSymbol() == null || this.symbol == null)
            return false;
        return node.getSymbol().equals(this.symbol);
    }
}
