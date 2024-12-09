package org.example.CardPanel;

import org.example.UDZNote;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import static org.example.UDZNote.DEFAULT_TEXT_COLOR;

public class Card {

    private final String title;
    private final String description;
    private final String bookMark;
    private final String lastModifiedDate;
    private final ArrayList<String> tags;
    private final File file;
    private JPanel card;

    public Card(String title, String bookMark, String description, ArrayList<String> tags, String lastModifiedDate, File file) {
        this.title = title;
        this.bookMark = bookMark;
        this.description = description;
        this.tags = tags;
        this.lastModifiedDate = lastModifiedDate;
        this.file = file;
        initPanel();
    }

    private void initPanel() {
        card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 40, 0, 40), // Отступы слева и справа
                BorderFactory.createLineBorder(Color.GRAY, 1) // Граница карточки
        ));

        // Внутренний контейнер для текста
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        //contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Заголовок карточки
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        //titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        titleLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                titleLabel.setForeground(new Color(69, 142, 217));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                titleLabel.setForeground(DEFAULT_TEXT_COLOR);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                UDZNote.openFile(file.getPath());
            }
        });

        // Описание карточки
        JLabel descriptionLabel = new JLabel("<html>" + description + "</html>");
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        //descriptionLabel.setForeground(new Color(70, 70, 70));
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Список тегов
        JPanel tagsPanel = new JPanel();
        tagsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        //tagsPanel.setBackground(Color.WHITE);
        tagsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        tagsPanel.setPreferredSize(new Dimension(350, 55));

        for (String tag : tags) {
            JButton tagLabel = new JButton(tag);
            tagLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            tagLabel.setForeground(new Color(232, 181, 12));
            tagLabel.setOpaque(true);
            //tagLabel.setBackground(new Color(100, 150, 200));
            tagLabel.setMargin(new Insets(0, 4, 0, 4));
            tagsPanel.add(tagLabel);
        }

        // Добавление элементов в contentPanel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(titleLabel);
        if (!bookMark.isEmpty()) {
            JButton bookMarkButton = new JButton(bookMark);
            bookMarkButton.setFont(new Font("Arial", Font.ITALIC, 16));
            bookMarkButton.setForeground(new Color(86, 118, 253));
            //bookMarkButton.setBorder(new LineBorder(new Color(86, 118, 253)));
            RoundedBorder border = new RoundedBorder(6);
            border.setColor(new Color(86, 118, 253));
            bookMarkButton.setBorder(border);
            titlePanel.add(Box.createRigidArea(new Dimension(12, 0)));
            titlePanel.add(bookMarkButton);
        }
        //contentPanel.add(titleLabel);
        contentPanel.add(titlePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 6))); // Отступ между элементами
        contentPanel.add(descriptionLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        contentPanel.add(tagsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        // Надпись с датой последнего изменения
        JLabel dateLabel = new JLabel("Последнее изменение: " + lastModifiedDate);
        dateLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        //dateLabel.setForeground(new Color(100, 100, 100));
        dateLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Добавляем contentPanel и дату в карточку
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(dateLabel, BorderLayout.SOUTH);
    }

    public JPanel getCard() {
        return card;
    }

    public String getTitle() {
        return title;
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }
}
