package com.example.ankitjha.buddyfinder;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationPreferenceActivity extends AppCompatActivity {

    ActionBar bar;
    int locSharePer;
    Users userSignedIn;
    Switch aSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_preference);

        bar=getSupportActionBar();
        bar.setTitle("Location Preferences");

        Intent intent=getIntent();
        int id=intent.getIntExtra("userID",-1);

        //Toast.makeText(getApplicationContext(), "Loc UserID: "+String.valueOf(id), Toast.LENGTH_SHORT).show();

        for(Users u:LogInActivity.userList)
            if(u.getUid()==id)
                userSignedIn=u;
        aSwitch=findViewById(R.id.loc_pref);
        if(userSignedIn.getLocSharePer()==1)
            aSwitch.setChecked(true);
        else
            aSwitch.setChecked(false);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    locSharePer = 1;
                    Intent serviceIntent=new Intent(getApplicationContext(),LocServiceClass.class);
                    serviceIntent.putExtra("userID",userSignedIn.getUid());
                    startService(serviceIntent);
                    Log.i("User ID111",String.valueOf(userSignedIn.getUid()));
                    //LocServiceClass.c.start();
                }
                else {
                    LocServiceClass.c.cancel();
                    stopService(new Intent(getApplicationContext(),LocServiceClass.class));
                    NotificationManagerCompat nmc=NotificationManagerCompat.from(getApplicationContext());
                    nmc.cancel(0);
                    locSharePer = 0;
                }

                ConnectivityManager connectivitymanager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                boolean stat=connectivitymanager.getActiveNetworkInfo().isConnected();
                if(stat==false)
                {
                    Toast.makeText(getApplicationContext(),"You are not connected to a Server",Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("User ID",String.valueOf(userSignedIn.getUid()));
                DatabaseReference usLocRef=FirebaseDatabase.getInstance().getReference("Users");
                usLocRef.child(String.valueOf(userSignedIn.getUid())).child("locSharePer").setValue(locSharePer);
            }
        });


    }
}
