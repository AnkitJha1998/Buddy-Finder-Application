package com.example.ankitjha.buddyfinder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Users userSignedIn;
    Groups groupSelected;
    int activityIntent;
    ArrayList<Users> userDisplayedList;

    LocationManager mapUpdateManager;
    LocationListener mapUpdateListener;
    LocationListener mapUpdateListener1;
    ArrayList<LatLng> adminPath;

    PolylineOptions polylineOptions;
    //Polyline pathLine;
    boolean linePlot;
    Location curLocation;
    CountDownTimer timer1, timer2;


    public void assignPath() {
        if (groupSelected.pathLongs != null && groupSelected.pathLats != null)
            for (int i = 0; i < groupSelected.pathLongs.size(); i++)
                adminPath.add(new LatLng(groupSelected.pathLats.get(i), groupSelected.pathLongs.get(i)));
    }

    public void plotPath()
    {
        if(adminPath==null)
            return;
        polylineOptions=new PolylineOptions();
        polylineOptions.addAll(adminPath);
        mMap.addPolyline(polylineOptions);



    }

    public void setMarkers()
    {
        for(Users u2:userDisplayedList)
        {
            if(u2.getUid()==userSignedIn.getUid())
                continue;
            mMap.addMarker(new MarkerOptions().position(new LatLng(u2.getLat(),u2.getLng())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(u2.getUname()));
        }
        if(groupSelected.destLat!=null||groupSelected.destLong!=null)
            mMap.addMarker(new MarkerOptions().position(new LatLng(groupSelected.destLat,groupSelected.destLong)).title("Our Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent recvIntent=getIntent();
        activityIntent=recvIntent.getIntExtra("intent",-1);
        int gid=recvIntent.getIntExtra("groupID",-1);
        int uid=recvIntent.getIntExtra("userID",-1);

        for(Groups g:MainActivity.groupList)
            if(g.getGid()==gid)
            {
                groupSelected=g;
                break;
            }

        for(Users u:LogInActivity.userList)
            if(u.getUid()==uid)
            {
                userSignedIn=u;
                break;
            }

        adminPath=new ArrayList<>();
        adminPath.clear();

        mapUpdateManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        linePlot=false;
        //if(adminPath)
        /*polyOpt=new PolylineOptions();
        polyOpt.addAll(adminPath)
        .clickable(true);
        linePlot=false;
        mapUpdateManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
*/
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        adminPath.clear();
        /*polyOpt.addAll(adminPath);
        pathLine = mMap.addPolyline(polyOpt);
        pathLine.setVisible(false);*/


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(userSignedIn.lat, userSignedIn.lng);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,13));
        if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);


        if(activityIntent==0)
        {
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(final LatLng latLng) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setTitle("Group Destination");
                    builder.setMessage("Do you want to use this destination?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Double lat=latLng.latitude,lng=latLng.longitude;
                                    ConnectivityManager connectivitymanager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                                    boolean stat=connectivitymanager.getActiveNetworkInfo().isConnected();
                                    if(!stat)
                                    {
                                        Toast.makeText(getApplicationContext(),"You are not connected to a Server",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    DecimalFormat df=new DecimalFormat("#.#######");
                                    lat=Double.valueOf(df.format(lat));
                                    lng=Double.valueOf(df.format(lng));
                                    groupSelected.destLat=lat;
                                    groupSelected.destLong=lng;
                                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
                                    ref.child(String.valueOf(groupSelected.getGid())).setValue(groupSelected);
                                    Toast.makeText(getApplicationContext(),"Destination Set",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog ad=builder.create();
                    ad.show();

                }
            });
            AlertDialog.Builder builder=new AlertDialog.Builder(MapsActivity.this);
            builder.setMessage("Long Tap to select your Group's location")
                    .setTitle("Set Destination")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog dialog1=builder.create();
            dialog1.show();
        }
        else if(activityIntent==1)
        {

            //Toast.makeText(getApplicationContext(),"This is yet to be implemented",Toast.LENGTH_SHORT).show();

            mMap.clear();

            AlertDialog.Builder builder=new AlertDialog.Builder(MapsActivity.this);
            builder.setMessage("Purple marker is the Destination and Green Markers are your friends.\nLong press to toggle your leader's path")
                    .setTitle("Marker Info")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog dialog1=builder.create();
            dialog1.show();



            if(groupSelected.destLat!=null||groupSelected.destLong!=null)
            mMap.addMarker(new MarkerOptions().position(new LatLng(groupSelected.destLat,groupSelected.destLong)).title("Our Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

            if(userDisplayedList==null)
                userDisplayedList=new ArrayList<>();
            else
                userDisplayedList.clear();
            for(int uid1:groupSelected.getFriendList())
            {
                for(Users u:LogInActivity.userList)
                    if(u.getUid()==uid1)
                        userDisplayedList.add(u);
            }
            mMap.clear();
            for(Users u3:userDisplayedList)
            {
                if(u3.getUid()==userSignedIn.getUid())
                    continue;
                mMap.addMarker(new MarkerOptions().position(new LatLng(u3.getLat(),u3.getLng())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(u3.getUname()));
            }
            if(groupSelected.destLat!=null||groupSelected.destLong!=null)
            mMap.addMarker(new MarkerOptions().position(new LatLng(groupSelected.destLat,groupSelected.destLong)).title("Our Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));




            timer2=new CountDownTimer(5000,1000)
            {

                @Override
                public void onFinish() {


                    mMap.clear();

                    if(linePlot==true)
                        plotPath();
                    setMarkers();
                    if(adminPath!=null)
                        Log.i("Currently Size",String.valueOf(adminPath.size()));
                    start();
                }

                @Override
                public void onTick(long millisUntilFinished) {
                    if(userDisplayedList==null)
                        userDisplayedList=new ArrayList<>();
                    else
                        userDisplayedList.clear();
                    for(int uid1:groupSelected.getFriendList())
                    {
                        for(Users u:LogInActivity.userList)
                            if(u.getUid()==uid1)
                                userDisplayedList.add(u);
                    }
                }
            }.start();

            mapUpdateListener=new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if(curLocation==null)
                        curLocation=location;
                    else if(curLocation.getLatitude()!=location.getLatitude() || curLocation.getLongitude()!=location.getLongitude())
                        curLocation=location;
                    else return;
                    if(userSignedIn.getUid()==groupSelected.getAdmin_user())
                        adminPath.add(new LatLng(curLocation.getLatitude(),curLocation.getLongitude()));

                    Log.i("Size",String.valueOf(adminPath.size()));
                    Log.i("SizeView",adminPath.toString());


                    //mMap.addPolyline(polylineOptions);

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



            if(groupSelected.getAdmin_user()==userSignedIn.getUid()) {

                AlertDialog.Builder checkStart=new AlertDialog.Builder(MapsActivity.this);
                checkStart.setTitle("Starting Trek?")
                        .setMessage("Are you Starting your Trek Now?")
                        .setPositiveButton("Yeah", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                                    mapUpdateManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mapUpdateListener);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                assignPath();
                                if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                                    mapUpdateManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mapUpdateListener);
                            }
                        });
                (checkStart.create()).show();


                timer1=new CountDownTimer(4000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        ArrayList<Double> latList1=new ArrayList<>(),longList1=new ArrayList<>();
                        for(LatLng obj1:adminPath)
                        {
                            DecimalFormat df=new DecimalFormat("#.#######");
                            latList1.add(Double.valueOf(df.format(obj1.latitude)));
                            longList1.add(Double.valueOf(df.format(obj1.longitude)));
                        }
                        ConnectivityManager mana=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                        boolean stat=mana.getActiveNetworkInfo().isConnected();
                        if(stat==false)
                        start();
                        else {
                            groupSelected.pathLats = latList1;
                            groupSelected.pathLongs = longList1;

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                            ref.child(String.valueOf(groupSelected.getGid())).setValue(groupSelected);
                            start();
                        }

                        Log.i("Database(asses):","Updated");
                    }
                };

                timer1.start();


                //mapUpdateManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mapUpdateListener);

            }
            else {
                assignPath();
                timer1=new CountDownTimer(4000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        for(Groups g:MainActivity.groupList)
                            if(g.getGid()==groupSelected.getGid())
                            {
                                groupSelected=g;
                                break;
                            }
                        if(groupSelected.pathLats!=null && groupSelected.pathLongs!=null  )
                            if(groupSelected.pathLats.size()!=0 &&groupSelected.pathLongs.size()!=0)
                            {
                                Log.i("Group Sizes",String.valueOf(groupSelected.pathLats.size()));
                                adminPath.clear();
                                for(int i=0;i<groupSelected.pathLats.size();i++)
                                    adminPath.add(new LatLng(groupSelected.pathLats.get(i),groupSelected.pathLongs.get(i)));

                            }

                        Log.i("Is my timer Running?","yes");
                        start();

                    }
                };
                timer1.start();


            }




            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    mMap.clear();
                    linePlot=!linePlot;
                    if(linePlot)
                    {
                        plotPath();
                        setMarkers();
                    }
                    else
                        setMarkers();

                }
            });

            //adminPath=new ArrayList<>();
            /*if(groupSelected.getAdmin_user()==userSignedIn.getUid()) {

                AlertDialog.Builder builder3=new AlertDialog.Builder(MapsActivity.this);
                builder3.setTitle("Leader's Path")
                        .setMessage("Are you Starting your trek?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                polyOpt.addAll(adminPath);
                                pathLine.setPoints(adminPath);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(groupSelected.pathLats==null||groupSelected.pathLongs==null)
                                    adminPath=new ArrayList<>();
                                else
                                    assignPath();
                                polyOpt.addAll(adminPath);
                                pathLine.setPoints(adminPath);
                            }
                        });
                AlertDialog dd=builder3.create();
                dd.show();

                if (linePlot == false)
                    pathLine.setVisible(false);
                mapUpdateListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if(curLocation!=null)
                            Log.i("Cur Loc",String.valueOf(curLocation.getAccuracy())+","+curLocation.toString());
                        Log.i("New Loc",String.valueOf(location.getAccuracy())+","+location.toString());
                        if (curLocation == null)
                            curLocation = location;
                        else if (curLocation.getAccuracy() <= location.getAccuracy()&& (curLocation.getLatitude()!=location.getLatitude() ||
                            curLocation.getLongitude()!=location.getLongitude()
                        ))
                            curLocation = location;
                        else return;
                        Log.i("Location Changes","Yes");
                        if (userSignedIn.getUid() == groupSelected.getAdmin_user()) {
                            adminPath.add(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()));
                            polyOpt.addAll(adminPath);
                            pathLine.setPoints(adminPath);
                            if (linePlot)
                                pathLine.setVisible(true);


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
                mapUpdateListener1 = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (curLocation == null)
                            curLocation = location;
                        else if (curLocation.getAccuracy() <= location.getAccuracy()&& (curLocation.getLatitude()!=location.getLatitude() ||
                                curLocation.getLongitude()!=location.getLongitude()
                        ))
                            curLocation = location;
                        else return;

                        if (userSignedIn.getUid() == groupSelected.getAdmin_user()) {
                            adminPath.add(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()));
                            polyOpt.addAll(adminPath);
                            pathLine.setPoints(adminPath);
                            if (linePlot)
                                pathLine.setVisible(true);


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

                mapUpdateManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,mapUpdateListener);
                mapUpdateManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,mapUpdateListener1);



                timer1=new CountDownTimer(3000,1000)
                {
                    @Override
                    public void onFinish() {
                        ConnectivityManager manager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                        boolean stat=manager.getActiveNetworkInfo().isConnected();
                        if(!stat)
                            start();
                        else
                        {
                            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
                            ArrayList<Double> lats=new ArrayList<>(),longs=new ArrayList<>();
                            for(LatLng cor:adminPath)
                            {
                                lats.add(cor.latitude);
                                longs.add(cor.longitude);
                            }
                            groupSelected.pathLats=lats;
                            groupSelected.pathLongs=longs;
                            Log.i("No Of Points",String.valueOf(adminPath.size()));
                            reference.child(String.valueOf(groupSelected.getGid())).setValue(groupSelected);
                            start();

                        }
                    }

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }
                };
                timer1.start();

            }
            else
            {
                polyOpt.addAll(adminPath);
                pathLine.setPoints(adminPath);
                timer1=new CountDownTimer(4000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        ArrayList<LatLng> temp=new ArrayList<>();
                        if(groupSelected.pathLats==null)
                        {
                            start();
                            return;
                        }
                        for(int i=0;i<groupSelected.pathLats.size();i++)
                            temp.add(new LatLng(groupSelected.pathLats.get(i),groupSelected.pathLongs.get(i)));
                        adminPath=temp;
                        polyOpt.addAll(adminPath);
                        pathLine.setPoints(adminPath);
                        start();
                    }
                };
            }
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    linePlot=!linePlot;
                    if(linePlot)
                        pathLine.setVisible(true);
                    else pathLine.setVisible(false);

                    mMap.addPolyline(new PolylineOptions().add(new LatLng(19.121,72.890),new LatLng(19.131,72.830)));

                    Log.i("mapsStatus-",String.valueOf(linePlot));
                }
            });*/

        }





    }

    @Override
    public void onBackPressed() {

        /*timer1.cancel();
        timer2.cancel();
        mapUpdateManager.removeUpdates(mapUpdateListener);
        mapUpdateManager.removeUpdates(mapUpdateListener1);*/

        timer2.cancel();
        timer1.cancel();
        mapUpdateManager.removeUpdates(mapUpdateListener);

        super.onBackPressed();


    }
}
