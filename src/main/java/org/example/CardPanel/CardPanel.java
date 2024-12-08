package org.example.CardPanel;

import org.example.UDZNote;
import org.example.UFileService;
import org.example.UTextPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.UDZNote.DEFAULT_TEXT_COLOR;

public class CardPanel extends JPanel {

    private ArrayList<Card> cards = new ArrayList<>();
    private final JPanel cardsPanel;
    private final JScrollPane cardsScrollPane;

    public CardPanel() {
        setLayout(new BorderLayout());
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsScrollPane = new JScrollPane(cardsPanel);
        SearchPanel searchPanel = new SearchPanel();
        add(searchPanel.createSearchPanel(this), BorderLayout.NORTH);
        add(cardsScrollPane, BorderLayout.CENTER);
    }

    public void setDataForCards(ArrayList<File> files) {
        for(File file : files) {
            String lastModifiedTime = getLastModifiedTime(file);
            if (file.isDirectory()) {
                setDataForCards(UFileService.getFiles(file.getPath()));
            } else {
                Card card = createCard(file.getName(), "Заметка", getTagsFromFile(file), lastModifiedTime, file);
                cards.add(card);
                cardsPanel.add(card.getCard());
                cardsPanel.add(Box.createVerticalStrut(10));
            }
        }
        cardsScrollPane.repaint();
        cardsScrollPane.updateUI();
    }

    public void reloadCardPanel() {
        cardsPanel.removeAll();
        cards = new ArrayList<>();
    }

    public void findCards(String name, ArrayList<String> tags) {
        cardsPanel.removeAll();
        for (var card : cards) {
            if (name.isEmpty() || card.getTitle().toLowerCase().contains(name.toLowerCase())) {
                boolean flag = true;
                for (String tag : tags) {
                    if (!card.hasTag(tag)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    cardsPanel.add(card.getCard());
                    cardsPanel.add(Box.createVerticalStrut(10));
                }
            }
        }
        cardsScrollPane.repaint();
        cardsScrollPane.updateUI();
    }

    private String getLastModifiedTime(File file) {
        String lastModifiedTime = "?";
        try {
            Instant lastModifiedTime2 = Files.getLastModifiedTime(Path.of(file.getPath())).toInstant();
            ZoneId zoneId = ZoneId.systemDefault(); // Можно указать другой часовой пояс, если нужно
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy  |  HH:mm", Locale.getDefault());
            lastModifiedTime = formatter.format(lastModifiedTime2.atZone(zoneId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lastModifiedTime;
    }

    private String getTextFromRtfFile(File file) {
        String result = "?";
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/rtf");
        textPane.setDocument(new HTMLDocument());
        RTFEditorKit kit = new RTFEditorKit();
        textPane.setEditorKit(kit);

        BufferedInputStream out;
        try {
            out = new BufferedInputStream(new FileInputStream(file.getPath()));
            kit.read(out, textPane.getDocument(), 0);
            out.close();
        } catch (IOException | BadLocationException ignored) {
        }
        try {
            result = textPane.getDocument().getText(0, textPane.getDocument().getLength());

        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private ArrayList<String> getTagsFromFile(File file) {
        String text = getTextFromRtfFile(file);
        ArrayList<String> tags = new ArrayList<>();
        Pattern pattern = Pattern.compile("#[\\p{L}_\\d]+");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            tags.add(matcher.group().substring(1));
        }
        return tags;
    }


    public static Card createCard(String title, String description, ArrayList<String> tags, String lastModifiedDate, File file) {
        return new Card(title, description, tags, lastModifiedDate, file);
    }
}