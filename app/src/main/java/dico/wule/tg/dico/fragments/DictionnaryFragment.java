package dico.wule.tg.dico.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dico.wule.tg.dico.R;
import dico.wule.tg.dico.activities.MainActivity;
import dico.wule.tg.dico.activities.SetupNoteActivity;
import dico.wule.tg.dico.adapters.HistoricLvAdapter;
import dico.wule.tg.dico.adapters.Lang_From_To_SpinnerAdapter;
import dico.wule.tg.dico.customviews.CustomListview;
import dico.wule.tg.dico.customviews.CustomScrollview;
import dico.wule.tg.dico.customviews.MySpinner;
import dico.wule.tg.dico.db.localdb.DbUtils;
import dico.wule.tg.dico.entities.DictResult;
import dico.wule.tg.dico.entities.Item;
import dico.wule.tg.dico.entities.RetData;
import dico.wule.tg.dico.interfaces.Keyboard_show_hide_inteface;
import dico.wule.tg.dico.interfaces.NeedUpdateInterface;
import dico.wule.tg.dico.interfaces.ScrollViewListener;
import dico.wule.tg.dico.utils.Constants;
import dico.wule.tg.dico.utils.HttpJsonToObj;
import dico.wule.tg.dico.utils.UtilsFunctions;


public class DictionnaryFragment extends android.support.v4.app.Fragment implements View.OnClickListener ,
        ScrollViewListener,
        Keyboard_show_hide_inteface {

    private static final int TRANSLATION_FINISH = 99;
    private static final int TRANSLATION_ERROR = 100;
    private static final int TRANSLATION_TOFAVORITE_SUCCESS = 101;
    private static final int TRANSLATION_START = 102;
    private static final int TRANSLATION_FINISH_FROMFAV = 103;
    private static final int MENU_IV_PADDING = 10;
    private static final int FAV_DEL_SUCCESSFULL = 122;
    private static final int IS_CHINESE = 23;
    private static final int START_HISTORIC_LOADING = 36;


    // variables
    MySpinner spinner_up_from, spinner_up_to;
    Context mContext;
    ImageView iv_toFav;
    ImageView iv_interchange;
    ImageView iv_rubbish;
    View action_translate;
    View sheet_1, sheet_2;
    EditText ed_fromtext;
    TextView /*tv_comments,*/ tv_totranslate, tv_pinyin_sheet_up;
    RelativeLayout lny_comments;
    // views from sheet_2
    View sheet2_top, mess_error, textviews, sheet2_searching, pinyin_sheet;
    CustomScrollview scrollview_dictfr;
    // variables
    Item item;
    int current_favorite_id = -1;
    boolean westart;
    // interfaces
    private OnFragmentInteractionListener mListener;
    private int TRUE = 1, FALSE = -1;

    private boolean translated = false;
    private Map<String, Integer> previous_item;
    // handler
    public android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            //
            if (previous_item == null)
                previous_item = new HashMap<>();
            switch (msg.what) {
                case TRANSLATION_ERROR:
                    // hide the keyboard before starting the animation
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideKeyboard();
                                    translated = true;
                                    sheet2_searching.setVisibility(View.GONE);
                                    pinyin_sheet.setVisibility(View.GONE);
                                    translateErrorAnimation(mContext);
                                }
                            });
                        }
                    }, 1000);

                    break;
                case TRANSLATION_START:
                    // change the txt to an progressbar.
                    textviews.setVisibility(View.GONE);
                    mess_error.setVisibility(View.GONE);
                    pinyin_sheet.setVisibility(View.GONE);
                    sheet2_searching.setVisibility(View.VISIBLE);
                    tv_pinyin_sheet_up.setVisibility(View.GONE);
                    // on progressing view that makes the background unusable.


                    break;
                case TRANSLATION_FINISH:
                    translated = true;
                    current_favorite_id = DbUtils.isFavorite(item, mContext);
                    if (sheet_2 != null) item = (Item) sheet_2.getTag();
                    if (!previous_item.containsKey(item.toString()) && current_favorite_id == -1) {
                        // if got data... then save it to historic.
                        if (item.retData.dict_result != null  || item.retData.trans_result != null) {
                            saveTranslateToHistoricDb(item);
                        }
                    }
                    final String text = ed_fromtext.getText().toString();
                    sheet2_searching.setVisibility(View.GONE);
                    translateSuccessFullAnimation(mContext, current_favorite_id > 0 ? true : false);
                    Item  p = new Item(item);
                    previous_item.put(p.toString(), current_favorite_id == -1 ? FALSE : TRUE);
                    current_favorite_id = -1;
                    new Thread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    String pinyin = HttpJsonToObj.getPinyinOfStr(text);
                                    if (pinyin.trim().equals(text.trim())) {
                                        pinyin_sheet.setTag(null);
                                    } else {
                                        if (text.trim() != "") {
                                            Message msg = Message.obtain();
                                            msg.what = IS_CHINESE;
                                            pinyin_sheet.setTag(R.string.pinyin_up, pinyin);
                                            mHandler.sendMessage(msg);
                                        }
                                    }
                                }
                            }
                    ).start();
                    break;
                case IS_CHINESE:
                    // top part
                    tv_pinyin_sheet_up.setText((String) pinyin_sheet.getTag(R.string.pinyin_up));
                    tv_pinyin_sheet_up.setVisibility(View.VISIBLE);
                    pinyin_sheet.setVisibility(View.VISIBLE);
                    // reinint the tags
                    pinyin_sheet.setTag(R.string.pinyin_up, null);
                    break;
                case TRANSLATION_FINISH_FROMFAV:
                    translated = true;
                    current_favorite_id = DbUtils.isFavorite(item, mContext);
                    sheet2_searching.setVisibility(View.GONE);
                    tv_totalcount.setVisibility(View.GONE);
                    translateSuccessFullAnimation(mContext, current_favorite_id > 0 ? true : false);
                    p = new Item(item);
                    previous_item.put(p.toString(), current_favorite_id == -1 ? FALSE : TRUE);
                    current_favorite_id = -1;
                    break;
                case TRANSLATION_TOFAVORITE_SUCCESS:
                    current_favorite_id = DbUtils.isFavorite(item, mContext);
