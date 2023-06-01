package com.algebratech.pulse_wellness.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.algebratech.pulse_wellness.models.ActivityListModel;
import com.algebratech.pulse_wellness.models.DailyReads;
import com.algebratech.pulse_wellness.utils.Constants;
//import com.google.gson.JsonObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tgio.rncryptor.RNCryptorNative;

import static android.content.Context.MODE_PRIVATE;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "PulseDB.crypt12";
    public static final String TABLE_ACTIVTIES = "activities";
    public static final String TABLE_DAILY_READ = "dailyreads";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_POINT = "points";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_STATUS = "sync";

    //database version
    private static final int DB_VERSION = 1;
    private static final String CCOLUMN_POINT = "points" ;
    SharedPreferences sharedPreferences;
    RNCryptorNative rnCryptorNative;


    //Constructor
    public DatabaseHelper(Context context) {
        //super(context, DB_NAME, null, DB_VERSION);
        super(context,  Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/" + Constants.PREF_NAME
                +"/" + ".Database/"+ DB_NAME, null, DB_VERSION);
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        rnCryptorNative = new RNCryptorNative();

        String outputSource = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+Constants.PREF_NAME+"/" + ".Database/";
        Log.d(Constants.TAG+"Folder",outputSource);
        File dir = new File(outputSource);
        if (!dir.exists()){
            dir.mkdirs();
            Log.d(Constants.TAG+"@Database","Success");
            Log.d(Constants.TAG+"Folder","Ndagadzira");
        }else{
            Log.d(Constants.TAG+"Folder","Ndiripo");
        }

    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_DAILY_READ
                + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER +
                " VARCHAR, "+ COLUMN_POINT + " VARCHAR, steps TEXT, distance TEXT, kcals TEXT, "+ COLUMN_DATE + " VARCHAR, "+ COLUMN_STATUS +
                " BOOLEAN);";

        db.execSQL( "CREATE TABLE " + TABLE_ACTIVTIES + "(id INTEGER primary key autoincrement NOT NULL, activity_type TEXT, " +
                "user TEXT,duration TEXT, steps INTEGER, average_heartrate TEXT, kcals TEXT, date TEXT,map_path TEXT, map_image TEXT, camera_image TEXT ,sync BOOLEAN)");


        db.execSQL(sql);
        Log.d(Constants.TAG+"@Database","Create Tables Success");
    }

    //Insert Activities

    public void insertActivities(String activity_type,String user, String duration, int steps, String avg_heart_rate, String kCals, String date,String map_path, String map_image,String camera_image,Boolean sync) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("activity_type", activity_type);
        contentValues.put("user", user);
        contentValues.put("duration", duration);
        contentValues.put("steps", steps);
        contentValues.put("average_heartrate", avg_heart_rate);
        contentValues.put("kcals", kCals);
        contentValues.put("date", date);
        contentValues.put("map_path", map_path);
        contentValues.put("map_image", map_image);
        contentValues.put("camera_image", camera_image);
        contentValues.put("sync", sync);


        long result = db.insert(TABLE_ACTIVTIES, null, contentValues);
        db.close(); // Close database connection
