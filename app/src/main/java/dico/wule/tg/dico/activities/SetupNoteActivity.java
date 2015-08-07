package dico.wule.tg.dico.activities;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import dico.wule.tg.dico.R;
import dico.wule.tg.dico.db.localdb.DbUtils;
import dico.wule.tg.dico.db.localdb.MyOpenDbHelper;
import dico.wule.tg.dico.entities.DictResult;
import dico.wule.tg.dico.entities.Item;
import dico.wule.tg.dico.entities.RetData;
import dico.wule.tg.dico.fragments.DictionnaryFragment;
import dico.wule.tg.dico.utils.Constants;

public class SetupNoteActivity extends ActionBarActivity {


    private static final int GOT_MESSAGE = 21;
    public static final String NEWREMARK = "beauxyeux";
    public static final String ITEM = "myitem";
    public android.os.Handler mHandler = new android.os.Handler () {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // handling message.
            switch (msg.what) {
                case GOT_MESSAGE:
                    String remark = (String) msg.obj;
                    ed_notebody.setText(remark);
                    entering_val = remark;
                    break;
            }
        }
    };

    // views
    TextView tv_title;
    EditText ed_notebody;
    Button bt_valid;
    LinearLayout sheet_2;
    // variables
    int id;
    String title;
    Item item;
    String entering_val = "";

    private TextView tv_translated_content;
    private View sheet2_searching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_note);
        getSupportActionBar().hide();
        // get views
     /*   id = getIntent().getExtras().getInt(MyOpenDbHelper.F_ID, -1);
        title = getIntent().getExtras().getString(MyOpenDbHelper.F_ITEM);*/
        final String s_item = getIntent().getStringExtra(ITEM);
        item = (new Gson()).fromJson(s_item, Item.class);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.lny_comments);
        sheet_2 = (LinearLayout) findViewById(R.id.sheet_2);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.rl_sheet2_top);
        tv_translated_content = (TextView) sheet_2.findViewById(R.id.tv_translated);
        sheet2_searching = sheet_2.findViewById(R.id.ll_sheet2_progressing);
        ed_notebody = (EditText) findViewById(R.id.ed_notebody);
// setting up
        viewGroup.setVisibility(View.GONE);
        sheet2_searching.setVisibility(View.GONE);
        rl.setVisibility(View.GONE);
        tv_translated_content.setVisibility(View.VISIBLE);
        tv_translated_content.setText("Ulrich");
        sheet_2.setVisibility(View.VISIBLE);
        sheet_2.setBackgroundResource(R.color.sheetgreen);



        new Thread(){
            @Override
            public void run() {
                super.run();
                // check in the db if any info
                String s = DbUtils.getRemark(SetupNoteActivity.this, s_item);
//                Constants.makeLog(s.toString());
                Message msg = Message.obtain();
                msg.what = GOT_MESSAGE;
                msg.obj = s;
                mHandler.sendMessage(msg);
            }
        }.start();

    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        TranslateAnimation anim;
        anim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.abc_slide_in_top);
        anim.setDuration(400);
        anim.setStartOffset(100);
        anim.setFillAfter(true);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (item.retData.dict_result != null) {
                    handleDictResult ();
                } else if (item.retData.trans_result != null) {
                    handleTranslateResult ();
                } else {
                    handleNoResultFound ();
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        sheet_2.startAnimation(anim);
//        makeToast("post create");
    }

    private void handleNoResultFound() {
        tv_translated_content.setText("---");
    }

    private void handleTranslateResult() {
        if (item.retData.trans_result != null)
            tv_translated_content.setText((CharSequence) ((RetData.LittleElem)((item.retData.trans_result)[0])).dst);
    }

    private void handleDictResult() {

        // we have the official item and the views apparently...
        if (item != null) {
            DictResult dictResult = item.retData.dict_result;
            String str = Constants.convertDictResultToString(dictResult);
            tv_translated_content.setText(Html.fromHtml(str));
        }
    }


    public void saveFavNote (View view) {

        String string = ed_notebody.getText().toString();
        DbUtils.saveAsRemark(this, string, (new Gson()).toJson(item));
        finish();
    }


    public void cancelNote (View view) {

        finish();
    }

    @Override
    public void finish() {
        Intent i = new Intent();
        if (ed_notebody.getText().toString().equals(entering_val))
            i.putExtra(NEWREMARK, false);
        else
            i.putExtra(NEWREMARK, true);
        setResult(RESULT_OK, i);
        super.finish();
    }

    private void makeToast(String favorite) {
        Toast.makeText(this, favorite, Toast.LENGTH_SHORT).show();
    }

}
