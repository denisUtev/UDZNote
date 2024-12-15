package org.example;

import com.github.rjeschke.txtmark.Configuration;
import com.github.rjeschke.txtmark.Processor;
import org.example.TextPaneActions.*;
import org.example.TextPaneComponents.TagButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.List;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.min;

public class UTextPane extends JTextPane {

    final UndoManager undoMgr = new UndoManager();
    private JLabel titleTab;

    public FileTime lastModifiedTime;
    public File filePath;
    public String fileName;
    private Timer timerCheckUpdating;
    private JPopupMenu imagePopup;
    private JPopupMenu popupMenu;

    public final static String UNDO_ACTION = "Undo";
    public final static String REDO_ACTION = "Redo";
    public final static String SAVE_ACTION = "Save";
    public final static String PASTE_ACTION = "Paste";
    public final static String CHOOSE_STYLE_ACTION = "Set style";
    private boolean isChooseStyle = false;//Включен ли выбор стилизирования текста


    public UTextPane(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = new File(filePath);

        if (fileName.endsWith(".rtf")) {
            readRtfFile();
        } else if (fileName.endsWith(".md")) {
            readMdFile();
        }

        try {
            lastModifiedTime = Files.getLastModifiedTime(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(35, 35, 35, 35), // Отступы слева и справа
                BorderFactory.createLineBorder(Color.GRAY, 1) // Граница карточки
        ));

        setSettings();
    }


