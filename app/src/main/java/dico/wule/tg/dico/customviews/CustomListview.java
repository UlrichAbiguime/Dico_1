package dico.wule.tg.dico.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import dico.wule.tg.dico.utils.Constants;
import dico.wule.tg.dico.utils.UtilsFunctions;

/**
 * Created by lakiu_000 on 5/8/2015.
 */
public class CustomListview extends ListView {
    public CustomListview(Context context) {
        super(context);
    }

    public CustomListview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomListview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    boolean expanded = false;


    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // HACK!  TAKE THAT ANDROID!

        if (isExpanded()) {
            // Calculate entire height by providing a very large height hint.
            // View.MEASURED_SIZE_MASK represents the largest height possible.
            try {
                int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
                        MeasureSpec.AT_MOST);
                super.onMeasure(widthMeasureSpec, expandSpec);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
                params.height = getMeasuredHeight();
                params.width = getMeasuredWidth();
                params.gravity = Gravity.CENTER_VERTICAL;
//                Constants.makeLog("params height --- "+ params.height);
                params.height += UtilsFunctions.convertPxtoDip(3, getContext()) * (getCount()-1);
                setMinimumHeight(params.height);
//                Constants.makeLog("params height --- "+ params.height);
                setMeasuredDimension(params.width, params.height);
            } catch (Exception e) {
                e.printStackTrace();
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
