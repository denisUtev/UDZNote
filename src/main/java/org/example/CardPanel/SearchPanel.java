package org.example.CardPanel;

import org.example.Params;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static org.example.UDZNote.ROOT_PATH;

public class SearchPanel {

    // Список тегов
    ArrayList<String> tags = new ArrayList<>();

    public SearchPanel() {

    }

    public JPanel createSearchPanel(CardPanel cardPanel) {
        JPanel searchPanel = new JPanel();
        searchPanel.setPreferredSize(new Dimension(150, 150));
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBorder(new EmptyBorder(10, 10, 25, 10));

        // Поле для поиска по имени
        JPanel nameSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel("Название:");
        nameLabel.setFont(Params.BIG_LABEL_FONT);
        JTextField nameField = new JTextField();
        nameField.setFont(Params.TAB_TITLE_FONT);
        nameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardPanel.findCards(nameField.getText(), tags);
            }
        });

        nameSearchPanel.add(nameLabel, BorderLayout.WEST);
        nameSearchPanel.add(nameField, BorderLayout.CENTER);

        // Поле для поиска по тегам
        JPanel tagSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel tagLabel = new JLabel("Тэг:");
        tagLabel.setFont(Params.BIG_LABEL_FONT);
        JTextField tagField = new JTextField();
        tagField.setFont(Params.TAB_TITLE_FONT);
        JButton addTagButton = new JButton("\uE145");
        addTagButton.setFont(Params.BUTTONS_FONT2);
        addTagButton.setMargin(new Insets(4, 6, 4, 6));
        tagSearchPanel.add(tagLabel, BorderLayout.WEST);
        tagSearchPanel.add(tagField, BorderLayout.CENTER);
        tagSearchPanel.add(addTagButton, BorderLayout.EAST);


        // Панель для списка тегов
        JPanel tagsListPanel = new JPanel();
        tagsListPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        //tagsListPanel.setBackground(Color.WHITE);
        JButton updateCardsPanelButton = new JButton();
        updateCardsPanelButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cardPanel.reloadCardPanel();
                cardPanel.setDataForSearchCards(org.example.UFileService.getFiles(ROOT_PATH));
                cardPanel.findCards(nameField.getText(), tags);
            }
        });
        updateCardsPanelButton.setText("\uE042");
        updateCardsPanelButton.setFont(Params.BUTTONS_FONT2);
        updateCardsPanelButton.setMargin(new Insets(4, 6, 4, 6));
        tagsListPanel.add(updateCardsPanelButton);


        // Обработчик для добавления тегов
        addTagButton.addActionListener((ActionEvent e) -> {
            String tagText = tagField.getText().trim();
            if (!tagText.isEmpty() && !tags.contains(tagText)) {
                tags.add(tagText);

                // Создаем панель для нового тега
                JPanel tagPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                //tagPanel.setBackground(new Color(134, 134, 134));
                //tagPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
                tagPanel.setBorder(new RoundedBorder(5));
                //tagPanel.setBounds(5, 5, 5, 5);

                JLabel tagNameLabel = new JLabel(tagText);
                tagNameLabel.setFont(Params.LABEL_FONT);
                tagNameLabel.setForeground(new Color(232, 181, 12));
                JButton removeButton = new JButton("\uE14C");
                removeButton.setFont(Params.BUTTONS_FONT3);
                //removeButton.setMargin(new Insets(0, 5, 0, 5));
                removeButton.setForeground(Color.WHITE);

                // Обработчик удаления тега
                removeButton.addActionListener((ActionEvent removeEvent) -> {
                    tags.remove(tagText);
                    tagsListPanel.remove(tagPanel);
                    tagsListPanel.revalidate();
                    tagsListPanel.repaint();
                    cardPanel.findCards(nameField.getText(), tags);
                });

                tagPanel.add(tagNameLabel);
                tagPanel.add(removeButton);
                tagsListPanel.add(tagPanel);
                tagsListPanel.revalidate();
                tagsListPanel.repaint();
            }
            tagField.setText("");
            cardPanel.findCards(nameField.getText(), tags);
        });

        nameField.setPreferredSize(new Dimension(400, 30));
        tagField.setPreferredSize(new Dimension(200, 30));
        // Добавляем элементы в основную панель
        searchPanel.add(nameSearchPanel);
        searchPanel.add(Box.createVerticalStrut(10));
        searchPanel.add(tagSearchPanel);
        searchPanel.add(Box.createVerticalStrut(0));
        //searchPanel.add(new JLabel("Selected tags:"));
        searchPanel.add(tagsListPanel);

        return searchPanel;
    }
}
