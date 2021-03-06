package com.onmyway.ppe.ppe_onmyway;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.onmyway.ppe.ppe_onmyway.R;

import java.util.ArrayList;

public class AccueilActivity extends AppCompatActivity {

    private TextView rechercheButton;
    private TextView createButton;
    private TextView profilButton;
    private int currentIdUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        rechercheButton = (TextView) findViewById(R.id.rechercheButton);
        createButton = (TextView) findViewById(R.id.createButton);
        profilButton = (TextView) findViewById(R.id.profilButton);

        // retrieve the intent and redirect if we don't have it to the login!
        final Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        currentIdUser = -1;
        if(bd!=null){
            currentIdUser = intent.getIntExtra("CURRENT_ID_USER",-1);
            // add a condition in the case that we were in the activity of description of the activity
            if(currentIdUser == -1){
                Intent intent2 = new Intent(this, LoginActivity.class);
                startActivity(intent2);
            }

        }else{
            System.out.println("error in the retrieving of the intent");
            return ;
        }
        if(currentIdUser == -1){
            Intent intent2 = new Intent(this, LoginActivity.class);
            startActivity(intent2);
        }

    }


    public void ChercherWay(View view) {

        Intent intent = new Intent(this,ListWayActivity.class);
        //wayList
        intent.putExtra("CURRENT_ID_USER",currentIdUser);
        startActivity(intent);
    }

    public void monProfil(View view) {
        Intent intent = new Intent(this,ProfilActivity.class);
        intent.putExtra("CURRENT_ID_USER",currentIdUser);
        startActivity(intent);
    }

    public void createWay(View view) {
        Intent intent = new Intent(this,CreationWayActivity.class);
        intent.putExtra("CURRENT_ID_USER",currentIdUser);
        startActivity(intent);
    }
}
