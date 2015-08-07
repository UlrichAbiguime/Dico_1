package dico.wule.tg.dico.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayout;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import dico.wule.tg.dico.R;
import dico.wule.tg.dico.activities.MainActivity;
import dico.wule.tg.dico.activities.ReadOnlyNoteActivity;
import dico.wule.tg.dico.adapters.FavoriteGvAdapter;
import dico.wule.tg.dico.db.localdb.DbUtils;
import dico.wule.tg.dico.entities.FavoriteSave;
import dico.wule.tg.dico.entities.Item;
import dico.wule.tg.dico.interfaces.FavFrgInterface;
import dico.wule.tg.dico.interfaces.NeedUpdateInterface;
import dico.wule.tg.dico.utils.Constants;
import dico.wule.tg.dico.utils.UtilsFunctions;


public class FavoriteFragment extends android.support.v4.app.Fragment implements FavFrgInterface {


    private static final int DATA_RECEIVED = 332;
    private static final int START_UPDATING = 331, END_UPDATING = 330;
    private static   int LNY_PADDING = 0, ITEM_INTERSPACING = 0;

    //Views
    RelativeLayout rl_first, rl_second, rl_third;
    GridLayout gridLayout;
    ProgressBar pb_updating;
    View inner_view;
    // then the header.
    // layouts

    // variables
    private int VIEWPAGER_W = -1, VIEWPAGER_H = -1;


    private OnFragmentInteractionListener mListener;
    private Context mContext = null;
    private FavoriteGvAdapter adap = null;
    private int GRIDVIEW_COLUMN_WIDTH = -1, GRIDVIEW_COLUMN_HEIGHT = -1;
    private FrameLayout rootview;

    public static FavoriteFragment newInstance() {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public android.os.Handler mHandler = new android.os.Handler () {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DATA_RECEIVED :
                    createViews((List<FavoriteSave>) msg.obj);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pb_updating.setVisibility(View.GONE);
                        }
                    }, 1000);
//                    makeToast("GWidth = "+GRIDVIEW_COLUMN_WIDTH+" \n "+"GHeigth = "+GRIDVIEW_COLUMN_HEIGHT+" \n SCREENWIDTH = "+VIEWPAGER_W);
                    break;
                case START_UPDATING:
                    pb_updating.setVisibility(View.VISIBLE);
                    break;
                case END_UPDATING:
                    createViews((List<FavoriteSave>) msg.obj);
                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = (FrameLayout) inflater.inflate(R.layout.fragment_notes, container, false);
        mContext = getActivity();


        // algo
        updateViewpagerSizes ();

        loadFavoriteFromDb();

