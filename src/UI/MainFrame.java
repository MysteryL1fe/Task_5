package UI;

import exceptions.WrongPathException;
import logic.Tree;

import javax.swing.*;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private final Tree tree;

    public MainFrame() {
        this.setTitle("Binary tree");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1500, 1000);

        tree = new Tree<String>();

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setLayout(null);
        this.add(scrollPane);

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setSize(1500, 900);
        scrollPane.add(mainPanel);

        try {
            tree.add("", "");
            TreeNode node = new TreeNode(tree);
            node.setBounds(getWidth() / 3, 0, Math.min(getWidth(), getHeight()) / 2,
                    Math.min(getWidth(), getHeight()) / 2);
            mainPanel.add(node);
        } catch (WrongPathException ignored) {}
    }
}
