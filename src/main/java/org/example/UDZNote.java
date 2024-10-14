package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static org.example.Params.initFonts;

public class UDZNote {

    private JFrame mainFrame;
    private JSplitPane splitPane;
    private LeftPanel leftPanel;
    private DnDTabbedPane tabbedPane;

    public static String workingDir;

    public UDZNote() {
        workingDir = System.getProperty("user.dir");
        initFonts(workingDir);

        initMainFrame();

        initLeftPanel();
        initTabbedPane();
        initSplitPane();
    }

    private void initMainFrame() {
        mainFrame = new JFrame("UDZNote");
        mainFrame.setSize(650, 550);
        mainFrame.setDefaultCloseOperation(mainFrame.EXIT_ON_CLOSE);
        mainFrame.setTitle("UDZNote");
        mainFrame.setVisible(true);

        //Display the window.
        //frame.pack();
        mainFrame.setVisible(true);
    }

    private void initSplitPane() {
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftPanel, tabbedPane);
        splitPane.setDividerSize(12);
        //splitPane.setDividerLocation(mainFrame.getWidth() - 250);
        mainFrame.add(splitPane);
    }

    private void initLeftPanel() {
        leftPanel = new LeftPanel();
    }

    private void initTabbedPane() {
        tabbedPane = new DnDTabbedPane(JTabbedPane.NORTH, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFont(Params.TAB_TITLE_FONT);
        for (int i = 0; i < 5; i++) {
            createTab("Tab " + i);
        }

    }

    private void createTab(String nameTab) {
        JTextPane textPane = new JTextPane();
        textPane.setFont(Params.CODE_FONT);
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBackground(new Color(0, 0, 0, 0));
        scrollPane.setBorder(null);
        addTab(nameTab, scrollPane);
    }

    public void addTab(String title, JComponent contentPanel){
        tabbedPane.addTab(title, contentPanel);

        //JLabel name = new JLabel(title);
        //name.setFont(Params.TAB_TITLE_FONT);
        //tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new ButtonTabComponent(tabbedPane, name));
    }
}
