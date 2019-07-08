package com.example.ankitjha.buddyfinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GroupViewActivity extends AppCompatActivity {

    ActionBar bar;
    Users userSignedIn;
    Groups groupSelected;

    TextView adminView;

    ArrayList<String> frndName;
    ArrayList<Integer> avail;

    ListView friendListView;
    groupFriendAdapter adp;



    public void locateFriends(View view)
    {
        Intent locIntent=new Intent(getApplicationContext(),MapsActivity.class);
        locIntent.putExtra("userID",userSignedIn.getUid());
        locIntent.putExtra("groupID",groupSelected.getGid());
        locIntent.putExtra("intent",1);
        startActivity(locIntent);
    }

    public void openMaps(View view)
    {

        if(userSignedIn.getUid()!=groupSelected.getAdmin_user())
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(GroupViewActivity.this);
            builder.setTitle("Not Allowed")
                    .setMessage("You are not an Admin")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog d=builder.create();
            d.show();

        }
        else {
            Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
            mapIntent.putExtra("userID", userSignedIn.getUid());
            mapIntent.putExtra("groupID", groupSelected.getGid());
            mapIntent.putExtra("intent", 0);
            startActivity(mapIntent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);

        Intent recvIntent=getIntent();
        int uid=recvIntent.getIntExtra("userID",-1);
        int gid=recvIntent.getIntExtra("groupID",-1);

        for(Users u:LogInActivity.userList)
            if(uid==u.getUid())
            {
                userSignedIn=u;
                break;
            }
        if(userSignedIn==null)
            Toast.makeText(getApplicationContext(), "User is not reaching", Toast.LENGTH_SHORT).show();
        //else
          //  Toast.makeText(getApplicationContext(),String.valueOf(userSignedIn.getGroupList().get(1)),Toast.LENGTH_SHORT).show();
        for(Groups g:MainActivity.groupList)
            if(gid==g.getGid())
            {
                groupSelected=g;
                break;
            }


        frndName=new ArrayList<>();
        avail=new ArrayList<>();
        String admin_name="";

        for(int uuid:groupSelected.getFriendList())
        {
            for(Users u:LogInActivity.userList)
            {
                if(groupSelected.getAdmin_user()==u.getUid())
                    admin_name=u.getUname();
                if(u.getUid()==uuid)
                {

                    frndName.add(u.getUname());
                    if(u.getLat()==-1|| u.getLng()==-1 || u.getLocSharePer()==0)
                    {
                        avail.add(-1);
                    }
                    else
                        avail.add(0);
                }
            }
        }
        friendListView=findViewById(R.id.friend_list_view);
        adp=new groupFriendAdapter(this,frndName,avail);
        friendListView.setAdapter(adp);






        bar=getSupportActionBar();
        bar.setTitle(groupSelected.getGname());

        adminView=findViewById(R.id.admin_view);
        adminView.setText("Admin: "+admin_name);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_view_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        switch(id)
        {
            case R.id.del_grp:

                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Delete Group");
                builder.setMessage("Are you Sure you want to delete the group ?")
                       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                               ConnectivityManager connectivitymanager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                               boolean stat=connectivitymanager.getActiveNetworkInfo().isConnected();
                               if(!stat)
                               {
                                   Toast.makeText(getApplicationContext(),"You are not connected to a Server",Toast.LENGTH_SHORT).show();
                                   return;
                               }

                               int gidToBeRemoved=groupSelected.getGid();
                               ArrayList<Users> userToBeModified=new ArrayList<>();
                               userToBeModified.clear();
                               for(int uid:groupSelected.getFriendList())
                                   for(Users u:LogInActivity.userList)
                                       if(u.getUid()==uid)
                                           userToBeModified.add(u);

                               for(Users u1:userToBeModified)
                               {
                                   Log.i("userList",u1.groupList.toString());
                                   ArrayList<Integer> grpLis1=new ArrayList<>();
                                   grpLis1.clear();
                                   for (int i=0;i<u1.groupList.size();i++)
                                   {
                                       if(gidToBeRemoved==u1.groupList.get(i))
                                       {
                                           continue;
                                       }
                                       else
                                           grpLis1.add(u1.groupList.get(i));
                                   }
                                   u1.groupList=grpLis1;
                               }


                               DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
                               ref.child(String.valueOf(gidToBeRemoved)).removeValue();

                               ref=FirebaseDatabase.getInstance().getReference("Users");

                               for(Users u:userToBeModified)
                               {
                                   ref.child(String.valueOf(u.getUid())).setValue(u);
                               }
                               Toast.makeText(getApplicationContext(),"Group Deleted",Toast.LENGTH_SHORT).show();
                               finish();

                           }
                       })
                       .setNegativeButton("No", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                           }
                       });
                AlertDialog d=builder.create();
                d.show();


                return true;
            case R.id.rem_me:
                if(userSignedIn.getUid()==groupSelected.getAdmin_user())

                {
                    if(groupSelected.getFriendList().size()<=2) {
                        AlertDialog.Builder builder1=new AlertDialog.Builder(this);
                        builder1.setTitle("Remove User")
                                .setMessage("Better you delete the group")
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        AlertDialog dialog1=builder1.create();
                        dialog1.show();
                        //Toast.makeText(getApplicationContext(), "It's better to delete the group", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    else
                    {
                        Intent grpAdminAssignIntent=new Intent(getApplicationContext(),GroupRemovalActivity.class);
                        grpAdminAssignIntent.putExtra("userID",userSignedIn.getUid())
                                .putExtra("groupID",groupSelected.getGid())
                                .putExtra("intent",1);
                        startActivity(grpAdminAssignIntent);
                        finish();
                    }
                }

                else
                {
                    ConnectivityManager connectivitymanager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    boolean stat=connectivitymanager.getActiveNetworkInfo().isConnected();
                    if(!stat)
                    {
                        Toast.makeText(getApplicationContext(),"You are not connected to a Server",Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    for(int i=0;i<groupSelected.getFriendList().size();i++)
                    {
                        if(groupSelected.getFriendList().get(i)==userSignedIn.getUid())
                        {
                            groupSelected.getFriendList().remove(i);
                            break;
                        }

                    }
                    ArrayList<Integer> frList=userSignedIn.groupList;
                    if(frList.get(0)==null)
                    {
                        Toast.makeText(getApplicationContext(),"Error!!"+String.valueOf(frList.size()),Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    for(int i=0;i<frList.size();i++)
                    {
                        if(frList.get(i)==groupSelected.getGid())
                        {
                            userSignedIn.getGroupList().remove(i);
                            break;
                        }
                    }

                    DatabaseReference groupRef=FirebaseDatabase.getInstance().getReference("Groups");
                    DatabaseReference userRef=FirebaseDatabase.getInstance().getReference("Users");

                    groupRef.child(String.valueOf(groupSelected.getGid())).setValue(groupSelected);
                    userRef.child(String.valueOf(userSignedIn.getUid())).setValue(userSignedIn);

                    Toast.makeText(getApplicationContext(),"Removed Successfully",Toast.LENGTH_SHORT).show();
                    finish();
                }




                return true;
            case R.id.rem_friend:
                if(userSignedIn.getUid()!=groupSelected.getAdmin_user() ) {
                    Toast.makeText(getApplicationContext(), "You are not an admin ! ", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if(groupSelected.getFriendList().size()<=2) {
                    AlertDialog.Builder builder2=new AlertDialog.Builder(this);
                    builder2.setTitle("Remove User")
                            .setMessage("Better you delete the group")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog dialog2=builder2.create();
                    dialog2.show();
                    return true;
                }
                else
                {
                    Intent grpRemovalIntent=new Intent(getApplicationContext(),GroupRemovalActivity.class);
                    grpRemovalIntent.putExtra("userID",userSignedIn.getUid());
                    grpRemovalIntent.putExtra("groupID",groupSelected.getGid());
                    grpRemovalIntent.putExtra("intent",0);
                    startActivity(grpRemovalIntent);
                }
                finish();
                return true;
            default:

                return false;
        }

    }
}