    public void setSettings() {
        setTabSize(22);
        getDocument().addUndoableEditListener(pEvt -> {
            undoMgr.addEdit(pEvt.getEdit());
            if (titleTab != null) {
                titleTab.setForeground(new Color(35, 135, 204));
            }
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    replaceHashtagsWithButtons();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    handleBackspace();
                }
            }
        });


        // Add undo/redo actions
        getActionMap().put(UNDO_ACTION, new AbstractAction(UNDO_ACTION) {
            public void actionPerformed(ActionEvent pEvt) {
                try {
                    int lastLength = getDocument().getLength();
                    //if (undoMgr.canUndo()) {
                    while (undoMgr.canUndo() && lastLength == getDocument().getLength()) {
                        undoMgr.undo();
                    }
                } catch (CannotUndoException e) {
                    //e.printStackTrace();
                }
            }
        });
        getActionMap().put(REDO_ACTION, new AbstractAction(REDO_ACTION) {
            public void actionPerformed(ActionEvent pEvt) {
                try {
                    int lastLength = getDocument().getLength();
                    while (undoMgr.canRedo() && lastLength == getDocument().getLength()) {
                        undoMgr.redo();
                    }
                } catch (CannotRedoException e) {
                    //e.printStackTrace();
                }
            }
        });
        getActionMap().put(SAVE_ACTION, new AbstractAction(SAVE_ACTION) {
            public void actionPerformed(ActionEvent pEvt) {
                saveText();
            }
        });
        getActionMap().put(PASTE_ACTION, new AbstractAction(PASTE_ACTION) {
            public void actionPerformed(ActionEvent pEvt) {
                // Получаем доступ к системному буферу обмена
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Clipboard clipboard = toolkit.getSystemClipboard();

                Transferable contents = clipboard.getContents(null);

                // Проверяем, есть ли изображение в буфере обмена
                if (clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
                    BufferedImage image = null;
                    try {
                        image = (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
                        ImageIcon icon = new ImageIcon(image);
                        new PasteImageAction(UTextPane.this, icon);
                    } catch (IOException | UnsupportedFlavorException e) {
                        throw new RuntimeException(e);
                    }
                } else if (contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    try {
                        java.util.List<File> files = (java.util.List<File>) contents.getTransferData(DataFlavor.javaFileListFlavor);
                        if (!files.isEmpty()) {
                            File firstFile = files.get(0);
                            ImageIcon icon = new ImageIcon(String.valueOf(firstFile));
                            new PasteImageAction(UTextPane.this, icon);
                        }
                    } catch (UnsupportedFlavorException | IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        String text = (String) clipboard.getData(DataFlavor.stringFlavor);
                        getDocument().insertString(getCaretPosition(), text, new SimpleAttributeSet());
                    } catch (UnsupportedFlavorException | IOException ex) {
                        ex.printStackTrace(); // Обрабатываем возможные исключения
                    } catch (BadLocationException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        getActionMap().put(CHOOSE_STYLE_ACTION, new AbstractAction(CHOOSE_STYLE_ACTION) {
            public void actionPerformed(ActionEvent pEvt) {
                isChooseStyle = !isChooseStyle;
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK),
                UNDO_ACTION);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK),
                REDO_ACTION);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK),
                SAVE_ACTION);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK),
                SAVE_ACTION);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK),
                PASTE_ACTION);

        timerCheckUpdating = new Timer(3000, e -> checkUpdatesFile());
        timerCheckUpdating.start();

        setComponentPopupMenu(createPopupMenu());
        initImagePopup();
        setTextPadding();

        setUI(new javax.swing.plaf.basic.BasicTextPaneUI() {
            @Override
            protected void paintSafely(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintSafely(g2d);
            }
        });
    }

    private void setTextPadding() {
        StyledDocument doc = getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setLeftIndent(attributes, 20); // Отступ слева
        StyleConstants.setRightIndent(attributes, 20); // Отступ справа
        StyleConstants.setFirstLineIndent(attributes, 30); // Отступ для первой строки
        doc.setParagraphAttributes(0, doc.getLength(), attributes, false);
    }


    private void replaceHashtagsWithButtons() {
        var doc = this.getStyledDocument();
        try {
            String text = doc.getText(0, doc.getLength());

            // проверяем хештег ли
            int start = getCaretPosition() - 2;
            start = getStartTag(text, start);
            if (text.charAt(start) == '#' && (text.charAt(start - 1) == ' '
                    || text.charAt(start - 1) == '\n' || text.charAt(start - 1) == '\t')) {
                int end = getEndTag(text, start + 1);
                String hashtag = text.substring(start, end);
                if (!hashtag.trim().isEmpty()) {
                    // Создаем кнопку для хештега
                    JButton button = createHashTagButton(hashtag);

                    // Заменяем текст на кнопку
                    SimpleAttributeSet attrs = new SimpleAttributeSet();
                    StyleConstants.setComponent(attrs, button);

                    doc.remove(start, end - start);
                    doc.insertString(start, " ", attrs);
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private int getEndTag(String text, int startPos) {
        String stopChars = "# \n\t.,!?\"'*-+=~`:^%$№;<>\\|/";
        int end = startPos;
        while (end < text.length() && stopChars.indexOf(text.charAt(end)) == -1) {
            end++;
        }
        return end;
    }

    private int getStartTag(String text, int startPos) {
        String stopChars = "# \n\t.,!?\"'*-+=~`:^%$№;<>\\|/";
        int start = startPos;
        while (start >= 0 && stopChars.indexOf(text.charAt(start)) == -1) {
            start--;
        }
        return start;
    }

    private void handleBackspace() {
        var doc = this.getStyledDocument();
        try {
            int caretPosition = getCaretPosition();
            if (caretPosition > 0) {
                Element elem = doc.getCharacterElement(caretPosition - 1);
                AttributeSet attrs = elem.getAttributes();
                Component comp = StyleConstants.getComponent(attrs);

                if (comp instanceof TagButton) {
                    String hashtag = ((JButton) comp).getText();
                    doc.insertString(caretPosition - 1, hashtag, new SimpleAttributeSet());
                    setCaretPosition(caretPosition + hashtag.length());
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }



    boolean isImagePopupMenu = false;
    Element selectedImageElement;
    int selectedImageElementPosition = -1;
    private void initImagePopup() {
        // Создание контекстного меню для изображений
        imagePopup = new JPopupMenu();
        JMenuItem changeImageItem = new JMenuItem("edit image");
        changeImageItem.addActionListener(e -> changeImage());
        imagePopup.add(changeImageItem);
        Component separator = new JPopupMenu.Separator();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    showContextMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            private void showContextMenu(MouseEvent e) {
                int pos = viewToModel2D(e.getPoint());
                if (pos >= 0) {
                    StyledDocument doc = getStyledDocument();
                    Element element = doc.getCharacterElement(pos);
                    Element element2 = doc.getCharacterElement(pos - 1);
                    boolean isImageSelected = false;
                    if (isDocElementHasStyle(element, StyleConstants.IconElementName)) {
                        selectedImageElement = element;
                        selectedImageElementPosition = pos;
                        isImageSelected = true;
                    }
                    if (isDocElementHasStyle(element2, StyleConstants.IconElementName)) {
                        selectedImageElement = element2;
                        selectedImageElementPosition = pos - 1;
                        isImageSelected = true;
                    }
                    if (isImageSelected) {
                        popupMenu.add(separator);
                        popupMenu.add(changeImageItem);
                        isImagePopupMenu = true;
                    } else if (isImagePopupMenu) {
                        isImagePopupMenu = false;
                        popupMenu.remove(changeImageItem);
                        popupMenu.remove(separator);
                    }
                }
            }
        });
    }

    private void changeImage() {
        AttributeSet attrs = selectedImageElement.getAttributes();
        Object value = attrs.getAttribute(StyleConstants.IconElementName);
        for (Iterator<?> it = attrs.getAttributeNames().asIterator(); it.hasNext(); ) {
            var at = it.next();
            if (at.toString().equals("icon")) {
                value = attrs.getAttribute(at);
            }
        }

        String linkImage = linksImagesDictionary.get(value);
        int width = -1;
        int height = -1;
        Pattern pattern = Pattern.compile("!\\[(.*?)]\\((.*?)\\)");
        Matcher matcher = pattern.matcher(linkImage);
        if (matcher.find()) {
            Pattern pattern1 = Pattern.compile("(\\d+)x(\\d+)");
            Matcher matcher1 = pattern1.matcher(matcher.group(1));
            if (matcher1.find()) {
                String textSize = matcher.group(1);
                width = Integer.parseInt(textSize.substring(0, textSize.indexOf('x')));
                height = Integer.parseInt(textSize.substring(textSize.indexOf('x') + 1));
            }
            new EditImageAction(this, (ImageIcon) value, matcher.group(2), width, height, selectedImageElementPosition);
        }
    }

    public void stopCheckUpdatingFile() {
        timerCheckUpdating.stop();
    }

    public void saveText() {
        try {
            //String filePath = fil;
            if (filePath != null && filePath.exists() && filePath.isFile()) {
                if (fileName.endsWith(".rtf")) {
                    exportToRtf();
                } else if (fileName.endsWith(".md")) {
                    exportToMd();
                } else {
                    UFileService.saveFile(filePath.getPath(), getText());
                }
                titleTab.setForeground(Color.WHITE);
                try {
                    lastModifiedTime = Files.getLastModifiedTime(Paths.get(filePath.getPath()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (CannotRedoException e) {
            //e.printStackTrace();
        }
    }

    protected void exportToRtf() {
        replaceImagesToLinks();
        replaceTagButtons();
        replaceButtonsWithMarkdownLinks();
        StyledDocument doc = (StyledDocument) getDocument();
        RTFEditorKit kit = new RTFEditorKit();

        BufferedOutputStream out;

        try {
            out = new BufferedOutputStream(new FileOutputStream(filePath));
            kit.write(out, doc, doc.getStartPosition().getOffset(), doc.getLength());
            out.close();
        } catch (IOException | BadLocationException ignored) {
        }
        updateDocumentView();
    }

    private void replaceImagesToLinks() {
        StyledDocument doc = getStyledDocument();
        String text;
        try {
            text = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        int length = doc.getLength();
        for (int i = length - 1; i >= 0; i--) {
            Element element = doc.getCharacterElement(i);
            AttributeSet attrs = element.getAttributes();
            if (text.charAt(i) == '~') {
                if (attrs.containsAttribute(StyleConstants.NameAttribute, StyleConstants.IconElementName)) {
                    Object value = attrs.getAttribute(StyleConstants.IconElementName);
                    for (Iterator<?> it = attrs.getAttributeNames().asIterator(); it.hasNext(); ) {
                        var at = it.next();
                        if (at.toString().equals("icon")) {
                            value = attrs.getAttribute(at);
                        }
                    }
                    try {
                        doc.remove(i, 1);
                        doc.insertString(i, linksImagesDictionary.get(value), new SimpleAttributeSet());
                    } catch (BadLocationException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private boolean isDocElementHasStyle(Element element, String styleName) {
        AttributeSet attrs = element.getAttributes();
        return attrs.containsAttribute(StyleConstants.NameAttribute, styleName);
    }

    Dictionary<ImageIcon, String> linksImagesDictionary = new Hashtable<>();

    private void readRtfFile() {
        setContentType("text/rtf");
        //setContentType("text/html");
        setDocument(new HTMLDocument());

        RTFEditorKit kit = new RTFEditorKit();
        setEditorKit(kit);

        BufferedInputStream out;
        try {
            out = new BufferedInputStream(new FileInputStream(filePath));
            kit.read(out, getDocument(), 0);
            out.close();
        } catch (IOException | BadLocationException ignored) {
        }
        updateDocumentView();
    }


    Dictionary<JButton, String> linksButtonsDictionary = new Hashtable<>();
    public void insertMarkdownLinks() {
        StyledDocument doc = getStyledDocument();
        String text = null;
        try {
            text = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        Pattern pattern = Pattern.compile("\\[(.*?)]\\((.*?)\\)");  // Регулярное выражение для поиска ссылок в формате [текст](ссылка)
        Matcher matcher = pattern.matcher(text);
        int accumulator = 0;

        while (matcher.find()) {
            // Создаем кнопку с текстом ссылки
            String linkText = matcher.group(1);  // Текст ссылки
            String url = matcher.group(2);       // URL ссылки
            JButton linkButton = createLinkButton(linkText, url);
            linksButtonsDictionary.put(linkButton, url);

            // Вставляем кнопку в документ
            try {
                getDocument().remove(matcher.start() - accumulator, matcher.end() - matcher.start());
                if (lastCaretPosition >= matcher.start()) {
                    lastCaretPosition -= matcher.end() - matcher.start() - 1;
                }
                setCaretPosition(matcher.start() - accumulator);
                insertComponent(linkButton);
                accumulator += matcher.end() - matcher.start();
                accumulator--;
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void insertTagButtons() {
        StyledDocument doc = getStyledDocument();
        String text = null;
        try {
            text = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        Pattern pattern = Pattern.compile("#[\\p{L}_\\d]+");
        Matcher matcher = pattern.matcher(text);
        int accumulator = 0;

        while (matcher.find()) {
            // Создаем кнопку с текстом ссылки
            String tagText = matcher.group();
            JButton linkButton = createHashTagButton(tagText);

            // Вставляем кнопку в документ
            try {
                getDocument().remove(matcher.start() - accumulator, matcher.end() - matcher.start());
                if (lastCaretPosition >= matcher.start()) {
                    lastCaretPosition -= matcher.end() - matcher.start() - 1;
                }
                setCaretPosition(matcher.start() - accumulator);
                insertComponent(linkButton);
                accumulator += matcher.end() - matcher.start();
                accumulator--;
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static JButton createLinkButton(String text, String url) {
        JButton button = new JButton(text);
        button.setAlignmentY(0.8f);

        // Действие при нажатии на кнопку
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Link clicked: " + url);
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        URI uri = null;
                        try {
                            uri = new URI(url);
                            desktop.browse(uri);
                        } catch (URISyntaxException | IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else {
                        System.err.println("Открытие браузера не поддерживается.");
                    }
                } else {
                    System.err.println("Рабочий стол не поддерживается.");
                }
                // Здесь можно добавить переход по ссылке или другое действие
            }
        });

        return button;
    }

    private static TagButton createHashTagButton(String tag) {
        TagButton button = new TagButton(tag);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        return button;
    }


    public void replaceTagButtons() {
        StyledDocument doc = getStyledDocument();

        for (int i = 0; i < doc.getLength(); i++) {
            try {
                Element element = doc.getCharacterElement(i);
                AttributeSet attrs = element.getAttributes();
                if (StyleConstants.getComponent(attrs) instanceof TagButton) {
                    JButton button = (JButton) StyleConstants.getComponent(attrs);
                    String buttonText = button.getText();

                    getDocument().remove(i, 1);
                    getDocument().insertString(i, buttonText, new SimpleAttributeSet());
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    public void replaceButtonsWithMarkdownLinks() {
        StyledDocument doc = getStyledDocument();

        for (int i = 0; i < doc.getLength(); i++) {
            try {
                Element element = doc.getCharacterElement(i);
                AttributeSet attrs = element.getAttributes();
                if (StyleConstants.getComponent(attrs) instanceof JButton) {
                    JButton button = (JButton) StyleConstants.getComponent(attrs);
                    String buttonText = button.getText();
                    String url = linksButtonsDictionary.get(button); // Здесь можно установить URL, если он сохранен с кнопкой

                    getDocument().remove(i, 1);
                    getDocument().insertString(i, String.format("[%s](%s)", buttonText, url), new SimpleAttributeSet());
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }


    //Функция находит в файле ссылки на изображения и вставляет их в документ
    private void findImages() {
        String text = null;
        try {
            text = getDocument().getText(0, getDocument().getLength());
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        // Регулярное выражение для поиска изображений в формате Markdown
        Pattern pattern = Pattern.compile("!\\[(.*?)]\\((.*?)\\)");
        Matcher matcher = pattern.matcher(text);

        int accumulator = 0;
        while (matcher.find()) {
            try {
                getDocument().remove(matcher.start() - accumulator, matcher.end() - matcher.start());
                if (lastCaretPosition >= matcher.start()) {
                    lastCaretPosition -= matcher.end() - matcher.start() - 1;
                }
                ImageIcon image;
                Pattern pattern1 = Pattern.compile("(\\d+)x(\\d+)");
                Matcher matcher1 = pattern1.matcher(matcher.group(1));
                if (matcher1.find()) {
                    String textSize = matcher.group(1);
                    int width = Integer.parseInt(textSize.substring(0, textSize.indexOf('x')));
                    int height = Integer.parseInt(textSize.substring(textSize.indexOf('x') + 1));
                    image = getResizedImage(matcher.group(2), width, height);
                } else {
                    image = loadImage(matcher.group(2));
                }
                addImage(image, matcher.start() - accumulator);
                linksImagesDictionary.put(image, matcher.group());
                accumulator += matcher.end() - matcher.start();
                accumulator--;
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    int lastCaretPosition = 0;
    public void updateDocumentView() {
        Container parentScrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, this);
        JScrollPane scrollPane = (JScrollPane) parentScrollPane;
        lastCaretPosition = getCaretPosition();
        int verticalScroll = 0;
        int horizontalScroll = 0;
        if (scrollPane != null) {
            verticalScroll = scrollPane.getVerticalScrollBar().getValue();
            horizontalScroll = scrollPane.getHorizontalScrollBar().getValue();
        }

        findImages();
        insertTagButtons();
        insertMarkdownLinks();

        if (scrollPane != null) {
            setCaretPosition(min(lastCaretPosition, getDocument().getLength()));
            scrollPane.getVerticalScrollBar().setValue(verticalScroll);
            scrollPane.getHorizontalScrollBar().setValue(horizontalScroll);
        }
    }


    Dictionary<String, ImageIcon> pathsImagesDictionary = new Hashtable<>();
    private ImageIcon loadImage(String path) {
        ImageIcon image = pathsImagesDictionary.get(path);
        if (image == null) {
            image = new ImageIcon(path);
            pathsImagesDictionary.put(path, image);
        }
        return image;
    }

    private ImageIcon getResizedImage(String path, int width, int height) {
        String mnimoPath = width + "x" + height + "=" + path;
        ImageIcon image = pathsImagesDictionary.get(mnimoPath);
        if (image != null) {
            return image;
        }
        File file = new File(path); // Укажите путь к вашему изображению
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Создаем новое изображение с новыми размерами
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        resizedImage.getGraphics().drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        image = new ImageIcon(resizedImage);
        pathsImagesDictionary.put(mnimoPath, image);
        return image;
    }


    private void exportToMd() {
        ExportMdFile.export(getText(), getStyledDocument(), filePath);
    }

    private void readMdFile() {
        String text = UFileService.loadFile(filePath.getPath());
        //setText(text);
        // Создаем парсер и рендерер
        String htmlText = convertMarkdownToHtml(text);
        setContentType("text/html");
        setForeground(UDZNote.DEFAULT_TEXT_COLOR);
        setText(htmlText);
    }

    public static String convertMarkdownToHtml(String markdownText) {
        Configuration config = Configuration.builder().forceExtentedProfile().build();
        return Processor.process(markdownText, config);
    }

    private void checkUpdatesFile() {
        try {
            var lastTime = Files.getLastModifiedTime(Paths.get(filePath.getPath()));
            if (lastModifiedTime.compareTo(lastTime) != 0 && !fileName.endsWith(".md")) {
                setText(UFileService.loadFile(filePath.getPath()));
                lastModifiedTime = lastTime;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Не удалось обновить содержимое файла " + fileName);
            throw new RuntimeException(e);
        }
    }

    public void setTabSize(int tabSize) {
        // Once tab count exceed x times, it will make a small space only
        int maxTabsPerRow = 10;

        TabStop[] tabs = new TabStop[maxTabsPerRow];
        for(int i = 0; i < maxTabsPerRow; i++) {
            tabs[i] = new TabStop(tabSize*(i+1), TabStop.ALIGN_LEFT, TabStop.LEAD_NONE);
        }

        TabSet tabset = new TabSet(tabs);

        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.TabSet, tabset);
        setParagraphAttributes(aset, false);
    }

    private void addImage(ImageIcon imageIcon, int index) {
        try {
            StyledDocument doc = getStyledDocument();
            Style style = doc.addStyle(StyleConstants.IconElementName, null);
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER); // Выравнивание по центру
            StyleConstants.setIcon(style, imageIcon);

            doc.insertString(index, "~", style); // Добавляем строку с изображением
        } catch (BadLocationException e) {
            e.printStackTrace(); // Обработка исключения
        }
    }


    private JPopupMenu createPopupMenu() {
        popupMenu = new JPopupMenu("Menu");

        JMenu headersMenu = new JMenu("Headers   ");
        JMenuItem header1 = getHeaderMenuItem("H1", 40);
        headersMenu.add(header1);
        JMenuItem header2 = getHeaderMenuItem("H2", 32);
        headersMenu.add(header2);
        JMenuItem header3 = getHeaderMenuItem("H3", 26);
        headersMenu.add(header3);

        popupMenu.add(headersMenu);

        JMenu stylesMenu = new JMenu("Styles   ");
        JMenuItem style1 = getBoldMenuItem();
        stylesMenu.add(style1);
        JMenuItem style2 = getCursiveItem();
        stylesMenu.add(style2);
        JMenuItem style3 = getPastMenuItem();
        stylesMenu.add(style3);

        popupMenu.add(stylesMenu);

        JMenu colorsMenu = new JMenu("Colors   ");
        JMenuItem color1 = getColorMenuItem(Color.BLACK, "black");
        colorsMenu.add(color1);
        JMenuItem color2 = getColorMenuItem(Color.GRAY, "gray");
        colorsMenu.add(color2);
        JMenuItem color3 = getColorMenuItem(Color.BLUE, "blue");
        colorsMenu.add(color3);
        JMenuItem color4 = getColorMenuItem(Color.GREEN, "green");
        colorsMenu.add(color4);
        JMenuItem color5 = getColorMenuItem(Color.RED, "red");
        colorsMenu.add(color5);
        JMenuItem color6 = getColorMenuItem(Color.ORANGE, "orange");
        colorsMenu.add(color6);
        JMenuItem color7 = getColorMenuItem(Color.YELLOW, "yellow");
        colorsMenu.add(color7);
        JMenuItem color8 = getColorMenuItem(Color.WHITE, "white");
        colorsMenu.add(color8);

        popupMenu.add(colorsMenu);


        JMenu backgroundsMenu = new JMenu("Backgrounds");
        JMenuItem background1 = getBackgroundMenuItem(Color.BLACK, "black");
        backgroundsMenu.add(background1);
        JMenuItem background2 = getBackgroundMenuItem(Color.GRAY, "gray");
        backgroundsMenu.add(background2);
        JMenuItem background3 = getBackgroundMenuItem(Color.BLUE, "blue");
        backgroundsMenu.add(background3);
        JMenuItem background4 = getBackgroundMenuItem(Color.GREEN, "green");
        backgroundsMenu.add(background4);
        JMenuItem background5 = getBackgroundMenuItem(Color.RED, "red");
        backgroundsMenu.add(background5);
        JMenuItem background6 = getBackgroundMenuItem(Color.ORANGE, "orange");
        backgroundsMenu.add(background6);
        JMenuItem background7 = getBackgroundMenuItem(Color.YELLOW, "yellow");
        backgroundsMenu.add(background7);
        JMenuItem background8 = getBackgroundMenuItem(Color.WHITE, "white");
        backgroundsMenu.add(background8);
        JMenuItem background9 = getBackgroundMenuItem(getBackground(), "clear");
        backgroundsMenu.add(background9);

        popupMenu.add(backgroundsMenu);


        JMenu alginmentMenu = new JMenu("Alignment");
        JMenuItem algign1 = getAlignLeftMenuItem();
        alginmentMenu.add(algign1);
        JMenuItem algign2 = getAlignCenterMenuItem();
        alginmentMenu.add(algign2);
        JMenuItem algign3 = getAlignRightMenuItem();
        alginmentMenu.add(algign3);
        JMenuItem algign4 = getAlignJustifiedMenuItem();
        alginmentMenu.add(algign4);

        popupMenu.add(alginmentMenu);

        JMenuItem addLine = new JMenuItem("Add line  ");
        addLine.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //addLine(getCaretPosition());
            }
        });
        addLine.setText("Add line  ");
        popupMenu.add(addLine);

        JMenuItem addImage = new JMenuItem("Add image ");
        addImage.setAction(new AddImageAction(this));
        addImage.setText("Add image ");
        popupMenu.add(addImage);

        JMenuItem addLink = getSetLinkItem();
        popupMenu.add(addLink);

        return popupMenu;
    }

    private JMenuItem getSetLinkItem() {
        JMenuItem setLink = new JMenuItem("Add link  ");
        setLink.setAction(new AddLinkAction(this));
        setLink.setText("Add link  ");
        return setLink;
    }

    private JMenuItem getHeaderMenuItem(String name, int fontSize) {
        JMenuItem header = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SimpleAttributeSet headerStyle = new SimpleAttributeSet();
                StyleConstants.setFontSize(headerStyle, fontSize);
                StyledDocument doc = getStyledDocument();
                doc.setCharacterAttributes(getSelectionStart(), getSelectionEnd() - getSelectionStart(), headerStyle, false);
                titleTab.setForeground(new Color(35, 135, 204));
            }
        });
        header.setText(name);
        return header;
    }

    private JMenuItem getPastMenuItem() {
        JMenuItem past = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SimpleAttributeSet headerStyle = new SimpleAttributeSet();
                StyleConstants.setFontSize(headerStyle, 18);
                StyleConstants.setItalic(headerStyle, false);
                StyleConstants.setBold(headerStyle, false);
                StyleConstants.setUnderline(headerStyle, false);
                StyledDocument doc = getStyledDocument();
                doc.setCharacterAttributes(getSelectionStart(), getSelectionEnd() - getSelectionStart(), headerStyle, false);
                titleTab.setForeground(new Color(35, 135, 204));
            }
        });
        past.setText("Past");
        return past;
    }

    private JMenuItem getBoldMenuItem() {
        JMenuItem bold = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SimpleAttributeSet headerStyle = new SimpleAttributeSet();
                StyleConstants.setBold(headerStyle, true);
                StyledDocument doc = getStyledDocument();
                doc.setCharacterAttributes(getSelectionStart(), getSelectionEnd() - getSelectionStart(), headerStyle, false);
                titleTab.setForeground(new Color(35, 135, 204));
            }
        });
        bold.setText("Bold");
        return bold;
    }

    private JMenuItem getCursiveItem() {
        JMenuItem bold = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SimpleAttributeSet headerStyle = new SimpleAttributeSet();
                StyleConstants.setItalic(headerStyle, true);
                StyledDocument doc = getStyledDocument();
                doc.setCharacterAttributes(getSelectionStart(), getSelectionEnd() - getSelectionStart(), headerStyle, false);
                titleTab.setForeground(new Color(35, 135, 204));
            }
        });
        bold.setText("Cursive");
        return bold;
    }

    private JMenuItem getAlignLeftMenuItem() {
        JMenuItem alignLeftItem = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SimpleAttributeSet alignStyle = new SimpleAttributeSet();
                StyleConstants.setAlignment(alignStyle, StyleConstants.ALIGN_LEFT);
                StyledDocument doc = getStyledDocument();
                doc.setParagraphAttributes(getSelectionStart(),
                        getSelectionEnd() - getSelectionStart(), alignStyle, false);
                titleTab.setForeground(new Color(35, 135, 204));
            }
        });
        alignLeftItem.setText("Left");
        return alignLeftItem;
    }

    private JMenuItem getAlignCenterMenuItem() {
        JMenuItem bold = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SimpleAttributeSet alignStyle = new SimpleAttributeSet();
                StyleConstants.setAlignment(alignStyle, StyleConstants.ALIGN_CENTER);
                StyledDocument doc = getStyledDocument();
                doc.setParagraphAttributes(getSelectionStart(), getSelectionEnd() - getSelectionStart(), alignStyle, false);
                titleTab.setForeground(new Color(35, 135, 204));
            }
        });
        bold.setText("Center");
        return bold;
    }

    private JMenuItem getAlignRightMenuItem() {
        JMenuItem bold = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SimpleAttributeSet alignStyle = new SimpleAttributeSet();
                StyleConstants.setAlignment(alignStyle, StyleConstants.ALIGN_RIGHT);
                StyledDocument doc = getStyledDocument();
                doc.setParagraphAttributes(getSelectionStart(), getSelectionEnd() - getSelectionStart(), alignStyle, false);
                titleTab.setForeground(new Color(35, 135, 204));
            }
        });
        bold.setText("Right");
        return bold;
    }

    private JMenuItem getAlignJustifiedMenuItem() {
        JMenuItem bold = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SimpleAttributeSet alignStyle = new SimpleAttributeSet();
                StyleConstants.setAlignment(alignStyle, StyleConstants.ALIGN_JUSTIFIED);
                StyledDocument doc = getStyledDocument();
                doc.setParagraphAttributes(getSelectionStart(), getSelectionEnd() - getSelectionStart(), alignStyle, false);
                titleTab.setForeground(new Color(35, 135, 204));
            }
        });
        bold.setText("Justified");
        return bold;
    }

    private JMenuItem getColorMenuItem(Color col, String name) {
        JMenuItem bold = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SimpleAttributeSet headerStyle = new SimpleAttributeSet();
                StyleConstants.setForeground(headerStyle, col);
                StyledDocument doc = getStyledDocument();
                doc.setCharacterAttributes(getSelectionStart(), getSelectionEnd() - getSelectionStart(), headerStyle, false);
                titleTab.setForeground(new Color(35, 135, 204));
            }
        });
        bold.setText(name);
        return bold;
    }

    private JMenuItem getBackgroundMenuItem(Color col, String name) {
        JMenuItem bold = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SimpleAttributeSet headerStyle = new SimpleAttributeSet();
                //StyleConstants.setForeground(headerStyle, col);
                StyleConstants.setBackground(headerStyle, col);
                StyledDocument doc = getStyledDocument();
                doc.setCharacterAttributes(getSelectionStart(), getSelectionEnd() - getSelectionStart(), headerStyle, false);

                SimpleAttributeSet clearStyle = new SimpleAttributeSet(); // Создаем пустой набор атрибутов
                StyleConstants.setBackground(clearStyle, getBackground()); // Устанавливаем прозрачный фон
                doc.setCharacterAttributes(getSelectionEnd(), doc.getLength() - getSelectionEnd(), clearStyle, false);
                titleTab.setForeground(new Color(35, 135, 204));
            }
        });
        bold.setText(name);
        return bold;
    }

    private void addLine(int index) {
        try {
            String rtfLine = "{\\pard\\qr{\\brdrb\\brdrs\\brdrw10\\brsp20 \\cell}\\par}";
            ByteArrayInputStream rtfStream = new ByteArrayInputStream(rtfLine.getBytes());
            getEditorKit().read(rtfStream, getDocument(), index);
            titleTab.setForeground(new Color(35, 135, 204));
        } catch (BadLocationException e) {
            e.printStackTrace(); // Обработка исключения
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTitleTab(JLabel titleTab) {
        this.titleTab = titleTab;
    }

    public static DefaultStyledDocument cloneStyledDoc(StyledDocument source) {
        try {
            DefaultStyledDocument retDoc = new DefaultStyledDocument();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(source); // write object to byte stream

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray() );
            ObjectInputStream ois = new ObjectInputStream(bis);
            retDoc = (DefaultStyledDocument) ois.readObject(); //read object from stream
            ois.close();
            return retDoc;
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Override
//    protected void paintComponent(Graphics g) {
//        Graphics2D g2d = (Graphics2D) g;
//
//        // Включение антиалиасинга
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//
//        g2d.setColor(getBackground());
//        g2d.fillRect(0, 0, getWidth(), getHeight());
//        try {
//            Rectangle rect = modelToView(getCaretPosition());
//            if (rect != null) {
//                if(THEME.equals("light"))
//                    g2d.setColor(new Color(0xe3f2fd));
//                else
//                    g2d.setColor(Color.DARK_GRAY);
//                //g.fillRect(0, rect.y, getWidth(), rect.height);
//                g2d.fillRoundRect(0, rect.y, getWidth(), rect.height, 50, 50);
//            }
//        } catch (BadLocationException e) {
//            System.out.println(e);
//        }
//        super.paintComponent(g);
//
//
//        try {
//            Rectangle rect = modelToView(getCaretPosition());
//            if (rect != null && isChooseStyle) {
//                String word = "";
//                int start = 0;
//                if(codeExtension.wordAdditions.size() >= 4)
//                    start = Math.min(codeExtension.choosedAddition, codeExtension.wordAdditions.size() - 4);
//                int height = rect.height * (Math.min(codeExtension.choosedAddition + 4, codeExtension.wordAdditions.size()) - start);
//                g2d.setColor(new Color(0x444444));
//                //g2d.fillRect(rect.x - 2, rect.y + rect.height - 2, rect.height * 10 + 4, height + 4);
//                g2d.fillRoundRect(rect.x - 2, rect.y + rect.height - 2, rect.height * 10 + 4, height + 4, 7, 7);
//                for(int i = start; i < Math.min(codeExtension.choosedAddition + 4, codeExtension.wordAdditions.size()); i++) {
//                    word = codeExtension.wordAdditions.get(i);
//                    g2d.setColor(new Color(0xFFFFFF));
//                    if(ideModel.getTheme().equals("dark"))
//                        g2d.setColor(new Color(0x777777));
//                    if(i == codeExtension.choosedAddition)
//                        g2d.setColor(new Color(0x619DEA));
//                    g2d.fillRect(rect.x, rect.y + rect.height * (i - start + 1), rect.height * 10, rect.height);
//                    g2d.setFont(getFont());
//                    g2d.setColor(Color.BLACK);
//                    if(ideModel.getTheme().equals("dark"))
//                        g2d.setColor(Color.WHITE);
//                    g2d.drawString(word, rect.x + rect.height / 3, rect.y - rect.height / 3 + rect.height * (i - start + 2));
//                }
//                if(codeExtension.wordAdditions.size() > 4) {
//                    g2d.setColor(new Color(0xCCCCCC));
//                    g2d.fillRect(rect.x + (int) (rect.height * 9.5f), rect.y + rect.height, rect.height / 2, height);
//                    g2d.setColor(new Color(0x999999));
//                    float sizeSingleStroke = height / (float)codeExtension.wordAdditions.size();
//                    int w = (int)((height / (float)(codeExtension.wordAdditions.size())) * 4);
//                    g2d.fillRect(rect.x + (int) (rect.height * 9.5f), rect.y + rect.height + (int)(Math.min(codeExtension.choosedAddition, codeExtension.wordAdditions.size() - 4) * sizeSingleStroke), rect.height / 2, w + 1);
//                }
//            }
//        } catch (BadLocationException e) {
//            System.out.println(e);
//        }
//    }
//
//    @Override
//    public void repaint(long tm, int x, int y, int width, int height) {
//        super.repaint(tm, 0, 0, getWidth(), getHeight());
//    }
}
