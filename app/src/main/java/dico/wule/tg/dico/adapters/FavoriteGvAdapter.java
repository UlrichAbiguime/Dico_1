package dico.wule.tg.dico.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

import dico.wule.tg.dico.R;
import dico.wule.tg.dico.entities.FavoriteSave;

/**
 * Created by lakiu_000 on 4/11/2015.
 */
public class FavoriteGvAdapter extends BaseAdapter {


    List<FavoriteSave> data;
    Context mContext;


    public FavoriteGvAdapter(Context c, List<FavoriteSave> sv) {

        mContext = c;
        data = sv;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // inflate views
        View view = null;
        ViewHolder vh = null;
        if (convertView != null) {
            view = convertView;
            vh = (ViewHolder) convertView.getTag();
        }
        else {
            view = LayoutInflater.from(mContext).inflate(R.layout.fav_item, null);
            vh = new ViewHolder();
        }

        vh.title = (TextView) view.findViewById(R.id.tv_title);
        vh.body = (TextView) view.findViewById(R.id.tv_body);

        FavoriteSave item = (FavoriteSave) getItem(position);

        vh.title.setText(item.title);
        vh.body.setText(item.body);

        // give margin to the items

        view.setTag(vh);
        return view;
    }

    private class ViewHolder  {
        public TextView title;
        public TextView body;
    }

}
