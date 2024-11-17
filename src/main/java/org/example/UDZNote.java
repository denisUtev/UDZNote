package org.example;

import org.example.FileTreeActions.UFileService;
import org.example.PDFPanel.PDFViewerPanel;
import org.example.TextPaneActions.PasteImageAction;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;

import static org.example.Params.initFonts;

public class UDZNote {

    private static JFrame mainFrame;
    private JSplitPane splitPane;
    private LeftPanel leftPanel;
    private static DnDTabbedPane tabbedPane;

    public static String WORKING_DIR;
    public static String ROOT_PATH = "C:\\Users\\utev2\\Documents\\Мой Дневник\\DATA2";
    public static String IMAGE_DIRECTORY = "-";
    //public static String ROOT_PATH = "C:\\Users\\utev2\\Documents\\База знаний";

    public UDZNote() {
        WORKING_DIR = System.getProperty("user.dir");
        initFonts(WORKING_DIR);
        checkImageDirectory();
        //ROOT_PATH = WORKING_DIR + File.separator + "data" + File.separator;

        initMainFrame();

        initLeftPanel();
        initTabbedPane();
        initSplitPane();
    }

    private void checkImageDirectory() {
        IMAGE_DIRECTORY = WORKING_DIR + File.separator + "data" + File.separator + "imageDirectory";
        File file = new File(IMAGE_DIRECTORY);
        if (!file.exists() || !file.isDirectory()) {
            UFileService.createPackage(IMAGE_DIRECTORY);
        }
    }

    private void initMainFrame() {
        mainFrame = new JFrame("UDZNote");
        mainFrame.setSize(800, 550);
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
        if (!filePath.endsWith(".md") && !filePath.endsWith(".rtf")) {
            textPane.setText(text);
        }
        textPane.setFont(Params.TEXT_FONT);
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBackground(new Color(0, 0, 0, 0));
        scrollPane.setBorder(null);
        textPane.setCaretPosition(0);

        nameTab = getShortedTitle(nameTab);
        JLabel titleTab = new JLabel(nameTab);
        titleTab.setFont(Params.TAB_TITLE_FONT);
        textPane.setTitleTab(titleTab);
        titleTab.setForeground(Color.WHITE);

        textPane.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    java.util.List<File> droppedFiles = (java.util.List<File>)
                            evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles) {
                        new PasteImageAction(textPane, new ImageIcon(file.getPath()));
                    }
                    evt.dropComplete(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        ButtonTabComponent tabComponent = new ButtonTabComponent(tabbedPane, titleTab, textPane);
        addTab(nameTab, scrollPane, tabComponent);
    }

    private static String getShortedTitle(String title) {
        if (title.length() > 25) {
            title = title.substring(0, 25) + "...";
        }
        StringBuilder titleBuilder = new StringBuilder(title);
        while (titleBuilder.length() < 25) {
            titleBuilder.append(' ');
        }
        return titleBuilder.toString();
    }

    public static void addTab(String title, JComponent contentPanel, ButtonTabComponent tabComponent){
        tabbedPane.addTab(title, contentPanel);
        //JLabel name = new JLabel(title);
        //name.setFont(Params.TAB_TITLE_FONT);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabComponent);
    }

    public static void openFile(String path) {
        String title = new File(path).getName();
        String text = UFileService.loadFile(path);
        String extension = org.example.UFileService.getExtension(path);
        if (extension.equals("pdf")) {
            openPdfFile(title, path);
        } else {
            createTab(title, text, path);
        }
//        if (extension.equals("png") || extension.equals("jpg")) {
//            addImageTab(title, path);
//        }
    }

    private static void openPdfFile(String nameTab, String path) {
        File pdfFile = new File(path);
        PDFViewerPanel pdfViewer = null;
        try {
            pdfViewer = new PDFViewerPanel(pdfFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JPanel allPanel = new JPanel(new BorderLayout());
        allPanel.add(pdfViewer.createControlPanel(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(pdfViewer);
        scrollPane.setBackground(new Color(0, 0, 0, 0));
        scrollPane.setBorder(null);
        allPanel.add(scrollPane, BorderLayout.CENTER);

        nameTab = getShortedTitle(nameTab);
        JLabel titleTab = new JLabel(nameTab);
        titleTab.setFont(Params.TAB_TITLE_FONT);
        titleTab.setForeground(Color.WHITE);

        ButtonTabComponent tabComponent = new ButtonTabComponent(tabbedPane, titleTab, null);
        tabComponent.setPDFViewerPanel(pdfViewer);
        addTab(nameTab, allPanel, tabComponent);
    }

    public static JFrame getMainFrame() {
        return mainFrame;
    }
}
