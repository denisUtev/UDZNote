package org.example.FileTreeActions;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class UFileService {

    public static void createFile(String path, String text) {
        File f = new File(path);
        try (FileWriter writer = new FileWriter(path, false)) {
            writer.write(text);
            writer.flush();
        } catch (Exception ex) {
            //printResults(ex.getMessage());
            System.out.println(ex.getMessage());
        }
    }

    public static void saveFile(String path, String text){
        File f = new File(path);
        if(f.exists() && !f.isDirectory()) {
            try (FileWriter writer = new FileWriter(path, false)) {
                writer.write(text);
                writer.flush();
            } catch (Exception ex) {
                //printResults(ex.getMessage());
            }
        }
    }

    public static String loadFile(String path) {
        StringBuilder result = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new FileReader(path)))
        {
            String s;
            while((s=br.readLine())!=null) {
                result.append(s).append("\n");
            }
            if (!result.isEmpty()) {
                result.delete(result.length() - 1, result.length());
            }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
            //JOptionPane.showMessageDialog(, "Файловое дерево с количеством элементов > 35 автоматически не обновляется");
            //printResults(ex.getMessage());
        }
        return result.toString();
    }

    public static void createPackage(String path, String name) {
        createPackage(path + "/" + name);
    }

    public static void createPackage(String path) {
        //new File(path + "/" + name).mkdirs();
        //System.out.println(path);
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void deleteFile(String path) {
//        deleteFiles(path);
//    }

    public static void deleteFiles(String path){
        ArrayList<String> files = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths
                    .forEach((s) -> files.add(s.toString()));
            for(int i=files.size() - 1; i >= 0; i--){
                File file = new File(files.get(i));
                if (file.delete()) {
                    files.remove(i);
                }
            }
            for(int i=files.size() - 1; i >= 0; i--){
                File file = new File(files.get(i));
                if (file.delete()) {
                    files.remove(i);
                }
            }
        } catch (IOException e) {
            //uideModel.printResults("Не удалось удалить данный файл: " + path);
        }
    }

    public static void copyFile(String source, String dest) {//source - что копировать, dest - куда копировать
        try {
            ArrayList<String> files = new ArrayList<>();
            if(source.indexOf('.') == -1)
                try (Stream<Path> paths = Files.walk(Paths.get(source))) {
                    paths
                            .forEach((s) -> files.add(s.toString()));
                }
            Path bytes = Files.copy(
                    new File(source).toPath(),
                    new File(dest).toPath(),
                    REPLACE_EXISTING,
                    COPY_ATTRIBUTES,
                    NOFOLLOW_LINKS);
            String file;
            for(int i = 1; i < files.size(); i++){
                file = files.get(i);
                copyFile(file, dest + "\\" + file.substring(source.length()));
            }
        } catch (IOException e) {
            //printResults("Была обнаружена какая-то ошибка при копировании. Проверьте всё ли успешно скопировалось");
            //printResults(e.getMessage());
        }
    }

    public static void renameFile(String path, String newName) {
        // File (or directory) with old name
        File file = new File(path);

        // File (or directory) with new name
        File file2 = new File(file.getParent() + "/" + newName);

        if (file2.exists()) {
//            throw new java.io.IOException("file exists");
        }

        // Rename file (or directory)
        boolean success = file.renameTo(file2);

        if (!success) {
            // File was not successfully renamed
        }
    }
}
