package org.example.FileTreeActions;

import org.example.FileTree;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CreateNewFileAction extends AbstractAction {

    private final FileTree fileTree;

    public CreateNewFileAction(FileTree fileTree) {
        putValue(NAME, "Create new file");
        this.fileTree = fileTree;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (fileTree.isChoosedDirectory) {
            String nameNewFile = JOptionPane.showInputDialog("Enter file name");
            if (nameNewFile != null) {
                if (!nameNewFile.contains(".")) {
                    nameNewFile += ".rtf";
                }
                UFileService.createFile(fileTree.getChoosedPath() + "/" + nameNewFile, "");
                fileTree.updateFileTree();
            }
        }
    }
}