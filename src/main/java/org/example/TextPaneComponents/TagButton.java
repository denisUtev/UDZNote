package org.example.TextPaneComponents;

import org.example.Params;

import javax.swing.*;
import java.awt.*;

public class TagButton extends JButton {

    public TagButton(String text) {
        super(text);
        setAlignmentY(0.8f);
        setForeground(new Color(232, 181, 12));
        setFont(Params.TEXT_FONT);
        setMargin(new Insets(0, 2, 0, 2));
    }
}
