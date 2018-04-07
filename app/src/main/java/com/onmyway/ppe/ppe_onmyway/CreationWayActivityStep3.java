package com.onmyway.ppe.ppe_onmyway;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.SimpleTimeZone;

public class CreationWayActivityStep3 extends AppCompatActivity {

    // The itineraire
    private List<LatLng> wayList;

    //DATABASE SQLLITE
    protected MyOpenDatabase myOpenDatabase = null;
    protected SQLiteDatabase myDB = null;

    private Context mListener;

    //checkPoints
    private List<MarkerOptions> checkPointList;

    //descriptionCheckPoint
    private List<CheckPoint> checkPointListDesc;


    //view elements
    private ListView listCheckPoint;
    private Button buttonConfirmStep3;

    //View in the listView
    private TextView ETdescriptionStep3;
    private TextView ETCheckpointName;

    private String name_way;

    private boolean authorized;

    private int position;

    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_way_step3);

        authorized = false;
        System.out.println("dans create 1");
        //Initisation of the checkPointList
        checkPointListDesc = new ArrayList<>();

        listCheckPoint = (ListView) findViewById(R.id.listCheckPoint);
        buttonConfirmStep3 = (Button) findViewById(R.id.buttonConfirmStep3);

        customAdapter = new CustomAdapter();
        listCheckPoint.setAdapter(customAdapter);

        //Intialisation of the database
        mListener = getApplicationContext();
        myOpenDatabase = new MyOpenDatabase(mListener);

        final Intent intent = getIntent();
        String result;
        Bundle bd = intent.getExtras();
        if(bd!=null){

            wayList = intent.getParcelableArrayListExtra("EXTRA_LIST_ITINERAIRE");
            checkPointList = intent.getParcelableArrayListExtra("EXTRA_LIST_CHECKPOINT");
            name_way = intent.getStringExtra("EXTRA_NAME_WAY");
            position = intent.getIntExtra("EXTRA_POSITION_CHEKPOINT",-1);

            int size = intent.getIntExtra("SIZE_LIST_CHECK", 0);
            for(int i = 0; i< size; i++){
                String name = intent.getStringExtra("NAME"+i);
                String desc = intent.getStringExtra("DESCRIPTION"+i);
                int id = intent.getIntExtra("ID_CHECK"+i,0);
                checkPointListDesc.add(new CheckPoint(name,desc, id));

            }
            System.out.println("SIZE_LIST_CHECK" + size);


            // add a condition in the case that we were in the activity of description of the activity

        }else{
            System.out.println("error in the retrieving of the intent");
            return ;
        }
        System.out.println("avant custom");
        System.out.println("après custom");
        System.out.println("dans create 2");

        customAdapter.notifyDataSetChanged();


    }



    public void finaliseWay(View view) {

        //customAdapter.notifyDataSetChanged();
        // Send to the bdd
        System.out.println("finaliseWay");

        //Boolean find = verificationName();
        // first we look in the database if there are no already a way with the current name
        System.out.println("finaliseWay3");

        System.out.println("sql 1");
        //if not

        myDB = myOpenDatabase.getWritableDatabase();
        /*
        ContentValues values3 = new ContentValues();
        values3.put("username","jeremyCtln");
        values3.put("mail","jeremy.catelain@ece.fr");
        values3.put("password","password");
        myDB.insert("users",null,values3);*/

        // table way
        ContentValues values = new ContentValues();
        values.put("nameway",name_way);
        values.put("noteway",0);
        values.put("iduser",1);
        myDB.insert("way",null,values);

        System.out.println("sql 1");
        // table itineraire
        for (int i=0; i<wayList.size(); i++){
            ContentValues values2 = new ContentValues();
            values2.put("nameway",name_way);
            values2.put("latitude",wayList.get(i).latitude);
            values2.put("longitude",wayList.get(i).longitude);
            myDB.insert("itineraire",null,values2);
        }
        System.out.println("sql 1");

        //table checkpoint
        for (int i=0; i<checkPointListDesc.size(); i++) {
            ContentValues values2 = new ContentValues();
            values2.put("nameway", name_way);
            values2.put("latitude", checkPointList.get(i).getPosition().latitude);
            values2.put("longitude", checkPointList.get(i).getPosition().longitude);
            values2.put("namecheckpoint", checkPointListDesc.get(i).getName());
            values2.put("description", checkPointListDesc.get(i).getDescription());
            myDB.insert("checkpoint",null,values2);
        }


        System.out.println("sql 1");
        // redirection to the new one
        Intent intent = new Intent(this, CreationWayActivity.class);
        startActivity(intent);


    }

    class CustomAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return checkPointListDesc.size();
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
            /*
            ETdescriptionStep3
            ETCheckpointName
             */
            convertView = getLayoutInflater().inflate(R.layout.layout_list_checkpoint, null);
            ETCheckpointName = (TextView) convertView.findViewById(R.id.ETCheckpointName);
            ETdescriptionStep3 = (TextView) convertView.findViewById(R.id.ETdescriptionStep3);



            //checkPointListDesc.get(position).setName("name"+position);
            //checkPointListDesc.get(position).setDescription("desc"+ position);

            ETCheckpointName.setText(checkPointListDesc.get(position).getName());
            ETdescriptionStep3.setText(checkPointListDesc.get(position).getDescription());


            return convertView;
        }
    }

    public void redirection(int position){

        Intent intent = new Intent(this, CreationWayActivityCheckpoint.class);
        //wayList
        intent.putParcelableArrayListExtra("EXTRA_LIST_CHECKPOINT", (ArrayList<? extends Parcelable>) checkPointList);
        intent.putParcelableArrayListExtra("EXTRA_LIST_ITINERAIRE", (ArrayList<? extends Parcelable>) wayList);
        intent.putExtra("EXTRA_NAME_WAY",name_way);
        intent.putExtra("EXTRA_POSITION_CHEKPOINT",position);
        intent.putExtra("SIZE_LIST_CHECK", checkPointListDesc.size());

        System.out.print(" name_way " + name_way);
        System.out.print("position " + position);
        System.out.print(" checkPointListDesc.size() "+ checkPointListDesc.size());

        for(int i = 0;i<checkPointListDesc.size(); i++ ){
            intent.putExtra("NAME"+i,checkPointListDesc.get(i).getName());
            intent.putExtra("DESCRIPTION"+i,checkPointListDesc.get(i).getDescription());
            intent.putExtra("ID_CHECK"+i, checkPointListDesc.get(i).getId());
            System.out.print("checkPointListDesc.get(i).getName() " + checkPointListDesc.get(i).getName());
            System.out.print("checkPointListDesc.get(i).getDescription() " + checkPointListDesc.get(i).getDescription());
            System.out.print("checkPointListDesc.get(i).getId() " + checkPointListDesc.get(i).getId());
        }

        startActivity(intent);

    }

}
