package dico.wule.tg.dico.activities;

import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import dico.wule.tg.dico.R;

public class ReadOnlyNoteActivity extends ActionBarActivity {

    public static final String NOTE = "innernote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_only_note);
        ActionBar actionBar = getSupportActionBar();
       /* actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.mytransparent));
        actionBar.setTitle("");*/
        actionBar.hide();
        // get the string and show it.
        String s = getIntent().getStringExtra(NOTE);
        TextView tv = (TextView) findViewById(R.id.tv_main);
        Typeface rel_regular = Typeface.createFromAsset(getAssets(), "fonts/" + MainActivity.REGULAR_FONT);
        tv.setTypeface(rel_regular);
        tv.setText(s);

//        makeToast(s);
    }

    private void makeToast(String s) {
//        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }
}
