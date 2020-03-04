package com.example.developer.instgramclone.Utils;

import android.os.Environment;


public class FilePath {
    //store the root dir
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();
    public String PICTURES = ROOT_DIR + "/Pictures";
    public String DOWNLOADS = ROOT_DIR + "/Download";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";

    public  String FIRE_BASE_IMAGE_STORAGE ="photos/users";

}
