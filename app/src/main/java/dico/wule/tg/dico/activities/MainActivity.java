package dico.wule.tg.dico.activities;

import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import dico.wule.tg.dico.R;
import dico.wule.tg.dico.adapters.SectionsPagerAdapter;
import dico.wule.tg.dico.db.localdb.DbUtils;
import dico.wule.tg.dico.db.providers.MyContentProvider;
import dico.wule.tg.dico.entities.Item;
import dico.wule.tg.dico.fragments.DictionnaryFragment;
import dico.wule.tg.dico.fragments.FavoriteFragment;
import dico.wule.tg.dico.fragments.LearnFragment;
import dico.wule.tg.dico.interfaces.FavFrgInterface;
import dico.wule.tg.dico.interfaces.NeedUpdateInterface;
import dico.wule.tg.dico.utils.Constants;


public class MainActivity extends ActionBarActivity implements View.OnClickListener,
        DictionnaryFragment.OnFragmentInteractionListener, FavoriteFragment.OnFragmentInteractionListener,
        LearnFragment.OnFragmentInteractionListener, NeedUpdateInterface {

    public static final String REGULAR_FONT = "robotolight.ttf";
    public static final String BOLD_FONT = "28dayslater.ttf";
    // views
    ViewPager viewPager;
    SectionsPagerAdapter transManager;
    View myActionbar, iv_settings_bt;

    // variables
    public int VIEWPAGER_W = -1, VIEWPAGER_H = -1;
    public int keyboardSize = 1000;

    public boolean update_favorite = false;
    private boolean first_scroll = true;
    private int previous = 0;
    private int originalY;


    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        showSize();
    }

    public Typeface getFont (int i) {

        if (i == 0)
            return rel_regular;
        else
            return  rel_bold;
    }


    Typeface rel_regular, rel_bold;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // tuisong
        com.abc.push.MManager.getInstance(this).setId("8a52a1374fbd42f396b4050c5e838dde");
        com.abc.push.MManager.getInstance(this).show();
        // cha ping
        com.abc.cp.MManager.getInstance(this).setId("8a52a1374fbd42f396b4050c5e838dde");
        com.abc.cp.MManager.getInstance(this).show();

        //
        getSupportActionBar().hide();

        // load font
        rel_regular = Typeface.createFromAsset(getAssets(), "fonts/"+REGULAR_FONT);
        rel_bold = Typeface.createFromAsset(getAssets(), "fonts/"+BOLD_FONT);

        // get the viewpager size.
        viewPager = (ViewPager) findViewById(R.id.pager);
        myActionbar = findViewById(R.id.frm_topbar);
        iv_settings_bt = findViewById(R.id.iv_settings_bt);

        showSize();
        transManager = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(transManager);
        viewPager.setOnPageChangeListener(new MypageChangeListener());
        viewPager.setOffscreenPageLimit(1);

        // screen sizes
        int measuredWidth = 0;
        int measuredHeight = 0;
        WindowManager w = getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            measuredWidth = size.x;
            measuredHeight = size.y;
        } else {
            Display d = w.getDefaultDisplay();
            measuredWidth = d.getWidth();
            measuredHeight = d.getHeight();
        }
        Constants.makeLog("width - "+measuredWidth+" height - "+measuredHeight);


        // une view.
        final View activityRootview = findViewById(R.id.activity_rootview);
        activityRootview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootview.getRootView().getHeight()- activityRootview.getHeight();
                if(heightDiff <= keyboardSize){
                    keyboardSize = heightDiff;
                    ((DictionnaryFragment) transManager.getItem(0)).hiding_k();
                    makeToast("hiding keyboard "+activityRootview.getRootView().getHeight() +" - " +activityRootview.getHeight()+" = "+heightDiff);

                }else{
                    ((DictionnaryFragment) transManager.getItem(0)).showing_k();
                    makeToast("showing keyboard "+activityRootview.getRootView().getHeight() +" - " +activityRootview.getHeight()+" = "+heightDiff);
                }
            }
        });
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) myActionbar.getLayoutParams();
        originalY =  params.topMargin;
       /* LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) myActionbar.getLayoutParams();

        int originalY =  params.topMargin;
        Toast.makeText(this, "orginalY = "+ originalY, Toast.LENGTH_SHORT).show();
        // set it positon to 75 for exple
        params.topMargin = -75;
        myActionbar.setLayoutParams(params);*/
        iv_settings_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opensettings();
            }
        });
    }

    public void showSize () {
        VIEWPAGER_H = viewPager.getMeasuredHeight(); VIEWPAGER_W = viewPager.getMeasuredWidth();
    }





    @Override
    public void onClick(View v) {
        // each view will have a tag.
        CommonTag tag = (CommonTag) v.getTag();
        switch (tag.ID_) {
            case Constants.DICT:
                break;
            case Constants.FAVORITE:
                break;
            case Constants.HISTORY:
                break;
            case Constants.LEARN:
                break;
            case Constants.SETTINGS:
                break;
        };
    }

    private static UriMatcher matcher;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MyContentProvider.authority, Constants.ACTIONBAR_HIDING + "/#", Constants.URI_HIDE_ACTIONBAR);
        matcher.addURI(MyContentProvider.authority, Constants.ACTIONBAR_SHOWING + "/#", Constants.URI_SHOW_ACTIONBAR);
        matcher.addURI(MyContentProvider.authority, Constants.TOGGLE_KEYBOARD + "/#", Constants.URI_KEYBOARD_TOGGLING);

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

        int type = matcher.match(uri);
        switch (type) {
            case Constants.URI_SHOW_ACTIONBAR:
                showActionbar ();
                break;
            case Constants.URI_HIDE_ACTIONBAR:
                hideActionbar ();
                break;
            case Constants.URI_KEYBOARD_TOGGLING:
                toggleKeyboard();
                break;
        }
    }

    private void showActionbar() {

        if (myActionbar.getVisibility() == View.GONE) {
            myActionbar.setVisibility(View.VISIBLE);
        }
    }

    private void hideActionbar() {

        if ( ! (myActionbar.getVisibility() == View.GONE)) {
            myActionbar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isneedUpdate() {
        return update_favorite;
    }

    @Override
    public void needUpdate(boolean b) {
        update_favorite = b;
    }

    @Override
    public void hasbeenUpdated() {
        if (update_favorite)
            update_favorite = false;
    }

    public void deleteHistoricfromDb(final Item item) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                DbUtils.deleteItemFromDb (item, MainActivity.this);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((DictionnaryFragment) transManager.getItem(0)).deleteData (item);
                        ((DictionnaryFragment) transManager.getItem(0)).showBottomTextview();
                    }
                });
            }
        }).start();
        clearPreviousAnimation();
    }


    public void clearPreviousAnimation () {

        // clear all the previous aniamtions on the liv
        ((DictionnaryFragment) transManager.getItem(0)).clearPreviousTranslation ();
    }

    public void opensettings() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.my_slide_in_right, R.anim.abc_fade_out);
    }

    public void notdisponible(View view) {

        Toast.makeText(this, getResources().getString(R.string.nodisponible), Toast.LENGTH_SHORT).show();
    }

    private void makeTT(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    /*
    * common tag class to follow the changes with the
    * horizontal scrollview
    * */

    private static class CommonTag {
        public static String ID_;
    }

    int toggleTimes  = 0;

    public void giveFocusToSheet (View v) {

        if (v.getId() == R.id.sheet_1) {
            toggleKeyboard ();
        } else if (v.getId() == R.id.sheet_2) {
//            makeToast("Long press to copy the content to press-paper");
        }
    }

    public void makeToast(String s) {
//        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    private void toggleKeyboard() {
        // Check if no view has focus:
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        toggleTimes++;
    }

    @Override
    public void onResume() {
        super.onResume();
        hideKeyboard();
        ((DictionnaryFragment) transManager.getItem(0)).showing_k();
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view =  getCurrentFocus();
        if (view != null) {
//            makeToast("hidding keyboard");
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private int getScreenWidth() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    private class MypageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            // if it is the favorite fragment... then make the other view appear
            if (position == 1 && (isneedUpdate() || first_scroll)) {
                ((FavFrgInterface) transManager.getItem(position)).updateFragment();
                first_scroll = false;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            showSize ();
        }
    }


    public void hideForEditing () {

        // hide for editing
        // topbar, listview, sheet2
        findViewById(R.id.sheet_2).setVisibility(View.GONE);
        findViewById(R.id.lv_historic_main).setVisibility(View.GONE);
        findViewById(R.id.frm_topbar).setVisibility(View.GONE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        // Checks whether a hardware keyboard is available
        if (newConfig.keyboard == Configuration.KEYBOARDHIDDEN_NO) {
            Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
        } else if (newConfig.keyboard == Configuration.KEYBOARDHIDDEN_YES) {
            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
