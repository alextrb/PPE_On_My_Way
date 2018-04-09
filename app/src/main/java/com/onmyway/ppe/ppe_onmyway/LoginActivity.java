package com.onmyway.ppe.ppe_onmyway;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {



    private EditText passwordEditText;
    private EditText loginEditText;
    private TextView validationButton;
    private TextView inscriptionButton;

    private Context mListener;

    //DATABASE SQLLITE
    protected MyOpenDatabase myOpenDatabase = null;
    protected SQLiteDatabase myDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginEditText = (EditText) findViewById(R.id.loginEditText);
        validationButton = (TextView) findViewById(R.id.validationButton);
        inscriptionButton = (TextView) findViewById(R.id.inscriptionButton);

        //SQL LITE
        mListener = getApplicationContext();
        myOpenDatabase = new MyOpenDatabase(mListener);


    }


    public void validationLogin(View view) {

        if(passwordEditText.getText().toString().equals("") || loginEditText.getText().toString().equals("")){
            Toast.makeText(this, "Il faut remplire le login et le password!", Toast.LENGTH_SHORT).show();
        }else{

            myDB = myOpenDatabase.getReadableDatabase();
            String [] columns = {"id","username","mail","password"};
            Cursor result = myDB.query("users",columns,null,null,null,null,null);
            result.moveToFirst();
            int currentId = -1;
            while(!result.isAfterLast()){

                if(result.getString(1).equals(loginEditText.getText().toString())&& result.getString(3).equals(passwordEditText.getText().toString())){
                    currentId = result.getInt(0);
                }
                result.moveToNext();
            }
            result.close();
            if(currentId == -1){
                Toast.makeText(this, "Identification a échouée! ", Toast.LENGTH_SHORT).show();
            }else{
                //redirection
                Intent intent = new Intent(this, AccueilActivity.class);
                //wayList
                intent.putExtra("CURRENT_ID_USER",currentId);
                startActivity(intent);
            }




        }

    }

    public void incriptionRedirect(View view) {

        Intent intent = new Intent(this, InscriptionActivity.class);
        startActivity(intent);

    }
}
