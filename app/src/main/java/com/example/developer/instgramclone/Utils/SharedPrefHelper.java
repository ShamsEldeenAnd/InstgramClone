package com.example.developer.instgramclone.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


import static android.content.Context.MODE_PRIVATE;

public class SharedPrefHelper {

    public static void saveUserSetting(String username, String userID, String profilePhoto, Context mContext) {
        SharedPreferences mPrefs = ((Activity) mContext).getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("username", username);
        editor.putString("user_id", userID);
        editor.putString("profile_photo", profilePhoto);
        editor.commit();
    }

    public static String getUserName(Context mContext) {
        SharedPreferences mPrefs = ((Activity) mContext).getSharedPreferences("UserData", MODE_PRIVATE);
        return mPrefs.getString("username", "");
    }

    public static String getUserID(Context mContext) {
        SharedPreferences mPrefs = ((Activity) mContext).getSharedPreferences("UserData", MODE_PRIVATE);
        return mPrefs.getString("user_id", "");
    }

    public static String getUserImage(Context mContext) {
        SharedPreferences mPrefs = ((Activity) mContext).getSharedPreferences("UserData", MODE_PRIVATE);
        return mPrefs.getString("profile_photo", "");
    }
}
