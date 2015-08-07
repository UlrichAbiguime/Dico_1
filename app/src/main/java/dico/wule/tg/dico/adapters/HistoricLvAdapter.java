package dico.wule.tg.dico.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dico.wule.tg.dico.R;
import dico.wule.tg.dico.activities.MainActivity;
import dico.wule.tg.dico.db.localdb.DbUtils;
import dico.wule.tg.dico.entities.Item;
import dico.wule.tg.dico.fragments.DictionnaryFragment;
import dico.wule.tg.dico.utils.Constants;
import dico.wule.tg.dico.utils.UtilsFunctions;

/**
 * Created by lakiu_000 on 3/28/2015.
 */
public class HistoricLvAdapter extends ArrayAdapter {

    Context mContext;
    List<Item> data;
    private LayoutInflater inflater;
    Typeface font;
    boolean add_with_animation = false;

    public HistoricLvAdapter(Context context, int resource, List<Item> d) {
        super(context, resource);
        mContext = context;
        inflater = LayoutInflater.from(context);
        data = d;
        if (data == null)
            data = new ArrayList<>();
        font = ((MainActivity) context).getFont(0);
        add_with_animation = false;
    }

    @Override
    public void addAll(Collection collection) {
//        super.addAll(collection);
        List<Item> d = (List<Item>) collection;
        if (d!= null && !d.equals(data)) {
            data.addAll((List<Item>) collection);
            notifyDataSetChanged();
            add_with_animation = true;
        }
    }

    public void addAllFront(Collection collection) {
//        super.addAll(collection);
        List<Item> d = (List<Item>) collection;
        if (d!= null && !d.equals(data)) {
            data.addAll(0, (List<Item>) collection);
            notifyDataSetChanged();
            add_with_animation = true;
        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view;
        // inflate view.
        if (convertView == null)
            view = inflater.inflate(R.layout.historic_lv_item, null);
        else
            view = convertView;
        // i need the jsonObj
        makeStandard (view);
        final Item item = (Item) getItem(position);
        ((TextView) view.findViewById(R.id.tv_historic_item_from)).setText(item.retData.from);
        ((TextView) view.findViewById(R.id.tv_historic_item_to)).setText(item.retData.to);

        ((TextView) view.findViewById(R.id.tv_historic_item_from)).setTypeface(font);
        ((TextView) view.findViewById(R.id.tv_historic_item_to)).setTypeface(font);

        String s = "";
        Holder holder = new Holder();
        holder.inner_data = (Item) getItem(position);

        // views
        final ImageView iv = (ImageView) view.findViewById(R.id.iv_item_del_bg);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mContext).makeToast("deleting");
                // we delete inside the dictionnary fragment...
                data.remove(position);
                ((MainActivity) mContext).deleteHistoricfromDb(item);
                notifyDataSetChanged();
            }
        });

        if (item.retData.trans_result != null) {
            s = item.retData.trans_result[0].src;
            s = UtilsFunctions.trimAll(s);
            ((TextView) view.findViewById(R.id.tv_historic_item_origin)).setText(s);
            ((TextView) view.findViewById(R.id.tv_historic_item_origin)).setTypeface(font);
            if (add_with_animation)
                holder.startAnimation(view);
            view.setTag(holder);
        }
        else if (item.retData.dict_result != null) {
            s = item.retData.dict_result.word_name;
            s = UtilsFunctions.trimAll(s);
            ((TextView) view.findViewById(R.id.tv_historic_item_origin)).setText(s);
            ((TextView) view.findViewById(R.id.tv_historic_item_origin)).setTypeface(font);
            if (add_with_animation)
                holder.startAnimation(view);
            view.setTag(holder);
        }
        return view;
    }

    private void makeStandard(View view) {

        View foreground = view.findViewById(R.id.relative_foreground);
        foreground.clearAnimation();
    }

    public class Holder {

        private boolean isAnimationFinished = false;
        public Item inner_data = null;

        public void Holder () {
            isAnimationFinished = false;
        }

        boolean isAnimationFinished () {
            return isAnimationFinished;
        }

        public void startAnimation (View view) {
            AlphaAnimation anim = new AlphaAnimation(0f, 1f);
            anim.setDuration(500);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isAnimationFinished = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isAnimationFinished = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            view.startAnimation(anim);
        }
    }

}
