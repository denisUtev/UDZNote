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
                    if(leaf && name.contains(".")) {
                        String extension = UFileService.getExtension(name);
                        if (extension.equals("rtf")) {
                            setIcon(new FileImage2("rtf", new Color(0x58508d), 10, 18 + dy));
                        } else if (extension.equals("md")) {
                            setIcon(new FileImage2("md", new Color(0x003f5c), 5, 16 + dy));
                        } else if (extension.equals("pdf")) {
                            setIcon(new FileImage2("pdf", new Color(0xff6361), 5, 16 + dy));
                        } else if (extension.equals("png") || extension.equals("jpg")) {
                            setIcon(new FileImage2(extension, new Color(0xbc5090), 5, 16 + dy));
                        } else {
                            setIcon(new FileImage2(extension, new Color(0x9975767C, true), 8, 17 + dy));
                        }
//                        if(name.contains(".java"))
//                            setIcon(new FileImage2("c", new Color(0xFC5185), 5, 16 + dy));
//                        else if(name.contains(".pde"))
//                            setIcon(new FileImage2("p", new Color(0xB31FA4FF, true), 5.3f, 15 + dy));
//                        else if(name.contains(".png") || name.contains(".jpg") || name.contains(".jpeg"))
//                            setIcon(new FileImage2("i", new Color(0x3FC1C9), 7.5f, 17 + dy));
//                        else if(name.contains(".txt") || name.contains(".md")){
//                            setIcon(new FileImage2("t", new Color(0xAAAAAA), 7.5f, 16.5f + dy));
//                        } else if(name.contains(".py")){
//                            setIcon(new FileImage2("p", new Color(0xFF22AB3C, true), 5.3f, 15 + dy));
//                        } else {
//                            setIcon(new FileImage2("?", new Color(0xAAAAAA), 5.5f, 17f + dy));
//                        }
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
                return 22;
            }
            public int getIconHeight() {
                return 18;
            }
            public void paintIcon(Component c, Graphics g, int w, int h) {
                Graphics2D g2 = (Graphics2D)g.create();
                // Включение высокого качества рендеринга
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(color);
                g2.setFont(new Font("unicode", Font.BOLD, 22));
                g2.drawString("\uD83D\uDDC0", -2, 21);
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

        public class FileImage2 implements Icon {

            String symbol;
            Color color;
            float posX, posY;

            public FileImage2(String str, Color col, float x, float y){
                symbol = str;
                color = col;
                posX = x;
                posY = y;
            }
            public int getIconWidth() {
                return 28;
            }
            public int getIconHeight() {
                return 18;
            }
            public void paintIcon(Component c, Graphics g, int w, int h) {
                Graphics2D g2 = (Graphics2D)g.create();
                // Включение высокого качества рендеринга
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHints(hints);
                Stroke s = new BasicStroke(2);
                g2.setColor(color);
                g2.setStroke(s);
                g2.fillRoundRect(2, 8, 28, 16, 5, 5);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("unicode", Font.PLAIN, 14));
                g2.drawString(symbol, posX, posY);
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
