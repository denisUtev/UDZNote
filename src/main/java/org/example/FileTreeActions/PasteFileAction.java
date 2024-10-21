package org.example.FileTreeActions;

import org.example.FileTree;
import org.example.UDZNote;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.example.FileTreeActions.UFileService.copyFile;

public class PasteFileAction extends AbstractAction {

    private final FileTree fileTree;

    public PasteFileAction(FileTree fileTree) {
        putValue(NAME, "Import file");
        this.fileTree = fileTree;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose file");
        // настроим для выбора каталога
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int res = fileChooser.showOpenDialog(UDZNote.getMainFrame());
        if ( res == JFileChooser.APPROVE_OPTION ) {
            String path = fileChooser.getSelectedFile().getPath();
            try {
                copyFile(path, fileTree.getChoosedPath() + "\\" + fileChooser.getSelectedFile().getName());
                fileTree.updateFileTree();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(UDZNote.getMainFrame(), ex);
            }
        }
    }
}