//                    setToFav_animation (/* check into the db */ current_favorite_id > 0 ? true : false);
                    sheet2_searching.setVisibility(View.GONE);
                    translateSuccessFullAnimation(mContext, current_favorite_id > 0 ? true : false);
                    p = new Item(item);
                    previous_item.put(p.toString(), current_favorite_id == -1 ? FALSE : TRUE);
                    current_favorite_id = -1;
                    // we got to know that he is favorite, and beside, which are the datas that are
                    // included.
//                    ((NeedUpdateInterface) getActivity()).needUpdate(true);
                    break;
                case FAV_DEL_SUCCESSFULL:
                    sheet2ToBlueAnimation();
                    break;
            };
        }
    };
    private boolean historic_all_loaded = false;
    private TextView tv_totalcount;
    private LoadFromPositionThread current_thread;
    private TranslatingThread translating_thread;


    private void sheet2ToBlueAnimation() {

        AlphaAnimation anim = new AlphaAnimation(0, 1);
        anim.setDuration(500);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                iv_toFav.setImageResource(R.drawable.star_des);
                sheet_2.setBackgroundResource(R.color.sheetblue);
                lny_comments.setVisibility(View.GONE);
                ((MainActivity)getActivity()).needUpdate(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        sheet_2.startAnimation(anim);
    }


    private static final int JUMP = 20;
    private static final int STILL_LEFT = 21;
    private static final int END_OF_LIST = 22;
    // views
    HistoricLvAdapter adapter;
    List<Item> data = null;

    // variables
    int last_id_westopped = 0;


    private CustomListview lv_historic_main;
    private View hv_loading_sv;
    public android.os.Handler mHandler2 = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final List<Item> d = (List<Item>) msg.obj;
//            data = d;
            if (adapter == null || adapter.getCount() == 0) {
                adapter = new HistoricLvAdapter(mContext, R.layout.historic_lv_item, d);
                lv_historic_main.setAdapter(adapter);
            }
            if (data == null)
                data = new ArrayList<>();

            if (!data.equals(d))
                switch (msg.what) {
                    case END_OF_LIST:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1500);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (d != null && d.size() > 0) {
                                                data.addAll(d);
                                                if (adapter == null || adapter.getCount() == 0) {
                                                    data = new ArrayList<>();
                                                    adapter = new HistoricLvAdapter(mContext, R.layout.historic_lv_item, data);
                                                    lv_historic_main.setAdapter(adapter);
                                                }
                                                adapter.addAll(d);
                                            }
                                            hv_loading_sv.setVisibility(View.GONE);
                                            showBottomTextview ();
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        // bottom shows the total entries...
                        break;
                    case STILL_LEFT:
                        makeToast("getting still left data from db");
                        // do this after 1.5 seconds.
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1500);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (d != null && d.size() > 0) {
                                                data.addAll(d);
//                                                last_id_westopped += d.size();
                                            }
                                            if (adapter == null || adapter.getCount() == 0) {
                                                adapter = new HistoricLvAdapter(mContext, R.layout.historic_lv_item, data);
                                                lv_historic_main.setAdapter(adapter);
                                            }
                                            adapter.addAll(d);
                                            hv_loading_sv.setVisibility(View.GONE);
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        break;
                    case START_HISTORIC_LOADING:
                        // show loading bar
                        hv_loading_sv.setVisibility(View.VISIBLE);
                        break;
                }


            // height of lv + heigh of divider
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lv_historic_main.getLayoutParams();
            params.height = lv_historic_main.getMeasuredHeight();
