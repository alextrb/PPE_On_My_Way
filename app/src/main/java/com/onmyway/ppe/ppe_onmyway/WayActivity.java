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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class WayActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    //DATABASE SQLLITE
    protected MyOpenDatabase myOpenDatabase = null;
    protected SQLiteDatabase myDB = null;

    //provider
    private LocationManager locationManager;

    // asking for the localisation of the user
    private static final int LOCATION_REQUEST = 500;

    private GoogleMap mMap;

    private Context mListener;

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
            imageViewNote.setImageResource(R.drawable.unetoiles);
        }else if(way.getNoteway()==2){
            imageViewNote.setImageResource(R.drawable.deuxetoiles);
        }else if(way.getNoteway()==3){
            imageViewNote.setImageResource(R.drawable.troisetoiles);
        }else if(way.getNoteway()==4){
            imageViewNote.setImageResource(R.drawable.quatreetoiles);
        }else if(way.getNoteway()==5){
            imageViewNote.setImageResource(R.drawable.cinqetoiles);
        }

        myDB = myOpenDatabase.getReadableDatabase();

        // To retrieve the itineraire of the way
        System.out.println("query  itineraire");
        String nameWay = way.getNameway();

        Cursor result2 = myDB.rawQuery("SELECT * FROM itineraire WHERE nameway = '"+nameWay+"'",null);
        result2.moveToFirst();

        System.out.println("query  itineraire2");

        while(!result2.isAfterLast()){
            LatLng latLng = new LatLng(Double.parseDouble(result2.getString(2)),Double.parseDouble(result2.getString(3)));
            latLngList.add(latLng);
            result2.moveToNext();
        }

        result2.close();

        System.out.println("query for checkpoints");
        // To retrieve the position of markers
        Cursor result3 = myDB.rawQuery("SELECT * FROM checkpoint WHERE nameway = '"+nameWay+"'",null);
        System.out.println("query for checkpoints2");
        result3.moveToFirst();
        while(!result3.isAfterLast()){

            //for the table object
            CheckPoint checkPoint = new CheckPoint(result3.getString(4),result3.getString(5), result3.getInt(0));
            listObjectCheckpoint.add(checkPoint);

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

    System.out.println("fin du oncreate");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // enable the zoom functionality
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        //mMap.getMaxZoomLevel();

        //recentrage to the way
        LatLng latLng = new LatLng(latLngList.get(2).latitude, latLngList.get(2).longitude);
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
        startActivity(intent);

    }

}
