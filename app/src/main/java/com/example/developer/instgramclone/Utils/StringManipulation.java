package com.example.developer.instgramclone.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class StringManipulation {
    public static String expandUsername(String userName) {
        return userName.replace(".", " ");
    }

    public static String condenseUsername(String userName) {
        return userName.replace(" ", ".");
    }


    //extract tags from caption
    public static String getTags(String string) {
        if (string.indexOf("#") > 0) {
            StringBuilder sb = new StringBuilder();
            char[] charArray = string.toCharArray();
            boolean foundWord = false;
            for (char c : charArray) {
                if (c == '#') {
                    foundWord = true;
                    sb.append(c);
                } else {
                    if (foundWord) {
                        sb.append(c);
                    }
                }
                if (c == ' ') {
                    foundWord = false;
                }
            }
            String s = sb.toString().replace(" ", "").replace("#", ",#");
            return s.substring(1, s.length());
        }
        return string;
    }

    //---------getting timeStampDifference------------
    public static String getTimeStampDifference(String photoDate) {
        String difference = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Africa/Cairo"));
        String photoTimeStamp = photoDate;
        Date today = calendar.getTime();
        dateFormat.format(today);
        Date timeStamp;
        try {
            timeStamp = dateFormat.parse(photoTimeStamp);
            // min to hours to days (/ 1000 / 60 / 60 / 24)
            //today.getTime() - timeStamp.getTime()) get diff in millisecondes
            difference = String.valueOf(Math.round(((today.getTime() - timeStamp.getTime()) / 1000 / 60 / 60 / 24)));

        } catch (ParseException e) {
            difference = "0";
        }
        return difference;
    }

}
