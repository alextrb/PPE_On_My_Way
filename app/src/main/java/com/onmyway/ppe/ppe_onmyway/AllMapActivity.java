package com.onmyway.ppe.ppe_onmyway;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class AllMapActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback  {

    // asking for the localisation of the user
    private static final int LOCATION_REQUEST = 500;

    //DATABASE SQLLITE
    protected MyOpenDatabase myOpenDatabase = null;
    protected SQLiteDatabase myDB = null;

    // context of the activity
    private Context mListener;

    //LocationManager
    private LocationManager locationManager;

    //current Location of the user
    private Location currentLocation;

    //map parameter
    private GoogleMap mMap;

    private int currentIdUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);


        //Intialisation of the database
        mListener = getApplicationContext();
        myOpenDatabase = new MyOpenDatabase(mListener);

        final Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd!=null){

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

        // initialisation of the LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


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




    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        System.out.println("In the onMapReady");

        // enable the zoom functionality
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getMaxZoomLevel();

        //for the initialisation of the map
        if(currentLocation !=null){
            System.out.println("current not null");
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            // Zoom on the current position of the user
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            mMap.animateCamera(cameraUpdate);
        }

        // lister tous les way et les afficher ensuite sur la map !!

        // we looked in the databae each way



        // for each way we retrieve the



    }


    public class GPSUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = (Location)intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
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

    // PART FOR THE PERMISSION OF THE USER

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
        Intent intent = new Intent(this, AllMapActivity.class);
        startActivity(intent);
    }

}
