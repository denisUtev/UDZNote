package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.CardPanel.BookMarkPanel;
import org.example.CardPanel.CardPanel;
import org.example.CardPanel.SearchPanel;
import org.example.FileTreeActions.UFileService;
import org.example.PDFPanel.PDFViewerPanel;
import org.example.TextPaneActions.PasteImageAction;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;

import static org.example.Params.initFonts;
import static org.example.Params.loadSettings;

public class UDZNote {

    public static JFrame mainFrame;
    private JSplitPane splitPane;
    public static LeftPanel leftPanel;
    public static DnDTabbedPane tabbedPane;
    public static Color DEFAULT_TEXT_COLOR;
    public static HashMap<String, String> dictDescriptions;
    public static HashMap<String, String> dictBookMarks;
    public static HashMap<String, String> dictBookNames;

    public static String WORKING_DIR;
    public static String DESCRIPTIONS_FILE = "descriptions.txt";
    public static String BOOKMARKS_FILE = "bookmarks.txt";
    public static String BOOK_NAMES_FILE = "booknames.txt";
    public static String ROOT_PATH = "C:\\Users\\utev2\\Documents\\Мой Дневник\\DATA2";
    public static String IMAGE_DIRECTORY = "-";
    //public static String ROOT_PATH = "C:\\Users\\utev2\\Documents\\База знаний";

