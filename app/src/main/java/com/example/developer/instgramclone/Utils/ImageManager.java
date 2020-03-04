package com.example.developer.instgramclone.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageManager {

    //covert image to bitmap
    public static Bitmap getBitmap(String imgUrl) {
        File fileImage = new File(imgUrl);
        FileInputStream stream = null;
        Bitmap imageBitmap = null;

        try {
            stream = new FileInputStream(fileImage);
            imageBitmap = BitmapFactory.decodeStream(stream);
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException ", e.getMessage());
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                Log.e("IOException ", e.getMessage());
            }
        }
        return imageBitmap;

    }

    public static byte[] getBytesFromBitmap(Bitmap bm, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }
}
