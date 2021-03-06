package com.onmyway.ppe.ppe_onmyway;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WayActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private String[] ids;
    private String[] lats;
    private String[] lngs;

    //DATABASE SQLLITE
    protected MyOpenDatabase myOpenDatabase = null;
    protected SQLiteDatabase myDB = null;

    //provider
    private LocationManager locationManager;

    // asking for the localisation of the user
    private static final int LOCATION_REQUEST = 500;

    private GoogleMap mMap;

    private Context mListener;

    private int currentIdUser;

    //List of the Latlng for the itineraire
    List<LatLng> latLngList;

    //List of checkpoint
    List<MarkerOptions> checkPointList;

    //previous and new point
    private LatLng origin;
    private LatLng destination;

    // TextVizw
    private TextView EditTextNameWay;
    private ImageView imageViewNote;
    private ListView listCheckPoint;

    private TextView TVnameCheckpoint;

    CustomAdapter customAdapter;

    private List<CheckPoint> listObjectCheckpoint;

    private int wayID=0;
    private String wayName = "noName";

    private int checkpointID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_way);

        //intialisation of textView and imageView
        EditTextNameWay = (TextView) findViewById(R.id.EditTextNameWay);
        imageViewNote = (ImageView) findViewById(R.id.imageViewNote);
        listCheckPoint = (ListView) findViewById(R.id.listCheckPoint);

        //Intialisation of the database
        mListener = getApplicationContext();
        myOpenDatabase = new MyOpenDatabase(mListener);

        latLngList = new ArrayList<>();
        checkPointList = new ArrayList<>();
        listObjectCheckpoint = new ArrayList<>();

        customAdapter = new CustomAdapter();
        listCheckPoint.setAdapter(customAdapter);

        final Intent intent = getIntent();

        Bundle bd = intent.getExtras();
        if(bd!=null){
            wayID = intent.getIntExtra("ID_WAY",0);
            wayName = intent.getStringExtra("NAME_WAY");
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
        System.out.println("wayID " + wayID);

        myDB = myOpenDatabase.getReadableDatabase();

        //to retrieve the informatio of the way
        //String [] columns = {"id","nameway","noteway","iduser"};
        Cursor result = myDB.rawQuery("SELECT * FROM way WHERE id = "+wayID,null);

        result.moveToFirst();
        Way way = null;
        while(!result.isAfterLast()){

            way = new Way(result.getInt(0), result.getString(1), result.getInt(2), result.getInt(3));
            result.moveToNext();
            System.out.println("wayName1 " + way.getNameway());

        }
        //System.out.println(" result " + way.getNameway());

        result.close();
        myDB.close();

        // modification of the TextView for the name of the way
        EditTextNameWay.setText(way.getNameway());

        //modification of the note
        if(way.getNoteway()==1){
            imageViewNote.setImageResource(android.R.color.transparent);
            imageViewNote.setImageResource(R.drawable.unetoiles);
        }else if(way.getNoteway()==2){
            imageViewNote.setImageResource(android.R.color.transparent);
            imageViewNote.setImageResource(R.drawable.deuxetoiles);
        }else if(way.getNoteway()==3){
            imageViewNote.setImageResource(android.R.color.transparent);
            imageViewNote.setImageResource(R.drawable.troisetoiles);
        }else if(way.getNoteway()==4){
            imageViewNote.setImageResource(android.R.color.transparent);
            imageViewNote.setImageResource(R.drawable.quatreetoiles);
        }else if(way.getNoteway()==5){
            imageViewNote.setImageResource(android.R.color.transparent);
            imageViewNote.setImageResource(R.drawable.cinqetoiles);
        }

        myDB = myOpenDatabase.getReadableDatabase();

        // To retrieve the itineraire of the way
        String nameWay = way.getNameway();

        Cursor result2 = myDB.rawQuery("SELECT * FROM itineraire WHERE nameway = '"+nameWay+"'",null);
        result2.moveToFirst();

        while(!result2.isAfterLast()){
            LatLng latLng = new LatLng(Double.parseDouble(result2.getString(2)),Double.parseDouble(result2.getString(3)));
            latLngList.add(latLng);
            result2.moveToNext();
        }

        result2.close();

        System.out.println("query for checkpoints");
        // To retrieve the position of markers
        Cursor result3 = myDB.rawQuery("SELECT * FROM checkpoint WHERE nameway = '"+nameWay+"'",null);
        result3.moveToFirst();
        while(!result3.isAfterLast()){

            //for the table object
            CheckPoint checkPoint = new CheckPoint(result3.getString(4),result3.getString(5), result3.getInt(0));
            listObjectCheckpoint.add(checkPoint);

            // for the map
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            LatLng latLng = new LatLng(Double.parseDouble(result3.getString(2)),Double.parseDouble(result3.getString(3)));
            markerOptions.position(latLng);
            checkPointList.add(markerOptions);
            result3.moveToNext();
        }
        System.out.println(" fin query for checkpoints");

        result3.close();
        myDB.close();

        // modification of the textView


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

        erasePreviousWayFromExternalDataBase();
    System.out.println("fin du oncreate");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // enable the zoom functionality
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.getMaxZoomLevel();

        //recentrage to the way
        LatLng latLng = new LatLng(latLngList.get(0).latitude, latLngList.get(0).longitude);
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
        Intent intent = new Intent(this, WayActivity.class);
        startActivity(intent);
    }

    public void redirectionNote(View view) {

        Intent intent = new Intent(this, NoteWayActivity.class);
        //wayList
        intent.putExtra("ID_WAY",wayID);
        intent.putExtra("CURRENT_ID_USER",currentIdUser);
        intent.putExtra("NAME_WAY", wayName);
        startActivity(intent);


    }


    class CustomAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return listObjectCheckpoint.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            redirection(position);
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            System.out.println("in the getView");
            //TVnameCheckpoint

            convertView = getLayoutInflater().inflate(R.layout.custom_layout_checkpoint, null);
            TVnameCheckpoint = (TextView) convertView.findViewById(R.id.TVnameCheckpoint);

            TVnameCheckpoint.setText(listObjectCheckpoint.get(position).getName());


            return convertView;
        }
    }

    public void redirection(int position){

        Intent intent = new Intent(this, Checkpoint_Activity.class);
        //wayList
        intent.putExtra("ID_CHECKPOINT",listObjectCheckpoint.get(position).getId());
        intent.putExtra("CURRENT_ID_USER",currentIdUser);
        startActivity(intent);

    }


    public void erasePreviousWayFromExternalDataBase(){
        class ErasePreviousData extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                URL url;
                HttpURLConnection conn;
                try {

                    url = new URL("http://ultra-instinct-ece.000webhostapp.com/erasePreviousWay.php");

                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "exception";
                }
                try {
                    // Setup HttpURLConnection class to send and receive data from php and mysql
                    conn = (HttpURLConnection) url.openConnection();
                    conn.connect();

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return "exception";
                }

                try {

                    int response_code = conn.getResponseCode();

                    // Check if successful connection made
                    if (response_code == HttpURLConnection.HTTP_OK) {

                        // Read data sent from server
                        InputStream input = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        StringBuilder result = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        // Pass data to onPostExecute method
                        return(result.toString());

                    }else{

                        return("unsuccessful");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return "exception";
                } finally {
                    conn.disconnect();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result.equalsIgnoreCase("false")){

                    // If username and password does not match display a error message
                    Toast.makeText(WayActivity.this, "Erreur avec l'insertion dans la BDD", Toast.LENGTH_LONG).show();

                } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                    Toast.makeText(WayActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
                }
               // addWayToExternalDataBase(view);
            }
        }
        ErasePreviousData EraseDataAsync = new ErasePreviousData();
        EraseDataAsync.execute();
    }





    public void addWayToExternalDataBase(View view){

        myDB = myOpenDatabase.getReadableDatabase();
        Cursor wayCoordonnees = myDB.rawQuery("SELECT latitude,longitude FROM itineraire WHERE nameway = '" + wayName + "'",null);

        ids = new String[wayCoordonnees.getCount()];
        lats = new String[wayCoordonnees.getCount()];
        lngs = new String[wayCoordonnees.getCount()];

        int i = 0;
        wayCoordonnees.moveToFirst();
        while (!wayCoordonnees.isAfterLast()) {
          //  ids[i] = String.valueOf(wayCoordonnees.getInt(0));
          //  Log.d("ids",String.valueOf(wayCoordonnees.getInt(0)));
            ids[i] = String.valueOf(1);
            lats[i] = (String)wayCoordonnees.getString(0);
            lngs[i] = (String)wayCoordonnees.getString(1);
            i++;
            wayCoordonnees.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        wayCoordonnees.close();
        myDB.close();

        //System.out.println(" result " + way.getNameway());



        class InsertDataAsync extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings) {
                Log.d("test", strings[1]);
                URL url;
                HttpURLConnection conn;
                try {

                    url = new URL("http://ultra-instinct-ece.000webhostapp.com/addWay.php");

                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "exception";
                }
                try {
                    // Setup HttpURLConnection class to send and receive data from php and mysql

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");

                    // setDoInput and setDoOutput method depict handling of both send and receive
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    // Append parameters to URL
                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("id", strings[0])
                            .appendQueryParameter("latitude", strings[1])
                            .appendQueryParameter("longitude", strings[2]);
                    String query = builder.build().getEncodedQuery();

                    // Open connection for sending data
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();
                    conn.connect();

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    return "exception";
                }

                try {

                    int response_code = conn.getResponseCode();

                    // Check if successful connection made
                    if (response_code == HttpURLConnection.HTTP_OK) {

                        // Read data sent from server
                        InputStream input = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        StringBuilder result = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        // Pass data to onPostExecute method
                        return(result.toString());

                    }else{

                        return("unsuccessful");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return "exception";
                } finally {
                    conn.disconnect();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result.equalsIgnoreCase("false")){

                    // If username and password does not match display a error message
                    Toast.makeText(WayActivity.this, "Erreur avec l'insertion dans la BDD", Toast.LENGTH_LONG).show();

                } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                    Toast.makeText(WayActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
                }

            }
        }

        for(i = 0; i<lats.length; i++){
            InsertDataAsync insertDataAsync = new InsertDataAsync();
            insertDataAsync.execute(ids[i], lats[i], lngs[i]);
        }
    }

}
