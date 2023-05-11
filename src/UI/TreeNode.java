package UI;

import exceptions.WrongPathException;
import logic.Tree;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class TreeNode extends JTextField {
    private Shape shape, deleteShape, leftChildShape, rightChildShape;
    private final String path;
    private AddNodeButton addLeftChild, addRightChild;
    private DeleteNodeButton deleteNodeButton;
    private final TreeNode parentNode;
    private TreeNode leftChild, rightChild;
    private final Tree<String> tree;
    private MainFrame mainFrame;
    private boolean isGreen = false;

    public TreeNode(Tree<String> tree) {
        this(tree, "");
    }

    private TreeNode(Tree<String> tree, String path) {
        this(tree, null, path);
    }

    private TreeNode(Tree<String> tree, TreeNode parentNode, String path) {
        super();
        this.tree = tree;
        this.parentNode = parentNode;
        this.path = path;
        this.getDocument().addDocumentListener(new TextDocumentListener());
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                getWidth() - 1, getHeight() - 1);

        if (addLeftChild == null) {
            addLeftChild = new AddNodeButton();
            addLeftChild.setBounds(0, getHeight() - getHeight() / 5, getWidth() / 5, getHeight() / 5);
            addLeftChild.addActionListener(new AddNodeButtonActionListener(true));
            this.add(addLeftChild);
        }
        if (addRightChild == null) {
            addRightChild = new AddNodeButton();
            addRightChild.setBounds(getWidth() - getWidth() / 5, getHeight() - getHeight() / 5,
                    getWidth() / 5, getHeight() / 5);
            addRightChild.addActionListener(new AddNodeButtonActionListener(false));
            this.add(addRightChild);
        }
        if (deleteNodeButton == null) {
            deleteNodeButton = new DeleteNodeButton();
            deleteNodeButton.setBounds(getWidth() - getWidth() / 5, 0,
                    getWidth() / 5, getHeight() / 5);
            deleteNodeButton.addActionListener(new DeleteNodeActionListener());
            this.add(deleteNodeButton);
        }
        if (mainFrame == null) {
            Container c = getParent();
            while ((c != null) && !(c instanceof JFrame)) {
                c = c.getParent();
            }
            if (c != null) mainFrame = (MainFrame) c;
            else mainFrame = null;
        }

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(isGreen ? Color.GREEN : getForeground());
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                getWidth() - 1, getHeight() - 1);
    }

    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1,
                    getWidth() - 1, getHeight() - 1);
        }
        if (deleteShape == null) {
            deleteShape = new RoundRectangle2D.Double(getWidth() - getWidth() / 5, 0,
                    getWidth() / 5 - 1,getHeight() / 5 - 1, getWidth() / 5 - 1,getHeight() / 5 - 1);
        }
        if (addLeftChild != null && addLeftChild.isVisible() && addRightChild != null && addRightChild.isVisible()) {
            if (leftChildShape == null) {
                leftChildShape = new RoundRectangle2D.Double(
                        0,getHeight() - getHeight() / 5,  getWidth() / 5 - 1,
                        getHeight() / 5 - 1, getWidth() / 5 - 1, getHeight() / 5 - 1
                );
            }
            if (rightChildShape == null) {
                rightChildShape = new RoundRectangle2D.Double(
                        getWidth() - getWidth() / 5, getHeight() - getHeight() / 5,
                        getWidth() / 5 - 1,getHeight() / 5 - 1, getWidth() / 5 - 1,getHeight() / 5 - 1
                );
            }
            return shape.contains(x, y) || deleteShape.contains(x, y)
                    || leftChildShape.contains(x, y) || rightChildShape.contains(x, y);
        }
        if (addLeftChild != null && addLeftChild.isVisible()) {
            if (leftChildShape == null) {
                leftChildShape = new RoundRectangle2D.Double(
                        0, getHeight() - getHeight() / 5, getWidth() / 5 - 1,
                        getHeight() / 5 - 1, getWidth() / 5 - 1, getHeight() / 5 - 1
                );
            }
            return shape.contains(x, y) || deleteShape.contains(x, y) || leftChildShape.contains(x, y);
        }
        if (addRightChild != null && addRightChild.isVisible()) {
            if (rightChildShape == null) {
                rightChildShape = new RoundRectangle2D.Double(
                        getWidth() - getWidth() / 5, getHeight() - getHeight() / 5, getWidth() / 5 - 1,
                        getHeight() / 5 - 1, getWidth() / 5 - 1, getHeight() / 5 - 1
                );
            }
            return shape.contains(x, y) || deleteShape.contains(x, y) || rightChildShape.contains(x, y);
        }
        return shape.contains(x, y) || deleteShape.contains(x, y);
    }

    public void paintGreen(String path) {
        if (path.isEmpty()) {
            isGreen = true;
            if (leftChild != null) leftChild.paintGreen("");
            if (rightChild != null) rightChild.paintGreen("");
        } else {
            if (path.charAt(0) == 'l' && leftChild != null) {
                leftChild.paintGreen(path.substring(1));
            } else if (path.charAt(0) == 'r' && rightChild != null) {
                rightChild.paintGreen(path.substring(1));
            }
        }
    }

    public void paintBlack() {
        isGreen = false;
        if (leftChild != null) leftChild.paintBlack();
        if (rightChild != null) rightChild.paintBlack();
    }

    private class TextDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            try {
                tree.change(getText(), path);
                mainFrame.treeChanged();
            } catch (WrongPathException ex) {
                System.out.println(ex);
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            try {
                tree.change(getText(), path);
                mainFrame.treeChanged();
            } catch (WrongPathException ex) {
                System.out.println(ex);
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    }

    private class AddNodeButton extends JButton {
        private Shape shape;

        private AddNodeButton() {
            super();
            this.setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(Color.GREEN);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                    getWidth() - 1, getHeight() - 1);
            g.setColor(Color.BLACK);
            g.fillRect(getWidth() / 8, getHeight() / 2 - getHeight() / 10,
                    getWidth() * 3 / 4, getHeight() / 5);
            g.fillRect(getWidth() / 2 - getWidth() / 10, getHeight() / 8,
                    getWidth() / 5, getHeight() * 3 / 4);
        }

        @Override
        protected void paintBorder(Graphics g) {
            g.setColor(getForeground());
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                    getWidth() - 1, getHeight() - 1);
        }

        @Override
        public boolean contains(int x, int y) {
            if (shape == null || !shape.getBounds().equals(getBounds())) {
                shape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1,
                        getWidth() - 1, getHeight() - 1);
            }
            return shape.contains(x, y);
        }
    }

    private class DeleteNodeButton extends JButton {
        private Shape shape;

        private DeleteNodeButton() {
            super();
            this.setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(Color.RED);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                    getWidth() - 1, getHeight() - 1);
            g.setColor(Color.BLACK);
            g.fillRect(getWidth() / 8, getHeight() / 2 - getHeight() / 10,
                    getWidth() * 3 / 4, getHeight() / 5);
        }

        @Override
        protected void paintBorder(Graphics g) {
            g.setColor(getForeground());
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                    getWidth() - 1, getHeight() - 1);
        }

        @Override
        public boolean contains(int x, int y) {
            if (shape == null || !shape.getBounds().equals(getBounds())) {
                shape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1,
                        getWidth() - 1, getHeight() - 1);
            }
            return shape.contains(x, y);
        }
    }

    private void deleteNode() {
        if (leftChild != null) {
            leftChild.deleteNode();
        }
        if (rightChild != null) {
            rightChild.deleteNode();
        }
        if (parentNode == null) {
            try {
                tree.change("", "");
                setText("");
                mainFrame.treeChanged();
            } catch (WrongPathException e) {
                System.out.println("Can't delete node");
            }
        } else {
            try {
                tree.delete(path);
                if (path.charAt(path.length() - 1) == 'l') {
                    parentNode.addLeftChild.setVisible(true);
                    parentNode.leftChild = null;
                } else {
                    parentNode.addRightChild.setVisible(true);
                    parentNode.rightChild = null;
                }
                mainFrame.treeChanged();
                Container parent = getParent();
                parent.remove(this);
                parent.repaint();
            } catch (WrongPathException e) {
                System.out.println("Can't delete node");
            }
        }
    }

    private class AddNodeButtonActionListener implements ActionListener {
        private final boolean isLeft;

        public AddNodeButtonActionListener(boolean isLeft) {
            this.isLeft = isLeft;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isLeft) {
                addLeftChild.setVisible(false);
                try {
                    tree.add("", path + "l");
                    leftChild = new TreeNode(tree, TreeNode.this, path + "l");
                    leftChild.setBounds(TreeNode.this.getX() - TreeNode.this.getWidth() / 3,
                            TreeNode.this.getY() + TreeNode.this.getHeight() * 4 / 5,
                            TreeNode.this.getWidth() / 2, TreeNode.this.getHeight() / 2);
                    TreeNode.this.getParent().add(leftChild);
                    leftChild.repaint();
                    mainFrame.treeChanged();
                } catch (WrongPathException ex) {
                    System.out.println(ex);
                }
            } else {
                addRightChild.setVisible(false);
                try {
                    tree.add("", path + "r");
                    rightChild = new TreeNode(tree, TreeNode.this, path + "r");
                    rightChild.setBounds(TreeNode.this.getX() + TreeNode.this.getWidth() * 3 / 4,
                            TreeNode.this.getY() + TreeNode.this.getHeight() * 4 / 5,
                            TreeNode.this.getWidth() / 2, TreeNode.this.getHeight() / 2);
                    TreeNode.this.getParent().add(rightChild);
                    rightChild.repaint();
                    mainFrame.treeChanged();
                } catch (WrongPathException ex) {
                    System.out.println(ex);
                }
            }
        }
    }

    private class DeleteNodeActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteNode();
        }
    }
}
