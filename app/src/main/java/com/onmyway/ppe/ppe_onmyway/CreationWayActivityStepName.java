package com.onmyway.ppe.ppe_onmyway;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class CreationWayActivityStepName extends AppCompatActivity {


    //DATABASE SQLLITE
    protected MyOpenDatabase myOpenDatabase = null;
    protected SQLiteDatabase myDB = null;

    private Context mListener;

    private List<LatLng> wayList;
    //checkPoints
    private List<MarkerOptions> checkPointList;

    //descriptionCheckPoint
    private List<CheckPoint> checkPointListDesc;

    private String name_way;

    private EditText editTextNameWay;
    private Button buttonSuivantName;

    private boolean find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_way_step_name);
        find = false;

        //Intialisation of the database
        mListener = getApplicationContext();
        myOpenDatabase = new MyOpenDatabase(mListener);

        //Initisation of the checkPointList
        checkPointListDesc = new ArrayList<>();

        editTextNameWay = (EditText) findViewById(R.id.editTextNameWay);
        buttonSuivantName = (Button) findViewById(R.id.buttonSuivantName);

        final Intent intent = getIntent();
        String result;
        Bundle bd = intent.getExtras();
        if(bd!=null){
            wayList = intent.getParcelableArrayListExtra("EXTRA_LIST_ITINERAIRE");
            checkPointList = intent.getParcelableArrayListExtra("EXTRA_LIST_CHECKPOINT");
            int size = intent.getIntExtra("SIZE_LIST_CHECK", 0);
            for(int i = 0; i< size; i++){
                String name = intent.getStringExtra("NAME"+i);
                String desc = intent.getStringExtra("DESCRIPTION"+i);
                int id = intent.getIntExtra("ID_CHECK"+i,0);
                checkPointListDesc.add(new CheckPoint(name,desc, id));

                System.out.println("id" + checkPointListDesc.get(i).getId());

            }

            // add a condition in the case that we were in the activity of description of the activity

        }else{
            System.out.println("error in the retrieving of the intent");
            return ;
        }
        System.out.println("size of the checlpointlist " + checkPointListDesc.size());

    }

    public boolean verificationName(){
        System.out.println("finaliseWay2.1");
        myDB = myOpenDatabase.getReadableDatabase();
        System.out.println("finaliseWay2.2");
        String [] columns = {"id","nameway"};

        Cursor result = myDB.query("way",columns,null,null,null,null,null);

        System.out.println("finaliseWay2.2");
        boolean find = false;
        System.out.println("finaliseWay2.2");
        result.moveToFirst();
        while(!result.isAfterLast()){
            System.out.println("finaliseWay2.2");
            System.out.println("editTextStep3.getText().toString() " + name_way);
            System.out.println("result.getString(0) " + result.getString(1));
            if(result.getString(1).equals(name_way)){
                find = true;
            }
            result.moveToNext();
        }
        result.close();
        return find;
    }


    public void suivant(View view) {


        if(editTextNameWay.getText().toString().equals("")){
            Toast.makeText(this, "You have to enter a name!", Toast.LENGTH_SHORT).show();
        }else{

            find = verificationName();

            if (!find){
                Intent intent = new Intent(this, CreationWayActivityStep3.class);
                //wayList
                intent.putParcelableArrayListExtra("EXTRA_LIST_CHECKPOINT", (ArrayList<? extends Parcelable>) checkPointList);
                intent.putParcelableArrayListExtra("EXTRA_LIST_ITINERAIRE", (ArrayList<? extends Parcelable>) wayList);
                intent.putExtra("EXTRA_NAME_WAY",editTextNameWay.getText().toString());
                intent.putExtra("EXTRA_POSITION_CHEKPOINT",-1);
                intent.putExtra("SIZE_LIST_CHECK", checkPointListDesc.size());
                System.out.println("SIZE_LIST_CHECK "+ checkPointListDesc.size());
                for(int i = 0;i<checkPointListDesc.size(); i++ ){
                    intent.putExtra("NAME"+i,checkPointListDesc.get(i).getName());
                    intent.putExtra("DESCRIPTION"+i,checkPointListDesc.get(i).getDescription());
                    intent.putExtra("ID_CHECK"+i, checkPointListDesc.get(i).getId());
                }

                startActivity(intent);

            }


        }


    }
}
