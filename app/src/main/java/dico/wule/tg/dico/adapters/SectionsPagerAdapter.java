package dico.wule.tg.dico.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.Map;

import dico.wule.tg.dico.fragments.DictionnaryFragment;
import dico.wule.tg.dico.fragments.FavoriteFragment;
import dico.wule.tg.dico.fragments.HistoryFragment;
import dico.wule.tg.dico.fragments.LearnFragment;
import dico.wule.tg.dico.utils.Constants;

/**
 * Created by lakiu_000 on 3/25/2015.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    FragmentContainer container;


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        container = new FragmentContainer();
    }

    @Override
    public Fragment getItem(int i) {
        return container.getFragmentWithTag(Constants.TAB[i]);
    }

    @Override
    public int getCount() {
        return Constants.TAB.length;
    }

    private class FragmentContainer {

        private Map<String, Fragment> map_fragments;

        private FragmentContainer() {
            this.map_fragments = new HashMap<>();
        }

        public Fragment getFragmentWithTag (String tag) {

            Fragment fr = null;
            switch (tag) {
                case Constants.DICT:
                    if (map_fragments.get(tag) == null) {
                        map_fragments.put(tag, DictionnaryFragment.newInstance());
                    }
                    fr = map_fragments.get(tag);
                    break;
              /*  case Constants.HISTORY:
                    if (map_fragments.get(tag) == null) {
                        map_fragments.put(tag, HistoryFragment.newInstance());
                    }
                    fr = map_fragments.get(tag);
                    break;*/
                case Constants.FAVORITE:
                    if (map_fragments.get(tag) == null) {
                        map_fragments.put(tag, FavoriteFragment.newInstance());
                    }
                    fr = map_fragments.get(tag);
                    break;
            }
            return fr;
        }


        public   FragmentContainer getInstance() {
            return container;
        }
    }
}
