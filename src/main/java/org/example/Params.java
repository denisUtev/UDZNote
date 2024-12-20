package org.example;

import org.example.PDFPanel.PDFViewerPanel;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.example.UDZNote.ROOT_PATH;
import static org.example.UDZNote.WORKING_DIR;

public class Params {
    public static Font BIG_CODE_FONT = new Font("Monospaced", Font.PLAIN, 24);
    public static Font CODE_FONT = new Font("Monospaced", Font.PLAIN, 18);
    public static Font BIG_TAB_TITLE_FONT = new Font("unicode", Font.PLAIN, 18);

    public static Font FILE_TREE_TITLE_FONT = new Font("unicode", Font.PLAIN, 24);
    public static Font BUTTONS_FONT = new Font("Segoe UI", Font.PLAIN, 24);
    public static Font TAB_TITLE_FONT = new Font("unicode", Font.PLAIN, 18);
    public static Font TEXT_FONT = new Font("unicode", Font.PLAIN, 18);//font in UTextPane

    public static String THEME = "dark";

    public static String pdfPropertiesPath;

    public static void initFonts(String workingDir) {
        //String fontPath = workingDir + File.separator + "data" + File.separator + "JetBrainsMonoNL-Light.ttf";
        String fontPath = workingDir + File.separator + "data" + File.separator + "Manrope.ttf";
        String ButtonsFontPath = workingDir + File.separator + "data" + File.separator + "MaterialIcons-Regular.ttf";
        pdfPropertiesPath = workingDir + File.separator + "data/pdfParams" + File.separator + "properties.xml";
        try {
            // Загружаем шрифт из файла
            CODE_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(16f).deriveFont(Font.BOLD);
            BIG_CODE_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(24f).deriveFont(Font.BOLD);
            TAB_TITLE_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(18f).deriveFont(Font.BOLD);
            BUTTONS_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(ButtonsFontPath)).deriveFont(24f).deriveFont(Font.BOLD);
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
            Тема: тёмная
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
        }
        String settings = UFileService.loadFile(pathFileSettings);
//        String BDPath = getSettingsValue(settings, "База знаний:");
//        if (BDPath.equals("-")) {
//            ROOT_PATH = WORKING_DIR + File.separator + "База знаний";
//            File root = new File(ROOT_PATH);
//            if (!root.exists()) {
//                UFileService.createPackage(ROOT_PATH);
//            }
//        } else {
//            ROOT_PATH = BDPath;
//        }
        THEME = getSettingsValue(settings, "Тема:");

        String fontPath = getSettingsValue(settings, "Шрифт:");
        File fontFile = new File(fontPath);
//        if (fontFile.exists()) {
//            try {
//                TEXT_FONT = Font.createFont(Font.TRUETYPE_FONT, fontFile.deriveFont(16f).deriveFont(Font.BOLD));
//            } catch (FontFormatException | IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

        sizeH1 = Integer.parseInt(getSettingsValue(settings, "Размер H1:"));
        sizeH2 = Integer.parseInt(getSettingsValue(settings, "Размер H2:"));
        sizeH3 = Integer.parseInt(getSettingsValue(settings, "Размер H3:"));
        sizePast = Integer.parseInt(getSettingsValue(settings, "Размер Past:"));

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
        StringBuilder settings = new StringBuilder();
        settings.append("База знаний: ").append(ROOT_PATH);
        settings.append("Тема: ").append(THEME);
        settings.append("Шрифт: ").append(THEME);
    }
}
