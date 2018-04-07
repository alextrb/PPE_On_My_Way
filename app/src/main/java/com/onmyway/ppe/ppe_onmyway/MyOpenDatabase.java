package com.onmyway.ppe.ppe_onmyway;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jeremy_pc on 19/03/2018.
 */

public class MyOpenDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "my.dbOnMyWay4";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME1 = "way";
    private static final String TABLE_NAME2 = "itineraire";
    private static final String TABLE_NAME3 = "comment";
    private static final String TABLE_NAME4 = "checkpoint";
    private static final String TABLE_NAME5 = "users";
    private static final String TABLE_WAY_CREATE =
            "CREATE TABLE " + TABLE_NAME1 + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nameway VARCHAR, " +
                    "noteway INTEGER, " +
                    "iduser INTEGER);";
    private static final String TABLE_ITINERAIRE_CREATE =
            "CREATE TABLE " + TABLE_NAME2 + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nameway VARCHAR, " +
                    "latitude VARCHAR, " +
                    "longitude VARCHAR);";
    private static final String TABLE_COMMENT_CREATE =
            "CREATE TABLE " + TABLE_NAME3 + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nameway VARCHAR, " +
                    "commentaire VARCHAR, " +
                    "idusercomment INTEGER);";
    private static final String TABLE_CHECKPOINT_CREATE =
            "CREATE TABLE " + TABLE_NAME4 + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nameway VARCHAR, " +
                    "latitude VARCHAR, " +
                    "longitude VARCHAR, " +
                    "namecheckpoint VARCHAR, " +
                    "description VARCHAR);";
    private static final String TABLE_USERS_CREATE =
            "CREATE TABLE " + TABLE_NAME5 + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username VARCHAR, " +
                    "mail VARCHAR, " +
                    "password VARCHAR);";



    public MyOpenDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //context.deleteDatabase("my.dbOnMyWay4");
        //context.deleteDatabase("my.dbOnMyWay2");
        //context.deleteDatabase("my.dbOnMyWay3");
        //context.deleteDatabase("my.dbOnMyWay");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_WAY_CREATE);
        db.execSQL(TABLE_ITINERAIRE_CREATE);
        db.execSQL(TABLE_COMMENT_CREATE);
        db.execSQL(TABLE_CHECKPOINT_CREATE);
        db.execSQL(TABLE_USERS_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
