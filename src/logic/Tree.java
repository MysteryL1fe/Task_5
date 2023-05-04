package logic;

import exceptions.EmptyTreeException;
import exceptions.NullableNodeException;
import exceptions.WrongPathException;

import java.util.*;

public class Tree<T> {
    private Node<T> root = null;
    private final List<String[]> pairs = new ArrayList<>();

    public Tree() {}

    public Tree(T rootValue) {
        this.root = new Node<>(rootValue);
    }

    public void add(T value, String path) throws WrongPathException {
        if (path == null) throw new WrongPathException();
        if (path.isEmpty() && root != null) throw new WrongPathException();
        else if (path.isEmpty()) root = new Node<>(value);
        else {
            Node<T> parent = getNodeByPath(path.substring(0, path.length() - 1));
            char lastBranch = path.charAt(path.length() - 1);
            if (lastBranch == 'l' && parent.left == null) parent.left = new Node<>(value);
            else if (lastBranch == 'r' && parent.right == null) parent.right = new Node<>(value);
            else throw new WrongPathException();
        }
    }

    public void change(T value, String path) throws WrongPathException {
        getNodeByPath(path).setValue(value);
    }

    public void delete(String path) throws WrongPathException {
        if (path == null) throw new WrongPathException();
        if (path.isEmpty()) root = null;
        else {
            Node<T> parent = getNodeByPath(path.substring(0, path.length() - 1));
            char lastBranch = path.charAt(path.length() - 1);
            if (lastBranch == 'l') parent.left = null;
            else if (lastBranch == 'r') parent.right = null;
            else throw new WrongPathException();
        }
    }

    private Node<T> getNodeByPath(String path) throws WrongPathException {
        if (path == null) throw new WrongPathException();
        if (path.isEmpty()) return root;
        Node<T> node = root;
        for (Character c : path.toCharArray()) {
            if (c == 'l' && node.left != null) node = node.left;
            else if (c == 'r' && node.right != null) node = node.right;
            else throw new WrongPathException();
        }
        return node;
    }

    public List<String[]> findPairs() throws EmptyTreeException {
        if (root == null) throw new EmptyTreeException();
        try {
            findPairsForNode(root, "");
        } catch (NullableNodeException | WrongPathException e) {
            //something went wrong
            System.out.println(e);
        }
        return pairs;
    }

    private List<String> findPairsForNode(Node<T> node, String pathToNode)
            throws NullableNodeException, WrongPathException {
        if (node == null) throw new NullableNodeException();
        List<String> result = new ArrayList<>();
        List<String> leftChildPairs = null, rightChildPairs = null;
        if (node.left == null && node.right == null) {
            // Tree hasn't children => need to check pairs
            result = findPairsForSimpleNode(node, pathToNode);
        } else if ((node.left == null ||
                (leftChildPairs = findPairsForNode(node.left, pathToNode + "l")).size() != 0)
                && (node.right == null ||
                (rightChildPairs = findPairsForNode(node.right, pathToNode + "r")).size() != 0)) {
            // Both child of node has pairs => node may have child need to check
            result = findPairsForNodeWithChildren(node, pathToNode, leftChildPairs, rightChildPairs);
        }
        return result;
    }

    private List<String> findPairsForSimpleNode(Node<T> node, String pathToNode) throws NullableNodeException {
        if (node == null) throw new NullableNodeException();
        List<String> result = new ArrayList<>();
        PreorderNodePathIterator pathIterator = new PreorderNodePathIterator(node);
        // finding similar nodes
        while (pathIterator.hasNext()) {
            Node<T> curNode = pathIterator.next();
            // check if potential pair is simple node & it's value equals to our node's value
            if (curNode.left == null && curNode.right == null && curNode.value == node.value)
                result.add(pathIterator.nextPath());
        }
        // adding similar nodes to pairs
        for (String pathToPair : result) pairs.add(new String[] {pathToNode, pathToPair});
        return result;
    }

    private List<String> findPairsForNodeWithChildren(Node<T> node, String pathToNode,
                                                      List<String> leftChildPairs, List<String> rightChildPairs)
            throws NullableNodeException, WrongPathException {
        if (node == null) throw new NullableNodeException();
        List<String> result = new ArrayList<>();

        // finding pairs for node
        for (String leftChildPair : leftChildPairs) {
            for (String rightChildPair : rightChildPairs) {
                // if the pairs for the left and right child elements differ only in the last branching
                // then the parent element of the pairs can be a pair for our node if their values are equals
                String maybePair;
                if ((maybePair = leftChildPair.substring(0, leftChildPair.length() - 1))
                        .equals(rightChildPair.substring(0, rightChildPair.length() - 1))
                        && node.value.equals(getNodeByPath(maybePair).value)) {
                    result.add(maybePair);
                }
            }
        }

        // adding similar nodes to pairs
        for (String pathToPair : result) pairs.add(new String[]{pathToNode, pathToPair});
        return result;
    }

    protected static class Node<T> {
        private Node<T> left, right;
        private T value;

        public Node() {
            this(null, null, null);
        }

        public Node(T value) {
            this(value, null, null);
        }

        public Node(T value, Node<T> left, Node<T> right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }

        public Node<T> getLeft() {
            return left;
        }

        public void setLeft(Node<T> left) {
            this.left = left;
        }

        public Node<T> getRight() {
            return right;
        }

        public void setRight(Node<T> right) {
            this.right = right;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }

    private class PreorderNodeIterator implements Iterator<Node<T>> {
        private final Stack<Node<T>> stack;

        public PreorderNodeIterator() {
            stack = new Stack<>();
            if (root != null) stack.push(root);
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public Node<T> next() {
            Node<T> node = stack.pop();
            if (node.right != null) stack.push(node.right);
            if (node.left != null) stack.push(node.left);
            return node;
        }
    }

    private class PreorderNodePathIterator implements Iterator<Node<T>> {
        private final Stack<Node<T>> nodeStack;
        private final Stack<String> pathStack;
        private String lastPath;

        public PreorderNodePathIterator() {
            nodeStack = new Stack<>();
            pathStack = new Stack<>();
            if (root != null) {
                nodeStack.push(root);
                pathStack.push("");
            }
        }

        public PreorderNodePathIterator(Node<T> startNode) {
            this();
            Node<T> curNode = null;
            while (hasNext() && (curNode = next()) != startNode) {}
        }

        @Override
        public boolean hasNext() {
            return !nodeStack.isEmpty() && !pathStack.isEmpty();
        }

        @Override
        public Node<T> next() {
            Node<T> node = nodeStack.pop();
            lastPath = pathStack.pop();
            if (node.right != null) {
                nodeStack.push(node.right);
                pathStack.push(lastPath + "r");
            }
            if (node.left != null) {
                nodeStack.push(node.left);
                pathStack.push(lastPath + "l");
            }
            return node;
        }

        public String nextPath() {
            return lastPath;
        }
    }

    public class PreorderIterator implements Iterator<T> {
        Iterator<Node<T>> iterator = new PreorderNodeIterator();
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            return iterator.next().getValue();
        }
    }
}
