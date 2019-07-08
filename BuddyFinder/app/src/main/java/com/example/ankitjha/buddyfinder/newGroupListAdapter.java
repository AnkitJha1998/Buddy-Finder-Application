package com.example.ankitjha.buddyfinder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class newGroupListAdapter extends ArrayAdapter<String> {

    private ArrayList<String> name;
    private ArrayList<Integer> choose;
    private Activity context;

    public newGroupListAdapter(Activity context,ArrayList<String> name, ArrayList<Integer> choose)
    {
        super(context,R.layout.list_view_new_group,name);
        this.context=context;
        this.name=name;
        this.choose=choose;
    }

    public View getView(int position,View view,ViewGroup parent)
    {

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_view_new_group,null,true);

        ImageView tick=rowView.findViewById(R.id.chk_image);
        TextView list=rowView.findViewById(R.id.friend_list);

        if(choose.get(position)==1)
            tick.setVisibility(View.VISIBLE);
        else
            tick.setVisibility(View.INVISIBLE);

        list.setText(name.get(position));
        return rowView;
    }



}