//            Constants.makeLog("params height --- " + params.height);
            if (adapter != null)
                params.height += lv_historic_main.getDividerHeight() * adapter.getCount();
            lv_historic_main.setLayoutParams(params);
        }
    };

    public void showBottomTextview() {

        if (lv_historic_main.getVisibility() == View.VISIBLE) {
            hv_loading_sv.setVisibility(View.GONE);
            tv_totalcount.setText(getResources().getString(R.string.all) +" "+ data.size() + " "+ getResources().getString(R.string.loaded));
            if (data.size() >= 7)
                tv_totalcount.setVisibility(View.VISIBLE);
            else
                tv_totalcount.setVisibility(View.GONE);
        }
    }

    private TextView tv_translated_content;
    LinearLayout lny_maincontent;
    private int toggleTimes = 0;
    private List<Item> reserv;
    private View previous_view;

    private void saveTranslateToHistoricDb(Item item) {
        DbUtils.saveAsHistoric(mContext, item, "");
        // if the view is showing... add this item to the list.
        if (reserv == null)
            reserv = new ArrayList<>();
        reserv.add(0, item);
    }

    // when keyboard shown... start animations...


    // TODO: Rename and change types and number of parameters
    public static DictionnaryFragment newInstance() {
        DictionnaryFragment fragment = new DictionnaryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public DictionnaryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_dictionnary, null);
        mContext = rootview.getContext();
        if (previous_item == null)
            previous_item = new HashMap<>();
        Typeface font = ((MainActivity) mContext).getFont(0);
        final GestureDetector gestureDetector = new GestureDetector(((MainActivity) getActivity()), new SingleTapConfirm());

        // get the spinners and set up their values
        spinner_up_from = (MySpinner) rootview.findViewById(R.id.spinner_lang_from);
        spinner_up_to = (MySpinner) rootview.findViewById(R.id.spinner_lang_to);
        scrollview_dictfr = (CustomScrollview) rootview.findViewById(R.id.scrollview_dictfr);
        scrollview_dictfr.setScrollViewListener(this);
        hv_loading_sv = rootview.findViewById(R.id.hv_loading_sv);
        tv_totalcount = (TextView) rootview.findViewById(R.id.tv_totalcount);

        iv_toFav = (ImageView) rootview.findViewById(R.id.iv_toFav);
        action_translate = rootview.findViewById(R.id.rl_translate);
        sheet_1 = rootview.findViewById(R.id.sheet_1);
        sheet_2 = rootview.findViewById(R.id.sheet_2);
        ed_fromtext = (EditText) rootview.findViewById(R.id.ed_trans);
        pinyin_sheet = rootview.findViewById(R.id.rel_pinyin_sheet);
        tv_pinyin_sheet_up = (TextView) pinyin_sheet.findViewById(R.id.tv_pinyin_sheet_up);

        // from sheet2
        sheet2_top = sheet_2.findViewById(R.id.rl_sheet2_top);
        mess_error = sheet_2.findViewById(R.id.ll_sheet2_errormess);
        textviews = sheet_2.findViewById(R.id.ll_sheet2_textviews);
        sheet2_searching = sheet_2.findViewById(R.id.ll_sheet2_progressing);
        iv_interchange = (ImageView) rootview.findViewById(R.id.iv_exchange);
        iv_rubbish = (ImageView) rootview.findViewById(R.id.iv_rubbish);
        lny_comments = (RelativeLayout) sheet_2.findViewById(R.id.lny_comments);
