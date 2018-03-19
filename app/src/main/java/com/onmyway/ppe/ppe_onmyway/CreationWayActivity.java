package com.onmyway.ppe.ppe_onmyway;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CreationWayActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback{

    //For the Intent
    //public final static List<LatLng> EXTRA_LIST =null;

    private View mLayout;

    //map parameter
    private GoogleMap mMap;

    //current Location of the user
    private Location currentLocation;
    //LocationManager
    private LocationManager locationManager;

    //successive position put by the user
    private List<MarkerOptions> markerPositionList;

    // asking for the localisation of the user
    private static final int LOCATION_REQUEST = 500;

    //List of successive checkPoint
    private List<LatLng> checkPointList;

    //previous and new point
    private LatLng origin;
    private LatLng destination;

    //Coordinate click
    private double longitudeclick;
    private double latitudeClick;

    //EditText
    private EditText editText1;
    private Button buttonNext;
    private Button buttonDelete;
    private Button searchAddressButton;

    //geocoder
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_way);

        //initialisation of View content
        buttonNext = (Button) findViewById(R.id.buttonSuivantCreation1);
        buttonDelete = (Button) findViewById(R.id.buttonSuivantCreation1);
        editText1 = (EditText) findViewById(R.id.editTextAddress);
        searchAddressButton = (Button) findViewById(R.id.searchAddressButton);


        ///////////////////////////////////////MAP PART/////////////////////////////////

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initialisation of the checkPoint table and of the chekpointList
        markerPositionList = new ArrayList<>();
        checkPointList = new ArrayList<>();
        System.out.println("hey2");
        // initialisation of the LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //VERFICATION OF THE PERMISSION! IF NO PERMISSION, THE ACTIVITY IS NOT LAUNCH
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }

        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        //initialisation of the update
        Intent intent = new Intent(this, MapsActivity.GPSUpdateReceiver.class);
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

    ///////////////////////////////////////////FUNCTION IN THE VIEW/////////////////////////////////////
    public void delete(View view) {

        //suppression of the
        if (markerPositionList.size()>=1){
            markerPositionList.remove(markerPositionList.size()-1);
            checkPointList.remove(checkPointList.size()-1);
        }
        //clean the current map
        mMap.clear();
        //create new marker
        for(int i = 0; i<checkPointList.size();i++){
            mMap.addMarker(markerPositionList.get(i));

        }

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

    }

    public void next(View view) {

        if(checkPointList.size()>=2){
            System.out.println("1 intent");
            Intent intent = new Intent(this,CreationWayActivityStep2.class);
            System.out.println("2 intent");
            //intent.putExtra("EXTRA_SIZE", checkPointList.size());
            intent.putParcelableArrayListExtra("EXTRA_LIST", (ArrayList<? extends Parcelable>) checkPointList);
            System.out.println("3 intent");
            startActivity(intent);
        }

    }

    public void searchCoord(View view) {

        if(!editText1.equals("")){
            new GetCoordinate().execute();
        }

    }

    /////////////////////////////////////////END FUNCTION VIEW////////////////////////////////////////////

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

                longitudeclick = latLng.latitude;
                latitudeClick =latLng.longitude;

                //Part for the set text
                new GetAddress().execute(String.format("%.4f,%.4f",latitudeClick,longitudeclick));

                System.out.println("latLng.latitude" + latitudeClick);
                System.out.println("latLng.lng" + longitudeclick);

                //Reset the marker when already 2
                //mMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                System.out.println("1");

                boolean find = false;
                // If the List of marker is empty
                if(checkPointList.size()==0){
                    //if tables are empty
                    checkPointList.add(latLng);
                    markerPositionList.add(markerOptions);

                }else{
                    for(int i=0; i < checkPointList.size(); i++){
                        System.out.println("2");

                        System.out.println("checkPointList.get(i) " + checkPointList.get(i));
                        System.out.println("latLng " + latLng);

                        // a refaire pour enlever le bouton delete
                        if (checkPointList.get(i).equals(latLng)){
                            System.out.println("click on a exiting coord");
                            checkPointList.remove(i);
                            markerPositionList.remove(i);
                            find = true;
                        }
                    }
                    if (find==false){
                        //add the chekPoint to the list of checkPoint
                        checkPointList.add(latLng);
                        markerPositionList.add(markerOptions);


                    }
                }

                System.out.println("3");

                // it put a red marker on the map
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                // We remove all previous maker and we add the new one
                mMap.clear();
                for(int i = 0; i<checkPointList.size();i++){
                    mMap.addMarker(markerPositionList.get(i));

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

    // TO RETRIEVE THE ADDRESS WHEN WE CLICK ON THE MAP

    private class GetAddress extends AsyncTask<String,Void,String> {
        ProgressDialog dialog = new ProgressDialog(CreationWayActivity.this);

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dialog.setMessage("Please wait ....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response;
            try{

                double lat = latitudeClick;
                String lat2 = String.format("%.4f", lat);
                double lng = longitudeclick;
                String lng2 = String.format("%.4f", lng);
                double latfinal = Double.parseDouble(lat2.replace(',','.'));
                double lngfinal = Double.parseDouble(lng2.replace(',','.'));

                System.out.println("yo1" + latfinal);
                System.out.println("yo2 " +lngfinal);


                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng="+lngfinal+","+latfinal+"&sensor=false");
                //String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=37.7416,-122.4796";
                System.out.println("URL:" + url);
                response = http.GetHTTPData(url);
                return response;
            }catch (Exception e){
                //
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);
                String address = ((JSONArray) jsonObject.get("results")).getJSONObject(0).get("formatted_address").toString();
                editText1.setText(address);
                System.out.println("RESULT =" + address);
            }catch (JSONException e){
                e.printStackTrace();
            }
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }

    // TO RETRIEVE THE COORDINATE WHEN WE ENTER AN ADDRESS IN THE EDITTEXT

    private class GetCoordinate extends AsyncTask<String,Void,String> {
        ProgressDialog dialog = new ProgressDialog(CreationWayActivity.this);

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dialog.setMessage("Please wait ....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response;
            try{
                String address = editText1.getText().toString();

                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address="+ URLEncoder.encode(address)+"&sensor=false");
                System.out.println("URL:" + url);
                response = http.GetHTTPData(url);
                System.out.println("response = " + response);
                return response;
            }catch (Exception e){
                //
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);
                String latitudeSearch = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString();
                String longitudeSearch = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString();

                System.out.println("latitude : " + latitudeSearch);
                System.out.println("longitude : " + longitudeSearch);

                double lat = Double.parseDouble(latitudeSearch);
                double lng = Double.parseDouble(longitudeSearch);

                //update of the camera
                LatLng coordinateLatLng = new LatLng(lat,lng);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinateLatLng, 17);
                mMap.animateCamera(cameraUpdate);

            }catch (JSONException e){
                e.printStackTrace();
            }
            if(dialog.isShowing()){
                dialog.dismiss();
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
        Intent intent = new Intent(this, CreationWayActivity.class);
        startActivity(intent);
    }




}
