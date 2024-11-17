package org.example.PDFPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PDFSelectionPanel extends JPanel {
    private final Image pdfImage; // Изображение текущей страницы
    private Rectangle selectionRectangle = null;
    private Point startPoint;

    public PDFSelectionPanel(Image pdfImage) {
        this.pdfImage = pdfImage;

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectionRectangle != null) {
                    // Обработка выделения текста
                    System.out.println("Выделение: " + selectionRectangle);
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point endPoint = e.getPoint();
                selectionRectangle = new Rectangle(
                        Math.min(startPoint.x, endPoint.x),
                        Math.min(startPoint.y, endPoint.y),
                        Math.abs(startPoint.x - endPoint.x),
                        Math.abs(startPoint.y - endPoint.y)
                );
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(pdfImage, 0, 0, getWidth(), getHeight(), this);

        // Отрисовка выделения
        if (selectionRectangle != null) {
            g.setColor(new Color(0, 0, 255, 50));
            g.fillRect(selectionRectangle.x, selectionRectangle.y, selectionRectangle.width, selectionRectangle.height);
            g.setColor(Color.BLUE);
            g.drawRect(selectionRectangle.x, selectionRectangle.y, selectionRectangle.width, selectionRectangle.height);
        }
    }
}
