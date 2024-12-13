package org.example.CardPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.example.UDZNote.ROOT_PATH;

public class BookMarkPanel {

    // Список тегов
    String bookMark = "";

    public BookMarkPanel() {

    }

    public JPanel createBookMarkSearchPanel(CardPanel cardPanel) {
        JPanel searchPanel = new JPanel();
        searchPanel.setPreferredSize(new Dimension(150, 150));
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBorder(new EmptyBorder(10, 10, 25, 10));

        // Поле для поиска по имени
        JPanel nameSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel("Название:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 18));
        nameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardPanel.findCards(nameField.getText(), bookMark);
            }
        });

        nameSearchPanel.add(nameLabel, BorderLayout.WEST);
        nameSearchPanel.add(nameField, BorderLayout.CENTER);

        // Поле для поиска по тегам
        JPanel bookMarkSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel tagLabel = new JLabel("Избранное:");
        tagLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JTextField bookMarkField = new JTextField();
        bookMarkField.setFont(new Font("Arial", Font.PLAIN, 18));
        bookMarkSearchPanel.add(tagLabel, BorderLayout.WEST);
        bookMarkSearchPanel.add(bookMarkField, BorderLayout.CENTER);
        bookMarkField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookMark = bookMarkField.getText();
                cardPanel.findCards(nameField.getText(), bookMark);
            }
        });

        //tagsListPanel.setBackground(Color.WHITE);
        JButton updateCardsPanelButton = new JButton();
        updateCardsPanelButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cardPanel.reloadCardPanel();
                cardPanel.setDataForCards(org.example.UFileService.getFiles(ROOT_PATH));
                cardPanel.findCards(nameField.getText(), bookMark);
            }
        });
        updateCardsPanelButton.setText("Обновить");
        updateCardsPanelButton.setFont(new Font("Arial", Font.PLAIN, 16));


        nameField.setPreferredSize(new Dimension(200, 25));
        bookMarkField.setPreferredSize(new Dimension(150, 25));
        // Добавляем элементы в основную панель
        searchPanel.add(nameSearchPanel);
        searchPanel.add(Box.createVerticalStrut(10));
        searchPanel.add(bookMarkSearchPanel);
        searchPanel.add(Box.createVerticalStrut(10));
        JPanel updateButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        updateButtonPanel.add(updateCardsPanelButton);
        searchPanel.add(updateButtonPanel);

        return searchPanel;
    }
}