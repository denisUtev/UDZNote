package org.example.TextPaneActions;

import org.example.UFileService;
import org.example.UTextPane;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

import static org.example.UDZNote.IMAGE_DIRECTORY;

public class AddLinkAction extends AbstractAction {

    private final UTextPane parentTextPane;

    public AddLinkAction(UTextPane textPane) {
        super("Add Link");
        parentTextPane = textPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Создаем окно для ввода текста и ссылки
        JFrame linkFrame = new JFrame("Add Link");
        linkFrame.setSize(300, 150);
        linkFrame.setLayout(new BorderLayout(2, 5));

        JPanel paramsPanel = new JPanel(new BorderLayout(2, 5));

        // Поля для ввода текста ссылки и URL
        JTextField textField = new JTextField(15);
        JTextField urlField = new JTextField(15);

        JPanel textPanel = new JPanel(new BorderLayout(5, 5));
        textPanel.add(new JLabel("Text:"), BorderLayout.WEST);
        textPanel.add(textField, BorderLayout.EAST);

        JPanel urlPanel = new JPanel(new BorderLayout(5, 5));
        urlPanel.add(new JLabel("Link:"), BorderLayout.WEST);
        urlPanel.add(urlField, BorderLayout.EAST);
        // Метки для полей
        paramsPanel.add(textPanel, BorderLayout.NORTH);
        paramsPanel.add(urlPanel, BorderLayout.SOUTH);
        linkFrame.add(paramsPanel, BorderLayout.NORTH);

        // Кнопка для подтверждения
        JButton okButton = new JButton("Ok");
        okButton.addActionListener(event -> {
            String text = textField.getText().trim();
            String url = urlField.getText().trim();

            // Проверка на пустые поля
            if (text.isEmpty() || url.isEmpty()) {
                JOptionPane.showMessageDialog(linkFrame, "Both fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Вызываем функцию вставки ссылки, передавая введенные text и url
                insertLink(text, url);

                // Закрываем окно после вставки
                linkFrame.dispose();
            }
        });

        //linkFrame.add(new JLabel());  // Пустая метка для выравнивания
        linkFrame.add(okButton, BorderLayout.SOUTH);

        linkFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        linkFrame.setVisible(true);
    }

    private void insertLink(String text, String url) {
        try {
            String description = String.format("[%s](%s)", text, url);
            parentTextPane.getDocument().insertString(parentTextPane.getCaretPosition(),
                    description, new SimpleAttributeSet());
            parentTextPane.updateDocumentView();
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }
}
