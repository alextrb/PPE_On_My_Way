package com.onmyway.ppe.ppe_onmyway;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class CheckPointModifActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback  {

    private EditText textViewNameCheckpoint;
    private EditText TVdescription;
    private Button modification;

    //provider
    private LocationManager locationManager;

    //DATABASE SQLLITE
    protected MyOpenDatabase myOpenDatabase = null;
    protected SQLiteDatabase myDB = null;

    //previous and new point
    private LatLng origin;
    private LatLng destination;

    // asking for the localisation of the user
    private static final int LOCATION_REQUEST = 500;

    private GoogleMap mMap;

    private Context mListener;

    private int IDcheckpoint;

    private LatLng latLng;

    private List<LatLng> latLngList;
    private List<MarkerOptions> checkPointList;

    private int currentIdUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_point_modif);
        System.out.print("0");

        latLngList = new ArrayList<>();
        checkPointList = new ArrayList<>();

        textViewNameCheckpoint = (EditText) findViewById(R.id.textViewNameCheckpoint);
        TVdescription = (EditText) findViewById(R.id.TVdescription);
        modification = (Button) findViewById(R.id.modification);

        //Intialisation of the database
        mListener = getApplicationContext();
        myOpenDatabase = new MyOpenDatabase(mListener);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //VERFICATION OF THE PERMISSION! IF NO PERMISSION, THE ACTIVITY IS NOT LAUNCH
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }

        //initialisation of the map
        if (mapFragment != null) {
            System.out.println("mapFrag not null");
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    mMap = map;
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

        // WE retrieve the Intent
        final Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd!=null){
            IDcheckpoint = intent.getIntExtra("ID_CHECKPOINT",0);
            currentIdUser = intent.getIntExtra("CURRENT_ID_USER",-1);
            // add a condition in the case that we were in the activity of description of the activity
            if(currentIdUser == -1){
                Intent intent2 = new Intent(this,LoginActivity.class);
                startActivity(intent2);
            }
        }else{
            System.out.println("error in the retrieving of the intent");
            return ;
        }

        myDB = myOpenDatabase.getReadableDatabase();

        Cursor result = myDB.rawQuery("SELECT * FROM checkpoint WHERE id = "+ IDcheckpoint ,null);
        System.out.println("query for checkpoints2");
        result.moveToFirst();

        CheckPoint checkPoint = null;
        /*
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nameway VARCHAR, " +
                    "latitude VARCHAR, " +
                    "longitude VARCHAR, " +
                    "namecheckpoint VARCHAR, " +
                    "description VARCHAR);";
         */
        String nameWay = "";
        while(!result.isAfterLast()){

            nameWay = result.getString(1);

            checkPoint = new CheckPoint(result.getString(4),result.getString(5),result.getInt(0));

            latLng = new LatLng(Double.parseDouble(result.getString(2)),Double.parseDouble(result.getString(3)));

            result.moveToNext();
        }
        result.close();

        Cursor result2 = myDB.rawQuery("SELECT * FROM itineraire WHERE nameway = '"+nameWay+"'",null);
        result2.moveToFirst();

        System.out.println("query  itineraire2");

        while(!result2.isAfterLast()){
            LatLng latLng = new LatLng(Double.parseDouble(result2.getString(2)),Double.parseDouble(result2.getString(3)));
            latLngList.add(latLng);
            result2.moveToNext();
        }

        result2.close();

        Cursor result3 = myDB.rawQuery("SELECT * FROM checkpoint WHERE nameway = '"+nameWay+"'",null);
        System.out.println("query for checkpoints2");
        result3.moveToFirst();
        while(!result3.isAfterLast()){

            // for the map
            System.out.println("query for checkpoints4");
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            LatLng latLng = new LatLng(Double.parseDouble(result3.getString(2)),Double.parseDouble(result3.getString(3)));
            System.out.println("query for checkpoints6");
            markerOptions.position(latLng);
            System.out.println("query for checkpoints7");
            checkPointList.add(markerOptions);
            System.out.println("query for checkpoints8");
            result3.moveToNext();
        }
        System.out.println(" fin query for checkpoints");

        result3.close();
        myDB.close();

        // We modify the view
        textViewNameCheckpoint.setText(checkPoint.getName());
        TVdescription.setText(checkPoint.getDescription());
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
        Intent intent = new Intent(this, Checkpoint_Activity.class);
        startActivity(intent);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Zoom on the current position of the user
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        mMap.animateCamera(cameraUpdate);

        if(latLngList.size() >= 2){
            System.out.println("5");
            for (int i = 1; i < latLngList.size(); i++){
                origin = latLngList.get(i-1);
                destination = latLngList.get(i);
                mMap.addPolyline(new PolylineOptions().add(
                        origin,
                        destination
                ).color(Color.argb(255,255,153,51))
                        .width(15));
            }

        }

        for (int i =0; i< checkPointList.size();i++) {
            mMap.addMarker(checkPointList.get(i));
        }

    }

    public void modificationChekpoint(View view) {

        myDB = myOpenDatabase.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("namecheckpoint", textViewNameCheckpoint.getText().toString());
        cv.put("description", TVdescription.getText().toString());
        myDB.update("checkpoint", cv, "id="+IDcheckpoint, null);

        Intent intent = new Intent(this, CheckPointModifActivity.class);
        //wayList
        intent.putExtra("ID_CHECKPOINT",IDcheckpoint);
        intent.putExtra("CURRENT_ID_USER",currentIdUser);
        startActivity(intent);

    }
}