//        gridLayout.setUseDefaultMargins(true);
        return rootview;
    }

    private void updateViewpagerSizes() {
        VIEWPAGER_H = ((MainActivity) getActivity()).VIEWPAGER_H;
        VIEWPAGER_W = ((MainActivity) getActivity()).VIEWPAGER_W;
        Constants.makeLog("h -- "+VIEWPAGER_H+" w -- "+VIEWPAGER_W);

        if (gridLayout != null) {
            LNY_PADDING = gridLayout.getPaddingLeft();
        }
        // algo
        GRIDVIEW_COLUMN_WIDTH =  (VIEWPAGER_W - 2*LNY_PADDING - 4 * ITEM_INTERSPACING ) / 3;
        GRIDVIEW_COLUMN_HEIGHT =  (VIEWPAGER_H) / 4  ;

    }

    private void createViews (List<FavoriteSave> data) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        inner_view = inflater.inflate(R.layout.frg_favorite_inner, null);
        // getviews
        rl_first = (RelativeLayout) inner_view.findViewById(R.id.rl_first);
        rl_second = (RelativeLayout) inner_view.findViewById(R.id.rl_second);
        rl_third = (RelativeLayout) inner_view.findViewById(R.id.rl_third);
        gridLayout = (GridLayout) inner_view.findViewById(R.id.gridlayout);
        pb_updating = (ProgressBar) inner_view.findViewById(R.id.pb_updating);
        LNY_PADDING = gridLayout.getPaddingLeft();
        // a long loop that will treat everything
        for (int i = 0; i < data.size(); i++) {
            FavoriteSave save = data.get(i);
            save.body = UtilsFunctions.trimAll(save.body);
            save.title = UtilsFunctions.trimAll(save.title);
            if (i == 0) {
                View iv1 = inflater.inflate(R.layout.fav_item, null);
                TextView tv = (TextView) iv1.findViewById(R.id.tv_title);  tv.setText(save.title); tv.setTextSize(20);
                TextView body = (TextView) iv1.findViewById(R.id.tv_body);  body.setText(Html.fromHtml(save.body)); body.setTextSize(14);
                tv.setTypeface(((MainActivity) getActivity()).getFont(9));
                body.setTypeface(((MainActivity) getActivity()).getFont(0));
                iv1.setBackgroundResource(R.drawable.fav_back1);
                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                params1.width = 2*GRIDVIEW_COLUMN_WIDTH; tv.setMaxWidth(2*GRIDVIEW_COLUMN_WIDTH); iv1.setMinimumWidth(2*GRIDVIEW_COLUMN_WIDTH);
                params1.height = 2*GRIDVIEW_COLUMN_HEIGHT; body.setMaxWidth(2*GRIDVIEW_COLUMN_WIDTH);  iv1.setMinimumHeight(2*GRIDVIEW_COLUMN_HEIGHT);
                rl_first.setTag(save.body);
                rl_first.setOnClickListener(new SingleNoteOnclickListener());
                rl_first.addView(iv1, params1);
            } else if (i == 1) {
                View iv2 = inflater.inflate(R.layout.fav_item, null);
                TextView tv = (TextView) iv2.findViewById(R.id.tv_title); tv.setText(save.title); tv.setTextSize(14);
                TextView body = (TextView) iv2.findViewById(R.id.tv_body);body.setText(Html.fromHtml(save.body)); body.setTextSize(8);
                tv.setTypeface(((MainActivity) getActivity()).getFont(9));
                body.setTypeface(((MainActivity) getActivity()).getFont(0));
                iv2.setBackgroundResource(R.drawable.fav_back1);
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                params2.width =  GRIDVIEW_COLUMN_WIDTH; tv.setMaxWidth(GRIDVIEW_COLUMN_WIDTH); iv2.setMinimumWidth(GRIDVIEW_COLUMN_WIDTH);
                params2.height =  GRIDVIEW_COLUMN_HEIGHT; body.setMaxWidth(GRIDVIEW_COLUMN_WIDTH);  iv2.setMinimumHeight(GRIDVIEW_COLUMN_HEIGHT);
//                params2.setMargins(ITEM_INTERSPACING, ITEM_INTERSPACING, 0, ITEM_INTERSPACING);
                rl_second.setTag(save.body);
                rl_second.setOnClickListener(new SingleNoteOnclickListener());
                rl_second.addView(iv2, params2);
            } else if (i == 2) {

                View iv3 = inflater.inflate(R.layout.fav_item, null);
                TextView tv = (TextView) iv3.findViewById(R.id.tv_title); tv.setText(save.title); tv.setTextSize(14);
                TextView body = (TextView) iv3.findViewById(R.id.tv_body); body.setText(Html.fromHtml(save.body)); body.setTextSize(8);
                tv.setTypeface(((MainActivity) getActivity()).getFont(9));
                body.setTypeface(((MainActivity) getActivity()).getFont(0));

                iv3.setBackgroundResource(R.drawable.fav_back1);
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                params2.width =  GRIDVIEW_COLUMN_WIDTH; tv.setMaxWidth(GRIDVIEW_COLUMN_WIDTH); iv3.setMinimumWidth(GRIDVIEW_COLUMN_WIDTH);
                params2.height =  GRIDVIEW_COLUMN_HEIGHT; body.setMaxWidth(GRIDVIEW_COLUMN_WIDTH);  iv3.setMinimumHeight(GRIDVIEW_COLUMN_HEIGHT);
//                params2.setMargins(ITEM_INTERSPACING, ITEM_INTERSPACING, 0, ITEM_INTERSPACING);
                rl_third.setTag(save.body);
                rl_third.setOnClickListener(new SingleNoteOnclickListener());
                rl_third.addView(iv3, params2);
            } else {

                RelativeLayout layout = new RelativeLayout(mContext);
                View iv = inflater.inflate(R.layout.fav_item, null);
                TextView tv = (TextView) iv.findViewById(R.id.tv_title); tv.setText(save.title); tv.setTextSize(14);
                TextView body = (TextView) iv.findViewById(R.id.tv_body); body.setText(Html.fromHtml(save.body)); body.setTextSize(8);
                tv.setTypeface(((MainActivity) getActivity()).getFont(9));
                body.setTypeface(((MainActivity) getActivity()).getFont(0));
                iv.setBackgroundResource(R.drawable.fav_back1);
                layout.addView(iv);
                GridLayout.LayoutParams par = new GridLayout.LayoutParams();
                par.width =  GRIDVIEW_COLUMN_WIDTH;  tv.setMaxWidth(GRIDVIEW_COLUMN_WIDTH); iv.setMinimumWidth(GRIDVIEW_COLUMN_WIDTH);
                par.height =  GRIDVIEW_COLUMN_HEIGHT; body.setMinimumHeight(GRIDVIEW_COLUMN_WIDTH);  iv.setMinimumHeight(GRIDVIEW_COLUMN_HEIGHT);
                par.setGravity(Gravity.FILL_HORIZONTAL | Gravity.FILL_VERTICAL);
                layout.setTag(save.body);
                layout.setOnClickListener(new SingleNoteOnclickListener());
                gridLayout.addView(layout, par);
            }
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rootview != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rootview.setVisibility(View.GONE);
                            rootview.removeAllViews();
                            rootview.addView(inner_view);
                            rootview.setVisibility(View.VISIBLE);
                            pb_updating.setVisibility(View.GONE);
                        }
                    });

                }
                pb_updating.setVisibility(View.GONE);
            }
        }, 1000);
    }


    private void loadFavoriteFromDb() {

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        // get the data from the db
                        List<FavoriteSave> data = DbUtils.loadFavorite (mContext);
                        Constants.makeLog(data.toString());
                        Message msg = Message.obtain();
                        msg.obj = data;
                        msg.what = DATA_RECEIVED;
                        mHandler.sendMessage(msg);
                    }
                }
        ).start();
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void updateFragment() {
        // reload the data and show a view in the top
        // during the updating
        new Thread() {
            @Override
            public void run() {

                // send a message to handler
                mHandler.sendEmptyMessage(START_UPDATING);
                updateViewpagerSizes ();
                loadFavoriteFromDb();
                Constants.makeLog("updating fragment");
                ((NeedUpdateInterface) getActivity()).hasbeenUpdated();
            }
        }.start();
    }

    public void makeToast (String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class SingleNoteOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // get the tag // open the read_only activity
            Intent i = new Intent(getActivity(), ReadOnlyNoteActivity.class);
            i.putExtra(ReadOnlyNoteActivity.NOTE, (String)v.getTag());
            startActivity(i); // change after by start for result.
            getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

}
