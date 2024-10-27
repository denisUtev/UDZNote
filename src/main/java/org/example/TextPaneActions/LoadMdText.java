package org.example.TextPaneActions;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import static org.example.TextPaneActions.ExportMdFile.*;

public class LoadMdText {

    public static void load(String text, StyledDocument doc) throws BadLocationException {
        StringBuilder resultParagraph = new StringBuilder();
        //Find paragraph style
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                if (resultParagraph.substring(0, 2).equals("# ")) {
                    resultParagraph.delete(0, 2);
                    doc.insertString(doc.getLength(), resultParagraph.toString(), HEADER1);
                } else if (resultParagraph.substring(0, 3).equals("## ")) {
                    resultParagraph.delete(0, 3);
                    doc.insertString(doc.getLength(), resultParagraph.toString(), HEADER2);
                } else if (resultParagraph.substring(0, 4).equals("### ")) {
                    resultParagraph.delete(0, 4);
                    doc.insertString(doc.getLength(), resultParagraph.toString(), HEADER3);
                } else {
                    doc.insertString(doc.getLength(), resultParagraph.toString(), new SimpleAttributeSet());
                }
                doc.insertString(doc.getLength(), "\n", new SimpleAttributeSet());
                resultParagraph = new StringBuilder();
            } else {
                resultParagraph.append(text.charAt(i));
            }
        }
        if (!resultParagraph.isEmpty()) {
            doc.insertString(doc.getLength(), resultParagraph.toString(), new SimpleAttributeSet());
        }


        boolean isBold = false;
        int startBold = -1;

        boolean isItalic = false;
        int startItalic = -1;

        //find text style
//        String prevChar = doc.getText(0, 0);
//        for (int i = 1; i < doc.getLength(); i++) {
//            String thisChar = doc.getText(i, i+1);
//            if (prevChar.equals("*")) {
//                if (thisChar.equals("*")) {
//                    isBold = true;
//                    startBold = i;
//                }
//            }
//        }
    }
}
