package org.example.PDFPanel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.text.TextPosition;
import org.example.UDZNote;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFViewerPanel extends JPanel {

    private final List<TextPosition> characterPositions = new ArrayList<>();
    private List<TextPosition> selectedCharacters = new ArrayList<>();
    private PDDocument document = new PDDocument();
    private final PDFRenderer renderer;
    private PDRectangle documentBox;
    private File pdfFile;

    private int currentPage = 0;
    private double zoomFactor = 1.0;
    private int lastOpenedPage = -1;

    private BufferedImage currentPageImage;
    private Rectangle selectionRectangle;

    private Point startPoint;

    private final JTextField pageTextField = new JTextField("1/?");
    private final JTextField zoomTextField = new JTextField("100%");
    private final JPanel canvas = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (currentPageImage != null) {
                int width = (int) (currentPageImage.getWidth() * zoomFactor);
                int height = (int) (currentPageImage.getHeight() * zoomFactor);
                g.drawImage(currentPageImage, 0, 0, width, height, this);

                // Рисование выделения
                if (selectionRectangle != null) {
                    g.setColor(new Color(0, 0, 255, 50));
                    g.fillRect(selectionRectangle.x, selectionRectangle.y, selectionRectangle.width, selectionRectangle.height);
                    g.setColor(Color.BLUE);
                    g.drawRect(selectionRectangle.x, selectionRectangle.y, selectionRectangle.width, selectionRectangle.height);
                }
                if (!selectedCharacters.isEmpty()) {
                    highlightSelectedCharacters(selectedCharacters, g);
                }
            }
        }
    };

    public final static String COPY_ACTION = "Copy";
    public PDFViewerPanel(File pdfFile) throws IOException {
        this.pdfFile = pdfFile;
        ActionListener actionListener = e -> {
            if (isNumber(pageTextField.getText())) {
                currentPage = Integer.parseInt(pageTextField.getText().substring(0, indexSlash(pageTextField.getText())));
                currentPage--;
                if (currentPage < 0) {
                    currentPage = 0;
                }
                if (currentPage > document.getNumberOfPages() - 1) {
                    currentPage = document.getNumberOfPages() - 1;
                }
            }
            renderPage();
        };
        pageTextField.addActionListener(actionListener);


        document = PDDocument.load(pdfFile);
        renderer = new PDFRenderer(document);

        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);

        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                canvas.requestFocusInWindow(); // Получение фокуса при клике
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selectionRectangle = null;
                canvas.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                canvas.requestFocusInWindow(); // Получение фокуса при клике
            }
        });

        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point endPoint = e.getPoint();
                selectionRectangle = new Rectangle(
                        Math.min(startPoint.x, endPoint.x),
                        Math.min(startPoint.y, endPoint.y),
                        Math.abs(startPoint.x - endPoint.x),
                        Math.abs(startPoint.y - endPoint.y)
                );
                selectedCharacters = getSelectedCharacters(selectionRectangle);
                canvas.repaint();
            }
        });
        canvas.setFocusable(true);
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_C) && (e.isControlDown())) {
                    StringBuilder text = new StringBuilder();
                    for (var character : selectedCharacters) {
                        text.append(character.getUnicode());
                    }
                    Toolkit toolkit = Toolkit.getDefaultToolkit();
                    Clipboard clipboard = toolkit.getSystemClipboard();
                    StringSelection stringSelection = new StringSelection(text.toString());
                    clipboard.setContents(stringSelection, null);
                    e.consume();
                }
            }
        });
        canvas.setComponentPopupMenu(createPopupMenu());

        renderPage();
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu("Menu");

        JMenuItem copyTextMenuItem = getCopyTextMenuItem();
        popupMenu.add(copyTextMenuItem);

        JMenuItem createNoteMenuItem = getCreateNoteMenuItem();
        popupMenu.add(createNoteMenuItem);

        return popupMenu;
    }

    private JMenuItem getCopyTextMenuItem() {
        JMenuItem header = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                StringBuilder text = new StringBuilder();
                for (var character : selectedCharacters) {
                    text.append(character.getUnicode());
                }
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Clipboard clipboard = toolkit.getSystemClipboard();
                StringSelection stringSelection = new StringSelection(text.toString());
                clipboard.setContents(stringSelection, null);
            }
        });
        header.setText("Copy     ");
        return header;
    }

    private JMenuItem getCreateNoteMenuItem() {
        JMenuItem header = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                StringBuilder text = new StringBuilder();
                for (var character : selectedCharacters) {
                    text.append(character.getUnicode());
                }
                UDZNote.createNoteForBook(pdfFile, text.toString());
            }
        });
        header.setText("Создать заметку");
        return header;
    }

    public JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();

        JButton prevButton = new JButton("<");
        prevButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                renderPage();
            }
        });

        JButton nextButton = new JButton(">");
        nextButton.addActionListener(e -> {
            if (currentPage < document.getNumberOfPages() - 1) {
                currentPage++;
                renderPage();
            }
        });

        JButton zoomInButton = new JButton("+");
        zoomInButton.addActionListener(e -> {
            zoomFactor += 0.1;
            renderPage();
        });

        JButton zoomOutButton = new JButton("-");
        zoomOutButton.addActionListener(e -> {
            if (zoomFactor > 0.2) {
                zoomFactor -= 0.1;
                renderPage();
            }
        });

        zoomTextField.setText(String.valueOf(zoomFactor * 100));
        ActionListener actionListener = e -> {
            if (isNumber(zoomTextField.getText())) {
                zoomFactor = Integer.parseInt(zoomTextField.getText()) / 100.0;
            }
            renderPage();
        };
        zoomTextField.addActionListener(actionListener);

        controlPanel.add(prevButton);
        controlPanel.add(pageTextField);
        controlPanel.add(nextButton);

        controlPanel.add(zoomInButton);
        controlPanel.add(zoomTextField);
        controlPanel.add(zoomOutButton);

        return controlPanel;
    }

    private void renderPage() {
        try {
            currentPageImage = renderer.renderImageWithDPI(currentPage, (float) (150 * zoomFactor));
            documentBox = document.getPage(currentPage).getBBox();
            int width = (int) (currentPageImage.getWidth() * zoomFactor);
            int height = (int) (currentPageImage.getHeight() * zoomFactor);
            zoomTextField.setText(String.valueOf((int) (zoomFactor * 100)));

            if (isNewPage()) {
                updateCharacterPositions();
            }
            lastOpenedPage = currentPage;

            // Установка размеров панели
            canvas.setPreferredSize(new Dimension(width, height));
            canvas.revalidate(); // Обновление размеров панели

            currentPageImage = renderer.renderImageWithDPI(currentPage, (float) (150 * zoomFactor));
            pageTextField.setText(String.valueOf(currentPage+1) + '/' + document.getNumberOfPages());
            selectionRectangle = null;
            canvas.repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateCharacterPositions() {
        characterPositions.clear();
        selectedCharacters.clear();
        PDFTextStripper stripper = null;
        try {
            stripper = new PDFTextStripper() {
                @Override
                protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
                    characterPositions.addAll(textPositions);
                }

            };
            stripper.setStartPage(currentPage + 1);
            stripper.setEndPage(currentPage + 1);
            stripper.getText(document);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        canvas.repaint();
    }

    private List<TextPosition> getSelectedCharacters(Rectangle selection) {
        List<TextPosition> selectedCharacters = new ArrayList<>();

        int width = (int) (currentPageImage.getWidth() * zoomFactor);
        int height = (int) (currentPageImage.getHeight() * zoomFactor);

        Rectangle2D selectionInPDFCoordinates = new Rectangle2D.Double(
                selection.x,
                selection.y,
                selection.width,
                selection.height
        );

        boolean isStartingSelectingText = false;
        for (TextPosition character : characterPositions) {
            Rectangle2D charBounds = new Rectangle2D.Double(
                    (int) ((character.getXDirAdj() / documentBox.getWidth()) * width),
                    (int) ((character.getYDirAdj() / documentBox.getHeight()) * height - ((character.getHeightDir() / documentBox.getHeight()) * height)/2),
                    (int) ((character.getWidthDirAdj() / documentBox.getWidth()) * width),
                    (int) ((character.getHeightDir() / documentBox.getHeight()) * height / 2)
            );
            //Деление на два высоты символа нужно для того чтобы метод intersects() удобнее выделял символы

            if (!isStartingSelectingText && selectionInPDFCoordinates.intersects(charBounds)) {
                isStartingSelectingText = true;
            }

            //Умножение на 3 нужно для того чтобы вычислить высоту в полтора раза большую реальной высоты символа для того
            //чтобы определять границу выделения. Умножаю на 0.5 высоту символа для того чтобы
            if (isStartingSelectingText && ((charBounds.getY() + charBounds.getHeight() * 3 > selectionInPDFCoordinates.getY() + selectionInPDFCoordinates.getHeight()
                    && charBounds.getX() > selectionInPDFCoordinates.getX() + selectionInPDFCoordinates.getWidth())
            || charBounds.getY() + charBounds.getHeight() * 0.5 > selectionInPDFCoordinates.getY() + selectionInPDFCoordinates.getHeight())) {
                break;
            }
            if (isStartingSelectingText) {
                selectedCharacters.add(character);
            }
        }
        return selectedCharacters;
    }


    private void highlightSelectedCharacters(List<TextPosition> selectedCharacters, Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        g.setColor(new Color(19, 122, 255, 124));

        int width = (int) (currentPageImage.getWidth() * zoomFactor);
        int height = (int) (currentPageImage.getHeight() * zoomFactor);

        for (TextPosition character : selectedCharacters) {
            Rectangle2D charBounds = new Rectangle2D.Double(
                    character.getXDirAdj(),
                    character.getYDirAdj() - character.getHeightDir(),
                    character.getWidthDirAdj(),
                    character.getHeightDir()
            );


            g.fill(new Rectangle(
                    (int) ((charBounds.getX() / documentBox.getWidth()) * width),
                    (int) ((charBounds.getY() / documentBox.getHeight()) * height),
                    (int) ((charBounds.getWidth() / documentBox.getWidth()) * width),
                    (int) ((charBounds.getHeight() / documentBox.getHeight()) * height)
            ));
        }
    }

    private boolean isNewPage() {
        return currentPage != lastOpenedPage;
    }


    public void closeDocument() throws IOException {
        if (document != null) {
            document.close();
        }
    }

    private boolean isNumber(String str) {
        try {
            Integer.parseInt(str.substring(0, indexSlash(str)));
            return true; // Строка содержит число
        } catch (NumberFormatException e) {
            return false; // Строка не содержит число
        }
    }

    private int indexSlash(String str) {
        int result = str.indexOf('/');
        if (result == -1) {
            return str.length();
        }
        return result;
    }
}