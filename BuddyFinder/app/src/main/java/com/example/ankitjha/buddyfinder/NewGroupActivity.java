package com.example.ankitjha.buddyfinder;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewGroupActivity extends AppCompatActivity {


    ActionBar bar;
    Users userLoggedIn;
    EditText groupName;
    TextView grpNameView;
    ArrayList<String> names;
    ArrayList<Integer> chkList;
    ListView frndList;
    newGroupListAdapter adp;
    Keys k;


    public int idOfUser(String name)
    {
        for(Users u:LogInActivity.userList)
            if(name.equals(u.getUname()))
                return u.getUid();
        return -1;
    }
    public Users getUs(int id)
    {
        for(Users u:LogInActivity.userList)
            if(u.getUid()==id)
                return u;
        return null;
    }


    public void createGroup(View view)
    {
        Groups g=new Groups();
        g.gid=k.getGid();                                           //ID assigned
        g.gname=groupName.getText().toString().trim();              //Name Assigned

        ArrayList<Integer> frndList=new ArrayList<Integer>();
        int count=0;
        for(int i=0;i<chkList.size();i++)
        {
            if(chkList.get(i)==1)
            {
                frndList.add(idOfUser(names.get(i)));
                count++;
            }
        }
        if(count==1)
        {
            Toast.makeText(getApplicationContext(),"Add members first ",Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            g.friendList=frndList;                                  //Friend List Assigned
            ConnectivityManager connectivitymanager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean stat=connectivitymanager.getActiveNetworkInfo().isConnected();
            if(stat==false)
            {
                Toast.makeText(getApplicationContext(),"You are not connected to a Server",Toast.LENGTH_SHORT).show();
                return;
            }
            for(int id1:frndList)
            {
                Users temp=getUs(id1);

                DatabaseReference ref1=FirebaseDatabase.getInstance().getReference("Users");
                if(temp.getGroupList()==null)
                    temp.groupList=new ArrayList<Integer>();
                else if(temp.getGroupList().size()==0)
                    temp.groupList=new ArrayList<Integer>();
                temp.groupList.add(g.getGid());
                ref1.child(String.valueOf(temp.getUid())).setValue(temp);
            }
                                                                    //Users Assigned with group Ids
            g.admin_user=userLoggedIn.getUid();                     //Admin Assigned

            DatabaseReference ref2=FirebaseDatabase.getInstance().getReference("Groups");
            ref2.child(String.valueOf(g.getGid())).setValue(g);
            DatabaseReference keyref3=FirebaseDatabase.getInstance().getReference("Keys");
            k.gid++;
            Log.i("gid Code",String.valueOf(k.gid));
            keyref3.child("0").setValue(k);
            Toast.makeText(getApplicationContext(), "Group Created", Toast.LENGTH_SHORT).show();
            finish();
            return;

        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        Intent recvIntent=getIntent();
        int uid=recvIntent.getIntExtra("userID",-1);
        if(uid==-1)
        {
            Toast.makeText(getApplicationContext(), "There was an error", Toast.LENGTH_SHORT).show();
            finish();
        }
        for(Users u:LogInActivity.userList)
            if(u.getUid()==uid)
                userLoggedIn=u;

        bar=getSupportActionBar();
        bar.setTitle("Create your own group, "+userLoggedIn.getUname());

        groupName=findViewById(R.id.gname);
        grpNameView=findViewById(R.id.gnameView);
        groupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(groupName.getText().toString().isEmpty()==true)
                {
                    grpNameView.setVisibility(View.INVISIBLE);
                }
                else
                {
                    grpNameView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        names=new ArrayList<String>();
        chkList=new ArrayList<Integer>();
        for(Users u:LogInActivity.userList)
        {
            names.add(u.getUname());
            if(u.getUid()==userLoggedIn.getUid())
                chkList.add(1);
            else
                chkList.add(0);
        }
        frndList=findViewById(R.id.friend_list);
        adp=new newGroupListAdapter(this,names,chkList);
        frndList.setAdapter(adp);
        frndList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(names.get(position).equals(userLoggedIn.getUname()))
                {
                    Toast.makeText(getApplicationContext(), "You can't unselect yourself", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    if(chkList.get(position)==0)
                    chkList.set(position,1);
                    else
                        chkList.set(position,0);
                }
                adp.notifyDataSetChanged();
            }
        });

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Keys");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren())
                    k=d.getValue(Keys.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
