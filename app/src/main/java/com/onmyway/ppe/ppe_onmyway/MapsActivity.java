package com.training.jeremy_pc.mapway;

import android.Manifest;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.View;
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

public class MapsActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback  {

    private View mLayout;

    //map parameter
    private GoogleMap mMap;

    //current Location of the user
    private Location currentLocation;
    //LocationManager
    private LocationManager locationManager;

    //successive position put by the user
    private List<MarkerOptions> checkPoint;

    // asking for the localisation of the user
    private static final int LOCATION_REQUEST = 500;

    //List of successive checkPoint
    private List<LatLng> checkPointList;

    //previous and new point
    private LatLng origin;
    private LatLng destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initialisation of the checkPoint table and of the chekpointList
        checkPoint = new ArrayList<>();
        checkPointList = new ArrayList<>();

        // initialisation of the LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }

        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        //initialisation of the update
        Intent intent = new Intent(this, GPSUpdateReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


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

    public class GPSUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = (Location)intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
        }
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // enable the zoom functionality
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getMaxZoomLevel();

        //for the initialisation of the map
        if(currentLocation !=null){
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            // Zoom on the current position of the user
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            mMap.animateCamera(cameraUpdate);
        }


        // Current location
        LatLng currentLatLong = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        //MarkerOptions markerUserPosition = new MarkerOptions();
        //markerUserPosition.position(currentLatLong);
        //marker that we can modify with the profil picture maybe
        //markerUserPosition.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLong));
        //mMap.addMarker(markerUserPosition);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                //Reset the marker when already 2
                //mMap.clear();

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                System.out.println("1");

                boolean find = false;
                // If the List of marker is empty
                if(checkPoint.size()==0){
                    //if tables are empty
                    checkPoint.add(markerOptions);
                    checkPointList.add(latLng);
                }else{
                    for(int i=0; i < checkPoint.size(); i++){
                        System.out.println("2");
                        if (checkPoint.get(i).getPosition().equals(latLng)){
                            System.out.println("click on a exiting coord");
                            checkPoint.remove(i);
                            find = true;
                        }
                    }
                    if (find==false){

                        // add the position selected to the List of marker
                        checkPoint.add(markerOptions);
                        //add the chekPoint to the list of checkPoint
                        checkPointList.add(latLng);

                    }
                }

                System.out.println("3");

                // it put a red marker on the map
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                // We remove all previous maker and we add the new one
                mMap.clear();
                for(int i = 0; i<checkPoint.size();i++){
                    mMap.addMarker(checkPoint.get(i));
                }

                System.out.println("4");

                 if(checkPointList.size() >= 2){
                     System.out.println("5");
                     for (int i = 1; i < checkPointList.size(); i++){
                         origin = checkPointList.get(i-1);
                         destination = checkPointList.get(i);
                         mMap.addPolyline(new PolylineOptions().add(
                                 origin,
                                 destination
                         ));
                     }

                }
                System.out.println("6");

                //add to the list checkPointList

            }
        });
        //markerUserPosition.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));


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
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
