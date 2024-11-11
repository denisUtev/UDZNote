package org.example.TextPaneActions;

import org.example.UFileService;
import org.example.UTextPane;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static org.example.UDZNote.IMAGE_DIRECTORY;

public class EditImageAction {

    JFrame frame;
    UTextPane parentTextPane;

    private JTextField widthField;
    private JTextField heightField;
    private JLabel imageLabel;
    private File selectedFile;
    private int imagePosition;
    private String linkImage;

    public EditImageAction(UTextPane textPane, ImageIcon imageIcon, String linkImage, int width, int height, int position) {
        parentTextPane = textPane;
        this.imageIcon = imageIcon;
        imagePosition = position;
        this.linkImage = linkImage;

        frame = new JFrame("Edit image");
        frame.setSize(450, 400);
        frame.setTitle("Edit image");
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());
        initComponents(width, height);
    }

    private void initComponents(int width, int height) {
        // Панель для размеров
        JPanel ParamsPanel = new JPanel(new BorderLayout(5, 5));
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        widthField = new JTextField(5);
        heightField = new JTextField(5);
        sizePanel.add(new JLabel("Ширина:"));
        sizePanel.add(widthField);
        sizePanel.add(new JLabel("Высота:"));
        sizePanel.add(heightField);
        widthField.setText(String.valueOf(width));
        heightField.setText(String.valueOf(height));

        JPanel updateButtonPanel = new JPanel(new BorderLayout());
        JButton updateImageButton = new JButton("Предпросмотр");
        updateImageButton.addActionListener(new UpdateButtonListener());
        updateButtonPanel.add(updateImageButton, BorderLayout.CENTER);
        sizePanel.add(updateButtonPanel, BorderLayout.EAST);

        ParamsPanel.add(sizePanel, BorderLayout.SOUTH);

        // Панель с изображением
        imageLabel = new JLabel();
        imageLabel.setIcon(imageIcon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JScrollPane imageScrollPane = new JScrollPane(imageLabel);

        // Кнопка Ок
        JButton okButton = new JButton("Загрузить");
        okButton.addActionListener(new OkButtonListener());

        // Добавление компонентов
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(ParamsPanel, BorderLayout.NORTH);
        contentPanel.add(imageScrollPane, BorderLayout.CENTER);

        frame.add(contentPanel, BorderLayout.CENTER);
        frame.add(okButton, BorderLayout.SOUTH);

        updateImagePreview();
    }


    private class BrowseButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {
                updateImagePreview();
            }
        }
    }

    private class UpdateButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            updateImagePreview();
        }
    }


    private class OkButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String widthText = widthField.getText();
            String heightText = heightField.getText();

            int width = widthText.isEmpty() ? -1 : Integer.parseInt(widthText);
            int height = heightText.isEmpty() ? -1 : Integer.parseInt(heightText);

            handleImageSelection(width, height);
            frame.dispose();
        }
    }


    ImageIcon imageIcon;
    private void updateImagePreview() {
        try {
            int width = widthField.getText().isEmpty() ? imageIcon.getIconWidth() : Integer.parseInt(widthField.getText());
            int height = heightField.getText().isEmpty() ? imageIcon.getIconHeight() : Integer.parseInt(heightField.getText());
            Image image = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(image));
            imageLabel.setText("");
        } catch (Exception e) {
            imageLabel.setText("Не удалось загрузить изображение");
        }
    }

    private void handleImageSelection(int width, int height) {
        try {
            String description = "![image](" + linkImage + ")";
            if (!(width == -1 || height == -1)) {
                description = String.format("![%dx%d](%s)", width, height, linkImage);
            }
            parentTextPane.getDocument().remove(imagePosition, 1);
            parentTextPane.getDocument().insertString(imagePosition, description, new SimpleAttributeSet());
            parentTextPane.updateDocumentView();
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

}
