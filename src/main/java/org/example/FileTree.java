package org.example;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class FileTree extends JScrollPane {

    JTree fileTree;

    public FileTree() {
        initFileTree();
    }

    private void initFileTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Заметки");

        DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("Системный анализ");
        DefaultMutableTreeNode child2 = new DefaultMutableTreeNode("Искусство программирования");
        DefaultMutableTreeNode child3 = new DefaultMutableTreeNode("Преступление и наказание");

        root.add(child1);
        root.add(child2);
        root.add(child3);

        DefaultMutableTreeNode child11 = new DefaultMutableTreeNode("Глава 1");
        DefaultMutableTreeNode child12 = new DefaultMutableTreeNode("Глава 2");
        DefaultMutableTreeNode child13 = new DefaultMutableTreeNode("Глава 3");

        child1.add(child11);
        child1.add(child12);
        child1.add(child13);

        DefaultMutableTreeNode child21 = new DefaultMutableTreeNode("Глава 1");
        DefaultMutableTreeNode child22 = new DefaultMutableTreeNode("Глава 2");
        DefaultMutableTreeNode child23 = new DefaultMutableTreeNode("Глава 3");
        DefaultMutableTreeNode child24 = new DefaultMutableTreeNode("Глава 4");
        DefaultMutableTreeNode child25 = new DefaultMutableTreeNode("Глава 5");
        DefaultMutableTreeNode child26 = new DefaultMutableTreeNode("Глава 6");

        child2.add(child21);
        child2.add(child22);
        child2.add(child23);
        child2.add(child24);
        child2.add(child25);
        child2.add(child26);

        DefaultMutableTreeNode child31 = new DefaultMutableTreeNode("Глава 1");
        DefaultMutableTreeNode child32 = new DefaultMutableTreeNode("Глава 2");
        DefaultMutableTreeNode child33 = new DefaultMutableTreeNode("Глава 3");

        child3.add(child31);
        child3.add(child32);
        child3.add(child33);

        fileTree = new JTree(root);
        getViewport().add(fileTree);
    }
}
