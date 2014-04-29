import java.util.*;
/**
 * A simple trie implementation by me
 */
public class Trie {

    Node rootNd = new Node('?');

    /**
     * A terminal node have "$" as symbol;
     * The root node has "?" symbol
     */
    public static class Node {
        int startPos = -1;
        String value = null;
        char chart; // a node can have character or collapsed string as valuel
        Node[] nextNode; // node this node points to 
        List<Node> nodeList; // node this node points to, same as above but this will be easy to work with
        public Node(String value) {
            this.value = value;
            this.nodeList = new ArrayList<Node>();
        }

        public Node(char chart) {
            this.chart = chart; 
            this.nodeList = new ArrayList<Node>();
        }
     
        public void setStartPos(int pos) {
            this.startPos = pos;
        }

        public int getStartPos() {
            return this.startPos;
        }

        public List<Node> getNodeList() {
            return this.nodeList;
        }

        public boolean same(char chart) {
            return this.chart == chart;
        }
        
        public String toString() {
            if(this.value != null)
                return this.value;
            else
                return this.chart + "";
        }
    }; 

    /**
     * A very bad trie.
     */
    public void printTrie() {
        Node node = this.rootNd;
        Queue<Node> nodeQ = new LinkedList<Node>();
        while (node != null) {
            System.out.println(node);
            nodeQ.addAll(node.getNodeList());
            node = nodeQ.poll();
        }
    }    

    public void printAllString(Node node, String prevString) {
        if(node.same('$')) {
            System.out.println(prevString);
        }
        for(Node cnode : node.getNodeList()) {
            String nextString = prevString + cnode.toString(); 
            this.printAllString(cnode, nextString);
        }
    }

    public void autoComplete(String string) {
        Node node = this.rootNd;
        for(int index=0; index < string.length(); index++) {
            char chart = string.charAt(index);
            Node next = null;
            for(Node cnode : node.getNodeList()) {
                if(cnode.same(chart))
                    next = cnode; 
            }
            if(next == null)
                return;
            node = next;
        }

        this.printAllString(node, string);   
    }

    public void findAllStartPos(Node node, List<Integer> positions) {
        if(node.same('$')) {
            positions.add(node.getStartPos());
        }

        for(Node cnode : node.getNodeList()) {
            this.findAllStartPos(cnode, positions);
        }
    }

  
    /**
     * find as string in depth first manner
     */
    public Integer[] findSubstring(String string) {

        Node  node = this.rootNd;
        for(int index=0; index < string.length(); index++) {
            char chart = string.charAt(index); 
            Node found = null;
            for(Node cnode : node.getNodeList()) {
                if(cnode.same(chart))
                    found = cnode; 
            }
            if(found == null) {
                return null; // Could not find the substring so return immemdiately
            }
            node = found;
        }
        //       System.out.println("found substring");
        
        List<Integer> positions = new ArrayList<Integer>();
        this.findAllStartPos(node, positions);
        return positions.toArray(new Integer[positions.size()]);
    }
    
    public void insertString(String string, int startPos) {
        List<Node> searchList = this.rootNd.getNodeList();
        Node lastNode = null;
        for(int index = 0 ; index < string.length(); index++) {
            char chart = string.charAt(index);
            Node found = null;
            for(Node node : searchList) {
                if (node.same(chart))
                    found = node;
            }

            if(found != null) {
                searchList = found.getNodeList();
                lastNode = found;
            }
            else {
                lastNode = new Node(chart);
                searchList.add(lastNode);
                searchList = lastNode.getNodeList();
            }
        }

        if(lastNode != null && string.length() > 0) { // We will add terminal node each time we encounter a string to insert
            Node terminal = new Node('$');
            terminal.setStartPos(startPos);
            lastNode.getNodeList().add(terminal);
        }
    }
  
    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insertString("teststring", 0);
        trie.insertString("eststring", 1);
        trie.insertString("ststring", 2);
        trie.insertString("tstring", 3);
        trie.insertString("string", 4);
        trie.insertString("tring", 5);
        trie.insertString("ring", 6);
        trie.insertString("ing", 7); 
        trie.insertString("ng", 8);
        trie.insertString("g", 9);
        trie.autoComplete("t");
        System.out.println(trie.findSubstring("t"));
    }
};