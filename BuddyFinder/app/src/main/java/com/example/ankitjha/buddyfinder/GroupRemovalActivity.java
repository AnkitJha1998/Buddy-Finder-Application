package com.example.ankitjha.buddyfinder;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GroupRemovalActivity extends AppCompatActivity {

    Users userSinedIn;
    Groups groupSelected;
    int intentOfAct;
    ActionBar bar;
    ListView friendListview;
    ArrayList<String> friendName;
    ArrayAdapter<String> adp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_removal);

        Intent recvIntent=getIntent();
        int uid=recvIntent.getIntExtra("userID",-1);
        int gid=recvIntent.getIntExtra("groupID",-1);
        intentOfAct=recvIntent.getIntExtra("intent",2);

        bar=getSupportActionBar();

        friendName=new ArrayList<String>();

        if(intentOfAct==0)
        {
            bar.setTitle("Select Friend to Remove");
        }
        else if(intentOfAct==1)
        {
            bar.setTitle("Assign New Admin");
        }

        for(Users u:LogInActivity.userList)
            if(u.getUid()==uid)
            {
                userSinedIn=u;
                break;
            }
        for(Groups g:MainActivity.groupList)
            if(g.getGid()==gid)
            {
                groupSelected=g;
                break;
            }
        for(int uuid:groupSelected.getFriendList())
        {
            for(Users u:LogInActivity.userList)
            {
                if(u.getUid()==uuid)
                {
                    friendName.add(u.getUname());
                    break;
                }
            }
        }
        friendListview=findViewById(R.id.friendListView);
        adp=new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,friendName);
        friendListview.setAdapter(adp);
        friendListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(intentOfAct==0)
                {

                    if(groupSelected.getFriendList().get(position)==userSinedIn.getUid())
                    {
                        Toast.makeText(getApplicationContext(),"You can't remove yourself",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ConnectivityManager connectivitymanager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    boolean stat=connectivitymanager.getActiveNetworkInfo().isConnected();
                    if(stat==false)
                    {
                        Toast.makeText(getApplicationContext(),"You are not connected to a Server",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Users toBeDeleted=new Users();
                    int utbd=groupSelected.getFriendList().get(position);
                    for(Users u:LogInActivity.userList)
                        if(u.getUid()==utbd)
                        {
                            toBeDeleted=u;
                            break;
                        }
                    ArrayList<Integer> frndList=new ArrayList<>();
                    frndList.clear();
                    for(int i=0;i<groupSelected.getFriendList().size();i++)
                    {
                        if(groupSelected.getFriendList().get(i)==utbd)
                        {
                            continue;
                        }
                        else
                            frndList.add(groupSelected.getFriendList().get(i));
                    }
                    groupSelected.friendList=frndList;
                    ArrayList<Integer> grpLis=new ArrayList<>();
                    grpLis.clear();
                    for(int i=0;i<toBeDeleted.getGroupList().size();i++)
                    {
                        if(toBeDeleted.getGroupList().get(i)==groupSelected.getGid())
                        {
                            continue;
                        }
                        else
                            grpLis.add(toBeDeleted.getGroupList().get(i));

                    }
                    toBeDeleted.groupList=grpLis;



                    DatabaseReference groupRef=FirebaseDatabase.getInstance().getReference("Groups");
                    DatabaseReference userRef=FirebaseDatabase.getInstance().getReference("Users");

                    groupRef.child(String.valueOf(groupSelected.getGid())).setValue(groupSelected);
                    userRef.child(String.valueOf(toBeDeleted.getUid())).setValue(toBeDeleted);

                    Toast.makeText(getApplicationContext(),"User Removed Successfully",Toast.LENGTH_SHORT).show();
                    finish();

                }
                else if(intentOfAct==1)
                {
                    if(groupSelected.getFriendList().get(position)==userSinedIn.getUid())
                    {
                        Toast.makeText(GroupRemovalActivity.this, "You can't select yourself as Admin", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    {
                        ConnectivityManager connectivitymanager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                        boolean stat=connectivitymanager.getActiveNetworkInfo().isConnected();
                        if(stat==false)
                        {
                            Toast.makeText(getApplicationContext(),"You are not connected to a Server",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        groupSelected.admin_user=groupSelected.getFriendList().get(position);
                        ArrayList<Integer> gpList=new ArrayList<>();
                        gpList.clear();
                        for(int i=0;i<userSinedIn.groupList.size();i++)
                        {
                            if(userSinedIn.groupList.get(i)==groupSelected.getGid())
                            {
                                continue;
                            }
                            else
                                gpList.add(userSinedIn.groupList.get(i));

                        }
                        userSinedIn.groupList=gpList;
                        ArrayList<Integer> frList=new ArrayList<>();
                        for(int i=0;i<groupSelected.friendList.size();i++)
                        {
                            if(groupSelected.friendList.get(i)==userSinedIn.getUid())
                            {
                                continue;
                            }
                            else
                                frList.add(groupSelected.friendList.get(i));
                        }
                        groupSelected.friendList=frList;
                        DatabaseReference groupRef=FirebaseDatabase.getInstance().getReference("Groups");
                        DatabaseReference userRef=FirebaseDatabase.getInstance().getReference("Users");

                        groupRef.child(String.valueOf(groupSelected.getGid())).setValue(groupSelected);
                        userRef.child(String.valueOf(userSinedIn.getUid())).setValue(userSinedIn);

                        Toast.makeText(getApplicationContext(),"Removed Successfully",Toast.LENGTH_SHORT).show();




                    }
                    //Toast.makeText(getApplicationContext(), "This is not implemented yet", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

            }
        });
    }
}
