package com.onmyway.ppe.ppe_onmyway;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProfilActivity extends AppCompatActivity {


    CustomAdapter customAdapter;

    private TextView nameUser;
    private TextView emailUser;
    private TextView newWay;

    private int currentIdUser;

    private Context mListener;

    //DATABASE SQLLITE
    protected MyOpenDatabase myOpenDatabase = null;
    protected SQLiteDatabase myDB = null;

    private ListView listViewWay;
    TextView textViewNameWay;
    TextView textViewNameUser;
    ImageView noteWayList;

    private List<Way> wayList;
    private List<Users> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        nameUser = (TextView) findViewById(R.id.nameUser);
        emailUser = (TextView) findViewById(R.id.emailUser);
        newWay = (TextView) findViewById(R.id.newWay);
        listViewWay = (ListView) findViewById(R.id.listViewWay);
        textViewNameWay = (TextView) findViewById(R.id.textViewNameWay);
        textViewNameUser = (TextView) findViewById(R.id.textViewNameUser);

        wayList = new ArrayList<>();
        usersList =new ArrayList<>();

        customAdapter = new CustomAdapter();
        listViewWay.setAdapter(customAdapter);

        //SQL LITE
        mListener = getApplicationContext();
        myOpenDatabase = new MyOpenDatabase(mListener);

        final Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        currentIdUser = -1;
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
        if(currentIdUser == -1){
            Intent intent2 = new Intent(this,LoginActivity.class);
            startActivity(intent2);
        }
        System.out.println( "ID CURRENT USER : " + currentIdUser);

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

        String [] columns2 = {"id","nameway","noteway","iduser"};
        Cursor result2 = myDB.query("way",columns2,null,null,null,null,null);

        result2.moveToFirst();

        while(!result2.isAfterLast()){

            if(result2.getInt(3)== currentIdUser){
                Way way = new Way(result2.getInt(0), result2.getString(1),result2.getInt(2), result2.getInt(3));
                wayList.add(way);
            }

            result2.moveToNext();
        }

        result2.close();

        String [] columns3 = {"id","username","mail"};
        Cursor result3 = myDB.query("users",columns3,null,null,null,null,null);

        System.out.println("9");
        result3.moveToFirst();

        while(!result3.isAfterLast()){
            System.out.println("result2.getInt(0)"+ result3.getInt(0));
            Users users = new Users(result3.getInt(0),result3.getString(1),result3.getString(2));
            usersList.add(users);
            result3.moveToNext();
        }
        System.out.println("10");
        result3.close();

        customAdapter.notifyDataSetChanged();
    }


    public void createNewWay(View view) {

        Intent intent = new Intent(this, CreationWayActivity.class);
        //wayList
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
            noteWayList = (ImageView) convertView.findViewById(R.id.noteWayList);

            textViewNameWay.setText(wayList.get(position).getNameway());
            System.out.println("wayliqt" + wayList.get(position).getNameway());

            if(wayList.get(position).getNoteway()==1){
                noteWayList.setImageResource(0);
                noteWayList.setImageResource(R.drawable.unetoiles);
            }else if(wayList.get(position).getNoteway()==2){
                noteWayList.setImageResource(0);
                noteWayList.setImageResource(R.drawable.deuxetoiles);
                noteWayList.setImageResource(0);
            }else if(wayList.get(position).getNoteway()==3){
                noteWayList.setImageResource(0);
                noteWayList.setImageResource(R.drawable.troisetoiles);
                noteWayList.setImageResource(0);
            }else if(wayList.get(position).getNoteway()==4){
                noteWayList.setImageResource(0);
                noteWayList.setImageResource(R.drawable.quatreetoiles);
            }else if(wayList.get(position).getNoteway()==5){
                noteWayList.setImageResource(0);
                noteWayList.setImageResource(R.drawable.cinqetoiles);
            }


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

        Intent intent = new Intent(this, ProfilModifWayActivity.class);
        //wayList
        intent.putExtra("ID_WAY",wayList.get(position).getId());
        intent.putExtra("CURRENT_ID_USER",currentIdUser);
        intent.putExtra("NAME_WAY", wayList.get(position).getNameway());
        startActivity(intent);

    }

}
