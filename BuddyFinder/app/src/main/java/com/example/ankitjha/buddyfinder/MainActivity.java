package com.example.ankitjha.buddyfinder;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Users userSignedIn;

    ActionBar bar;

    DrawerLayout layout;
    ActionBarDrawerToggle toggle;
    int backToken;
    NavigationView nv;

    SharedPreferences sharedPreferences;

    ListView viewgroups;
    static ArrayList<Groups> groupList;
    DatabaseReference groupref;
    groupviewAdapter adp1;
    ArrayList<Integer> userGroups_Id;
    ArrayList<String> userGroups_Name;

    LocationManager manager;
    LocationManager manager1;
    LocationListener listener;
    LocationListener lis2;
    Location userLoc;

    AlertDialog.Builder builder;

    TextView userLocCity;
    TextView userlocLatLng;
    Geocoder gCode;



    public void createGroupHoverButton(View view)
    {

        // Hover Button Code

        Intent groupIntent=new Intent(getApplicationContext(),NewGroupActivity.class);
        groupIntent.putExtra("userID",userSignedIn.getUid());
        startActivity(groupIntent);

    }

    public void updateView()
    {

        //List View updation function



        userGroups_Id=new ArrayList<>();
        userGroups_Name=new ArrayList<>();
        for(Groups gg:groupList)
        {
            for(int ggid:gg.getFriendList())
                if(ggid==userSignedIn.getUid())
                {
                    userGroups_Id.add(gg.getGid());
                    userGroups_Name.add(gg.getGname());
                }
        }
        adp1=new groupviewAdapter(this,userGroups_Name,userGroups_Id);
        viewgroups.setAdapter(adp1);
        viewgroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent groupTrack=new Intent(getApplicationContext(),GroupViewActivity.class);
                groupTrack.putExtra("userID",userSignedIn.getUid());
                groupTrack.putExtra("groupID",userGroups_Id.get(position));
                startActivity(groupTrack);
            }
        });



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
                        finish();
                    }
                })
                .setNegativeButton("Don't Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"You may switch on the services from Settings -> Location",Toast.LENGTH_SHORT).show();
                        finish();
                        dialog.cancel();
                    }
                });
        AlertDialog alertB=builder.create();
        alertB.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager1=(LocationManager)getSystemService(LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        else if(!manager1.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            buildAlertBox();
            return;

        }

        backToken=0;

        gCode=new Geocoder(getApplicationContext(),Locale.getDefault());
        userLocCity=findViewById(R.id.userLocation);
        userlocLatLng=findViewById(R.id.latLngView);

        viewgroups=findViewById(R.id.list_group);

        //getting the user signed in

        Intent recieveIntent=getIntent();
        int id=recieveIntent.getIntExtra("userID",-1);
        for(Users u:LogInActivity.userList)
            if(u.getUid()==id) {
                userSignedIn = u;
                break;
            }


        if(userSignedIn.getLocSharePer()==1 && manager1.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            Intent serviceIntent=new Intent(getApplicationContext(),LocServiceClass.class);
            serviceIntent.putExtra("userID",userSignedIn.getUid());
            startService(serviceIntent);
        }
        // assigning action bar with a title

        bar=getSupportActionBar();
        bar.setTitle("Welcome, "+userSignedIn.getUname());
        sharedPreferences=this.getSharedPreferences("com.example.ankitjha.buddyfinder",Context.MODE_PRIVATE);

        // Navigation View with on click listener

        layout=findViewById(R.id.drawer_layout);
        toggle=new ActionBarDrawerToggle(this,layout,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        layout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nv=findViewById(R.id.navigation_view);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id=menuItem.getItemId();
                switch(id)
                {
                    case R.id.acc_setting:

                        Intent accSettingIntent=new Intent(getApplicationContext(),AccountSettingsActivity.class);
                        accSettingIntent.putExtra("userID",userSignedIn.getUid());
                        startActivity(accSettingIntent);

                        return true;
                    case R.id.loc_setting:
                        //Toast.makeText(getApplicationContext(),"This is not Implemented Yet",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(),LocationPreferenceActivity.class);
                        intent.putExtra("userID",userSignedIn.getUid());
                        startActivity(intent);

                        return true;
                    case R.id.log_out:

                        sharedPreferences.edit().putInt("userID",-1).apply();
                        Intent logOutIntent=new Intent(getApplicationContext(),LogInActivity.class);
                        manager.removeUpdates(listener);
                        manager.removeUpdates(lis2);
                        NotificationManagerCompat nmc=NotificationManagerCompat.from(getApplicationContext());
                        nmc.cancel(0);
                        stopService(new Intent(getApplicationContext(),LocServiceClass.class));
                        if(LocServiceClass.c!=null)
                            LocServiceClass.c.cancel();

                        startActivity(logOutIntent);
                        finish();
                        return true;
                }

                return false;
            }
        });

        // group updating

        groupList=new ArrayList<Groups>();
        groupref=FirebaseDatabase.getInstance().getReference("Groups");
        groupref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupList.clear();
                Log.i("Group Changes","Detected");
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    Groups g1=d.getValue(Groups.class);
                    groupList.add(g1);
                }
                updateView();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //location codes

        manager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        listener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Log.i("Activity: ","Main");
                if(userLoc==null)
                    userLoc=location;
                if(userLoc.getAccuracy()<=location.getAccuracy()) {
                    //Log.i("Location: " ,String.valueOf(location.getLatitude())+"-"+String.valueOf(location.getLongitude())+":"+String.valueOf(location.getAccuracy()));
                    userLoc = location;


                    try
                    {
                        List<Address> addrList=gCode.getFromLocation(userLoc.getLatitude(),userLoc.getLongitude(),1);
                        if(addrList==null || addrList.size()==0)
                        {
                            userLocCity.setText("Not able to retrieve your Address");
                            DecimalFormat df=new DecimalFormat("#.#######");
                            Double us_Lat=Double.valueOf(df.format(userLoc.getLatitude()));
                            Double us_Long=Double.valueOf(df.format(userLoc.getLongitude()));
                            userlocLatLng.setText("Lat/Long : "+String.valueOf(us_Lat)+" / "+String.valueOf(us_Long));
                            return;
                        }
                        Address address=addrList.get(0);
                        String addrComp[]=address.getAddressLine(0).split(",");

                        if(address.getLocality()==null){
                            userLocCity.setText("Not able to retrieve your Address");
                            DecimalFormat df=new DecimalFormat("#.#######");
                            Double us_Lat=Double.valueOf(df.format(userLoc.getLatitude()));
                            Double us_Long=Double.valueOf(df.format(userLoc.getLongitude()));
                            userlocLatLng.setText("Lat/Long : "+String.valueOf(us_Lat)+" / "+String.valueOf(us_Long));
                        }
                        else {
                            String city = address.getLocality().trim();
                            for (int cnt = 0; cnt < addrComp.length; cnt++) {
                                addrComp[cnt] = addrComp[cnt].trim();
                                if (addrComp[cnt].equals(city) == true) {
                                    userLocCity.setText("You are in " + addrComp[cnt - 1] + ", " + addrComp[cnt]);
                                    DecimalFormat df = new DecimalFormat("#.#######");
                                    Double us_Lat = Double.valueOf(df.format(userLoc.getLatitude()));
                                    Double us_Long = Double.valueOf(df.format(userLoc.getLongitude()));
                                    userlocLatLng.setText("Lat/Long : " + String.valueOf(us_Lat) + " / " + String.valueOf(us_Long));
                                }
                            }
                        }

                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        lis2=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Log.i("Activity: ","Main");
                //Log.i("Loca2: ",String.valueOf(location.getLatitude())+"-"+String.valueOf(location.getLongitude()));
                if(userLoc==null)
                    userLoc=location;
                if(userLoc.getAccuracy()<=location.getAccuracy()) {
                    userLoc = location;
                    try
                    {
                        List<Address> addrList=gCode.getFromLocation(userLoc.getLatitude(),userLoc.getLongitude(),1);
                        if(addrList==null || addrList.size()==0)
                        {
                            userLocCity.setText("Not able to retrieve your Address");
                            DecimalFormat df=new DecimalFormat("#.#######");
                            Double us_Lat=Double.valueOf(df.format(userLoc.getLatitude()));
                            Double us_Long=Double.valueOf(df.format(userLoc.getLongitude()));
                            userlocLatLng.setText("Lat/Long : "+String.valueOf(us_Lat)+" / "+String.valueOf(us_Long));
                            return;
                        }
                        Address address=addrList.get(0);
                        String addrComp[]=address.getAddressLine(0).split(",");

                        if(address.getLocality()==null){
                            userLocCity.setText("Not able to retrieve your Address");
                            DecimalFormat df=new DecimalFormat("#.#######");
                            Double us_Lat=Double.valueOf(df.format(userLoc.getLatitude()));
                            Double us_Long=Double.valueOf(df.format(userLoc.getLongitude()));
                            userlocLatLng.setText("Lat/Long : "+String.valueOf(us_Lat)+" / "+String.valueOf(us_Long));
                        }
                        else {
                            String city = address.getLocality().trim();
                            for (int cnt = 0; cnt < addrComp.length; cnt++) {
                                addrComp[cnt] = addrComp[cnt].trim();
                                if (addrComp[cnt].equals(city) == true) {
                                    userLocCity.setText("You are in " + addrComp[cnt - 1] + ", " + addrComp[cnt]);
                                    DecimalFormat df = new DecimalFormat("#.#######");
                                    Double us_Lat = Double.valueOf(df.format(userLoc.getLatitude()));
                                    Double us_Long = Double.valueOf(df.format(userLoc.getLongitude()));
                                    userlocLatLng.setText("Lat/Long : " + String.valueOf(us_Lat) + " / " + String.valueOf(us_Long));
                                }
                            }
                        }

                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,listener);
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,lis2);
            Location loc1,loc2;
            loc1=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            loc2=manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(loc1==null)
                userLoc = loc2;
            else if(loc2==null)
                userLoc = loc1;
            if(loc1!=null && loc2!=null)
            {
                if(loc1.getAccuracy()>loc2.getAccuracy())
                    userLoc=loc1;
                else
                    userLoc=loc2;
            }
        }







    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backToken++;
        Toast.makeText(getApplicationContext(), "Press back again to Exit", Toast.LENGTH_SHORT).show();
        if(backToken==2)
        {
            finish();
        }
        new CountDownTimer(2000,1000){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                backToken=0;
            }
        }.start();


    }
}
