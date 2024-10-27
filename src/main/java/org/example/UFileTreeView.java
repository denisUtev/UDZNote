package org.example;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class UFileTreeView {

    public static class MyTreeCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            // decide what icons you want by examining the node
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if (node.getUserObject() instanceof String) {
                    // your root node, since you just put a String as a user obj
                    //System.out.println(node.getUserObject());
                    String name = (String)node.getUserObject();
                    float dy = 3;
                    if(leaf) {
                        if(name.contains(".java"))
                            setIcon(new FileImage("c", new Color(0xFC5185), 5, 16 + dy));
                        else if(name.contains(".pde"))
                            setIcon(new FileImage("p", new Color(0xB31FA4FF, true), 5.3f, 15 + dy));
                        else if(name.contains(".png") || name.contains(".jpg") || name.contains(".jpeg"))
                            setIcon(new FileImage("i", new Color(0x3FC1C9), 7.5f, 17 + dy));
                        else if(name.contains(".txt") || name.contains(".md")){
                            setIcon(new FileImage("t", new Color(0xAAAAAA), 7.5f, 16.5f + dy));
                        } else if(name.contains(".py")){
                            setIcon(new FileImage("p", new Color(0xFF22AB3C, true), 5.3f, 15 + dy));
                        } else {
                            setIcon(new FileImage("?", new Color(0xAAAAAA), 5.5f, 17f + dy));
                        }
                    } else {
                        setIcon(new FolderImage(new Color(0x118ab2)));
                    }
                } else if (node.getUserObject() instanceof Contact) {
                    // decide based on some property of your Contact obj
                    Contact contact = (Contact) node.getUserObject();
                    if (contact.isSomeProperty()) {
                        setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
                    } else {
                        setIcon(UIManager.getIcon("FileChooser.homeFolderIcon"));
                    }
                }
            }

            return this;
        }

        public class FolderImage implements Icon {

            Color color;

            public FolderImage(Color col){
                color = col;
            }
            public int getIconWidth() {
                return 18;
            }
            public int getIconHeight() {
                return 18;
            }
            public void paintIcon(
                    Component c, Graphics g, int w, int h) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setColor(color);
                g2.setFont(new Font("unicode", Font.BOLD, 18));
                g2.drawString("\uD83D\uDDC0", 0, 19);
            }
        }

        public class FileImage implements Icon {

            String symbol;
            Color color;
            float posX, posY;

            public FileImage(String str, Color col, float x, float y){
                symbol = str;
                color = col;
                posX = x;
                posY = y;
            }
            public int getIconWidth() {
                return 18;
            }
            public int getIconHeight() {
                return 18;
            }
            public void paintIcon(
                    Component c, Graphics g, int w, int h) {
                Graphics2D g2 = (Graphics2D)g.create();
                RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHints(hints);
                g2.setColor(color);
                g2.setFont(new Font("unicode", Font.PLAIN, 14));
                g2.drawString(symbol, posX, posY);
                Stroke s = new BasicStroke(2);
                g2.setStroke(s);
                g2.drawOval(2,8, 14, 14);
            }
        }
    }

    private static class Contact {

        private boolean someProperty;
        private String name;

        public Contact(String name) {
            this(name, false);
        }

        public Contact(String name, boolean property) {
            this.someProperty = property;
            this.name = name;
        }

        public boolean isSomeProperty() {
            return someProperty;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