//        if(result == -1)
//            return  false;
//
//        else
//            return true;
    }

    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS Persons";
        db.execSQL(sql);
        onCreate(db);
    }

    /*
     * This method is taking two arguments
     * first one is the name that is to be saved
     * second one is the status
     * 0 means the name is synced with the server
     * 1 means the name is not synced with the server
     * */
    public boolean addName(String user ,String date,String points, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_USER, user);
        contentValues.put(COLUMN_POINT, points);
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_STATUS, status);


        db.insert(TABLE_DAILY_READ, null, contentValues);
        db.close();
        Log.d(Constants.TAG+"@Database","Insert Data Success");
        return true;
    }

    /*
     * This method taking two arguments
     * first one is the id of the name for which
     * we have to update the sync status
     * and the second one is the status that will be changed
     * */
    public boolean updateNameStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_DAILY_READ, contentValues, COLUMN_ID + "=" + id, null);
        db.close();
        return true;
    }

    /*
     * this method will give us all the name stored in sqlite
     * */
    public Cursor getNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_DAILY_READ + " ORDER BY " + COLUMN_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

   // select sum(amount) from transaction_table where category = 'Salary';

    public Cursor getUserTotalPoints(String user){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_DAILY_READ + " WHERE =" + user + " ;";
        Cursor c = db.rawQuery(sql, null);
        return c;

    }

    public Cursor getActivityData(String date){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACTIVTIES, new String[]{"map_image","activity_type"},
                "date" + " LIKE ?" ,
                new String[] {"%" + date + "%"},
                null,
                null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;

    }


    public String getTotal(String user){

        SQLiteDatabase db = this.getReadableDatabase();
            int total = 0;

            Cursor c = db.rawQuery("SELECT SUM(" + (CCOLUMN_POINT) + ") FROM " + TABLE_DAILY_READ+ " WHERE user = "+user, null);
            if(c.moveToFirst()){
                total = c.getInt(0);
            }
            return String.valueOf(total);
    }


    public boolean checkToday(String user){
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String today = df.format(c);
        SQLiteDatabase db = this.getReadableDatabase();
       // Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " where date = '" +today + "'" + " and where user = '" +user + "'" , null);

        //Cursor cursor = db.rawQuery("SELECT * FROM ? where date = ?", new String[] {TABLE_NAME, today});

        Cursor cursor = db.query(TABLE_DAILY_READ, new String[] { COLUMN_USER, COLUMN_DATE, COLUMN_POINT },
                COLUMN_DATE + " LIKE ? AND " + COLUMN_USER + " LIKE ?",
                new String[] {"%" + today + "%", "%" + user + "%"},
                null, null, null, null);

        return cursor != null && cursor.getCount() > 0;
    }

    public void addORupdate(String date, String points, int step, String distances, String kcals){
        SQLiteDatabase db = this.getWritableDatabase();
        String user = sharedPreferences.getString("userID","");


            if (checkToday(user)) {

                if (!points.equals("0" )){

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(COLUMN_POINT, points);
                    contentValues.put("steps", String.valueOf(step));
                    contentValues.put("distance", distances);
                    contentValues.put("kcals", kcals);
                    db.update(TABLE_DAILY_READ, contentValues, "user='"+user+"' and date='"+date+"'" , null);
                    db.close();
                    Log.d(Constants.TAG + "@Database", "Update Data Success:"+points);

                }


            } else {


                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_USER, user);
                contentValues.put(COLUMN_POINT, points);
                contentValues.put("steps", String.valueOf(step));
                contentValues.put("distance", distances);
                contentValues.put("kcals", kcals);
                contentValues.put(COLUMN_DATE, date);
                contentValues.put(COLUMN_STATUS, false);

                db.insert(TABLE_DAILY_READ, null, contentValues);
                db.close();
                Log.d(Constants.TAG + "@Database", "Insert Data Success:"+points);

            }

    }



    /*
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     * */
    public Cursor getUnsyncedNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_DAILY_READ + " WHERE " + COLUMN_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


    ////////// added sync methods data to be used //////////

    public ArrayList<ActivityListModel> toSync(){
        ArrayList<ActivityListModel> activityListModels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_ACTIVTIES + " WHERE sync = 'false';";
        Cursor c =  db.rawQuery(sql,null);

        // moving our cursor to first position.
        if (c.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                activityListModels.add(new ActivityListModel(
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        Integer.parseInt(c.getString(4)),
                        c.getString(5),
                        c.getString(6),
                        c.getString(7),
                        c.getString(8),
                        c.getString(9),
                        c.getString(10),
                        c.getString(11),
                        c.getString(12),
                        Boolean.parseBoolean(c.getString(13))

                ));

                Log.d(Constants.TAG+"DailyCursor",c.getString(1));


            } while (c.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        c.close();

        // list of rows which are not synced
        return activityListModels;
    }

    public List<DailyReads> dailyReads(){
        List<DailyReads> dailyReadsData = new ArrayList<DailyReads>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_DAILY_READ + " WHERE " +COLUMN_STATUS+" = 'false';";
        Cursor c =  db.rawQuery(sql,null);

        // looping through the cursor data
        if (c.moveToFirst()) {
            do {
                DailyReads daily = new DailyReads();
                daily.setId(Integer.parseInt(c.getString(0)));
                daily.setUser(c.getString(1));
                daily.setUser(c.getString(2));
                daily.setPoints(c.getString(3));
                daily.setSteps(c.getString(4));
                daily.setDistance(c.getString(5));
                daily.setKcals(c.getString(6));
                daily.setDate(c.getString(7));
                daily.setSync(Boolean.parseBoolean(c.getString(11)));

                Log.d(Constants.TAG+"DailyCursor",c.getString(1));

                // adding data to array list
                dailyReadsData.add(daily);
            } while (c.moveToNext());
        }

        // list of rows which are not synced
        return dailyReadsData;
    }

    ////////// added sync methods data to be used //////////
}
