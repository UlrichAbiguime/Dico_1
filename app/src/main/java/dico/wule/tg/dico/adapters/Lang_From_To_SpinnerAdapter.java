package dico.wule.tg.dico.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import dico.wule.tg.dico.R;
import dico.wule.tg.dico.activities.MainActivity;
import dico.wule.tg.dico.utils.Constants;

/**
 * Created by lakiu_000 on 3/26/2015.
 */
public class Lang_From_To_SpinnerAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    String[] data;

    public Lang_From_To_SpinnerAdapter (Context c, String[] data) {

        mContext = c;
        inflater = LayoutInflater.from(mContext);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // layout with flags.
        View v = inflater.inflate(R.layout.spinner_lang_layout, null);
        TextView tv = (TextView) v.findViewById(R.id.tv_langname);
        tv.setText((String) getItem(position));
        tv.setTypeface(((MainActivity) mContext).getFont(9));
//        ((ImageView) v.findViewById(R.id.iv_lang_flag)).setImageResource(Constants.flag_icon[position]);

        switch (getItem(position).toString()) {
            case Constants.EN:
                v.setTag("en");
                break;
            case Constants.FR:
                v.setTag("fra");
                break;
            case Constants.ZH:
                v.setTag("zh");
                break;
            case Constants.ES:
                v.setTag("spa");
                break;
            case Constants.PT:
                v.setTag("pt");
                break;
            case Constants.RS:
                v.setTag("ru");
                break;
            default:
                v.setTag("auto");
                break;
        };
        return v;
    }
}
