package com.onmyway.ppe.ppe_onmyway;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CreationWayActivityStep2 extends AppCompatActivity implements LocationListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback{

    //Intent
    Intent intent;

    private List<LatLng> wayList;

    private View mLayout;

    //map parameter
    private GoogleMap mMap;

    //current Location of the user
    private Location currentLocation;

    //LocationManager
    private LocationManager locationManager;

    //successive position put by the user
    private List<MarkerOptions> checkPointList;

    // asking for the localisation of the user
    private static final int LOCATION_REQUEST = 500;

    //previous and new point
    private LatLng origin;
    private LatLng destination;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_way_step2);
        System.out.println("debut du onCreate");

        //initialisation of the List
        wayList = new ArrayList<>();
        checkPointList = new ArrayList<>();

        //We retrieve the intent
        final Intent intent = getIntent();
        String result;
        Bundle bd = intent.getExtras();
        if(bd!=null){
            wayList = intent.getParcelableArrayListExtra("EXTRA_LIST");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        // initialisation of the LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        //initialisation of the update
        Intent intent2 = new Intent(this, MapsActivity.GPSUpdateReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);


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
        System.out.println("fin du onCreate");

    }


    public void delete(View view) {
        //suppression of the
        if (checkPointList.size()>=1){
            checkPointList.remove(checkPointList.size()-1);
        }
        //clean the current map
        mMap.clear();
        //create new marker
        for(int i = 0; i<checkPointList.size();i++){
            mMap.addMarker(checkPointList.get(i));

        }

        if(wayList.size() >= 2){
            System.out.println("5");

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

        }
    }

    public void next(View view) {



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
        System.out.println("debut du onMapReady");

        mMap = googleMap;

        // enable the zoom functionality
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getMaxZoomLevel();

        //for the initialisation of the map
        if(wayList !=null){

            // Zoom on the position of the way
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(wayList.get(0), 17);
            mMap.animateCamera(cameraUpdate);
        }

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

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                boolean find = false;
                // If the List of marker is empty
                if(checkPointList.size()==0){
                    //if tables are empty
                    checkPointList.add(markerOptions);

                }else{
                    for(int i=0; i < checkPointList.size(); i++){

                        System.out.println("checkPointList.get(i) " + checkPointList.get(i));
                        System.out.println("latLng " + latLng);

                        // a refaire pour enlever le bouton delete
                        if (checkPointList.get(i).getPosition().equals(latLng)){
                            System.out.println("click on a exiting coord");
                            checkPointList.remove(i);
                            find = true;
                        }
                    }
                    if (find==false){
                        //add the chekPoint to the list of checkPoint
                        checkPointList.add(markerOptions);

                    }
                }

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                mMap.clear();
                for(int i = 0; i<checkPointList.size();i++){
                    mMap.addMarker(checkPointList.get(i));

                }

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



            }
        });
        System.out.println("fin du onMapReady");
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
        Intent intent = new Intent(this, CreationWayActivity.class);
        startActivity(intent);
    }



    ///////////////////////////////Thread ///////////////////////////////////////
    //to don't block th main thread
/*
    private class LoadAllMatch extends AsyncTask<String, String, String> {

        private Context context;

        private String result;

        private static final String urlListMatch = "http://192.168.1.18/listMatch.php";

        LoadAllMatch(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            System.out.println("oncreate 1");
            URL url = null;

            try {
                url = new URL(urlListMatch);

                System.out.println("oncreate 1");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                System.out.println("oncreate 1");
                httpURLConnection.setRequestMethod("POST");
                System.out.println("oncreate 1");
                httpURLConnection.setDoOutput(true);
                System.out.println("oncreate 1");
                httpURLConnection.setDoInput(true);
                System.out.println("oncreate ici");

                InputStream inputStream = httpURLConnection.getInputStream();
                System.out.println("oncreate 1");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                result = "";
                String line;
                System.out.println("oncreate 2");

                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println("oncreate 3 boucle");
                    result += line;
                }
                System.out.println("oncreate 4");
                bufferedReader.close();
                System.out.println("oncreate 5");
                httpURLConnection.disconnect();
                System.out.println(result);
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            result = loadAllMatch.getResult();

            //Split the result received in JSON String Object
            String[] parts = result.split(Pattern.quote("}"));
            String part1 = parts[0]+"}"; // 004
            String part2 = parts[1]+"}"; // 034556
            System.out.println(part1);
            System.out.println(part2);

            JSONArray jsonArray2 = new JSONArray();
            for(int i =0;i<parts.length;i++){
                parts[i]+="}";
                jsonArray2.put(parts[i]);
            }
            System.out.println(jsonArray2);
            try {

                for (int i=0;i<jsonArray2.length();i++){
                    System.out.println("INDEX " +jsonArray2.getString(i));
                    JSONObject obj = new JSONObject(jsonArray2.getString(i));
                    //JSONObject row = array.getJSONObject(i);
                    Match match = new Match(obj.getString("joueur1"),obj.getString("joueur2"),obj.getString("lat_location"),
                            obj.getString("lng_location"),obj.getString("club_name"),
                            obj.getString("date"),obj.getString("score_joueur1"),obj.getString("score_joueur2"),
                            obj.getString("duration"),obj.getInt("nb_ace_joueur1"),obj.getInt("nb_ace_joueur2"),
                            obj.getInt("nb_double_fault_joueur1"),obj.getInt("nb_double_fault_joueur2"),
                            obj.getInt("nb_win_point_joueur1"),obj.getInt("nb_win_point_joueur2"),
                            obj.getInt("nb_fault_joueur1"),obj.getInt("nb_fault_joueur2"));
                    matchList.add(match);

                }
                for(int i =0; i<matchList.size();i++){
                    System.out.println(matchList.get(i).getClubName());

                }
                customAdapter.notifyDataSetChanged();





            } catch (JSONException e) {
                e.printStackTrace();
            }




        }
*/
}