//        tv_comments = (TextView) sheet_2.findViewById(R.id.tv_fav_notes);
        tv_translated_content = (TextView) sheet_2.findViewById(R.id.tv_translated);
        lny_maincontent = (LinearLayout) rootview.findViewById(R.id.lny_maincontent);
        tv_totranslate = (TextView) rootview.findViewById(R.id.tv_totranslate);
        updateCurrentSheet (item);

        // initially
        lny_comments.setVisibility(View.GONE);
        mess_error.setVisibility(View.GONE);
        sheet2_searching.setVisibility(View.GONE);
        textviews.setVisibility(View.VISIBLE);
        sheet2_top.setVisibility(View.VISIBLE);
        String[] langs = getResources().getStringArray(R.array.lang_from);
        Lang_From_To_SpinnerAdapter adap = new Lang_From_To_SpinnerAdapter(mContext, langs);

        spinner_up_from.setTag(Constants.Tag_SpL);
        spinner_up_to.setTag(Constants.Tag_SpR);
        spinner_up_from.setAdapter(adap);
        spinner_up_to.setAdapter(adap);

        // update L and R
        spinner_up_from.updateMe();
        spinner_up_to.updateMe();
//        spinner_up_from.setSelection(2); spinner_up_to.setSelection(3);

        iv_toFav.setOnClickListener(this);
        iv_interchange.setOnClickListener(this);
        lny_comments.setOnClickListener(this);
        iv_rubbish.setOnClickListener(this);
        // set fonts

        ed_fromtext.setTypeface(font);
//        tv_comments.setTypeface(font);
        tv_translated_content.setTypeface(font); tv_totranslate.setTypeface(font);

// add listener for edittext
        ed_fromtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_totranslate.setText(s.toString()+" ...");
            }
        });

        action_translate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                if (gestureDetector.onTouchEvent(motionEvent)) {

                    hideKeyboard();
                    if (ed_fromtext.getText().toString().trim() != "" && ed_fromtext.getText().toString().trim().length() != 0) {
                        translate(ed_fromtext.getText().toString().trim());
                        action_translate.setBackgroundResource(R.color.mywhite);
                    } else {
                        makeToast("request empty");
                        action_translate.setBackgroundResource(R.color.mywhite);
                    }
                    return true;
                } else {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                        action_translate.setBackgroundResource(R.color.pressed_blue);
                        return true;
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE || motionEvent.getAction() == MotionEvent.ACTION_UP
                            || motionEvent.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                        action_translate.setBackgroundResource(R.color.mywhite);
                    }
                    return false;
                }
            }
        });

        // historic list
        lv_historic_main = (CustomListview) rootview.findViewById(R.id.lv_historic_main);
        lv_historic_main.setExpanded(true);
        if (data == null) {
            data = new ArrayList<>();
        }
        adapter = new HistoricLvAdapter(mContext, R.layout.historic_lv_item, data);
