package org.example.FileTreeActions;

import org.example.FileTree;
import org.example.UDZNote;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

import static org.example.FileTreeActions.UFileService.deleteFiles;

public class DeleteFileAction extends AbstractAction {

    private final FileTree fileTree;

    public DeleteFileAction(FileTree fileTree) {
            putValue(NAME, "Delete file/folder");
            this.fileTree = fileTree;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String type = "file";
        if(fileTree.isChoosedDirectory)
            type = "folder";
        File filePath = new File(fileTree.getChoosedPath());
        int res = JOptionPane.showConfirmDialog(UDZNote.getMainFrame(), "<html><h2>Delete " + type + " " + filePath.getName() + "?</h2>\n<h4>" + fileTree.getChoosedPath(), "Confirmation", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            deleteFiles(fileTree.getChoosedPath());
        }
        fileTree.updateFileTree();
    }
}
