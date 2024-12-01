package org.example.CardPanel;

import org.example.UDZNote;
import org.example.UFileService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class CardPanel extends JPanel {

    private ArrayList<JPanel> cards;
    private FlowLayout flowLayout;
    private int maxCardsPerRow;
    private int cardWidth;
    private int cardHeight;
    private int gap;
    private JScrollPane parentPanel;
    private int maxPanelWidth;

    public CardPanel(int totalCards, int cardWidth, int cardHeight, int gap) {
        this.cards = new ArrayList<>();
        this.flowLayout = new FlowLayout(FlowLayout.LEFT, gap, gap);
        this.maxCardsPerRow = 0;
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
        this.gap = gap;

        //setLayout(flowLayout);
        //setLayout(new GridLayout(0, 5, 12, 12));
        setLayout(new WrapLayout(FlowLayout.LEFT, 10, 10));

        // Добавляем карточки в контейнер
//        for (int i = 1; i <= totalCards; i++) {
//            JPanel card = createCard("Card " + i, new Color(100 + i * 5, 150, 200));
//            add(card);
//        }

        // Слушатель изменения размеров окна
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                maxPanelWidth = getParent().getWidth();
                //updateLayout(getWidth());

                // Обновляем максимальную ширину панели при изменении размеров окна
                //maxPanelWidth = Math.min(maxPanelWidth, getParent().getWidth());
                //System.out.println(maxPanelWidth);
            }
        });
    }

    public void setDataForCards(ArrayList<File> files) {
        removeAll();
        for(File file : files) {
            if (file.isDirectory()) {
                JPanel card = createCard(file.getName(), new Color(200, 123, 100), file, this);
                add(card);
            } else {
                JPanel card = createCard(file.getName(), new Color(94, 172, 220), file, this);
                add(card);
            }
        }
        repaint();
    }

    // Метод для создания строки (горизонтальный контейнер)
    private static JPanel createRow() {
        JPanel row = new JPanel();
        row.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Добавляем отступы между карточками
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        return row;
    }

    // Метод для создания отдельной карточки
    private static JPanel createCard(String title, Color bgColor, File file, CardPanel cardPanel) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(150, 200)); // Размер карточки
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Добавляем изображение
        JLabel imageLabel = new JLabel(new ImageIcon(new BufferedImage(150, 120, BufferedImage.TYPE_INT_RGB)));
        //JLabel imageLabel = new JLabel(new ImageIcon(UDZNote.WORKING_DIR + File.separator + "data" + File.separator + "bookIcon.jpg"));
        //JLabel imageLabel = new JLabel(new ImageIcon(UDZNote.WORKING_DIR + File.separator + "data" + File.separator + "bookIcon.jpg"));
        //imageLabel.setSize(150, 120);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.LIGHT_GRAY); // Замените на реальное изображение
        card.add(imageLabel, BorderLayout.NORTH);

        // Добавляем подпись
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.BLACK);
        card.add(titleLabel, BorderLayout.CENTER);

        JButton button = new JButton("Open");
        button.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (file.isDirectory()) {
                    cardPanel.setDataForCards(UFileService.getFiles(file.getPath()));
                } else {
                    UDZNote.openFile(file.getPath());
                }
            }
        });
        button.setText("Open");
        button.setSize(80, 30);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        card.add(button, BorderLayout.SOUTH);

        return card;
    }
}