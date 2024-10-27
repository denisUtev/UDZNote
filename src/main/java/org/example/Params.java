package org.example;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Params {
    public static Font BIG_CODE_FONT = new Font("Monospaced", Font.PLAIN, 24);
    public static Font CODE_FONT = new Font("Monospaced", Font.PLAIN, 18);
    public static Font BIG_TAB_TITLE_FONT = new Font("unicode", Font.PLAIN, 18);

    public static Font FILE_TREE_TITLE_FONT = new Font("unicode", Font.PLAIN, 24);
    public static Font TAB_TITLE_FONT = new Font("unicode", Font.PLAIN, 14);
    public static Font TEXT_FONT = new Font("unicode", Font.PLAIN, 18);

    public static String THEME = "light";

    public static void initFonts(String workingDir) {
        String fontPath = workingDir + File.separator + "data" + File.separator + "JetBrainsMonoNL-Light.ttf";
        try {
            // Загружаем шрифт из файла
            CODE_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(16f);
            BIG_CODE_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(24f);
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
