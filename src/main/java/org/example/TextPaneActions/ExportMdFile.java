package org.example.TextPaneActions;

import org.example.FileTreeActions.UFileService;

import javax.swing.text.*;
import java.io.File;
import java.util.ArrayList;

public class ExportMdFile {

    public static void export(String text, StyledDocument doc, File filePath) {
        StringBuilder resultText = new StringBuilder();
        StringBuilder resultParagraph = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                resultText.append(getStyledParagraph(resultParagraph, doc, i - resultParagraph.length()));
                resultText.append('\n');
                resultParagraph = new StringBuilder();
            } else {
                resultParagraph.append(text.charAt(i));
            }
        }
        if (!resultParagraph.isEmpty()) {
            resultText.append(getStyledParagraph(resultParagraph, doc, text.length() - resultParagraph.length()));
        }
        UFileService.saveFile(filePath.getPath(), resultText.toString());
    }

    public static SimpleAttributeSet BOLD_STYLE;
    public static SimpleAttributeSet ITALIC_STYLE;
    public static SimpleAttributeSet HEADER1;
    public static SimpleAttributeSet HEADER2;
    public static SimpleAttributeSet HEADER3;
    static {
        BOLD_STYLE = new SimpleAttributeSet();
        StyleConstants.setBold(BOLD_STYLE, true);

        ITALIC_STYLE = new SimpleAttributeSet();
        StyleConstants.setItalic(ITALIC_STYLE, true);

        HEADER1 = new SimpleAttributeSet();
        StyleConstants.setFontSize(HEADER1, 40);

        HEADER2 = new SimpleAttributeSet();
        StyleConstants.setFontSize(HEADER2, 32);

        HEADER3 = new SimpleAttributeSet();
        StyleConstants.setFontSize(HEADER3, 26);
    }

    private static String getStyledParagraph(StringBuilder paragraph, StyledDocument doc, int startIndex) {
        StringBuilder result = new StringBuilder();
        result.append(getParagraphStyle(doc, startIndex));

        boolean isBold = false;
        int startBold = -1;

        boolean isItalic = false;
        int startItalic = -1;

        ArrayList<StyleAdder> styles = new ArrayList<>();
        //find styled symbols
        for (int i = 0; i < paragraph.length(); i++) {
            AttributeSet el = doc.getCharacterElement(startIndex + i).getAttributes();
            //check bold style
            if (el.containsAttributes(BOLD_STYLE)) {
                isBold = true;
                if (startBold == -1) {
                    startBold = i;
                }
            } else if (isBold) {
                isBold = false;
                styles.add(new StyleAdder(startBold, STYLE_NAME.BOLD));
                styles.add(new StyleAdder(i, STYLE_NAME.BOLD));
                startBold = -1;
            }
            //check italic style
            if (el.containsAttributes(ITALIC_STYLE)) {
                isItalic = true;
                if (startItalic == -1) {
                    startItalic = i;
                }
            } else if (isItalic) {
                isItalic = false;
                styles.add(new StyleAdder(startItalic, STYLE_NAME.ITALIC));
                styles.add(new StyleAdder(i, STYLE_NAME.ITALIC));
                startItalic = -1;
            }
        }
        sortStyleAdders(styles);

        //Добавлять стили в окончательный вариант текста надо с конца. Тогда не будет смещения индексов
        for (int i = styles.size() - 1; i >= 0; i--) {
            StyleAdder styleSet = styles.get(i);
            if (styleSet.name == STYLE_NAME.BOLD) {
                paragraph.insert(styleSet.index, "**");
            }
            if (styleSet.name == STYLE_NAME.ITALIC) {
                paragraph.insert(styleSet.index, "*");
            }
        }

        result.append(paragraph);
        return result.toString();
    }

    private static String getParagraphStyle(StyledDocument doc, int startIndex) {
        AttributeSet el = doc.getCharacterElement(startIndex).getAttributes();
        if (el.containsAttributes(HEADER1)) {
            return "# ";
        }
        if (el.containsAttributes(HEADER2)) {
            return "## ";
        }
        if (el.containsAttributes(HEADER3)) {
            return "### ";
        }
        return "";
    }

    private static void sortStyleAdders(ArrayList<StyleAdder> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).index > list.get(j).index) {
                    StyleAdder styleAdder1 = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, styleAdder1);
                }
            }
        }
    }


    record StyleAdder(int index, STYLE_NAME name){ }

    enum STYLE_NAME {
        BOLD,
        ITALIC
    }
}
