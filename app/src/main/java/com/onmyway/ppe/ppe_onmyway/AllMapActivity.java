package com.onmyway.ppe.ppe_onmyway;

import android.Manifest;
import android.content.BroadcastReceiver;
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

    private List<Way> listWay;

    //previous and new point
    private LatLng origin;
    private LatLng destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        //initialisation of the list!
        listWay = new ArrayList<>();

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
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
            mMap.animateCamera(cameraUpdate);
        }
        System.out.println("BEFORE SELECTION DATABASE");

        // lister tous les way et les afficher ensuite sur la map !!

        // We retrieve all ways and we stored them in a list
        myDB = myOpenDatabase.getReadableDatabase();
        String [] columns = {"id","nameway","noteway","iduser"};
        Cursor result = myDB.query("way",columns,null,null,null,null,null);
        result.moveToFirst();
        while(!result.isAfterLast()){
            Way way = new Way(result.getInt(0),result.getString(1),result.getInt(2),result.getInt(3));
            listWay.add(way);
            result.moveToNext();
        }
        result.close();
        System.out.println("AFTER FIRST SELECTION DATABASE");

        // for each way we retrieve the list of coord
        String [] columns2 = {"id","nameway","latitude","longitude"};
        Cursor result2 = myDB.query("itineraire",columns2,null,null,null,null,null);
        result2.moveToFirst();
        while(!result2.isAfterLast()){

            for(int i =0; i<listWay.size();i++){
                if(result2.getString(1).equals(listWay.get(i).getNameway())){
                    System.out.println("BEFORE THE PARSE");
                    LatLng latLng = new LatLng(Double.parseDouble(result2.getString(2)), Double.parseDouble(result2.getString(3)));
                    System.out.println("ADD TO THE LIST");
                    listWay.get(i).addToList(latLng);
                    System.out.println("after the parse");
                }
            }
            result2.moveToNext();
        }
        result2.close();
        System.out.println("AFTER SECOND SELECTION DATABASE");

        String [] columns3 = {"id","nameway","latitude","longitude","namecheckpoint","description"};
        Cursor result3 = myDB.query("checkpoint",columns3,null,null,null,null,null);

        result3.moveToFirst();
        while(!result3.isAfterLast()){

            for(int i=0; i<listWay.size();i++){
                if(result3.getString(1).equals(listWay.get(i).getNameway())){
                    System.out.println("BEFORE THE PARSE");
                    LatLng latLng = new LatLng(Double.parseDouble(result3.getString(2)), Double.parseDouble(result3.getString(3)));
                    System.out.println("ADD TO THE LIST");
                    listWay.get(i).addToListCheck(latLng);
                    System.out.println("after the parse");
                }
            }
            result3.moveToNext();
        }
        result3.close();
        System.out.println("AFTER Third SELECTION DATABASE");


        // we displayed now on the map all ways
        for(int i=0; i<listWay.size();i++){

            for(int j=1; j<listWay.get(i).getListCoord().size();j++){
                System.out.println("1");
                origin = listWay.get(i).getListCoord().get(j-1);
                destination = listWay.get(i).getListCoord().get(j);
                mMap.addPolyline(new PolylineOptions().add(
                        origin,
                        destination
                ).color(Color.argb(255,255,153,51))
                        .width(15));
            }

        }

        //Positioner les checkpoint sur la map !
        for(int i=0; i<listWay.size();i++){

            for(int j=1; j<listWay.get(i).getListCheck().size();j++){
                System.out.println("1");
                MarkerOptions markerOptions2 = new MarkerOptions();
                markerOptions2.position(listWay.get(i).getListCheck().get(j));
                markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                mMap.addMarker(markerOptions2);
            }

        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                double latitudeClick = latLng.latitude;
                double longitudeclick = latLng.longitude;
                double lat = latitudeClick;
                String lat2 = String.format("%.3f", lat);
                double lng = longitudeclick;
                String lng2 = String.format("%.3f", lng);
                double latfinal = Double.parseDouble(lat2.replace(',','.'));
                double lngfinal = Double.parseDouble(lng2.replace(',','.'));

                System.out.println("yo1" + latfinal);
                System.out.println("yo2 " +lngfinal);

                for(int i=0; i<listWay.size();i++) {

                    for (int j = 1; j < listWay.get(i).getListCoord().size(); j++) {

                        System.out.println("1");
                        // we looked in the itineraire list
                        String latStr = String.format("%.3f", listWay.get(i).getListCoord().get(j).latitude);
                        String lngStr = String.format("%.3f", listWay.get(i).getListCoord().get(j).longitude);
                        double latfinal2 = Double.parseDouble(latStr.replace(',', '.'));
                        double lngfinal2 = Double.parseDouble(lngStr.replace(',', '.'));

                        System.out.println("2");
                        if (latfinal == latfinal2 && lngfinal == lngfinal2) {
                            System.out.print("way trouvé! ");
                        }
                        System.out.println("3");

                    }
                }

                for(int i=0; i<listWay.size();i++) {
                    // we looked in the checkpoint list
                    for (int j = 0; j < listWay.get(i).getListCheck().size(); j++) {
                        String latStr2 = String.format("%.3f", listWay.get(i).getListCheck().get(j).latitude);
                        String lngStr2 = String.format("%.3f", listWay.get(i).getListCheck().get(j).longitude);
                        double latfinal3 = Double.parseDouble(latStr2.replace(',', '.'));
                        double lngfinal3 = Double.parseDouble(lngStr2.replace(',', '.'));

                        System.out.println("4");
                        if (latfinal == latfinal3 && lngfinal == lngfinal3) {
                            System.out.print("way trouvé! ");

                            String nameWay = listWay.get(i).getNameway();

                            int wayID = -1;
                            Cursor result2 = myDB.rawQuery("SELECT * FROM way WHERE nameway = '"+nameWay+"'",null);
                            result2.moveToFirst();

                            System.out.println("query  itineraire2");

                            while(!result2.isAfterLast()){
                                wayID = result2.getInt(0);
                                result2.moveToNext();
                            }

                            result2.close();
                            // redirection to the wayActivity with the currentIdUser and WayID
                            System.out.print("REDIRECTION");
                            redirection(wayID);


                        }
                    }
                }
            }


        });


                System.out.println("END ON MAP READY");


    }

    public void redirection(int wayID){

        Intent intent = new Intent(this, WayActivity.class);
        //wayList
        intent.putExtra("ID_WAY",wayID);
        intent.putExtra("CURRENT_ID_USER",currentIdUser);
        startActivity(intent);

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
