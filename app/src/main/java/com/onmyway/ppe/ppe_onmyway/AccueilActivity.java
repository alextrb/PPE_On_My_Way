package com.onmyway.ppe.ppe_onmyway;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class AccueilActivity extends AppCompatActivity {

    private Button rechercheButton;
    private Button createButton;
    private Button profilButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        rechercheButton = (Button) findViewById(R.id.rechercheButton);
        createButton = (Button) findViewById(R.id.createButton);
        profilButton = (Button) findViewById(R.id.profilButton);


    }


    public void ChercherWay(View view) {

        Intent intent = new Intent(this,ListWayActivity.class);
        startActivity(intent);
    }

    public void monProfil(View view) {
        //Intent intent = new Intent(this,ListWayActivity.class);
        //startActivity(intent);
    }

    public void createWay(View view) {
        Intent intent = new Intent(this,CreationWayActivity.class);
        startActivity(intent);
    }
}
