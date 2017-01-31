package com.camt.th.posowner.Component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.camt.th.posowner.Model.POS;
import com.camt.th.posowner.R;

import java.util.List;

/**
 * Created by W.J on 1/23/2016.
 */
public class POSAdapter extends BaseAdapter{
    Context mContext;
    List<POS> pos;

    public POSAdapter(Context context, List<POS> pos) {
        this.mContext = context;
        this.pos = pos;
    }

    public int getCount() {
        return pos.size();
    }

    public POS getItem(int position) {
        return pos.get(position);
    }

    public long getItemId(int position) {
        return pos.get(position).getId();
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = mInflater.inflate(R.layout.pos_list, parent, false);
        TextView posName = (TextView) view.findViewById(R.id.posName);
        posName.setText(pos.get(position).getName());
        return view;
    }
}
