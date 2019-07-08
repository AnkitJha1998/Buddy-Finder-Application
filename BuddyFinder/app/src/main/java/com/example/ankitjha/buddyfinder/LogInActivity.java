package com.example.ankitjha.buddyfinder;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LogInActivity extends AppCompatActivity {


    ActionBar bar;
    EditText userEntry,passEntry;
    TextView userV,passV;
    DatabaseReference ref;
    static ArrayList<Users> userList;
    LocationManager manager;
    AlertDialog.Builder builder;


    private void createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT>=26)
        {
            CharSequence name=getString(R.string.channel_name);
            String desc=getString(R.string.channel_desc);
            int importance=NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel=new NotificationChannel("loc_setting",name,importance);
            channel.setDescription(desc);

            NotificationManager notifyManager=getSystemService(NotificationManager.class);
            notifyManager.createNotificationChannel(channel);

        }
    }


    public void signUp(View view)
    {
        Intent signUpIntent=new Intent(getApplicationContext(),SignUpActivity.class);
        startActivity(signUpIntent);
        userEntry.setText("");
        passEntry.setText("");
    }

    public void signIn(View view)
    {
        String username,password;
        username=userEntry.getText().toString().trim();
        password=passEntry.getText().toString().trim();

        for(Users u:LogInActivity.userList)
        {
            if(u.getUsername().equals(username))
                if(u.getPassword().equals(password))
                {
                    Intent signInIntent=new Intent(getApplicationContext(),MainActivity.class);
                    signInIntent.putExtra("userID",u.getUid());
                    SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.ankitjha.buddyfinder", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putInt("userID",u.getUid()).apply();
                    startActivity(signInIntent);
                    finish();
                    return;
                }

        }
        Toast.makeText(getApplicationContext(),"Invalid Username or Password",Toast.LENGTH_SHORT).show();
        userEntry.setText("");
        passEntry.setText("");
    }


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
                Toast.makeText(getApplicationContext(),"You may switch on the services from Settins -> Location",Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
    AlertDialog alertB=builder.create();
    alertB.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED)
        {
            buildAlertBox();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        /*SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.ankitjha.buddyfinder", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("userID",-1).apply();*/


        userList=new ArrayList<Users>();
        ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users t;
                userList.clear();
                for(DataSnapshot s:dataSnapshot.getChildren())
                {
                    t=s.getValue(Users.class);
                    LogInActivity.userList.add(t);
                }
                //Toast.makeText(getApplicationContext(),String.valueOf(userList.size()),Toast.LENGTH_SHORT).show();
                //(t.getView()).getBackground().setColorFilter(Color.BLACK,PorterDuff.Mode.SRC_IN);
                //(t.getView())

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bar=getSupportActionBar();
        bar.setTitle("Log In to Buddy Finder");

        createNotificationChannel();

        userEntry=findViewById(R.id.user);
        passEntry=findViewById(R.id.pass);
        userV=findViewById(R.id.userTextView);
        passV=findViewById(R.id.passTextView);
        userEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(userEntry.getText().toString().isEmpty()==true)
                    userV.setVisibility(View.INVISIBLE);
                else
                    userV.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(passEntry.getText().toString().isEmpty()==true)
                    passV.setVisibility(View.INVISIBLE);
                else
                    passV.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        /*Keys k=new Keys();
        k.uid=0;
        k.gid=0;
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Keys");
        ref.child("0").setValue(k);
        */


        manager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED ||ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);

        }
        else if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            buildAlertBox();

        }


        SharedPreferences shPref=this.getSharedPreferences("com.example.ankitjha.buddyfinder", Context.MODE_PRIVATE);
        int idShrd=shPref.getInt("userID",-1);
        Log.i("Saved pref: ",String.valueOf(idShrd));
        if(idShrd==-1)
        {

        }
        else
        {

            Intent dirAccess=new Intent(getApplicationContext(),SplashActivity.class);
            dirAccess.putExtra("userID",idShrd);
            startActivity(dirAccess);
            finish();
        }





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.log_in_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        switch(id)
        {
            case R.id.about_us:
                Intent aboutIntent=new Intent(getApplicationContext(),AboutUsActivity.class);
                startActivity(aboutIntent);
                return true;

            default: return false;


        }

    }
}
