package logic;

import exceptions.EmptyTreeException;
import exceptions.NullableNodeException;
import exceptions.WrongPathException;

import java.util.*;

public class Tree<T> implements Iterable<T> {
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
            Node<T> parent = getNode(path.substring(0, path.length() - 1));
            char lastBranch = path.charAt(path.length() - 1);
            if (lastBranch == 'l' && parent.getLeft() == null) parent.setLeft(new Node<>(value));
            else if (lastBranch == 'r' && parent.getRight() == null) parent.setRight(new Node<>(value));
            else throw new WrongPathException();
        }
    }

    public void change(T value, String path) throws WrongPathException {
        getNode(path).setValue(value);
    }

    public void delete(String path) throws WrongPathException {
        if (path == null) throw new WrongPathException();
        if (path.isEmpty()) root = null;
        else {
            Node<T> parent = getNode(path.substring(0, path.length() - 1));
            char lastBranch = path.charAt(path.length() - 1);
            if (lastBranch == 'l') parent.setLeft(null);
            else if (lastBranch == 'r') parent.setRight(null);
            else throw new WrongPathException();
        }
    }

    public T get(String path) throws WrongPathException {
        return getNode(path).getValue();
    }

    private Node<T> getNode(String path) throws WrongPathException {
        if (path == null) throw new WrongPathException();
        if (path.isEmpty()) return root;
        Node<T> node = root;
        for (Character c : path.toCharArray()) {
            if (c == 'l' && node.getLeft() != null) node = node.getLeft();
            else if (c == 'r' && node.getRight() != null) node = node.getRight();
            else throw new WrongPathException();
        }
        return node;
    }

    public List<String[]> findPairs() throws EmptyTreeException {
        pairs.clear();
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
        List<String> leftChildPairs = new ArrayList<>();
        List<String> rightChildPairs = new ArrayList<>();
        if (node.getLeft() == null && node.getRight() == null) {
            // Tree hasn't children => need to check pairs
            result = findPairsForSimpleNode(node, pathToNode);
        } else if ((node.getLeft() == null ||
                (leftChildPairs = findPairsForNode(node.getLeft(), pathToNode + "l")).size() != 0)
                && (node.getRight() == null ||
                (rightChildPairs = findPairsForNode(node.getRight(), pathToNode + "r")).size() != 0)) {
            // Both child of node has pairs => node may have child need to check
            result = findPairsForNodeWithChildren(node, pathToNode, leftChildPairs, rightChildPairs,
                    node.getLeft() != null, node.getRight() != null
            );
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
            if (curNode.getLeft() == null && curNode.getRight() == null && curNode.value.equals(node.value))
                result.add(pathIterator.nextPath());
        }
        // adding similar nodes to pairs
        for (String pathToPair : result) pairs.add(new String[] {pathToNode, pathToPair});
        return result;
    }

    private List<String> findPairsForNodeWithChildren(Node<T> node, String pathToNode,
                                                      List<String> leftChildPairs, List<String> rightChildPairs,
                                                      boolean hasLeft, boolean hasRight)
            throws NullableNodeException, WrongPathException {
        if (node == null) throw new NullableNodeException();
        Set<String> result = new HashSet<>();

        if (!hasLeft) { // node has only right child
            for (String rightChildPair : rightChildPairs) {
                // if the parent of the pair for the right child does not have a left child
                // then it can be a pair for our node
                String maybePair = rightChildPair.substring(0, rightChildPair.length() - 1);
                Node<T> maybePairNode;
                if ((maybePairNode = getNode(maybePair)).getLeft() == null
                        && node.value.equals(maybePairNode.value)) result.add(maybePair);
            }
        } else if (!hasRight) { // node has only left child
            for (String leftChildPair : leftChildPairs) {
                // if the parent of the pair for the right child does not have a left child
                // then it can be a pair for our node
                String maybePair = leftChildPair.substring(0, leftChildPair.length() - 1);
                Node<T> maybePairNode;
                if ((maybePairNode = getNode(maybePair)).getRight() == null
                        && node.value.equals(maybePairNode.value)) result.add(maybePair);
            }
        } else { // node has both children
            // finding pairs for node
            for (String leftChildPair : leftChildPairs) {
                for (String rightChildPair : rightChildPairs) {
                    // if paths to the pairs for the left and right child elements are equals then this pair isn't
                    // suitable for us
                    if (leftChildPair.equals(rightChildPair)) continue;

                    // if paths to the pairs for the left and right child elements differ only in the last branching
                    // then the parent element of the pairs can be a pair for our node if their values are equals
                    String maybePair;
                    Node<T> maybePairNode;
                    if ((maybePair = leftChildPair.substring(0, leftChildPair.length() - 1))
                            .equals(rightChildPair.substring(0, rightChildPair.length() - 1))
                            && node.getLeft().value.equals((maybePairNode = getNode(maybePair)).getLeft().value)
                            && node.getRight().value.equals(maybePairNode.getRight().value)
                            && node.value.equals(maybePairNode.value)) {
                        result.add(maybePair);
                    }
                }
            }
        }

        // adding similar nodes to pairs
        for (String pathToPair : result) pairs.add(new String[]{pathToNode, pathToPair});
        return new ArrayList<>(result);
    }

    @Override
    public Iterator<T> iterator() {
        return new PreorderIterator();
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
            if (node.getRight() != null) stack.push(node.getRight());
            if (node.getLeft() != null) stack.push(node.getLeft());
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
            while (hasNext() && next() != startNode) {}
        }

        @Override
        public boolean hasNext() {
            return !nodeStack.isEmpty() && !pathStack.isEmpty();
        }

        @Override
        public Node<T> next() {
            Node<T> node = nodeStack.pop();
            lastPath = pathStack.pop();
            if (node.getRight() != null) {
                nodeStack.push(node.getRight());
                pathStack.push(lastPath + "r");
            }
            if (node.getLeft() != null) {
                nodeStack.push(node.getLeft());
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
