package dico.wule.tg.dico.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * Created by lakiu_000 on 5/11/2015.
 */
public class MyEdittext extends EditText {


    public MyEdittext(Context context) {
        super(context);
    }

    public MyEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyEdittext(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

}
