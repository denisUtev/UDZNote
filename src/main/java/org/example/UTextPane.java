package org.example;

import com.github.rjeschke.txtmark.Configuration;
import com.github.rjeschke.txtmark.Processor;
import org.example.TextPaneActions.ExportMdFile;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

public class UTextPane extends JTextPane {

    final UndoManager undoMgr = new UndoManager();
    private JLabel titleTab;

    public FileTime lastModifiedTime;
    public File filePath;
    public String fileName;
    private Timer timerCheckUpdating;

    public final static String UNDO_ACTION = "Undo";
    public final static String REDO_ACTION = "Redo";
    public final static String SAVE_ACTION = "Save";
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

        setSettings();
    }

    public void setSettings() {
        setTabSize(22);
        getDocument().addUndoableEditListener(pEvt -> {
            undoMgr.addEdit(pEvt.getEdit());
            titleTab.setForeground(new Color(35, 135, 204));
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

        timerCheckUpdating = new Timer(3000, e -> checkUpdatesFile());
        timerCheckUpdating.start();

        setComponentPopupMenu(createPopupMenu());
    }

    protected void exportToRtf() {
        StyledDocument doc = (StyledDocument) getDocument();
        RTFEditorKit kit = new RTFEditorKit();

        BufferedOutputStream out;

        try {
            out = new BufferedOutputStream(new FileOutputStream(filePath));
            kit.write(out, doc, doc.getStartPosition().getOffset(), doc.getLength());
            out.close();
        } catch (IOException | BadLocationException ignored) {
        }
    }

    private void readRtfFile() {
        //setContentType("text/rtf");
        setContentType("text/html");
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



    private JPopupMenu createPopupMenu() {
        JPopupMenu pmenu = new JPopupMenu("Menu");

        JMenu headersMenu = new JMenu("Headers   ");
        JMenuItem header1 = getHeaderMenuItem("H1", 40);
        headersMenu.add(header1);
        JMenuItem header2 = getHeaderMenuItem("H2", 32);
        headersMenu.add(header2);
        JMenuItem header3 = getHeaderMenuItem("H3", 26);
        headersMenu.add(header3);

        pmenu.add(headersMenu);

        JMenu stylesMenu = new JMenu("Styles   ");
        JMenuItem style1 = getBoldMenuItem();
        stylesMenu.add(style1);
        JMenuItem style2 = getCursiveItem();
        stylesMenu.add(style2);
        JMenuItem style3 = getPastMenuItem();
        stylesMenu.add(style3);

        pmenu.add(stylesMenu);

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

        pmenu.add(colorsMenu);

        JMenu alginmentMenu = new JMenu("Alignment");
        JMenuItem algign1 = getAlignLeftMenuItem();
        alginmentMenu.add(algign1);
        JMenuItem algign2 = getAlignCenterMenuItem();
        alginmentMenu.add(algign2);
        JMenuItem algign3 = getAlignRightMenuItem();
        alginmentMenu.add(algign3);
        JMenuItem algign4 = getAlignJustifiedMenuItem();
        alginmentMenu.add(algign4);

        pmenu.add(alginmentMenu);

        return pmenu;
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

    public void setTitleTab(JLabel titleTab) {
        this.titleTab = titleTab;
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
