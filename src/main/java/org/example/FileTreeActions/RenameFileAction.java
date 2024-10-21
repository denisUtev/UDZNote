package org.example.FileTreeActions;

import org.example.FileTree;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class RenameFileAction extends AbstractAction {

    private final FileTree fileTree;

    public RenameFileAction(FileTree fileTree) {
        putValue(NAME, "Rename");
        this.fileTree = fileTree;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (fileTree.isChoosedDirectory) {
            File filePath = new File(fileTree.getChoosedPath());
            String nameNewFile = JOptionPane.showInputDialog("Enter folder name", filePath.getName());
            if (nameNewFile != null) {
                UFileService.renameFile(fileTree.getChoosedPath(), nameNewFile);
                fileTree.updateFileTree();
            }
        } else {
            File filePath = new File(fileTree.getChoosedPath());
            String nameNewFile = JOptionPane.showInputDialog("Enter file name", filePath.getName());
            if (nameNewFile != null) {
                UFileService.renameFile(fileTree.getChoosedPath(), nameNewFile);
                fileTree.updateFileTree();
            }
        }
    }
}