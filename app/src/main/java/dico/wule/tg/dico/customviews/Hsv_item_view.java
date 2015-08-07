package dico.wule.tg.dico.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by lakiu_000 on 3/28/2015.
 */
public class Hsv_item_view extends TextView {

    public float paint;
    public float radius;
    public Canvas viewCanvas;

    public Hsv_item_view(Context context) {
        super(context);
    }

    public Hsv_item_view(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Hsv_item_view(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Hsv_item_view(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw a circle in the bacgkground
        viewCanvas = canvas;
    }
}
