package com.onmyway.ppe.ppe_onmyway;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class NoteWayActivity extends AppCompatActivity {

    private ImageView uneEtoileID;
    private ImageView deuxEtoileID;
    private ImageView troisEtoileID;
    private ImageView quatreEtoileID;
    private ImageView cinqEtoileID;

    private int wayID;
    private String wayName;
    private int currentIdUser;

    //DATABASE SQLLITE
    protected MyOpenDatabase myOpenDatabase = null;
    protected SQLiteDatabase myDB = null;

    private Context mListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_way);

        uneEtoileID = (ImageView) findViewById(R.id.uneEtoileID);
        deuxEtoileID = (ImageView) findViewById(R.id.deuxEtoileID);
        troisEtoileID = (ImageView) findViewById(R.id.troisEtoileID);
        quatreEtoileID = (ImageView) findViewById(R.id.quatreEtoileID);
        cinqEtoileID = (ImageView) findViewById(R.id.cinqEtoileID);

        //Intialisation of the database
        mListener = getApplicationContext();
        myOpenDatabase = new MyOpenDatabase(mListener);

        // recuperation des intents
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


    }


    public void uneEtoile(View view) {

        myDB = myOpenDatabase.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("noteway", 1);
        myDB.update("way", cv, "id="+wayID, null);
        myDB.close();
        Toast.makeText(this, "Félicitation votre vote a été enregistré!", Toast.LENGTH_SHORT).show();

    }

    public void deuxEtoiles(View view) {

        myDB = myOpenDatabase.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("noteway", 2);
        myDB.update("way", cv, "id="+wayID, null);
        myDB.close();
        Toast.makeText(this, "Félicitation votre vote a été enregistré!", Toast.LENGTH_SHORT).show();

    }

    public void troisEtoile(View view) {
        myDB = myOpenDatabase.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("noteway", 3);
        myDB.update("way", cv, "id="+wayID, null);
        myDB.close();
        Toast.makeText(this, "Félicitation votre vote a été enregistré!", Toast.LENGTH_SHORT).show();
    }

    public void quatreEtoile(View view) {
        myDB = myOpenDatabase.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("noteway", 4);
        myDB.update("way", cv, "id="+wayID, null);
        myDB.close();
        Toast.makeText(this, "Félicitation votre vote a été enregistré!", Toast.LENGTH_SHORT).show();
    }

    public void cinqEtoile(View view) {
        myDB = myOpenDatabase.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("noteway", 5);
        myDB.update("way", cv, "id="+wayID, null);
        myDB.close();
        Toast.makeText(this, "Félicitation votre vote a été enregistré!", Toast.LENGTH_SHORT).show();
    }
}
