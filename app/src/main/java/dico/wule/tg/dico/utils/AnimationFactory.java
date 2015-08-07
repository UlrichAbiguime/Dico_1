package dico.wule.tg.dico.utils;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

/**
 * Created by lakiu_000 on 3/27/2015.
 */
public class AnimationFactory {

    public static Animation getSecondSheetTranslationAnimation (Context context) {

        Animation anim = null;
        // gets from the bottom to the up with a speed interpolator.
        anim = new TranslateAnimation(0,0, 1f, -1f);
        anim.setDuration(1000);
        anim.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return 1.5f;
            }
        });
        return anim;
    }

}
