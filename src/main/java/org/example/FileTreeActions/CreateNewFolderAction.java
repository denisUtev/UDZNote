package org.example.FileTreeActions;

import org.example.FileTree;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CreateNewFolderAction extends AbstractAction {

    private final FileTree fileTree;

    public CreateNewFolderAction(FileTree fileTree) {
        putValue(NAME, "Create new folder");
        this.fileTree = fileTree;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (fileTree.isChoosedDirectory) {
            String nameNewFile = JOptionPane.showInputDialog("Enter folder name");
            if (nameNewFile != null) {
                UFileService.createPackage(fileTree.getChoosedPath(), nameNewFile);
                fileTree.updateFileTree();
            }
        }
    }
}