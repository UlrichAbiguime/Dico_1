package dico.wule.tg.dico.db.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import dico.wule.tg.dico.db.localdb.MyOpenDbHelper;

/**
 * Created by lakiu_000 on 3/25/2015.
 */
public class MyContentProvider extends ContentProvider{

    /*
      1-  content provider that will get historics data --- save, delete them... no modifying
      2-    get favorites, add to db and delete from db.
    */


    // authorities, and links matching for
    // all the different actions.
    public static final String authority = "dico.wule.tg.dico.authorities";
    private   static UriMatcher matcher;

    public static final int FAV_ITEM = 1000;
    public static final int RECENT_ITEM = 1001;
    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(authority, MyOpenDbHelper.FAVORITE_TB + "/#", FAV_ITEM);
        matcher.addURI(authority, MyOpenDbHelper.RECENT_TRANSLATION_TB + "/#", RECENT_ITEM);
    }

    // variables
    private MyOpenDbHelper openHelper;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        openHelper = new MyOpenDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        int type = matcher.match(uri);
        Cursor cursor = null;
        Cursor c = null;
        switch (type) {
            case FAV_ITEM:
                db = openHelper.getReadableDatabase();
                c = db.query(MyOpenDbHelper.FAVORITE_TB,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case RECENT_ITEM:
                db = openHelper.getReadableDatabase();
                c = db.query(MyOpenDbHelper.RECENT_TRANSLATION_TB,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
        }
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int type = matcher.match(uri);
        Uri newUri = null;
        long count = -1;
        switch (type) {
            case FAV_ITEM:
                db = openHelper.getWritableDatabase();
                count = db.insert(MyOpenDbHelper.FAVORITE_TB, "_id", values);
                newUri = ContentUris.withAppendedId(uri, count);
                break;
            case RECENT_ITEM:
                db = openHelper.getWritableDatabase();
                count = db.insert(MyOpenDbHelper.RECENT_TRANSLATION_TB, "_id", values);
                newUri = ContentUris.withAppendedId(uri, count);
                break;
        }
        return newUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = -1;
        int code = matcher.match(uri);
        switch (code) {
            case FAV_ITEM:
                db = openHelper.getWritableDatabase();
                db.delete(MyOpenDbHelper.FAVORITE_TB, selection, selectionArgs);
                break;
            case RECENT_ITEM:
                db = openHelper.getWritableDatabase();
                db.delete(MyOpenDbHelper.RECENT_TRANSLATION_TB, selection, selectionArgs);
                break;
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // im not going to update but, anyway...
        int count = -1;
        int code = matcher.match(uri);
        switch (code) {
            case FAV_ITEM:
                db = openHelper.getWritableDatabase();
                count = db.update(MyOpenDbHelper.FAVORITE_TB, values, selection, selectionArgs);
                break;
            case RECENT_ITEM:
                db = openHelper.getWritableDatabase();
                count = db.update(MyOpenDbHelper.RECENT_TRANSLATION_TB, values, selection, selectionArgs);
                break;
        }
        return count;
    }
}
