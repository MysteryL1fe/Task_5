package logic;

import exceptions.EmptyTreeException;
import exceptions.NullableNodeException;
import exceptions.WrongPathException;

import java.util.*;

public class Tree<T> {
    private Node<T> root = null;
    private List<String[]> pairs = new ArrayList<>();

    public Tree() {}

    public Tree(T rootValue) {
        this.root = new Node<>(rootValue);
    }

    public void add(T value, String path) throws WrongPathException {

    }

    public List<String[]> findPairs() throws EmptyTreeException {
        if (root == null) throw new EmptyTreeException();
        try {
            findPairsForNode(root, "");
        } catch (NullableNodeException e) {
            System.out.println(e);
        }
        return pairs;
    }

    private List<String> findPairsForNode(Node<T> node, String pathToNode) throws NullableNodeException {
        if (node == null) throw new NullableNodeException();
        List<String> result = new ArrayList<>();
        List<String> leftChildPairs = null, rightChildPairs = null;
        if (node.left == null && node.right == null) {
            //Tree hasn't children => need to check pairs
            result = findPairsForSimpleNode(node, pathToNode);
        } else if ((node.left == null ||
                (leftChildPairs = findPairsForNode(node.left, pathToNode + "l")).size() != 0)
                && (node.right == null ||
                (rightChildPairs = findPairsForNode(node.right, pathToNode + "r")).size() != 0)) {
            //Both child of node has pairs => node may have child need to check
            findPairsForNodeWithChildren(node, pathToNode, leftChildPairs, rightChildPairs);
        }
        return result;
    }

    private List<String> findPairsForSimpleNode(Node<T> node, String pathToNode) throws NullableNodeException {
        if (node == null) throw new NullableNodeException();
        List<String> result = new ArrayList<>();
        String pairNode;


        for (String pathToPair : result) pairs.add(new String[] {pathToNode, pathToPair});
        return result;
    }

    private List<String> findPairsForNodeWithChildren(Node<T> node, String pathToNode, List<String> leftChildPairs,
                                                      List<String> rightChildPairs) throws NullableNodeException {
        if (node == null) throw new NullableNodeException();
        List<String> result = new ArrayList<>();

        for (String pathToPair : result) pairs.add(new String[]{pathToNode, pathToPair});
        return result;
    }

    protected Iterator<Node<T>> nodeIterator() {
        return new PreorderNodeIterator();
    }

    public Iterator<T> iterator() {
        return new PreorderIterator();
    }

    protected class Node<T> {
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

    private class PreorderNodePathIterator implements Iterator<String> {
        private final Stack<Node<T>> nodeStack;
        //ToDo code path iterator
        private final String path;

        public PreorderNodePathIterator() {
            nodeStack = new Stack<>();
            path = "";
            if (root != null) nodeStack.push(root);
        }

        @Override
        public boolean hasNext() {
            return !nodeStack.isEmpty();
        }

        @Override
        public String next() {
            Node<T> node = nodeStack.pop();
            if (node.right != null) {
                nodeStack.push(node.right);
                path+=
            }
            if (node.left != null) stack.push(node.left);
            return stringStack.pop();
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
