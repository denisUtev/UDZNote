package org.example;

import javax.swing.*;
import java.awt.*;

public class SettingsFrame {

    private JFrame frame;

    public SettingsFrame() {
        initFrame();
    }

    private void initFrame() {
        frame = new JFrame("Настройки");
        frame.setSize(700, 400);
        frame.setTitle("Настройки");
        frame.setVisible(true);

        //Display the window.
        //frame.pack();
        frame.setVisible(true);


        // Создание панели для разделов настроек
        JList<String> settingsList = new JList<>(new String[] {
                "Общие настройки", "Внешний вид", "О программе"
        });
        settingsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        settingsList.setSelectedIndex(0);  // Выбираем первый элемент по умолчанию
        settingsList.setFont(Params.CODE_FONT);

        // Панель для отображения выбранных настроек
        JPanel settingsPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        settingsPanel.setLayout(cardLayout);

        // Панели для каждого раздела
        JPanel generalSettingsPanel = createGeneralSettingsPanel();
        JPanel appearanceSettingsPanel = createAppearanceSettingsPanel();
        //JPanel networkSettingsPanel = createNetworkSettingsPanel();
        JPanel aboutPanel = createAboutPanel();

        // Добавляем все панели в CardLayout
        settingsPanel.add(generalSettingsPanel, "Общие настройки");
        settingsPanel.add(appearanceSettingsPanel, "Внешний вид");
        //settingsPanel.add(networkSettingsPanel, "Сеть");
        settingsPanel.add(aboutPanel, "О программе");

        // Когда пользователь выбирает раздел, меняем правую панель
        settingsList.addListSelectionListener(e -> {
            String selectedValue = settingsList.getSelectedValue();
            cardLayout.show(settingsPanel, selectedValue);
        });

        // Создаем и настраиваем панель с разделами
        JScrollPane scrollPane = new JScrollPane(settingsList);
        scrollPane.setPreferredSize(new Dimension(150, 400));

        // Разделяем окно на две части с помощью JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, settingsPanel);
        splitPane.setDividerLocation(150);  // Задаем начальное положение разделителя
        frame.add(splitPane);
    }



    // Панель для общих настроек с GridBagLayout
    private static JPanel createGeneralSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Отступы между элементами
        gbc.anchor = GridBagConstraints.NORTH;  // Выровнять элементы по верху

        // Компоненты для общих настроек
        JLabel bdLabel = new JLabel("База знаний:");
        bdLabel.setFont(Params.CODE_FONT);
        JTextField fontTextField = new JTextField(20);
        fontTextField.setFont(Params.CODE_FONT);
        fontTextField.setText(UDZNote.ROOT_PATH);

        // Размещение компонентов
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(bdLabel, gbc);
        gbc.gridx = 1;
        panel.add(fontTextField, gbc);

        return panel;
    }

    // Панель для настроек внешнего вида с GridBagLayout
    private static JPanel createAppearanceSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Отступы между элементами
        gbc.anchor = GridBagConstraints.NORTH;  // Выровнять элементы по верху

        // Компоненты для настроек внешнего вида
        JLabel languageLabel = new JLabel("Тема:");
        languageLabel.setFont(Params.CODE_FONT);
        JComboBox<String> languageComboBox = new JComboBox<>(new String[] { "Светлая", "Темная" });
        languageComboBox.setFont(Params.CODE_FONT);
        if (Params.THEME.equals("dark")) {
            languageComboBox.setSelectedIndex(1);
        }

        // Размещение компонентов
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(languageLabel, gbc);
        gbc.gridx = 1;
        panel.add(languageComboBox, gbc);

        JLabel fontLabel = new JLabel("Шрифт:");
        fontLabel.setFont(Params.CODE_FONT);
        JTextField fontTextField = new JTextField(20);
        fontTextField.setFont(Params.CODE_FONT);
        fontTextField.setText(Params.TEXT_FONT.getName());

        JLabel fontSizeH1Label = new JLabel("Размер H1:");
        fontSizeH1Label.setFont(Params.CODE_FONT);
        JTextField fontSizeH1TextField = new JTextField(5);
        fontSizeH1TextField.setFont(Params.CODE_FONT);
        fontSizeH1TextField.setText("40");

        JLabel fontSizeH2Label = new JLabel("Размер H2:");
        fontSizeH2Label.setFont(Params.CODE_FONT);
        JTextField fontSizeH2TextField = new JTextField(5);
        fontSizeH2TextField.setFont(Params.CODE_FONT);
        fontSizeH2TextField.setText("32");

        JLabel fontSizeH3Label = new JLabel("Размер H3:");
        fontSizeH3Label.setFont(Params.CODE_FONT);
        JTextField fontSizeH3TextField = new JTextField(5);
        fontSizeH3TextField.setFont(Params.CODE_FONT);
        fontSizeH3TextField.setText("26");

        JLabel fontSizePastLabel = new JLabel("Размер Past:");
        fontSizePastLabel.setFont(Params.CODE_FONT);
        JTextField fontSizePastTextField = new JTextField(5);
        fontSizePastTextField.setFont(Params.CODE_FONT);
        fontSizePastTextField.setText("18");

        // Размещение компонентов
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(fontLabel, gbc);
        gbc.gridx = 1;
        panel.add(fontTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(fontSizeH1Label, gbc);
        gbc.gridx = 1;
        panel.add(fontSizeH1TextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(fontSizeH2Label, gbc);
        gbc.gridx = 1;
        panel.add(fontSizeH2TextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(fontSizeH3Label, gbc);
        gbc.gridx = 1;
        panel.add(fontSizeH3TextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(fontSizePastLabel, gbc);
        gbc.gridx = 1;
        panel.add(fontSizePastTextField, gbc);

        return panel;
    }

    // Панель с информацией о программе с GridBagLayout
    private static JPanel createAboutPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Отступы между элементами
        gbc.anchor = GridBagConstraints.NORTH;  // Выровнять элементы по верху

        // Компоненты для информации о программе
        JLabel aboutLabel = new JLabel("<html><b>Программа версии 1.0</b><br>Автор: Memento Programm</html>");
        aboutLabel.setFont(Params.CODE_FONT);
        // Размещение компонента
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(aboutLabel, gbc);

        return panel;
    }
}
