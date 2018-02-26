package com.example.codyhammond.newshub;

/**
 * Created by codyhammond on 2/5/18.
 */

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Environment;
import android.provider.BaseColumns;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

/**
 * Contains logic to return specific words from the dictionary, and load the
 * dictionary table when it needs to be created.
 */
public class PublisherDatabase extends SQLiteOpenHelper
{

    private  SQLiteDatabase mDatabase;
    private List<String> publishers;

    private static final String DATABASE_NAME = "/database/Publishers.db";
    private static final int DATABASE_VERSION = 1;



    // The columns we'll include in the dictionary table
    public static final String PUBLISHER_COL = "Publisher_COL";
    public final String PUBLISHER_ID="ID";
    public static final String PUBLISHER_TBL="Publisher_TBL";
    public static final String PUBLISHER_ALT="PublisherAltID";
    public final static String path=Environment.getExternalStorageDirectory().getAbsolutePath()+DATABASE_NAME;


    private Context context;


   public PublisherDatabase(Context context) {

       super(context, path, null, DATABASE_VERSION);
       this.context=context;
     //  context.deleteDatabase(path);

       if(checkifDBExists()) {
           return;
       }

                  mDatabase = getWritableDatabase();
   }


    private boolean checkifDBExists()
    {
        File file=context.getDatabasePath(path);
        Log.e("PATH",file.toString());
        if(mDatabase==null) {
            try {
               mDatabase = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
                //    Toast.makeText(context,"DB Exists",Toast.LENGTH_LONG).show();
                Log.i("Confirmed", "DB exists");

            } catch (Exception database) {
                //   Toast.makeText(context,"DB Exists",Toast.LENGTH_LONG).show();
                Log.e("SQLException",database.getLocalizedMessage());
                return false;
            }
        }
        return true;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Toast.makeText(context,"CREATING",Toast.LENGTH_SHORT);

        /*
        database.execSQL("CREATE TABLE IF NOT EXISTS "+ALARM_TABLE +
                " ( " + ALARM_ID + " INTEGER primary key autoincrement,"
                + ALARM_ACTIVE + " INTEGER NOT NULL,"
                + ALARM_SNOOZE + " INTEGER NOT NULL,"
                + ALARM_TIME + " TEXT NOT NULL,"
                + ALARM_DAYS + " BLOB,"
                + ALARM_TONE + " TEXT NOT NULL,"
                + ALARM_NAME + " TEXT NOT NULL)");
         */
        db.execSQL("CREATE TABLE IF NOT EXISTS "+PUBLISHER_TBL+
                " ( " + PUBLISHER_ID + " INTEGER primary key autoincrement,"
        +PUBLISHER_COL+" TEXT NOT NULL,"+PUBLISHER_ALT+" TEXT NOT NULL )");

        mDatabase=db;


        loadDictionary();
    }

    private void loadDictionary()
    {
        RetroFitClient.getRetroFitClient().getPublishers(mDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }


    public void getWordMatches(String query, ArrayAdapter<String> arrayAdapter)
    {
        arrayAdapter.clear();
        arrayAdapter.notifyDataSetChanged();

        //return mDatabase.rawQuery("SELECT "+PUBLISHER+" FROM "+FTS_VIRTUAL_TABLE+" WHERE "+PUBLISHER+" LIKE "+query+"%",null);
       Cursor cursor= mDatabase.rawQuery("SELECT "+PUBLISHER_COL+" FROM "+PUBLISHER_TBL+" WHERE "+PUBLISHER_COL+" LIKE "+"\'"+query+"%"+"\'",null);

       cursor.moveToFirst();


       while (!cursor.isAfterLast()) {
           arrayAdapter.add(cursor.getString(cursor.getColumnIndex(PUBLISHER_COL)));
           //pubIDs.add(cursor.getString(cursor.getColumnIndex(PUBLISHER_ALT_ID)));
           cursor.moveToNext();
    }
    cursor.close();

       arrayAdapter.notifyDataSetChanged();

    }

    public String getNameID(String name) {
       Cursor cursor=mDatabase.rawQuery("SELECT "+PUBLISHER_ALT+" FROM "+PUBLISHER_TBL+" WHERE "+PUBLISHER_COL+"="+"\'"+name+"\'",null);
        //Cursor cursor=mDatabase.rawQuery("SELECT "+PUBLISHER_ALT+" FROM "+PUBLISHER_TBL,null);

        cursor.moveToFirst();

        return cursor.getString(cursor.getColumnIndex(PUBLISHER_ALT));

    }

    public void setPubList(List<String>list) {
       publishers=list;
    }


}
