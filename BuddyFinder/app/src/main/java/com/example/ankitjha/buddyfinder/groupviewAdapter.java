package com.example.ankitjha.buddyfinder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class groupviewAdapter extends ArrayAdapter<String> {

    private Activity context;
    private ArrayList<String> gnames;
    private ArrayList<Integer> gIds;

    public groupviewAdapter(Activity context,ArrayList<String> gnames,ArrayList<Integer> gIds)
    {
        super(context,R.layout.list_view_group_display,gnames);
        this.context=context;
        this.gnames=gnames;
        this.gIds=gIds;
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inf=context.getLayoutInflater();
        View rowView=inf.inflate(R.layout.list_view_group_display,null,true);

        TextView gidV=rowView.findViewById(R.id.group_id);
        TextView gnameV=rowView.findViewById(R.id.group_name);

        gidV.setText("Group ID: "+String.valueOf(gIds.get(position)));
        gnameV.setText(gnames.get(position));

        return rowView;
    }

}
