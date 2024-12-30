package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.example.Params.*;
import static org.example.UDZNote.ROOT_PATH;
import static org.example.UDZNote.WORKING_DIR;

public class LeftPanel extends JPanel {

    private JPanel buttonsPanel;
    private FileTree fileTree;

    public LeftPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        buttonsPanel = new JPanel();
        JButton button = new JButton(WORKING_DIR + "/data/SettingIcon.png");
        button.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new SettingsFrame();
            }
        });
        button.setFont(BUTTONS_FONT);
        button.setText("\uE8B8");
        buttonsPanel.add(button);
        JButton button2 = new JButton(WORKING_DIR + "/data/SearchIcon.png");
        button2.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UDZNote.createSearchTab();
            }
        });
        button2.setFont(BUTTONS_FONT);
        button2.setText("\uE8B6");

        buttonsPanel.add(button2);
        JButton button3 = new JButton("Избранное");
        button3.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UDZNote.createBookMarkTab();
            }
        });
        button3.setFont(BUTTONS_FONT);
        button3.setText("\uE0E0");
        buttonsPanel.add(button3);
        add(BorderLayout.NORTH, buttonsPanel);

        buttonsPanel.setMaximumSize(new Dimension(9999, button.getHeight()));

        fileTree = new FileTree(UDZNote.ROOT_PATH);
        add(BorderLayout.NORTH, fileTree);
    }

    private BufferedImage resize(BufferedImage img, int width, int height) {
        BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.drawImage(img, 0, 0, width, height, null);
        g2d.dispose();
        return resizedImg;
    }

    public void updateFileTree() {
        fileTree.setRootPath(ROOT_PATH);
        fileTree.updateFileTree();
    }
}
