package org.example.TabPaneActions;

import org.example.DnDTabbedPane;
import org.example.TextPaneActions.AddImageAction;
import org.example.UDZNote;
import org.example.UTextPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddDescriptionAction extends AbstractAction {

    private final DnDTabbedPane tabPane;

    public AddDescriptionAction(DnDTabbedPane tabPane) {
        putValue(NAME, "Добавить описание");
        this.tabPane = tabPane;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JFrame frame = new JFrame("Добавить описание");

        frame.setSize(400, 300);
        //frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel("Описание:");
        label.setFont(new Font("Arial", Font.BOLD, 24));

        JTextArea textArea = new JTextArea(3, 3);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        if (UDZNote.dictDescriptions.containsKey(tabPane.getChoosingTab().getTextPane().filePath.getPath())) {
            textArea.setText(UDZNote.dictDescriptions.get(tabPane.getChoosingTab().getTextPane().filePath.getPath()));
        }
        textArea.setFont(new Font("Arial", Font.PLAIN, 18));
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Кнопка Ок
        JButton okButton = new JButton("Сохранить");
        okButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UDZNote.setDescriptionToFile(tabPane.getChoosingTab().getTextPane().filePath, textArea.getText());
                frame.dispose();
            }
        });
        //okButton.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(okButton, BorderLayout.SOUTH);

        frame.getContentPane().add(panel);

        frame.setVisible(true);

        //System.out.println(tabPane.getChoosingTab().getTextPane().fileName);
    }
}
