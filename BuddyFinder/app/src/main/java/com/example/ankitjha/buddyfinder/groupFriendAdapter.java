package com.example.ankitjha.buddyfinder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class groupFriendAdapter extends ArrayAdapter<String> {

    private Activity context;
    private ArrayList<String> friendNameList;
    private ArrayList<Integer> locSharingOptionList;

    public groupFriendAdapter(Activity context,ArrayList<String> friendNameList,ArrayList<Integer> locSharingOptionList)
    {
        super(context,R.layout.grp_friend_list_view,friendNameList);
        this.context=context;
        this.friendNameList=friendNameList;
        this.locSharingOptionList=locSharingOptionList;
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.grp_friend_list_view,null,true);

        TextView friendName=rowView.findViewById(R.id.frnd_name);
        TextView friendWish=rowView.findViewById(R.id.his_avail);

        friendName.setText(friendNameList.get(position));
        if(locSharingOptionList.get(position)==-1)
            friendWish.setText("Currently Not Sharing His Location");
        else
            friendWish.setText("Sharing their Location");

        return rowView;
    }


}
