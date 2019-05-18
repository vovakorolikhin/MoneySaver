package com.moneysaver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends BaseAdapter {
    private ArrayList<Expense> list;

    public Adapter(ArrayList<Expense> list){
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item, parent, false);
        TextView vName = view.findViewById(R.id.item_name);
        TextView vCost = view.findViewById(R.id.item_cost);
        Expense item = (Expense)getItem(position);
        vName.setText(item.getName());
        vCost.setText(Double.toString(item.getCost()));
        return view;
    }


}