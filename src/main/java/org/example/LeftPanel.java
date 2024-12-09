package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LeftPanel extends JPanel {

    private JPanel buttonsPanel;
    private FileTree fileTree;

    public LeftPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        buttonsPanel = new JPanel();
        JButton button = new JButton("Настройки");
        button.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new SettingsFrame();
            }
        });
        button.setText("Настройки");
        buttonsPanel.add(button);
        JButton button2 = new JButton("Поиск");
        button2.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UDZNote.createSearchTab();
            }
        });
        button2.setText("Поиск");
        buttonsPanel.add(button2);
        JButton button3 = new JButton("Избранное");
        buttonsPanel.add(button3);
        add(BorderLayout.NORTH, buttonsPanel);

        buttonsPanel.setMaximumSize(new Dimension(9999, button.getHeight()));

        fileTree = new FileTree(UDZNote.ROOT_PATH);
        add(BorderLayout.NORTH, fileTree);
    }
}
