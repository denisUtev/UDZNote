package org.example;

import org.example.FileTreeActions.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

public class FileTree extends JScrollPane {

    JTree fileTree;

    private final Path rootPath;
    private final File rootFile;
    private FileTime lastModifiedTime;

    private String choosedPath;
    public boolean isChoosedDirectory = false;
    public boolean isChoosedLeaf = false;

    public FileTree(String rootPath) {
        this.rootPath = Paths.get(rootPath);
        rootFile = new File(rootPath);
        initFileTree();
    }

    private void initFileTree() {
        fileTree = new JTree(new DefaultMutableTreeNode("UDZNote"));
        getViewport().add(fileTree);
        initMouseListeners();

        fileTree.setFont(Params.BIG_TAB_TITLE_FONT);
        fileTree.setCellRenderer(new UFileTreeView.MyTreeCellRenderer());
        fileTree.setComponentPopupMenu(createTreeFileMenu());

        updateFileTree();
        Timer timerCheckUpdating = new Timer(3000, e -> updateFileTree());
        timerCheckUpdating.start();
    }

    private void initMouseListeners() {
        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2 && choosedPath != null && !isChoosedDirectory) {
//                          Thread myThready = new Thread(() -> openFile.open(choosedPath));
//                          myThready.start();
                    UDZNote.openFile(choosedPath);
                }
            }
        });
        fileTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
                    .getPath().getLastPathComponent();
            isChoosedLeaf = node.isLeaf();

            StringBuilder allPath = new StringBuilder(rootFile.getPath());
            var nodes = node.getPath();
            for (int i = 1; i < nodes.length; i++) {
                allPath.append(File.separator);
                allPath.append(nodes[i]);
            }
            choosedPath = allPath.toString();

            isChoosedDirectory = (new File(choosedPath)).isDirectory();
            //System.out.println(choosedPath);
            //openFile.open(allPath.toString());
            //System.out.println("You selected " + Arrays.toString(node.getPath()));
        });
    }

    public void updateFileTree() {
        try {
            var lastTime = Files.getLastModifiedTime(rootPath);
            if (lastModifiedTime == null || lastModifiedTime.compareTo(lastTime) != 0) {
                DefaultTreeModel model = (DefaultTreeModel) fileTree.getModel();
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                MutableTreeNode newTreeFile = scan(rootFile);
                updateFileTree(root, newTreeFile);
                lastModifiedTime = lastTime;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Не удалось обновить содержимое дерева: " + rootPath);
            throw new RuntimeException(e);
        }
    }

    private static MutableTreeNode scan(File node) {
        DefaultMutableTreeNode ret = new DefaultMutableTreeNode(node.getName());
        if (node.isDirectory())
            for (File child: Objects.requireNonNull(node.listFiles()))
                ret.add(scan(child));
        return ret;
    }

    private void updateFileTree(DefaultMutableTreeNode root, MutableTreeNode newTreeFile){
        DefaultTreeModel model = (DefaultTreeModel)fileTree.getModel();
        for(int i=0; i<newTreeFile.getChildCount(); i++){
            boolean flag = true;
            for(int j=0; j<root.getChildCount(); j++){
                if(root.getChildAt(j).toString().equals(newTreeFile.getChildAt(i).toString())){
                    flag = false;
                    updateFileTree((DefaultMutableTreeNode) root.getChildAt(j), (MutableTreeNode) newTreeFile.getChildAt(i));
                    break;
                }
            }
            if(flag){
                root.add(new DefaultMutableTreeNode(newTreeFile.getChildAt(i).toString()));
                model.reload(root);
            }
        }

        for(int j=0; j<root.getChildCount(); j++){
            boolean flag = true;
            for(int i=0; i<newTreeFile.getChildCount(); i++){
                if(root.getChildAt(j).toString().equals(newTreeFile.getChildAt(i).toString())){
                    flag = false;
                    updateFileTree((DefaultMutableTreeNode) root.getChildAt(j), (MutableTreeNode) newTreeFile.getChildAt(i));
                    break;
                }
            }
            if(flag){
                DefaultTreeModel model2 = (DefaultTreeModel)fileTree.getModel();
                DefaultMutableTreeNode choosedParent = (DefaultMutableTreeNode)root.getChildAt(j);
                model2.removeNodeFromParent(choosedParent);
                model2.reload(root);
            }
        }
    }

    private JPopupMenu createTreeFileMenu() {
        JPopupMenu pm = new JPopupMenu();
        JMenuItem createNewFile = new JMenuItem(new CreateNewFileAction(this));
        JMenuItem createNewFolder = new JMenuItem(new CreateNewFolderAction(this));
        JMenuItem ranameFile = new JMenuItem(new RenameFileAction(this));
        JMenuItem deleteFile = new JMenuItem(new DeleteFileAction(this));
        JMenuItem pasteFile = new JMenuItem(new PasteFileAction(this));
        JMenuItem copyPath = new JMenuItem(new CopyPathAction(this));
        JMenuItem updateTree = new JMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTreeModel model = (DefaultTreeModel) fileTree.getModel();
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                MutableTreeNode newTreeFile = scan(rootFile);
                updateFileTree(root, newTreeFile);
            }
        });
        updateTree.setText("Update tree");
        pm.add(createNewFile);
        pm.add(createNewFolder);
        pm.add(ranameFile);
        pm.addSeparator();
        pm.add(copyPath);
        pm.add(pasteFile);
        pm.addSeparator();
        pm.add(updateTree);
        pm.add(deleteFile);
        return pm;
    }

    public String getChoosedPath() {
        return choosedPath;
    }
}
