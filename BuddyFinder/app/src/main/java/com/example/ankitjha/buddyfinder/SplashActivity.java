package com.example.ankitjha.buddyfinder;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {


    boolean done,mainOpen;
    ActionBar bar;
    int id;
    LocationManager manager;

   // AlertDialog.Builder builder;



/*
    public void buildAlertBox()
    {
        builder=new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled. Enable it to enjoy our services. ")
                .setCancelable(false)
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Don't Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"You may switch on the services from Settings -> Location",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        AlertDialog alertB=builder.create();
        alertB.show();
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        manager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        /*if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED ||ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);

        }
        else if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            buildAlertBox();

        }*/


        done=false;
        mainOpen=false;

        bar=getSupportActionBar();
        bar.hide();

        Intent intent=getIntent();
        id=intent.getIntExtra("userID",-1);

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LogInActivity.userList.clear();
                for(DataSnapshot s:dataSnapshot.getChildren())
                {
                    Users u=s.getValue(Users.class);
                    LogInActivity.userList.add(u);

                }
                done=true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        CountDownTimer c=new CountDownTimer(5000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                if(done==true && mainOpen==false)
                {
                    Intent in=new Intent(getApplicationContext(),MainActivity.class);
                    in.putExtra("userID",id);
                    Log.i("OpeningT","Main");
                    startActivity(in);
                    mainOpen=true;
                    finish();
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                if(mainOpen==true)
                {finish();
                    return;}
                if(done==true)
                {
                    Intent in=new Intent(getApplicationContext(),MainActivity.class);
                    Log.i("Opening","Main");
                    in.putExtra("userID",id);
                    startActivity(in);
                    finish();
                }
                else
                {
                    SharedPreferences pr=getSharedPreferences("com.example.ankitjha.buddyfinder",Context.MODE_PRIVATE);
                    pr.edit().putInt("userID",-1);
                    Log.i("Opening","Log In Page"+String.valueOf(pr.getInt("userID",-1)));

                    Intent in1=new Intent(getApplicationContext(),LogInActivity.class);
                    startActivity(in1);
                    finish();
                }
            }
        };
        c.start();



    }
}
