package com.malawi_music.malawi_music;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.JavascriptInterface;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by OWEN KALUNGWE on 10/11/2019.
 */

public class helper {

    public static String setDateFromTimeStamp(String timeStamp){
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");
            return formatter.format(new Date(Long.parseLong(timeStamp)));
        }catch (Exception e){
            return timeStamp;
        }
    }

    public static String randomId(int limit){
        String ID="n658iI77";
        if(limit < 1) limit = 10;
        return ID.substring(0,Math.min(ID.length(),limit)).toUpperCase();
    }

    public static String valueOf(Object obj) {
        return (obj == null) ? "null" : obj.toString();
    }


    public static boolean savePreference(String key, String value, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(context).edit();
        editor.putString("MMC" + key.toUpperCase(), value);
        if (editor.commit()) {
            return true;
        }
        return false;
    }

    public static String getPreference(String key, Context context) {
        String str = PreferenceManager.getDefaultSharedPreferences(context).
                getString("MMC" + key.toUpperCase(), "");
        return !str.equalsIgnoreCase("") ? str : "";
    }
}
