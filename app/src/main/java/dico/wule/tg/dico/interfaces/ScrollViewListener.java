package dico.wule.tg.dico.interfaces;

import dico.wule.tg.dico.customviews.CustomScrollview;

/**
 * Created by lakiu_000 on 5/15/2015.
 */
public interface ScrollViewListener {
    void onScrollChanged(CustomScrollview scrollView,
                         int x, int y, int oldx, int oldy);
}