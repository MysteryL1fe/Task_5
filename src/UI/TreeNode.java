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
    private String path;
    private AddNodeButton addLeftChild, addRightChild;
    private DeleteNodeButton deleteNodeButton;
    private TreeNode parentNode, leftChild, rightChild;
    private final Tree tree;

    public TreeNode(Tree tree) {
        this(tree, "");
    }

    public TreeNode(Tree tree, String path) {
        this(tree, null, path);
    }

    public TreeNode(Tree tree, TreeNode parentNode, String path) {
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
        g.fillRoundRect(0, 0, getWidth(), getHeight(), getWidth(), getHeight());

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

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        g.drawRoundRect(0, 0, getWidth(), getHeight(), getWidth(), getHeight());
    }

    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), getWidth(), getHeight());
        }
        if (deleteShape == null) {
            deleteShape = new RoundRectangle2D.Double(getWidth() - getWidth() / 5, 0,
                    getWidth() / 5,getHeight() / 5, getWidth() / 5,getHeight() / 5);
        }
        if (addLeftChild != null && addLeftChild.isVisible() && addRightChild != null && addRightChild.isVisible()) {
            if (leftChildShape == null) {
                leftChildShape = new RoundRectangle2D.Double(
                        0,getHeight() - getHeight() / 5,  getWidth() / 5,
                        getHeight() / 5, getWidth() / 5, getHeight() / 5
                );
            }
            if (rightChildShape == null) {
                rightChildShape = new RoundRectangle2D.Double(
                        getWidth() - getWidth() / 5, getHeight() - getHeight() / 5,
                        getWidth() / 5,getHeight() / 5, getWidth() / 5,getHeight() / 5
                );
            }
            return shape.contains(x, y) || deleteShape.contains(x, y)
                    || leftChildShape.contains(x, y) || rightChildShape.contains(x, y);
        }
        if (addLeftChild != null && addLeftChild.isVisible()) {
            if (leftChildShape == null) {
                leftChildShape = new RoundRectangle2D.Double(
                        0, getHeight() - (float) getHeight() / 5, (float) getWidth() / 5,
                        (float) getHeight() / 5, (float) getWidth() / 5, (float) getHeight() / 5
                );
            }
            return shape.contains(x, y) || deleteShape.contains(x, y) || leftChildShape.contains(x, y);
        }
        if (addRightChild != null && addRightChild.isVisible()) {
            if (rightChildShape == null) {
                rightChildShape = new RoundRectangle2D.Double(
                        getWidth() - (float) getWidth() / 5, getHeight() - (float) getHeight() / 5,
                        (float) getWidth() / 5, (float) getHeight() / 5, (float) getWidth() / 5,
                        (float) getHeight() / 5
                );
            }
            return shape.contains(x, y) || deleteShape.contains(x, y) || rightChildShape.contains(x, y);
        }
        return shape.contains(x, y) || deleteShape.contains(x, y);
    }

    private class TextDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            try {
                tree.change(getText(), path);
            } catch (WrongPathException ex) {
                System.out.println(ex);
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            try {
                tree.change(getText(), path);
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
            g.fillRoundRect(0, 0, getWidth(), getHeight(), getWidth(), getHeight());
            g.setColor(Color.BLACK);
            g.fillRect(getWidth() / 3, getHeight() / 2 - getHeight() / 100,
                    getWidth() / 3, getHeight() / 50);
            g.fillRect(getWidth() / 2 - getWidth() / 100, getHeight() / 3,
                    getWidth() / 50, getHeight() / 3);
        }

        @Override
        protected void paintBorder(Graphics g) {
            g.setColor(getForeground());
            g.drawRoundRect(0, 0, getWidth(), getHeight(), getWidth(), getHeight());
        }

        @Override
        public boolean contains(int x, int y) {
            if (shape == null || !shape.getBounds().equals(getBounds())) {
                shape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), getWidth(), getHeight());
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
            g.fillRoundRect(0, 0, getWidth(), getHeight(), getWidth(), getHeight());
            g.setColor(Color.BLACK);
            g.fillRect(getWidth() / 3, getHeight() / 2 - getHeight() / 100,
                    getWidth() / 3, getHeight() / 50);
        }

        @Override
        protected void paintBorder(Graphics g) {
            g.setColor(getForeground());
            g.drawRoundRect(0, 0, getWidth(), getHeight(), getWidth(), getHeight());
        }

        @Override
        public boolean contains(int x, int y) {
            if (shape == null || !shape.getBounds().equals(getBounds())) {
                shape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), getWidth(), getHeight());
            }
            return shape.contains(x, y);
        }
    }

    private class AddNodeButtonActionListener implements ActionListener {
        private boolean isLeft;

        public AddNodeButtonActionListener(boolean isLeft) {
            this.isLeft = isLeft;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //ToDO fuck you fucken recursion
            if (isLeft) {
                addLeftChild.setVisible(false);
                try {
                    tree.add("", path + "l");
                    leftChild = new TreeNode(tree, TreeNode.this, path + "l");
                    leftChild.setBounds(getWidth() / 2, getHeight(), getWidth() / 2, getHeight() / 2);
                    TreeNode.this.getParent().add(leftChild);
                    leftChild.repaint();
                } catch (WrongPathException ex) {
                    System.out.println(ex);
                }
            } else {
                addRightChild.setVisible(false);
                try {
                    tree.add("", path + "r");
                    rightChild = new TreeNode(tree, TreeNode.this, path + "r");
                    rightChild.setBounds(0, 0, TreeNode.this.getWidth() / 2, TreeNode.this.getHeight() / 2);
                    TreeNode.this.getParent().add(rightChild);
                    rightChild.repaint();
                } catch (WrongPathException ex) {
                    System.out.println(ex);
                }
            }
        }
    }

    private class DeleteNodeActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }
}
