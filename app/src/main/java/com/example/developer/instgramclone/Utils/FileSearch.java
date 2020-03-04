package com.example.developer.instgramclone.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class FileSearch {


    public static ArrayList<String> getDirectorypath(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for (int i = 0; i < listfiles.length; i++) {
            if (listfiles[i].isDirectory()) {
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }

    public static ArrayList<String> getFilepath(String directory) {

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String img) {
                return img.endsWith(".png") || img.endsWith(".jpeg") || img.endsWith(".jpg");
            }
        };

        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles(filter);
        if (listfiles != null)
            for (int i = 0; i < listfiles.length; i++) {
                if (listfiles[i].isFile()) {
                    pathArray.add(listfiles[i].getAbsolutePath());
                }
            }
        return pathArray;
    }
}
