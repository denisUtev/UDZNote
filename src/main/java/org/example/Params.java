package org.example;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.example.PDFPanel.PDFViewerPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.example.UDZNote.*;

public class Params {
    public static Font BIG_CODE_FONT = new Font("Monospaced", Font.PLAIN, 24);
    public static Font CODE_FONT = new Font("Monospaced", Font.PLAIN, 18);
    public static Font LABEL_FONT = new Font("Monospaced", Font.PLAIN, 18);
    public static Font BIG_LABEL_FONT = new Font("Monospaced", Font.PLAIN, 18);
    public static Font BIG_TAB_TITLE_FONT = new Font("unicode", Font.PLAIN, 18);

    public static Font FILE_TREE_TITLE_FONT = new Font("unicode", Font.PLAIN, 24);
    public static Font BUTTONS_FONT = new Font("Segoe UI", Font.PLAIN, 24);
    public static Font BUTTONS_FONT2 = new Font("Segoe UI", Font.PLAIN, 18);
    public static Font BUTTONS_FONT3 = new Font("Segoe UI", Font.PLAIN, 16);
    public static Font TAB_TITLE_FONT = new Font("unicode", Font.PLAIN, 18);
    public static Font SMALL_TAB_TITLE_FONT = new Font("unicode", Font.PLAIN, 16);
    public static Font TEXT_FONT = new Font("unicode", Font.PLAIN, 18);//font in UTextPane

    public static String THEME = "Темная";

    public static String pdfPropertiesPath;
    public static String fontPath;

    public static void initFonts(String workingDir) {
        //String fontPath = workingDir + File.separator + "data" + File.separator + "JetBrainsMonoNL-Light.ttf";
        String fontPath = workingDir + File.separator + "data" + File.separator + "Manrope.ttf";
        String fontPathBold = workingDir + File.separator + "data" + File.separator + "ManropeBold.ttf";
        String ButtonsFontPath = workingDir + File.separator + "data" + File.separator + "MaterialIcons-Regular.ttf";
        pdfPropertiesPath = workingDir + File.separator + "data/pdfParams" + File.separator + "properties.xml";
        try {
            // Загружаем шрифт из файла
            CODE_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(16f).deriveFont(Font.BOLD);
            LABEL_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(fontPathBold)).deriveFont(16f).deriveFont(Font.BOLD);
            BIG_LABEL_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(fontPathBold)).deriveFont(18f).deriveFont(Font.BOLD);

            BIG_CODE_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(24f).deriveFont(Font.BOLD);
            TAB_TITLE_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(18f).deriveFont(Font.BOLD);
            SMALL_TAB_TITLE_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(16f).deriveFont(Font.BOLD);
            BUTTONS_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(ButtonsFontPath)).deriveFont(24f).deriveFont(Font.BOLD);
            BUTTONS_FONT2 = Font.createFont(Font.TRUETYPE_FONT, new File(ButtonsFontPath)).deriveFont(18f).deriveFont(Font.BOLD);
            BUTTONS_FONT3 = Font.createFont(Font.TRUETYPE_FONT, new File(ButtonsFontPath)).deriveFont(16f).deriveFont(Font.BOLD);
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }
    }



    public static int sizeH1 = 40;
    public static int sizeH2 = 32;
    public static int sizeH3 = 26;
    public static int sizePast = 18;
    static final String defaultSettings = """
            База знаний: -
            Тема: Темная
            Шрифт: unicode
            Размер H1: 40
            Размер H2: 32
            Размер H3: 26
            Размер Past: 18
            """;
    public static void loadSettings() {
        String pathFileSettings = WORKING_DIR + File.separator + "data" + File.separator + "settings.txt";
        File fileSettings = new File(pathFileSettings);
        if (!fileSettings.exists()) {
            UFileService.createFile(pathFileSettings, defaultSettings);
            showRootPathDialogWindow();
            return;
        }
        String settings = UFileService.loadFile(pathFileSettings);
        String BDPath = getSettingsValue(settings, "База знаний:");
        if (BDPath.equals("-")) {
            showRootPathDialogWindow();
            return;
//            ROOT_PATH = WORKING_DIR + File.separator + "База знаний";
//            File root = new File(ROOT_PATH);
//            if (!root.exists() && root.isDirectory()) {
//                UFileService.createPackage(ROOT_PATH);
//            }
        } else {
            ROOT_PATH = BDPath;
        }
        THEME = getSettingsValue(settings, "Тема:");

        fontPath = getSettingsValue(settings, "Шрифт:");
        File fontFile = new File(fontPath);
        if (fontFile.exists()) {
            try {
                TEXT_FONT = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(16f).deriveFont(Font.BOLD);
            } catch (FontFormatException | IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            fontPath = "unicode";
        }

        sizeH1 = Integer.parseInt(getSettingsValue(settings, "Размер H1:"));
        sizeH2 = Integer.parseInt(getSettingsValue(settings, "Размер H2:"));
        sizeH3 = Integer.parseInt(getSettingsValue(settings, "Размер H3:"));
        sizePast = Integer.parseInt(getSettingsValue(settings, "Размер Past:"));

        try {
            if (THEME.equals("Светлая")) {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } else {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            }
            if (mainFrame != null) {
                SwingUtilities.updateComponentTreeUI(mainFrame);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String getSettingsValue(String settings, String name) {
        int startIndex = settings.indexOf(name + " ");
        int endIndex = startIndex;
        while (settings.length() > endIndex && settings.charAt(endIndex) != '\n') {
            endIndex++;
        }
        return settings.substring(startIndex + name.length() + 1, endIndex);
    }

    public static void saveSettings() {
        String pathFileSettings = WORKING_DIR + File.separator + "data" + File.separator + "settings.txt";
        String settings = "База знаний: " + ROOT_PATH + '\n' +
                "Тема: " + THEME + '\n' +
                "Шрифт: " + fontPath + '\n' +
                "Размер H1: " + sizeH1 + '\n' +
                "Размер H2: " + sizeH2 + '\n' +
                "Размер H3: " + sizeH3 + '\n' +
                "Размер Past: " + sizePast;
        UFileService.saveFile(pathFileSettings, settings);
    }

    public static void showRootPathDialogWindow() {
        JOptionPane.showConfirmDialog(mainFrame, "Выберите директорию для вашей базы знаний", "Стартовое окно",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setName("Выберите директорию для вашей базы знаний");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(null);
        fileChooser.setName("Выберите директорию для вашей базы знаний");

        if (result == JFileChooser.APPROVE_OPTION) {
            ROOT_PATH = fileChooser.getSelectedFile().getPath();
            saveSettings();
            loadSettings();
        } else {
            loadSettings();
        }
    }
}
