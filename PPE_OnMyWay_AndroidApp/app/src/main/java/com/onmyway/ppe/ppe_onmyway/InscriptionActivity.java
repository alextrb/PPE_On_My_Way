package com.onmyway.ppe.ppe_onmyway;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InscriptionActivity extends AppCompatActivity {

    private EditText mailCreation;
    private EditText usernameCreation;
    private EditText passwordCreation;
    private Button validationButton;

    private Context mListener;

    //DATABASE SQLLITE
    protected MyOpenDatabase myOpenDatabase = null;
    protected SQLiteDatabase myDB = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        mailCreation = (EditText) findViewById(R.id.mailCreation);
        usernameCreation = (EditText) findViewById(R.id.usernameCreation);
        passwordCreation = (EditText) findViewById(R.id.passwordCreation);
        validationButton = (Button) findViewById(R.id.validationButton);

        //SQL LITE
        mListener = getApplicationContext();
        myOpenDatabase = new MyOpenDatabase(mListener);

    }

    public void validationInscription(View view) {

        if(mailCreation.getText().toString().equals("") || usernameCreation.getText().toString().equals("") || passwordCreation.getText().toString().equals("")){
            Toast.makeText(this, "Il faut remplire le login et le password!", Toast.LENGTH_SHORT).show();
        }else{

            // We looked in the database if the username and the email area already used by someone else
            myDB = myOpenDatabase.getReadableDatabase();
            String [] columns = {"id","username","mail","password"};
            Cursor result = myDB.query("users",columns,null,null,null,null,null);
            result.moveToFirst();
            boolean find = false;
            while(!result.isAfterLast()){

                if(result.getString(1).equals(usernameCreation.getText().toString()) || result.getString(3).equals(mailCreation.getText().toString())){
                    find = true;
                }
                result.moveToNext();
            }
            result.close();
            if(!find){
                System.out.print("no user with the same username and email has been found!");
                // had the user to the database and redirection to the accueil activity!
                myDB = myOpenDatabase.getWritableDatabase();
                System.out.println("before the insertion");
                ContentValues values = new ContentValues();
                values.put("username",usernameCreation.getText().toString());
                values.put("mail",mailCreation.getText().toString());
                values.put("password",passwordCreation.getText().toString());
                myDB.insert("users",null,values);
                System.out.println("before the insertion");
                System.out.println("usernameCreation.getText().toString()"+ usernameCreation.getText().toString());

                myDB.close();

                myDB = myOpenDatabase.getReadableDatabase();
                System.out.println("before the reading");
                int currentId = -1;
                String [] columns2 = {"id","username","mail","password"};
                Cursor result2 = myDB.query("users",columns2,null,null,null,null,null);
                result2.moveToFirst();
                while(!result2.isAfterLast()){
                    if(result2.getString(1).equals(usernameCreation.getText().toString()) && result2.getString(2).equals(mailCreation.getText().toString()) && result2.getString(3).equals(passwordCreation.getText().toString())){
                        currentId = result2.getInt(0);
                    }
                    result2.moveToNext();
                }
                result2.close();
                System.out.println("after the reading");
                if(currentId == -1){
                    System.out.println("THERE IS AN ERROR IN THE INSERTION OF THE USER");
                }else{
                    Intent intent = new Intent(this, AccueilActivity.class);
                    //wayList
                    intent.putExtra("CURRENT_ID_USER",currentId);
                    startActivity(intent);
                }


            }else{
                Toast.makeText(this, "L'email et/ou le logins sont deja pris!", Toast.LENGTH_SHORT).show();
            }


        }

    }
}
