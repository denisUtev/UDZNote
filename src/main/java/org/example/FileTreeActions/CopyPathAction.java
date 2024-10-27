package org.example.FileTreeActions;

import org.example.FileTree;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

public class CopyPathAction  extends AbstractAction {

    private final FileTree fileTree;

    public CopyPathAction(FileTree fileTree) {
        putValue(NAME, "Copy path");
        this.fileTree = fileTree;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StringSelection selection = new StringSelection(fileTree.getChoosedPath());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
}