//        lv_historic_main.setAdapter(adapter);
        if (!historic_all_loaded) {
            if (current_thread != null)
                current_thread.istop ();
            current_thread = new LoadFromPositionThread(last_id_westopped, last_id_westopped + JUMP);
            current_thread.start();
        }
        Constants.makeLog("historicallloaded " +historic_all_loaded);


        // sheet 1 listener
        sheet_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ed_fromtext.hasFocus())
                    ed_fromtext.requestFocus();
                else ed_fromtext.clearFocus();
            }
        });

        // historic listview listener
        lv_historic_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = ((HistoricLvAdapter.Holder) view.getTag()).inner_data;

                // get inner views

                // check if the item is favorite.
                if (sheet_2 != null)
                    sheet_2.setTag(item);
                if (ed_fromtext != null) {
                    if (item.retData.trans_result != null)
                        ed_fromtext.setText(UtilsFunctions.trimAll(item.retData.trans_result[0].src));
                    else
                        ed_fromtext.setText(UtilsFunctions.trimAll(item.retData.dict_result.word_name));
                }
                if (item != null)
                    mHandler.sendEmptyMessage(TRANSLATION_FINISH_FROMFAV);
            }
        });


        lv_historic_main.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {


                final ImageView iv = (ImageView) view.findViewById(R.id.iv_item_del_bg);
                final View foreground = view.findViewById(R.id.relative_foreground);
                final View background = view.findViewById(R.id.relative_background);
                foreground.clearAnimation();
                background.clearAnimation();
                if (previous_view != null)
                    previous_view.clearAnimation();
                if (foreground != previous_view) {
                    TranslateAnimation anim = new TranslateAnimation(0, -1 * iv.getWidth() - (MENU_IV_PADDING), 0, 0);
                    anim.setFillAfter(true);
                    anim.setDuration(200);

                    if (previous_view != null) {
                        previous_view.clearAnimation();
                        background.setVisibility(View.INVISIBLE);
                    }
                    previous_view = foreground;

                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            background.setVisibility(View.VISIBLE);
//                            Constants.makeLog("animatino start");
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
//                            Constants.makeLog("animatino end " + -iv.getWidth());
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    foreground.startAnimation(anim);
                } else {
                    previous_view.clearAnimation();
                }
                return true;
            }
        });
        return rootview;
    }




    private void toggleKeyboard() {
        Uri uri = Uri.parse(Constants.jobbing_authority+"/"+Constants.TOGGLE_KEYBOARD+"/3");
        ((MainActivity)  getActivity()).onFragmentInteraction(uri);
        toggleTimes++;
        if (toggleTimes % 2 == 0) {
            showKeyboardAnimation();
//            makeToast("Hidding keyboard");
        } else {
            hideKeyboardAnimation();
//            makeToast("Showing keyboard");
        }
    }

    private void showKeyboardAnimation() {

    }

    private void hideKeyboardAnimation() {

    }

    @Override
    public void showing_k() {
//        makeToast("showing keyboard");
        if (translated) {
            // show action_translate animation
            // hide sheet_2_animation
            lv_tohide_animation(false);
            sheet_2_to_showanimation(false);
            translate_bt_tohide_animation(false);
            translated = false;
            updateReserve ();
        }
    }

    private void updateReserve() {

        if (reserv != null)
            makeToast("updating reserve "+ reserv.size());
        if (reserv != null && reserv.size() > 0) {
//                data.addAll(0, reserv);
            adapter.addAllFront(reserv);
//                adapter.notifyDataSetChanged();
            reserv = new ArrayList<>();
        }
    }

    @Override
    public void hiding_k() {
//        makeToast("hiding keyboard");
    }

    @Override
    public void onScrollChanged(CustomScrollview scrollView, int x, int y, int oldx, int oldy) {

        View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
        int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

        // only make this stuffs twice a second

        if (diff == 0) {
            // do stuff
            if (!historic_all_loaded) {
                if (current_thread != null)
                    current_thread.istop ();
                current_thread = new LoadFromPositionThread(last_id_westopped, last_id_westopped + JUMP);
                current_thread.start();
            } else {
                showBottomTextview();
                makeToast("bottom of list");
            }
        }
    }

    public void clearPreviousTranslation() {
        if (previous_view != null)

            previous_view.clearAnimation();
    }

    public void deleteData(Item item) {

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) == item) {
                data.remove(i);
            }
        }
    }


    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // hide keyboard
        hideKeyboard();
    }



    private void updateCurrentSheet(Item item) {
        // according to if the value is favorite or not... set the params.
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
    public void onClick(View v) {

        if (v.equals(iv_toFav)) {

            Item item = (Item) sheet_2.getTag();
            if (previous_item == null) {
                previous_item = new HashMap<>();
            }
            if (previous_item.containsKey(item.toString()) && previous_item.get(item.toString()) == FALSE) {
                setToFavorite();
                previous_item.put(item.toString(), FALSE);
            }
            else if (previous_item.containsKey(item.toString()) && previous_item.get(item.toString()) == TRUE) {
                removeFromFav (item);
                previous_item.put(item.toString(), FALSE);
            } else{
//                makeToast("nothing to do");
            }
        }   else if (v.equals(iv_interchange)) {
            exchangeLanguages();
        } else if (v.equals(lny_comments)) {
            // open a dialog to enter some comments about the current explanation.
            openDialogForNotes();
        } else if (v.equals(iv_rubbish)) {
            ed_fromtext.setText("");
            showing_k();
        }
    }

    private void openDialogForNotes() {

        // open dialog for notes.
        Intent i = new Intent(mContext, SetupNoteActivity.class);
        // get the actual translated and the former content.
        i.putExtra(SetupNoteActivity.ITEM, (new Gson()).toJson((Item)sheet_2.getTag()));
        // then look in the db it equivalent then change it according to now.
        startActivityForResult(i, 1000);
    }

    // ROOT

    private void exchangeLanguages() {

        // exchange with animations.
        final int choice_from = spinner_up_from.getSelectedItemPosition(), choice_to = spinner_up_to.getSelectedItemPosition();
        Constants.makeLog("from "+choice_from+" == "+"to "+choice_to);
        Animation fade_out_anim_1 = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_out);
        Animation fade_out_anim_2 = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_out);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        fade_out_anim_1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                // on change les languages ici.
                spinner_up_from.setSelection(choice_to);
                Animation fade_in_anim_ = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_in);
                spinner_up_from.startAnimation(fade_in_anim_);
                fade_in_anim_.setFillAfter(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        fade_out_anim_2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                spinner_up_to.setSelection(choice_from);
                Animation fade_in_anim_ = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_in);
                spinner_up_to.startAnimation(fade_in_anim_);
                fade_in_anim_.setFillAfter(true);
            }

            @Override
                     public void onAnimationRepeat(Animation animation) {
            }
        });
        spinner_up_from.startAnimation(fade_out_anim_1);
        spinner_up_to.startAnimation(fade_out_anim_2);
        iv_interchange.startAnimation(rotateAnimation);
    }

    private void setToFavorite() {
        // set the current to favorite.
        final Item citem = (Item) sheet_2.getTag();
        if (item == citem) {
            makeToast("Can set to fav");
            new Thread() {
                @Override
                public void run() {
                    DbUtils.setFavorite(citem, mContext, "");
                    ((NeedUpdateInterface) getActivity()).needUpdate(true);
                    // when done, change the color to green.
                    mHandler.sendEmptyMessage(TRANSLATION_TOFAVORITE_SUCCESS);
                }
            }.start();
        }
    }

    private void removeFromFav (final Item item) {
        if (previous_item != null)
            previous_item.remove(item);
        // animation to remove...
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        DbUtils.delFromFavorite(item, mContext);
                        // fav delete successfull animation
                        mHandler.sendEmptyMessage(FAV_DEL_SUCCESSFULL);
                    }
                }
        ).start();
    }

    private void setToFav_animation(boolean isFavorite) {

        TranslateAnimation anim;
        if (isFavorite) {
            anim = (TranslateAnimation) AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_bottom);
            anim.setDuration(150);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    iv_toFav.setImageResource(R.drawable.star_des);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    iv_toFav.setImageResource(R.drawable.star_act);
                    sheet_2.setBackgroundResource(R.color.sheetgreen);
                    lny_comments.setVisibility(View.VISIBLE);
                    // rendre visible la zone d'ajouts de commentaires.
                    sheet_2_to_showanimation (true);
                    lv_tohide_animation (true);
                    translate_bt_tohide_animation (true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {
            anim = (TranslateAnimation) AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_top);
            anim.setDuration(150);
            iv_toFav.startAnimation(anim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    iv_toFav.setImageResource(R.drawable.star_act);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    iv_toFav.setImageResource(R.drawable.star_des);
                    action_translate.setBackgroundResource(R.color.pressed_blue);
                    // remove from db
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        iv_toFav.startAnimation(anim);
    }


    private void makeToast(String favorite) {
//        Toast.makeText(mContext, favorite, Toast.LENGTH_SHORT).show();
        Constants.makeLog(favorite);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    // functions
    private void translate(final String text) {

        if (translating_thread != null)
            translating_thread.istop ();
        translating_thread = new TranslatingThread(text);
        translating_thread.start();
    }


    private void hideKeyboard() {
        // Check if no view has focus:
        View view =  ((MainActivity) getActivity()).findViewById(R.id.sheet_1);
        if (view != null) {
            if (hv_loading_sv != null)
                hv_loading_sv.setVisibility(View.GONE);
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private void translateSuccessFullAnimation(Context mContext, final boolean favorite) {


        // set up the background of the view.
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_bottom);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd (Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                // get the textview
                // change the background to blue.

                Constants.makeLog("Isfavorite is "+favorite);

                if (favorite) {
                    sheet_2.setBackgroundResource(R.color.sheetgreen);
                    iv_toFav.setImageResource(R.drawable.star_act);
                    lny_comments.setVisibility(View.VISIBLE);
                }
                else {
                    sheet_2.setBackgroundResource(R.color.sheetblue);
                    iv_toFav.setImageResource(R.drawable.star_des);
                    lny_comments.setVisibility(View.GONE);
                    // lv to hide animation
                    // translate button to hide animation
                }
                sheet_2_to_showanimation (true);
                lv_tohide_animation (true);
                tv_totalcount.setVisibility(View.GONE);
                translate_bt_tohide_animation (true);
                hv_loading_sv.setVisibility(View.GONE);

                if (tv_translated_content!= null && item != null) {
                    textviews.setVisibility(View.VISIBLE);
                    mess_error.setVisibility(View.GONE);
                    sheet2_top.setVisibility(View.VISIBLE);
//                    Constants.makeLog(item.retData.trans_result[0].dst);

                    if (item.retData.dict_result != null) {
                        handleDictResult ();
                    } else if (item.retData.trans_result != null) {

                        handleTranslateResult ();
                    } else {
                        handleNoResultFound ();
                    }

                } else {
                    makeToast("Cant set up translate content");
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        sheet_2.clearAnimation();
        sheet_2.startAnimation(anim);
    }

    private void sheet_2_to_showanimation(boolean b) {

        b = !b;
        if (b) {
            makeToast("hidding sheet2");
            sheet_2.setVisibility(View.GONE);
            pinyin_sheet.setVisibility(View.GONE);
        } else {
            makeToast("showing sheet2");
            sheet_2.setVisibility(View.VISIBLE);
        }
    }

    private void translate_bt_tohide_animation(boolean b) {
        Animation anim = null;
        if (b) {
            action_translate.setVisibility(View.GONE);
        } else {
            // show
            action_translate.setVisibility(View.VISIBLE);
        }
    }

    private void lv_tohide_animation(boolean b) {
//        Animation anim = null;
        if (b) {
            // hide
            lv_historic_main.setVisibility(View.GONE);
        } else {
            // show
            lv_historic_main.setVisibility(View.VISIBLE);
            hv_loading_sv.setVisibility(View.GONE);
        }
    }

    private void handleNoResultFound() {
        tv_translated_content.setText("---");
    }

    private void handleTranslateResult() {
        if (item.retData.trans_result != null) {
            String str = ((RetData.LittleElem) ((item.retData.trans_result)[0])).dst;
            tv_translated_content.setText(str);
        }
    }



    private void handleDictResult() {

        // we have the official item and the views apparently...
        if (item != null) {

            DictResult dictResult = item.retData.dict_result;
            String str = Constants.convertDictResultToString (dictResult);
            tv_translated_content.setText(Html.fromHtml(str));
        }
    }

    private void translateErrorAnimation (Context mContext) {
        // set up the background of the view.
        Animation anim = null;
        anim = AnimationUtils.loadAnimation(mContext, R.anim.myerror_translation);
        sheet_2.clearAnimation();
        sheet_2.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd (Animation animation) {
                makeToast("Error");
            }
            @Override
            public void onAnimationStart(Animation animation) {
                // get the textview
                // change the background to red...
                sheet_2.setBackgroundResource(R.color.sheetred);
                // get views.
                textviews.setVisibility(View.GONE);
                lny_comments.setVisibility(View.GONE);
                mess_error.setVisibility(View.VISIBLE);
                sheet2_top.setVisibility(View.INVISIBLE);

                sheet_2_to_showanimation (true);
                lv_tohide_animation (true);
                translate_bt_tohide_animation (true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private class TranslatingThread extends Thread implements Runnable {

        private String text;
        private HttpJsonToObj httpjsontobj;

        public TranslatingThread (String t) {
            text = t;
            httpjsontobj = new HttpJsonToObj();
        }


        public void istop () {
            httpjsontobj = null;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(getActivity(), "Cancel previous research", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void run() {
            try {
                mHandler.sendEmptyMessage(TRANSLATION_START);
                // if it's a word then check search as a word...
                item = httpjsontobj.fromReqtoObj(getSpinner_lang(1), getSpinner_lang(2), text);
                // check if the original text is equal to the pinyin text,,, if yes... then ok
                // if not then show the other one.
                Message msg = Message.obtain();
                // check if the item is favorite.
                if (sheet_2 != null)
                    sheet_2.setTag(item);
                msg.what = TRANSLATION_FINISH;
                if (item != null)
                    mHandler.sendMessage(msg);
                // save the req as historic.
            } catch (Exception e) {
                e.printStackTrace();
                // send the reaction after 1000 second
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(TRANSLATION_ERROR);
                    }
                }, 1000);
            }
        }

        private String getSpinner_lang(int i) {

            String str = "";
            if (i == 1) {
                str =  spinner_up_from.getSelectedView().getTag().toString();
            } else if (i == 2) {
                str =  spinner_up_to.getSelectedView().getTag().toString();
            }
            return str;
        }

    }

    // class
    private class LoadFromPositionThread extends Thread implements Runnable {

        private int start, to;
        private DbUtils dbutils;

        public LoadFromPositionThread (int s, int t) {
            start = s;
            to = t;
            dbutils = new DbUtils();
        }


        public void istop () {
            dbutils = null;
        }

        @Override
        public void run() {

            // send loading start signal
            mHandler2.sendEmptyMessage(START_HISTORIC_LOADING);
            // load
            Message msg = Message.obtain();
            msg.what = STILL_LEFT;
            List<Item> item = dbutils.getTranslateItemFrom_To (mContext, start, to);

            if (item != null) {
                msg.obj = item;
                last_id_westopped += item.size();
                if (item.size() != (to - start)) {
                    msg.what = END_OF_LIST;
                    historic_all_loaded = true;
                }
            } else {
                // we may got to the end of the stuff
                msg.what = END_OF_LIST;
            }
            // send an update data signal
            mHandler2.sendMessage(msg);
        }
    }

    private void hideActionbar () {

        Uri uri = Uri.parse(Constants.jobbing_authority+"/"+Constants.URI_HIDE_ACTIONBAR+"/3");
        ((MainActivity)  getActivity()).onFragmentInteraction(uri);
    }

    private void showActionbar () {

        Uri uri = Uri.parse(Constants.jobbing_authority+"/"+Constants.URI_SHOW_ACTIONBAR+"/3");
        ((MainActivity)  getActivity()).onFragmentInteraction(uri);
    }

}
