package org.example.PDFPanel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

public class PDFTextExtractor {
    public static void extractTextWithCoordinates(File pdfFile) throws IOException {
        PDDocument document = PDDocument.load(pdfFile);
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();

        // Установить область для анализа текста (например, вся страница)
        Rectangle2D region = new Rectangle2D.Double(0, 0, 500, 500);
        stripper.addRegion("pageRegion", region);

        stripper.extractRegions(document.getPage(0)); // Только первая страница
        System.out.println("Text in region:\n" + stripper.getTextForRegion("pageRegion"));

        document.close();
    }
}
