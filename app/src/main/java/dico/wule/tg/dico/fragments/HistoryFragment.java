package dico.wule.tg.dico.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import dico.wule.tg.dico.R;
import dico.wule.tg.dico.adapters.HistoricLvAdapter;
import dico.wule.tg.dico.db.localdb.DbUtils;
import dico.wule.tg.dico.entities.Item;


public class HistoryFragment extends Fragment {

    private static final int JUMP = 20;
    private static final int STILL_LEFT = 21;
    private static final int END_OF_LIST = 22;
    // views
    ListView lv_historic_main;
    HistoricLvAdapter adapter;
    List<Item> data = null;

    // variables
    int last_id_westopped = 0;

    private OnFragmentInteractionListener mListener;
    private Context mContext;

    public static HistoryFragment newInstance( ) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public android.os.Handler mHandler = new android.os.Handler () {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<Item> d = (List<Item>) msg.obj;
            if (adapter == null) {
                adapter = new HistoricLvAdapter(mContext, R.layout.historic_lv_item, d);
                lv_historic_main.setAdapter(adapter);
            }
            data = d;
//            Constants.makeLog("Message treatment - "+d.toString());
            switch (msg.what) {
                case END_OF_LIST :
                    // recuperer les donnees et les appends a la listview...
                    adapter = new HistoricLvAdapter(mContext, R.layout.historic_lv_item, data);
                    lv_historic_main.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
                case STILL_LEFT:
                    adapter = new HistoricLvAdapter(mContext, R.layout.historic_lv_item, data);
                    lv_historic_main.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_history, null);
        mContext = rootview.getContext();

        lv_historic_main = (ListView) rootview.findViewById(R.id.lv_historic_main);
        if (data == null) {
            data = new ArrayList<>();
        }
        adapter = new HistoricLvAdapter(mContext, R.layout.historic_lv_item, data);
        lv_historic_main.setAdapter(adapter);
        // get the lastest 20 // historics...
        // subdiviser ca par rapport aux jours ou les recherches on ete faites.
        // today yesterday few days from now.
        LoadFromPositionThread th = new LoadFromPositionThread(last_id_westopped, last_id_westopped+JUMP);
        th.run();
        // du genre on a bcp de tables et par rapport a ces tables la...
        // on charge les jours precedents et consors...
        //  donc ce que je risk de faire ce sera des expandables views ouvertes par defaut... ??? /// nope.
        return rootview;
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    // class
    private class LoadFromPositionThread implements Runnable{

        private int start, to;

        public LoadFromPositionThread (int s, int t) {
            start = s;
            to = t;
        }

        @Override
        public void run() {
            // load
            Message msg = Message.obtain();
            List<Item> item = DbUtils.getTranslateItemFrom_To (mContext, start, to);
            if (item != null) {
                msg.obj = item;
                last_id_westopped += item.size();
                if (item.size() != (to - start - 1)/*size diff from the expected then we're done.*/) {
                    // we got to the bottom...
                    msg.what = END_OF_LIST;
                } else {
                    msg.what = STILL_LEFT;
                }
            } else {
                // we may got to the end of the stuff
                msg.what = END_OF_LIST;
            }
            mHandler.sendMessage(msg);
            // send message to handler...
            // this function will be lunched again each time we get down the list,
            // to add elements from the previous stopped position + ...

            // when a updating broadcast will be sent...

            // the final position of loading will still be the same, but still
            // it will only add the newest ones.
        }
    }


    // keep an eye on the fragment...
    // each time a new search is done, broadcast is sent from the 0_fragment to historic one.

    // which one sends message to handler who does the stuff.

}
