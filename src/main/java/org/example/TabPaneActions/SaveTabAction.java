package org.example.TabPaneActions;

import org.example.FileTree;
import org.example.UTextPane;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SaveTabAction extends AbstractAction {

    private UTextPane textPane;

    public SaveTabAction(UTextPane textPane) {
        putValue(NAME, "Save     ");
        this.textPane = textPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        textPane.saveText();
    }
}
