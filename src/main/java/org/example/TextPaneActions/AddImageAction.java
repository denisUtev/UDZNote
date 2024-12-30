package org.example.TextPaneActions;

import org.example.BoxLayoutUtils.BoxLayoutUtils;
import org.example.ImageLabel;
import org.example.Params;
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

public class AddImageAction extends AbstractAction {

    JFrame frame;
    UTextPane parentTextPane;

    private JTextField pathField;
    private JTextField widthField;
    private JTextField heightField;
    private ImageLabel imageLabel;
    private File selectedFile;

    public AddImageAction(UTextPane textPane) {
        parentTextPane = textPane;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        frame = new JFrame("Добавить изображение");
        frame.setSize(450, 400);
        frame.setTitle("Добавить изображение");
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        // Панель для пути и кнопки выбора файла
        JPanel pathPanel = new JPanel(new BorderLayout(5, 5));
        pathField = new JTextField();
        JButton browseButton = new JButton("Обзор...");
        browseButton.addActionListener(new BrowseButtonListener());
        browseButton.setFont(Params.BUTTONS_FONT2);
        browseButton.setText("\uE2C4");

        JPanel browseButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        //browseButtonPanel.setBorder(BorderFactory.createEmptyBorder());
        browseButtonPanel.add(browseButton);
        browseButtonPanel.add(BoxLayoutUtils.createHorizontalStrut(6));

        pathPanel.add(pathField, BorderLayout.CENTER);
        pathPanel.add(browseButtonPanel, BorderLayout.EAST);


        // Панель для размеров
        JPanel ParamsPanel = new JPanel(new BorderLayout(5, 5));
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        widthField = new JTextField(5);
        heightField = new JTextField(5);
        sizePanel.add(new JLabel("Ширина:"));
        sizePanel.add(widthField);
        sizePanel.add(new JLabel("Высота:"));
        sizePanel.add(heightField);

        JPanel updateButtonPanel = new JPanel(new BorderLayout());
        JButton updateImageButton = new JButton("Предпросмотр");
        updateImageButton.setFont(Params.BUTTONS_FONT2);
        updateImageButton.setText("\uE8BA");
        updateImageButton.addActionListener(new UpdateButtonListener());
        updateButtonPanel.add(updateImageButton, BorderLayout.CENTER);
        sizePanel.add(updateButtonPanel, BorderLayout.EAST);

        ParamsPanel.add(pathPanel, BorderLayout.NORTH);
        ParamsPanel.add(sizePanel, BorderLayout.SOUTH);

        // Панель с изображением
        imageLabel = new ImageLabel("Изображение не выбрано");
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // Оборачиваем в панель с FlowLayout
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapperPanel.add(imageLabel);
        JScrollPane imageScrollPane = new JScrollPane(wrapperPanel);

        // Кнопка Ок
        JButton okButton = new JButton("Загрузить");
        okButton.addActionListener(new OkButtonListener());

        // Добавление компонентов
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(ParamsPanel, BorderLayout.NORTH);
        contentPanel.add(imageScrollPane, BorderLayout.CENTER);

        frame.add(contentPanel, BorderLayout.CENTER);
        frame.add(okButton, BorderLayout.SOUTH);
    }


    private class BrowseButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {
                pathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
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
            String imagePath = pathField.getText();
            String widthText = widthField.getText();
            String heightText = heightField.getText();

            int width = widthText.isEmpty() ? -1 : Integer.parseInt(widthText);
            int height = heightText.isEmpty() ? -1 : Integer.parseInt(heightText);

            handleImageSelection(imagePath, width, height);
            frame.dispose();
        }
    }


    ImageIcon imageIcon;
    private void updateImagePreview() {
        if (!pathField.getText().isEmpty()) {
            try {
                if (selectedFile == null || !selectedFile.getPath().equals(pathField.getText())) {
                    selectedFile = new File(pathField.getText());
                    imageIcon = new ImageIcon(selectedFile.getAbsolutePath());
                }
                int width = widthField.getText().isEmpty() ? imageIcon.getIconWidth() : Integer.parseInt(widthField.getText());
                int height = heightField.getText().isEmpty() ? imageIcon.getIconHeight() : Integer.parseInt(heightField.getText());
                Image image = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(image));
                imageLabel.setText("");
            } catch (Exception e) {
                imageLabel.setText("Не удалось загрузить изображение");
            }
        }
    }

    private void handleImageSelection(String imagePath, int width, int height) {
        try {
            String newPath = IMAGE_DIRECTORY + File.separator + System.currentTimeMillis() + "." + UFileService.getExtension(imagePath);
            UFileService.saveImage(imageIcon, newPath);
            String description = "![image](" + newPath + ")";
            if (!(width == -1 || height == -1)) {
                description = String.format("![%dx%d](%s)", width, height, newPath);
            }
            parentTextPane.getDocument().insertString(parentTextPane.getCaretPosition(), description, new SimpleAttributeSet());
            parentTextPane.updateDocumentView();
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

}
