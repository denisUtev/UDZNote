package org.example;

import org.example.FileTreeActions.UFileService;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static org.example.Params.initFonts;

public class UDZNote {

    private static JFrame mainFrame;
    private JSplitPane splitPane;
    private LeftPanel leftPanel;
    private static DnDTabbedPane tabbedPane;

    public static String WORKING_DIR;
    public static String ROOT_PATH = "C:\\Users\\utev2\\Documents\\Мой Дневник\\DATA2";

    public UDZNote() {
        WORKING_DIR = System.getProperty("user.dir");
        initFonts(WORKING_DIR);
        //ROOT_PATH = WORKING_DIR + File.separator + "data" + File.separator;

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
        tabbedPane.setFont(Params.BIG_TAB_TITLE_FONT);
    }

    private static void createTab(String nameTab, String text, String filePath) {
        UTextPane textPane = new UTextPane(nameTab, filePath);
        textPane.setText(text);
        textPane.setFont(Params.TEXT_FONT);
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBackground(new Color(0, 0, 0, 0));
        scrollPane.setBorder(null);
        addTab(nameTab, scrollPane);
    }

    public static void addTab(String title, JComponent contentPanel){
        if (title.length() > 25) {
            title = title.substring(0, 25) + "...";
        }
        StringBuilder titleBuilder = new StringBuilder(title);
        while (titleBuilder.length() < 25) {
            titleBuilder.append(' ');
        }
        title = titleBuilder.toString();
        tabbedPane.addTab(title, contentPanel);

        JLabel name = new JLabel(title);
        name.setFont(Params.TAB_TITLE_FONT);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new ButtonTabComponent(tabbedPane, name));
    }

    public static void openFile(String path) {
        String title = new File(path).getName();
        String text = UFileService.loadFile(path);
        String extension = "";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i + 1);
        }
        createTab(title, text, path);
//        if (extension.equals("png") || extension.equals("jpg")) {
//            addImageTab(title, path);
//        }
    }

    public static JFrame getMainFrame() {
        return mainFrame;
    }
}
