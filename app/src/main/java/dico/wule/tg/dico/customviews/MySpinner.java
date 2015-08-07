package dico.wule.tg.dico.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Spinner;

import dico.wule.tg.dico.utils.Constants;

/**
 * Created by lakiu_000 on 4/8/2015.
 */
public class MySpinner extends Spinner{
    public MySpinner(Context context) {
        super(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MySpinner(Context context, int mode) {
        super(context, mode);
    }

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes, mode);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        String tag = (String) this.getTag(); // it will be like spL or spR
        // save infos to the db.
        SharedPreferences pref = getContext().getSharedPreferences(Constants.SPINNER_PREF, 0);
        SharedPreferences.Editor editor =  pref.edit();
        editor.putInt(tag, which);
        editor.commit();
    }

    public void updateMe () {
        SharedPreferences pref = getContext().getSharedPreferences(Constants.SPINNER_PREF, 0);
        String tag = (String) getTag();
        int c = 0;
        if (tag.equals(Constants.Tag_SpL))
            c = pref.getInt(tag, 0);
        else if (tag.equals(Constants.Tag_SpR))
            c = pref.getInt(tag,2);

        setSelection(c);
    }

}
