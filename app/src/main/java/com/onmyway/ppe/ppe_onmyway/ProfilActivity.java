package com.onmyway.ppe.ppe_onmyway;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ProfilActivity extends AppCompatActivity {

    private TextView nameUser;
    private TextView emailUser;
    private TextView newWay;

    private int currentIdUser;

    private Context mListener;

    //DATABASE SQLLITE
    protected MyOpenDatabase myOpenDatabase = null;
    protected SQLiteDatabase myDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        nameUser = (TextView) findViewById(R.id.nameUser);
        emailUser = (TextView) findViewById(R.id.emailUser);
        newWay = (TextView) findViewById(R.id.newWay);

        //SQL LITE
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

        String username="";
        String email="";

        myDB = myOpenDatabase.getReadableDatabase();
        String [] columns = {"id","username","mail","password"};
        Cursor result = myDB.query("users",columns,null,null,null,null,null);
        result.moveToFirst();
        while(!result.isAfterLast()){

            if(result.getInt(0) == currentIdUser){
                username = result.getString(1);
                email = result.getString(2);
            }
            result.moveToNext();
        }
        result.close();

        nameUser.setText(username);
        emailUser.setText(email);


    }


    public void createNewWay(View view) {

        Intent intent = new Intent(this, CreationWayActivity.class);
        //wayList
        intent.putExtra("CURRENT_ID_USER",currentIdUser);
        startActivity(intent);

    }



}
