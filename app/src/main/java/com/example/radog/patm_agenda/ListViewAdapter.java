package com.example.radog.patm_agenda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by radog on 03/04/2017.
 */

public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<itemEvent> arrayListItem;
    private LayoutInflater layoutInflater;

    public ListViewAdapter(Context context, ArrayList<itemEvent> arrayListItem) {
        this.context = context;
        this.arrayListItem = arrayListItem;
    }

    @Override
    public int getCount() {
        return arrayListItem.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayListItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewItem = layoutInflater.inflate(R.layout.layout_item, parent, false);

        ImageView ivPicture = (ImageView) viewItem.findViewById(R.id.ivPicture);
        TextView tvNameE = (TextView) viewItem.findViewById(R.id.tvNameE);
        TextView tvDescE = (TextView) viewItem.findViewById(R.id.tvDescE);
        TextView tvDateE = (TextView) viewItem.findViewById(R.id.tvDateE);

        ivPicture.setImageResource(arrayListItem.get(position).getPicture());
        tvNameE.setText(arrayListItem.get(position).getNameE());
        tvDateE.setText(arrayListItem.get(position).getDateE());
        tvDescE.setText(arrayListItem.get(position).getDescE());

        return viewItem;
    }
}
