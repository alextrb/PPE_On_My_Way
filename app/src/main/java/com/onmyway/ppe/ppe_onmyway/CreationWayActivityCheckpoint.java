package com.onmyway.ppe.ppe_onmyway;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class CreationWayActivityCheckpoint extends AppCompatActivity implements LocationListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback{

    private EditText EditTextNameCheckpoint;
    private EditText EditTextdescription;
    private Button buttonValidDescWay;

    // The itineraire
    private List<LatLng> wayList;
    //checkPoints
    private List<MarkerOptions> checkPointList;

    //descriptionCheckPoint
    private List<CheckPoint> checkPointListDesc;

    private String name_way;
    private int position;

    // asking for the localisation of the user
    private static final int LOCATION_REQUEST = 500;

    //LocationManager
    private LocationManager locationManager;

    //current Location of the user
    private Location currentLocation;

    //map parameter
    private GoogleMap mMap;

    //previous and new point
    private LatLng origin;
    private LatLng destination;

    private MarkerOptions checkpointPosition;

    private int currentIdUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_way_checkpoint);

        EditTextNameCheckpoint = (EditText) findViewById(R.id.EditTextNameCheckpoint);
        EditTextdescription = (EditText) findViewById(R.id.EditTextdescription);
        buttonValidDescWay = (Button) findViewById(R.id.buttonValidDescWay);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        //Initisation of the checkPointList
        checkPointListDesc = new ArrayList<>();

        // we retrieve the intent
        /*
        intent.putParcelableArrayListExtra("EXTRA_LIST_CHECKPOINT", (ArrayList<? extends Parcelable>) checkPointList);
        intent.putParcelableArrayListExtra("EXTRA_LIST_ITINERAIRE", (ArrayList<? extends Parcelable>) wayList);
        intent.putExtra("EXTRA_POSITION_CHEKPOINT",position);
         */

        final Intent intent = getIntent();
        String result;
        Bundle bd = intent.getExtras();
        if(bd!=null){

            wayList = intent.getParcelableArrayListExtra("EXTRA_LIST_ITINERAIRE");
            checkPointList = intent.getParcelableArrayListExtra("EXTRA_LIST_CHECKPOINT");
            name_way = intent.getStringExtra("EXTRA_NAME_WAY");
            position = intent.getIntExtra("EXTRA_POSITION_CHEKPOINT",0);
            int size = intent.getIntExtra("SIZE_LIST_CHECK", 0);

            currentIdUser = intent.getIntExtra("CURRENT_ID_USER",-1);
            // add a condition in the case that we were in the activity of description of the activity
            if(currentIdUser == -1){
                Intent intent2 = new Intent(this,LoginActivity.class);
                startActivity(intent2);
            }

            System.out.println(" wayList " + wayList);
            System.out.println(" checkPointList " + checkPointList);
            System.out.println(" name_way " + name_way);
            System.out.println("position " + position);
            System.out.println(" size " + size);
            for(int i = 0; i< size; i++){
                String name = intent.getStringExtra("NAME"+i);
                String desc = intent.getStringExtra("DESCRIPTION"+i);
                int id = intent.getIntExtra("ID_CHECK"+i,0);
                System.out.println(" name " + name);
                System.out.println(" desc " + desc);
                System.out.println(" id " + id);
                checkPointListDesc.add(new CheckPoint(name,desc, id));


            }


            // add a condition in the case that we were in the activity of description of the activity

        }else{
            System.out.println("error in the retrieving of the intent");
            return ;
        }

        for(int i = 0; i< checkPointList.size(); i++){
            if(i==position){
                checkpointPosition = checkPointList.get(i);
            }

        }



        // initialisation of the LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);



    }


    public void validation(View view) {

        if(EditTextNameCheckpoint.getText().toString().equals("") && EditTextdescription.getText().toString().equals("") ){
            Toast.makeText(this, "Please enter a name and a description!", Toast.LENGTH_SHORT).show();
        }else{

            System.out.println("PART WHICH IS SEND from actvity checkpoint!!!!!!!!!!!!!");
            System.out.println("name_way " + name_way);
            System.out.println("position " + position);
            System.out.println("checkPointListDesc.size() "+ checkPointListDesc.size());


            Intent intent = new Intent(this, CreationWayActivityStep3.class);
            //wayList
            intent.putParcelableArrayListExtra("EXTRA_LIST_CHECKPOINT", (ArrayList<? extends Parcelable>) checkPointList);
            intent.putParcelableArrayListExtra("EXTRA_LIST_ITINERAIRE", (ArrayList<? extends Parcelable>) wayList);
            intent.putExtra("EXTRA_NAME_WAY",name_way);
            intent.putExtra("EXTRA_POSITION_CHEKPOINT",position);
            intent.putExtra("SIZE_LIST_CHECK", checkPointListDesc.size());
            intent.putExtra("CURRENT_ID_USER",currentIdUser);

            for(int i = 0;i<checkPointListDesc.size(); i++ ){

                if(i==position){
                    System.out.println("EditTextNameCheckpoint.getText().toString() "+ EditTextNameCheckpoint.getText().toString());
                    System.out.println("EditTextdescription.getText().toString() " + EditTextdescription.getText().toString());
                    System.out.println("checkPointListDesc.get(i).getId()" +  checkPointListDesc.get(i).getId());
                    intent.putExtra("NAME"+i, EditTextNameCheckpoint.getText().toString());
                    intent.putExtra("DESCRIPTION"+i,  EditTextdescription.getText().toString());
                    intent.putExtra("ID_CHECK"+i, checkPointListDesc.get(i).getId());
                }else{
                    System.out.println("EditTextNameCheckpoint.getText().toString() "+ EditTextNameCheckpoint.getText().toString());
                    System.out.println("EditTextdescription.getText().toString() " + EditTextdescription.getText().toString());
                    System.out.println("checkPointListDesc.get(i).getId()" +  checkPointListDesc.get(i).getId());
                    intent.putExtra("NAME"+i,checkPointListDesc.get(i).getName());
                    intent.putExtra("DESCRIPTION"+i,checkPointListDesc.get(i).getDescription());
                    intent.putExtra("ID_CHECK"+i, checkPointListDesc.get(i).getId());
                }

            }

            startActivity(intent);

        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MarkerOptions markerOptions = new MarkerOptions();

        System.out.println("debut du onMapReady");

        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        System.out.println("2 onmapReady2");
        mMap.getMaxZoomLevel();

        System.out.println("just before the camera update");
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(checkpointPosition.getPosition(), 16);
        System.out.println("just after the camera update");
        mMap.animateCamera(cameraUpdate);

        if(wayList.size() >= 2){
            System.out.println("5");
            for (int i = 1; i < wayList.size(); i++){
                origin = wayList.get(i-1);
                destination = wayList.get(i);
                mMap.addPolyline(new PolylineOptions().add(
                        origin,
                        destination
                ).color(Color.argb(255,255,153,51))
                        .width(15));
            }

        }

        for (int i =0; i< checkPointList.size();i++) {
            if(i == position){
                MarkerOptions markerOptions1 = new MarkerOptions();
                LatLng latLng = new LatLng(checkPointList.get(i).getPosition().latitude,checkPointList.get(i).getPosition().longitude);
                markerOptions1.position(latLng);
                markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.addMarker(markerOptions1);
            }else{
                MarkerOptions markerOptions2 = new MarkerOptions();
                LatLng latLng = new LatLng(checkPointList.get(i).getPosition().latitude,checkPointList.get(i).getPosition().longitude);
                markerOptions2.position(latLng);
                markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                mMap.addMarker(markerOptions2);
            }

        }

    }



    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enable new provider" + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "disable new provider" + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1, this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {

        System.out.println("DEBUT DU ON PERMISSION");
        if (requestCode== LOCATION_REQUEST){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // NOW WE HAVE TO LAUNCH THE ACTIVITY
                startApp();

            }else{
                Toast.makeText(getApplicationContext(), "No localisation permission", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void startApp(){
        Intent intent = new Intent(this, CreationWayActivityCheckpoint.class);
        startActivity(intent);
    }


}
