package dico.wule.tg.dico.db.localdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import dico.wule.tg.dico.utils.Constants;

/**
 * Created by lakiu_000 on 3/28/2015.
 */
public class MyOpenDbHelper extends SQLiteOpenHelper {


    // favorite table
    public static final String FAVORITE_TB = "tab_favorite", F_ID = "_id", F_ITEM = "js_item", F_DATE = "date", F_REMARKS = "remarks";
    public static final String[] FAVORITE_ALL = {F_ID, F_ITEM, F_DATE, F_REMARKS};
    public static final String sql_favorite = "create table if not exists "+FAVORITE_TB+" (" +
            " "+ F_ID+" integer not null primary key autoincrement, " +
            " "+ F_ITEM+" text, " +
            " "+ F_DATE+" text, " +
            " "+ F_REMARKS+" text);";

    // recent translation tb
    public static final String RECENT_TRANSLATION_TB = "tab_recent", RT_ID="_id", RT_JS_OBJ = "rt_obj", RT_DATE = "trans_date";
    public static final String[] RECENT_TRANSLATION_ALL = {RT_ID, RT_JS_OBJ, RT_DATE};
    public static final String sql_recentTranslation = "create table if not exists "+RECENT_TRANSLATION_TB+" (" +
            " "+ RT_ID+" integer not null primary key autoincrement, " +
            " "+ RT_JS_OBJ+" text, " +
            " "+ RT_DATE+" text );";

    public MyOpenDbHelper(Context c) {
        super(c, Constants.Db_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql_favorite);
        db.execSQL(sql_recentTranslation);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // im upgrading nothing. sorry. Ulrich, Abiguime.
    }
}
