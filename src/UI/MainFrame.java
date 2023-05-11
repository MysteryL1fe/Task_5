package UI;

import exceptions.EmptyTreeException;
import exceptions.WrongPathException;
import logic.Tree;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class MainFrame extends JFrame {
    private final JPanel mainPanel;
    private final Tree<String> tree;
    private JButton prevPair, nextPair;
    private String[][] pairs;
    private int pairIndex;
    private TreeNode treeNode;

    public MainFrame() {
        this.setTitle("Binary tree");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1500, 1000);

        tree = new Tree<>();

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setSize(1500, 900);
        this.add(mainPanel);

        JButton solveBtn = new JButton("Найти пары");
        solveBtn.setBounds(getWidth() / 2 - 150, 900, 300, 50);
        solveBtn.addActionListener(new SolveBtnActionListener());
        mainPanel.add(solveBtn);

        try {
            tree.add("", "");
            treeNode = new TreeNode(tree);
            treeNode.setBounds(getWidth() / 3, 0, Math.min(getWidth(), getHeight()) / 2,
                    Math.min(getWidth(), getHeight()) / 2);
            mainPanel.add(treeNode);
        } catch (WrongPathException ignored) {}
    }

    public void treeChanged() {
        treeNode.paintBlack();
        if (prevPair != null && nextPair != null) {
            mainPanel.remove(prevPair);
            mainPanel.remove(nextPair);
            prevPair = null;
            nextPair = null;
            pairs = null;
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    private class SolveBtnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (prevPair != null && nextPair != null) {
                    mainPanel.remove(prevPair);
                    mainPanel.remove(nextPair);
                    prevPair = null;
                    nextPair = null;
                    pairs = null;
                }

                pairs = tree.findPairs().toArray(new String[0][0]);

                for (String[] s : pairs) {
                    System.out.println(Arrays.toString(s));
                }

                prevPair = new JButton("<");
                prevPair.setBounds(getWidth() / 2 - 200, 900, 50, 50);
                prevPair.addActionListener(new PrevBtnActionListener());
                prevPair.setVisible(false);
                mainPanel.add(prevPair);

                nextPair = new JButton(">");
                nextPair.setBounds(getWidth() / 2 + 150, 900, 50, 50);
                nextPair.addActionListener(new NextBtnActionListener());
                if (pairs.length < 2) nextPair.setVisible(false);
                mainPanel.add(nextPair);

                pairIndex = 0;
                treeNode.paintBlack();
                if (pairs.length > 0) {
                    treeNode.paintGreen(pairs[0][0]);
                    treeNode.paintGreen(pairs[0][1]);
                }

                mainPanel.revalidate();
                mainPanel.repaint();
            } catch (EmptyTreeException ex) {
                System.out.println(ex);
            }
        }
    }

    private class PrevBtnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            pairIndex--;
            nextPair.setVisible(true);
            if (pairIndex <= 0) {
                prevPair.setVisible(false);
            }
            treeNode.paintBlack();
            treeNode.paintGreen(pairs[pairIndex][0]);
            treeNode.paintGreen(pairs[pairIndex][1]);

            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    private class NextBtnActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            pairIndex++;
            prevPair.setVisible(true);
            if (pairIndex >= pairs.length - 1) {
                nextPair.setVisible(false);
            }
            treeNode.paintBlack();
            treeNode.paintGreen(pairs[pairIndex][0]);
            treeNode.paintGreen(pairs[pairIndex][1]);

            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }
}
