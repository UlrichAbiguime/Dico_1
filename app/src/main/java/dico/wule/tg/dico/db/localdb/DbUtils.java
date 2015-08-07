package dico.wule.tg.dico.db.localdb;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import dico.wule.tg.dico.entities.FavoriteSave;
import dico.wule.tg.dico.entities.Item;
import dico.wule.tg.dico.utils.Constants;

import static dico.wule.tg.dico.utils.UtilsFunctions.trimAll;

/**
 * Created by lakiu_000 on 3/28/2015.
 */
public class DbUtils {

    public static int isFavorite (Item item, Context mContext) {

        ContentResolver cr = mContext.getContentResolver();
        Gson gson = new Gson ();
        String obj = gson.toJson(item);
        Uri uri = Uri.parse(Constants.jobbing_authority+"/"+MyOpenDbHelper.FAVORITE_TB+"/1");
        Cursor c = cr.query(uri, MyOpenDbHelper.FAVORITE_ALL, MyOpenDbHelper.F_ITEM+" = ?"+" ", new String[]{obj}, null);
        if (c.moveToNext()) {
            return c.getInt(c.getColumnIndex(MyOpenDbHelper.F_ID));
        } else
            return -1;
    }

    public static boolean setFavorite (Item item, Context mContext, String remark) {

        ContentResolver cr = mContext.getContentResolver();
        Gson gson = new Gson ();
        String obj = gson.toJson(item);
        Uri uri = Uri.parse(Constants.jobbing_authority+"/"+MyOpenDbHelper.FAVORITE_TB+"/1");
        Cursor c = cr.query(uri, MyOpenDbHelper.FAVORITE_ALL, MyOpenDbHelper.F_ITEM+" = ?"+" ", new String[]{obj}, null);
        String date = "";
        ContentValues cv = new ContentValues();
        cv.put(MyOpenDbHelper.F_ITEM, obj);
        cv.put(MyOpenDbHelper.F_DATE, date);
        if (remark != "")
            cv.put(MyOpenDbHelper.F_REMARKS, remark);
        else
            cv.put(MyOpenDbHelper.F_REMARKS, "...");

        if (!c.moveToNext()) {
            // not exist
            cr.insert(uri, cv);
        } else {
            // exist
            int id =  c.getInt(c.getColumnIndex(MyOpenDbHelper.F_ID));
            cv.put(MyOpenDbHelper.F_ID, id);
            cr.update(uri, cv, MyOpenDbHelper.F_ITEM+" = ?"+" and "+MyOpenDbHelper.F_ID+" = ?", new String[]{obj, id+""});
        }
        return true;
    }

    public static List<Item> getTranslateItemFrom_To(Context mContext, int start, int to) {


        ContentResolver cr = mContext.getContentResolver();
        Gson gson = new Gson ();
        Uri uri = Uri.parse(Constants.jobbing_authority+"/"+MyOpenDbHelper.RECENT_TRANSLATION_TB+"/1");
        Cursor c = cr.query(uri, MyOpenDbHelper.RECENT_TRANSLATION_ALL, " ? = ? order by _id DESC  limit "+(to-start)+" offset "+start,
                new String[]{"1", "1"}, null);
        List<Item> item = new ArrayList<>();
        while (c.moveToNext()) {
            item.add(
                    gson.fromJson( c.getString(c.getColumnIndex(MyOpenDbHelper.RT_JS_OBJ)), Item.class  )
            );
        }
        return item;
    }

    public static void saveAsHistoric(Context mContext, Item item, String remarks) {

        ContentResolver cr = mContext.getContentResolver();
        // add the string to the historic db
        Uri uri = Uri.parse(Constants.jobbing_authority+"/"+MyOpenDbHelper.RECENT_TRANSLATION_TB+"/1");
        Gson gson = new Gson();
        String date = "";
        ContentValues cv = new ContentValues();
        cv.put(MyOpenDbHelper.RT_DATE, date);
        cv.put(MyOpenDbHelper.RT_JS_OBJ, gson.toJson(item));
        cr.insert(uri, cv);
        Constants.makeLog(item.toString()+" saved to historic");
    }

    public static List<FavoriteSave> loadFavorite(Context mContext) {

        ContentResolver cr = mContext.getContentResolver();
        Gson gson = new Gson();
        Uri uri = Uri.parse(Constants.jobbing_authority + "/" + MyOpenDbHelper.FAVORITE_TB + "/1");
        Cursor c = cr.query(uri, MyOpenDbHelper.FAVORITE_ALL,
                null,
                null, "_id DESC");
        List<FavoriteSave> item = new ArrayList<>();
        while (c.moveToNext()) {
            Item t = gson.fromJson(c.getString(c.getColumnIndex(MyOpenDbHelper.F_ITEM)), Item.class);
//            Constants.makeLog("t is " + t.toString());
            if (t != null) {
                FavoriteSave fs;
                if (t.retData.trans_result != null)
                    fs = new FavoriteSave(t.retData.trans_result[0].src, c.getString(c.getColumnIndex(MyOpenDbHelper.F_REMARKS)));
                else
                    fs = new FavoriteSave(t.retData.dict_result.word_name, c.getString(c.getColumnIndex(MyOpenDbHelper.F_REMARKS)));
                item.add(fs);
            }
        }
        return item;
    }

    public static String getRemark(Context mContext,  String obj) {

        // get historic
        ContentResolver cr = mContext.getContentResolver();

        Uri uri = Uri.parse(Constants.jobbing_authority+"/"+MyOpenDbHelper.FAVORITE_TB+"/1");
        Cursor c = cr.query(uri, MyOpenDbHelper.FAVORITE_ALL, MyOpenDbHelper.F_ITEM + " = ? ", new String[]{obj}, null);
        // get the result
        if (c.moveToNext()) {
            // get the item
            String remark = c.getString(c.getColumnIndex(MyOpenDbHelper.F_REMARKS));
            remark = trimAll (remark);
            return remark;
        }
        return "";
    }

    public static void saveAsRemark(Context mContext, String s, String obj) {

        ContentResolver cr = mContext.getContentResolver();
        Uri uri = Uri.parse(Constants.jobbing_authority+"/"+MyOpenDbHelper.FAVORITE_TB+"/1");
        ContentValues cv = new ContentValues();
        cv.put(MyOpenDbHelper.F_REMARKS, s);
        cr.update(uri, cv, MyOpenDbHelper.F_ITEM + " = ? ", new String[]{obj});
    }

    public static void deleteItemFromDb (Item item, Context mContext) {

        ContentResolver cr = mContext.getContentResolver();
        // add the string to the historic db
        Uri uri = Uri.parse(Constants.jobbing_authority+"/"+MyOpenDbHelper.RECENT_TRANSLATION_TB+"/1");
        Gson gson = new Gson();
        cr.delete(uri, MyOpenDbHelper.RT_JS_OBJ+" = ?", new String[]{gson.toJson(item)});
    }

    public static void delFromFavorite(Item item, Context mContext) {

        ContentResolver cr = mContext.getContentResolver();
        // add the string to the historic db
        Uri uri = Uri.parse(Constants.jobbing_authority+"/"+MyOpenDbHelper.FAVORITE_TB+"/1");
        Gson gson = new Gson();
        cr.delete(uri, MyOpenDbHelper.F_ITEM+" = ?", new String[]{gson.toJson(item)});
    }
}
