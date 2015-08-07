package dico.wule.tg.dico.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.Locale;

import dico.wule.tg.dico.activities.MainActivity;

/**
 * Created by lakiu_000 on 4/11/2015.
 */
public class UtilsFunctions {

    public static int getScreenHeigth (Context context) {

        DisplayMetrics metrics = new DisplayMetrics();
        ((MainActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static int getScreenWidth (Context context) {

        DisplayMetrics metrics = new DisplayMetrics();
        ((MainActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }


    public static String trimAll (String s) {
        s = trim ('"', s);
        s = trim ('“', s);
        s = trim ('”', s);
        s = trim ('\'', s);
        s = s.replace("》", "");
        s = s.replace("《", "");
        s = s.replace("%20", " ");
        return s;
    }

    public static String trim(char c, String s) {

        int start = 0, last =  s.length() - 1;
        int end = last;
        char[] value = s.toCharArray();
        while ((start <= end) && (value[start] == c)) {
            start++;
        }
        while ((end >= start) && (value[end] == c)) {
//            Constants.makeLog("position "+end+" char is ..."+value[end]+ " ...");
            end--;
        }
        if (start == 0 && end == last) {
            return s;
        }
        return s.substring(start, end+1);
    }

    public static int convertPxtoDip(int pixel, Context context){

        float scale = context.getResources().getDisplayMetrics().density;
        int dips=(int) ((pixel * scale) + 0.5f);
        return dips;
    }

    public static int convertDiptoPx(int dips, Context context){

        float scale = context.getResources().getDisplayMetrics().density;
        int pixel= (int)((dips - 0.5f) / scale);
        return pixel;
    }

    public static String getTime(int duration){
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        return dateFormat.format(duration);
    }

}
