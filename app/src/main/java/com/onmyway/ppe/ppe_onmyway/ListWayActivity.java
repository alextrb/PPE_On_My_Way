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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListWayActivity extends AppCompatActivity {

    CustomAdapter customAdapter;

    private Context mListener;

    //DATABASE SQLLITE
    protected MyOpenDatabase myOpenDatabase = null;
    protected SQLiteDatabase myDB = null;

    private List<Way> wayList;

    private List<Users> usersList;

    ListView listViewWay;
    TextView textViewNameWay;
    TextView textViewNameUser;

    private int currentIdUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_way);

        System.out.println("1");

        // retrieve the intent and redirect if we don't have it to the login!
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

        //SQL LITE
        mListener = getApplicationContext();
        myOpenDatabase = new MyOpenDatabase(mListener);
        System.out.println("2");

        listViewWay = (ListView) findViewById(R.id.listViewWay);

        System.out.println("4");
        wayList = new ArrayList<>();
        usersList = new ArrayList<>();

        System.out.println("3");
        customAdapter = new CustomAdapter();
        listViewWay.setAdapter(customAdapter);

        System.out.println("5");

        myDB = myOpenDatabase.getReadableDatabase();

        String [] columns = {"id","nameway","noteway","iduser"};
        Cursor result = myDB.query("way",columns,null,null,null,null,null);

        System.out.println("6");
        result.moveToFirst();

        while(!result.isAfterLast()){

            System.out.println("result.getInt(0)"+ result.getInt(0));
            Way way = new Way(result.getInt(0), result.getString(1),result.getInt(2), result.getInt(3));
            wayList.add(way);
            result.moveToNext();
        }
        System.out.println("7");
        result.close();

        System.out.println("8");
        String [] columns2 = {"id","username","mail"};
        Cursor result2 = myDB.query("users",columns2,null,null,null,null,null);

        System.out.println("9");
        result2.moveToFirst();

        while(!result2.isAfterLast()){
            System.out.println("result2.getInt(0)"+ result2.getInt(0));
            Users users = new Users(result2.getInt(0),result2.getString(1),result2.getString(2));
            usersList.add(users);
            result2.moveToNext();
        }
        System.out.println("10");
        result2.close();


        customAdapter.notifyDataSetChanged();

    }

    public void mapRedirection(View view) {
        Intent intent = new Intent(this, AllMapActivity.class);
        intent.putExtra("CURRENT_ID_USER",currentIdUser);
        startActivity(intent);
    }


    class CustomAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return wayList.size(); //matchList.size();

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
            convertView = getLayoutInflater().inflate(R.layout.custom_layout_listway, null);

            textViewNameWay =(TextView) convertView.findViewById(R.id.textViewNameWay);
            textViewNameUser = (TextView) convertView.findViewById(R.id.textViewNameUser);

            textViewNameWay.setText(wayList.get(position).getNameway());
            System.out.println("wayliqt" + wayList.get(position).getNameway());

            String username="";
            for(int i = 0; i<usersList.size();i++ ){
                System.out.println("userList" + usersList.get(i).getId());
                if(usersList.get(i).getId() == wayList.get(position).getIduser()){
                    username = usersList.get(i).getUsername();
                }
            }
            textViewNameUser.setText(username);

            return convertView;
        }
    }


    public void redirection(int position){

        Intent intent = new Intent(this, WayActivity.class);
        //wayList
        intent.putExtra("ID_WAY",wayList.get(position).getId());
        intent.putExtra("CURRENT_ID_USER",currentIdUser);
        startActivity(intent);

    }


}
