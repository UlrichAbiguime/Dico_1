package dico.wule.tg.dico.utils;

import android.util.Log;

import dico.wule.tg.dico.R;
import dico.wule.tg.dico.db.providers.MyContentProvider;
import dico.wule.tg.dico.entities.DictResult;
import dico.wule.tg.dico.entities.Parts_Array_Item;
import dico.wule.tg.dico.entities.Symbols_Array_Item;

/**
 * Created by lakiu_000 on 3/25/2015.
 */
public class Constants {

    // job authority
    public static final String jobbing_authority = "content://"+ MyContentProvider.authority;
    public static final String ACTIONBAR_HIDING = "viewmatchinguri" , ACTIONBAR_SHOWING = "hellobitch";

    // constants.
    public static final String DICT = "dict", LEARN = "learn", FAVORITE = "favorite", HISTORY = "history", SETTINGS = "settings";
    public static final String[] TAB = {DICT, FAVORITE/*, HISTORY, SETTINGS*/};
    public static final int ITEMPERPAGE = 3;
    public static final String SPINNER_PREF = "spinnerdata";
    public static final String TOGGLE_KEYBOARD = "keyboardtoggling";
    public static final int URI_KEYBOARD_TOGGLING = 35;
    public static String Db_name = "dico.db", Tag_SpL = "spL", Tag_SpR = "spR";

    /*
        <string-array name="lang_from">
        <item name="en">English</item>
        <item name="fr">Francais</item>
        <item name="zh">中文</item>
        <item name="spa">Espagnol</item>
        <item name="pt">Portuguese</item>
        <!--   <item name="ru">Russian</item>-->
        </string-array>*/
    public static final String EN = "English", FR = "Francais", ZH = "中文", ES = "Espagnol", PT = "Portuguese",
            RS = "Russian";

    public static final int URI_SHOW_ACTIONBAR = 33,  URI_HIDE_ACTIONBAR = 34;

    public static void makeLog(String jsonObj) {
//        Log.v("oneofakind", jsonObj);
    }

    public static String convertDictResultToString(DictResult dictResult) {

        StringBuilder str = new StringBuilder();
        str.append("<strong><u>"+dictResult.word_name+" </u></strong>"+ "<br/>");
        for (int i = 0; i < dictResult.symbols.length; i++) {
            Symbols_Array_Item symb = dictResult.symbols[i];

            if (symb.ph_am != null)
                str.append("<strong>[美]:</strong> "+symb.ph_am+"   " + "<strong>");
            if (symb.ph_en != null)
                str.append("[英]:</strong> "+symb.ph_en+ "<br/>"+ "<br/>");
            for (int j = 0; j < symb.parts.length; j++) {
                Parts_Array_Item part = symb.parts[j];
                str.append("<br/> "+(j+1)+"- "+ part.part);
                for (int k = 0; k < part.means.length; k++) {
                    str.append(part.means[k]+ (k < part.means.length-1?" - " : ""));
                }
            }
        }
        return str.toString();
    }


    public static String notUnderlinedconvertDictResultToString(DictResult dictResult) {

        StringBuilder str = new StringBuilder();
        str.append("<strong>"+dictResult.word_name+" </strong>"+ "<br/>");
        for (int i = 0; i < dictResult.symbols.length; i++) {
            Symbols_Array_Item symb = dictResult.symbols[i];
            str.append("<strong>[美]:</strong> "+symb.ph_am+"   " + "<strong>[英]:</strong> "+symb.ph_en+ "<br/>"+ "<br/>");
            for (int j = 0; j < symb.parts.length; j++) {
                Parts_Array_Item part = symb.parts[j];
                str.append("<br/> "+(j+1)+"- "+ part.part);
                for (int k = 0; k < part.means.length; k++) {
                    str.append(part.means[k]+ (k < part.means.length-1?" - " : ""));
                }
            }
        }
        return str.toString();
    }

}