    public UDZNote() {
        WORKING_DIR = System.getProperty("user.dir");
        initFonts(WORKING_DIR);
        loadSettings();
        checkImageDirectory();
        //ROOT_PATH = WORKING_DIR + File.separator + "data" + File.separator;
        dictDescriptions = getFilesDescription();
        dictBookMarks = getFilesBookMark();
        dictBookNames = getFilesBookNames();

        initMainFrame();

        initLeftPanel();
        initTabbedPane();
        initSplitPane();

        createSearchTab();
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
        mainFrame.setSize(860, 600);
        mainFrame.setDefaultCloseOperation(mainFrame.EXIT_ON_CLOSE);
        mainFrame.setTitle("UDZNote");
        mainFrame.setVisible(true);

        // Устанавливаем изображение для иконки
        Image icon = Toolkit.getDefaultToolkit().getImage(WORKING_DIR + File.separator + "data/Icon.png");
        mainFrame.setIconImage(icon);

        JLabel label = new JLabel();
        DEFAULT_TEXT_COLOR = label.getForeground();

        //Display the window.
        //frame.pack();
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

            }
        });
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            onExit();
            throwable.printStackTrace();
        });
    }

    public void onExit() {
        var textFields = findAllTextFields(tabbedPane);
        for (UTextPane textPane : textFields) {
            try {
                textPane.saveText();
            } catch (Exception e) {
                JOptionPane.showConfirmDialog(mainFrame, "Произошла ошибка при сохранении файла " + textPane.fileName, "Подтверждение выхода",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            }
        }
        int result = JOptionPane.showConfirmDialog(mainFrame, "Произошла ошибка работы программы", "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            mainFrame.dispose(); // Закрываем окно
            System.exit(0); // Завершаем программу
        }
    }

    public static void setDescriptionToFile(File file, String description) {
        dictDescriptions.put(file.getPath(), description);
        writeDictionaryToJsonFile(dictDescriptions, WORKING_DIR + File.separator + DESCRIPTIONS_FILE);
    }

    public static void setBookMarkToFile(File file, String bookMark) {
        dictBookMarks.put(file.getPath(), bookMark);
        writeDictionaryToJsonFile(dictBookMarks, WORKING_DIR + File.separator + BOOKMARKS_FILE);
    }

    public static void setBookNameToFile(File file, String bookName) {
        dictBookNames.put(file.getPath(), bookName);
        writeDictionaryToJsonFile(dictBookNames, WORKING_DIR + File.separator + BOOK_NAMES_FILE);
    }

    public HashMap<String, String> getFilesDescription() {
        return readJsonFile(WORKING_DIR + File.separator + DESCRIPTIONS_FILE);
    }

    public HashMap<String, String> getFilesBookMark() {
        return readJsonFile(WORKING_DIR + File.separator + BOOKMARKS_FILE);
    }

    public HashMap<String, String> getFilesBookNames() {
        return readJsonFile(WORKING_DIR + File.separator + BOOK_NAMES_FILE);
    }

    public static HashMap<String, String> readJsonFile(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(filePath);
        try {
            return mapper.readValue(file, new TypeReference<>(){});
        } catch (IOException e) {
            //throw new RuntimeException(e);
            writeDictionaryToJsonFile(new HashMap<>(), filePath);
            return new HashMap<>();
        }
    }

    public static void writeDictionaryToJsonFile(HashMap<String, String> dictionary, String filePath) {
        ObjectMapper mapper = new ObjectMapper(); // Создаем объект маппера
        File file = new File(filePath);           // Указываем путь к файлу
        try {
            mapper.writeValue(file, dictionary);      // Сохраняем словарь в файл
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createNoteForBook(File bookPath, String copiedText) {
        if (!dictBookNames.containsKey(bookPath.getPath())) {
            String nameBook = JOptionPane.showInputDialog("Введите название книги");
            if (nameBook != null) {
                dictBookNames.put(bookPath.getPath(), nameBook);
                UFileService.createPackage(ROOT_PATH, nameBook);
                writeDictionaryToJsonFile(dictBookNames, WORKING_DIR + File.separator + BOOK_NAMES_FILE);
            } else {
                return;
            }
        }
        String nameNote = JOptionPane.showInputDialog("Введите название заметки");
        if (nameNote != null) {
            if (!nameNote.contains(".")) {
                nameNote += ".rtf";
            }
            String filePath = ROOT_PATH + File.separator + dictBookNames.get(bookPath.getPath()) + File.separator + nameNote;
            StyledDocument doc = new DefaultStyledDocument();
            try {
                StyleContext context = new StyleContext();
                Style style = context.addStyle("textStyle", null);
                StyleConstants.setForeground(style, Color.GRAY);
                doc.insertString(doc.getLength(), copiedText, style);
                RTFEditorKit rtfKit = new RTFEditorKit();
                FileOutputStream fos = new FileOutputStream(filePath);
                rtfKit.write(fos, doc, 0, doc.getLength());
                fos.close();
            } catch (BadLocationException | IOException e) {
                throw new RuntimeException(e);
            }
            openFile(filePath);
        }
        leftPanel.updateFileTree();
    }

    private ArrayList<UTextPane> findAllTextFields(Component component) {
        ArrayList<UTextPane> fields = new ArrayList<>();
        if (component instanceof Container) {
            Component[] children = ((Container) component).getComponents();
            for (Component child : children) {
                if (child instanceof UTextPane) {
                    fields.add((UTextPane) child);
                } else {
                    fields.addAll(findAllTextFields(child));
                }
            }
        }
        return fields;
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

    public static void createSearchTab() {
        CardPanel cardPanel = new CardPanel(new SearchPanel());
        cardPanel.setDataForSearchCards(org.example.UFileService.getFiles(ROOT_PATH));

        String nameTab = "Поиск";
        JLabel titleTab = new JLabel(nameTab);
        titleTab.setFont(Params.TAB_TITLE_FONT);
//        if (Params.THEME.equals("Темная")) {
//            titleTab.setForeground(Color.WHITE);
//        } else {
//            titleTab.setForeground(Color.BLACK);
//        }

        ButtonEditorTabComponent tabComponent = new ButtonEditorTabComponent(tabbedPane, titleTab, null);
        addTab(nameTab, cardPanel, tabComponent);
    }

    public static void createBookMarkTab() {
        CardPanel cardPanel = new CardPanel(new BookMarkPanel());
        cardPanel.setDataForBookMarkCards(org.example.UFileService.getFiles(ROOT_PATH));

        String nameTab = "Избранное";
        JLabel titleTab = new JLabel(nameTab);
        titleTab.setFont(Params.TAB_TITLE_FONT);
//        if (Params.THEME.equals("Темная")) {
//            titleTab.setForeground(Color.WHITE);
//        } else {
//            titleTab.setForeground(Color.BLACK);
//        }

        ButtonEditorTabComponent tabComponent = new ButtonEditorTabComponent(tabbedPane, titleTab, null);
        addTab(nameTab, cardPanel, tabComponent);
    }

    private ArrayList<File> getNotesWithTags(ArrayList<String> tags, File directory) {
        ArrayList<File> result = new ArrayList<>();
        var files = org.example.UFileService.getFiles(directory.getPath());
        for (var file : files) {
            if (file.isFile() && !org.example.UFileService.getExtension(file.getPath()).equals("pdf")) {
                String text = org.example.UFileService.loadFile(file.getPath());
                boolean flag = true;
                for (String tag : tags) {
                    if (!text.contains("#" + tag)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    result.add(file);
                }
            }
        }
        return result;
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
//        if (Params.THEME.equals("Темная")) {
//            titleTab.setForeground(Color.WHITE);
//        } else {
//            titleTab.setForeground(Color.BLACK);
//        }

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

        ButtonEditorTabComponent tabComponent = new ButtonEditorTabComponent(tabbedPane, titleTab, textPane);
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

    public static void addTab(String title, JComponent contentPanel, ButtonEditorTabComponent tabComponent){
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
//        if (Params.THEME.equals("Темная")) {
//            titleTab.setForeground(Color.WHITE);
//        } else {
//            titleTab.setForeground(Color.BLACK);
//        }

        ButtonEditorTabComponent tabComponent = new ButtonEditorTabComponent(tabbedPane, titleTab, null);
        tabComponent.setPDFViewerPanel(pdfViewer);
        addTab(nameTab, allPanel, tabComponent);
    }

    public static JFrame getMainFrame() {
        return mainFrame;
    }
}
