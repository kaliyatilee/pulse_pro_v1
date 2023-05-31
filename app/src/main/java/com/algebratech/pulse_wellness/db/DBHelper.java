package com.algebratech.pulse_wellness.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.algebratech.pulse_wellness.models.ActivityListModel;
import com.algebratech.pulse_wellness.models.DailyReads;
import com.algebratech.pulse_wellness.models.WeightMonitoringModel;
import com.algebratech.pulse_wellness.utils.Constants;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tgio.rncryptor.RNCryptorNative;

import static android.content.Context.MODE_PRIVATE;

public class DBHelper extends SQLiteOpenHelper {

    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "pulse.db";
    public static final String TABLE_ACTIVTIES = "activities";
    public static final String TABLE_DAILY_READ = "dailyreads";
    public static final String TABLE_WEIGHT_MONITOR = "weightmonitoring";
    public static final String TABLE_TARGET_WEIGHT = "targetweight";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_POINT = "points";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_STATUS = "sync";
    public static final String COLUMN_ACTIVITY_TYPE = "activity_type";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_DATE_RECORDED = "recorded_on";
    public static final String COLUMN_IS_SYNCED = "sync";

    //database version
    private static final int DB_VERSION = 5;
    private static final String CCOLUMN_POINT = "points";
    SharedPreferences sharedPreferences;


    //Constructor
    public DBHelper(Context context) {
        //super(context, DB_NAME, null, DB_VERSION);
        super(context, DB_NAME, null, DB_VERSION);
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_DAILY_READ
                + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_USER +
                " VARCHAR, " + COLUMN_POINT + " VARCHAR, steps TEXT, distance TEXT, kcals TEXT, " + COLUMN_DATE + " VARCHAR, " + COLUMN_STATUS +
                " BOOLEAN);";

        db.execSQL("CREATE TABLE " + TABLE_ACTIVTIES + "(id INTEGER primary key autoincrement NOT NULL, activity_type TEXT, " +
                "user TEXT,duration TEXT, steps INTEGER, distance TEXT, average_pace TEXT, average_heartrate TEXT, kcals TEXT, date TEXT,map_path TEXT, map_image TEXT, camera_image TEXT ,sync BOOLEAN)");

        db.execSQL("CREATE TABLE " + TABLE_WEIGHT_MONITOR + "(id INTEGER primary key autoincrement NOT NULL, recorded_on LONG,weight FLOAT ,sync BOOLEAN)");
        db.execSQL("CREATE TABLE " + TABLE_TARGET_WEIGHT + "(id INTEGER primary key autoincrement NOT NULL, recorded_on LONG,weight FLOAT ,sync BOOLEAN)");

        db.execSQL(sql);
        Log.d(Constants.TAG + "@Database", "Create Tables Success");
    }

    //Insert Activities

    public void insertActivities(String activity_type, String user, String duration, int steps, String distance, String avg_pace, String avg_heart_rate,
                                 String kCals, String date, String map_path, String map_image, String camera_image, Boolean sync) {
        SQLiteDatabase db = this.getWritableDatabase(Constants.KEY);
        ContentValues contentValues = new ContentValues();
        contentValues.put("activity_type", activity_type);
        contentValues.put("user", user);
        contentValues.put("duration", duration);
        contentValues.put("steps", steps);
        contentValues.put("distance", distance);
        contentValues.put("average_pace", avg_pace);
        contentValues.put("average_heartrate", avg_heart_rate);
        contentValues.put("kcals", kCals);
        contentValues.put("date", date);
        contentValues.put("map_path", map_path);
        contentValues.put("map_image", map_image);
        contentValues.put("camera_image", camera_image);
        contentValues.put("sync", sync);


        long result = db.insert(TABLE_ACTIVTIES, null, contentValues);
        Log.e("dd", getTableAsString(db, TABLE_ACTIVTIES));
        db.close(); // Close database connection
    }

    public String getTableAsString(SQLiteDatabase db, String tableName) {
        Log.d("getTableAsString", "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }

    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            Log.d(Constants.TAG + "@Database :", " Old is" + oldVersion + " not equals to New :" + newVersion);
            String sql = "DROP TABLE IF EXISTS activities";
            String sql2 = "DROP TABLE IF EXISTS dailyreads";
            String sql3 = "DROP TABLE IF EXISTS weightmonitoring";
            String sql4 = "DROP TABLE IF EXISTS targetweight";
            db.execSQL(sql);
            db.execSQL(sql2);
            db.execSQL(sql3);
            db.execSQL(sql4);
        }
        Log.d(Constants.TAG + "@Database :", " Old is" + oldVersion + " equals to New :" + newVersion);
        onCreate(db);
    }

    /*
     * This method is taking two arguments
     * first one is the name that is to be saved
     * second one is the status
     * 0 means the name is synced with the server
     * 1 means the name is not synced with the server
     * */
    public boolean addName(String user, String date, String points, int status) {
        SQLiteDatabase db = this.getWritableDatabase(Constants.KEY);
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_USER, user);
        contentValues.put(COLUMN_POINT, points);
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_STATUS, status);


        db.insert(TABLE_DAILY_READ, null, contentValues);
        db.close();
        Log.d(Constants.TAG + "@Database", "Insert Data Success");
        return true;
    }

    /*
     * This method taking two arguments
     * first one is the id of the name for which
     * we have to update the sync status
     * and the second one is the status that will be changed
     * */
    public boolean updateNameStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase(Constants.KEY);
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
        SQLiteDatabase db = this.getReadableDatabase(Constants.KEY);
        String sql = "SELECT * FROM " + TABLE_DAILY_READ + " ORDER BY " + COLUMN_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    // select sum(amount) from transaction_table where category = 'Salary';

    public Cursor getUserTotalPoints(String user) {
        SQLiteDatabase db = this.getReadableDatabase(Constants.KEY);
        String sql = "SELECT * FROM " + TABLE_DAILY_READ + " WHERE =" + user + " ;";
        Cursor c = db.rawQuery(sql, null);
        return c;

    }

    public Cursor getActivityData(String date) {
        SQLiteDatabase db = this.getReadableDatabase(Constants.KEY);
        Cursor cursor = db.query(TABLE_ACTIVTIES, new String[]{"map_image", "activity_type"},
                "date" + " LIKE ?",
                new String[]{"%" + date + "%"},
                null,
                null,
                null
        );
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;

    }


    public String getTotal(String user) {

        SQLiteDatabase db = this.getReadableDatabase(Constants.KEY);
        int total = 0;

        Cursor c = db.rawQuery("SELECT SUM(" + (CCOLUMN_POINT) + ") FROM " + TABLE_DAILY_READ + " WHERE user = " + user, null);
        if (c.moveToFirst()) {
            total = c.getInt(0);
        }
        return String.valueOf(total);
    }


    public boolean checkToday(String user) {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String today = df.format(c);
        SQLiteDatabase db = this.getReadableDatabase(Constants.KEY);
        // Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " where date = '" +today + "'" + " and where user = '" +user + "'" , null);

        //Cursor cursor = db.rawQuery("SELECT * FROM ? where date = ?", new String[] {TABLE_NAME, today});

        Cursor cursor = db.query(TABLE_DAILY_READ, new String[]{COLUMN_USER, COLUMN_DATE, COLUMN_POINT},
                COLUMN_DATE + " LIKE ? AND " + COLUMN_USER + " LIKE ?",
                new String[]{"%" + today + "%", "%" + user + "%"},
                null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void addORupdate(String date, String points, int step, String distances, String kcals) {
        SQLiteDatabase db = this.getWritableDatabase(Constants.KEY);
        String user = sharedPreferences.getString("userID", "");


        if (checkToday(user)) {

            if (!points.equals("0")) {

                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_POINT, points);
                contentValues.put("steps", String.valueOf(step));
                contentValues.put("distance", distances);
                contentValues.put("kcals", kcals);
                db.update(TABLE_DAILY_READ, contentValues, "user='" + user + "' and date='" + date + "'", null);
                db.close();
                Log.d(Constants.TAG + "@Database", "Update Data Success:" + points);

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
            Log.d(Constants.TAG + "@Database", "Insert Data Success:" + points);

        }

    }


    /*
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     * */
    public Cursor getUnsyncedNames() {
        SQLiteDatabase db = this.getReadableDatabase(Constants.KEY);
        String sql = "SELECT * FROM " + TABLE_DAILY_READ + " WHERE " + COLUMN_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


    ////////// added sync methods data to be used //////////

    public ArrayList<ActivityListModel> toSync() {
        ArrayList<ActivityListModel> activityListModels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase(Constants.KEY);
        String sql = "SELECT * FROM " + TABLE_ACTIVTIES + " WHERE sync = 0;";
        Cursor c = db.rawQuery(sql, null);

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

                Log.d(Constants.TAG + "DailyCursor", c.getString(1));

            } while (c.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        c.close();
        Log.d(Constants.TAG + "DailyCursor", Arrays.toString(activityListModels.toArray()));

        // list of rows which are not synced
        return activityListModels;
    }

    public List<DailyReads> dailyReads() {
        List<DailyReads> dailyReadsData = new ArrayList<DailyReads>();
        SQLiteDatabase db = this.getReadableDatabase(Constants.KEY);
        String sql = "SELECT * FROM " + TABLE_DAILY_READ + " WHERE " + COLUMN_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);

        // looping through the cursor data

        if (c.moveToFirst()) {
            do {
                DailyReads daily = new DailyReads();
                daily.setId(Integer.parseInt(c.getString(0)));
                daily.setUser(c.getString(1));
                daily.setPoints(c.getString(2));
                daily.setSteps(c.getString(3));
                daily.setDistance(c.getString(4));
                daily.setKcals(c.getString(5));
                daily.setDate(c.getString(6));
                daily.setSync(false);

                Log.d(Constants.TAG + "DailyCursor", c.getString(1));

                // adding data to array list
                dailyReadsData.add(daily);
            } while (c.moveToNext());
        }

        // list of rows which are not synced
        return dailyReadsData;
    }

    public ArrayList<ActivityListModel> getAllActivities(String type) {
        ArrayList<ActivityListModel> activityListModels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase(Constants.KEY);
        String sql = "SELECT * FROM " + TABLE_ACTIVTIES + " WHERE " + COLUMN_ACTIVITY_TYPE + " = '" + type + "';";
        Cursor c = db.rawQuery(sql, null);

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

                Log.d(Constants.TAG + "DailyCursor", c.getString(1));

            } while (c.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        c.close();
        Log.d(Constants.TAG + "DailyCursor", Arrays.toString(activityListModels.toArray()));

        // list of rows which are not synced
        return activityListModels;
    }

    ////////// added sync methods data to be used //////////

    public void addORupdateTwo(String date, int step, String distances, String kcals) {

        SQLiteDatabase db = this.getWritableDatabase(Constants.KEY);
        String user = sharedPreferences.getString("userID", "");
        if (checkToday(user)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("steps", String.valueOf(step));
            contentValues.put("distance", distances);
            contentValues.put("kcals", kcals);
            contentValues.put(COLUMN_STATUS, false);
            db.update(TABLE_DAILY_READ, contentValues, "user='" + user + "' and date='" + date + "'", null);
            db.close();
            Log.d(Constants.TAG + "@Database", "Update Data Success:" + step);


        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_USER, user);
            contentValues.put("steps", String.valueOf(step));
            contentValues.put("distance", distances);
            contentValues.put("kcals", kcals);
            contentValues.put(COLUMN_DATE, date);
            contentValues.put(COLUMN_STATUS, false);

            db.insert(TABLE_DAILY_READ, null, contentValues);
            db.close();
            Log.d(Constants.TAG + "@Database", "Insert Data Success:" + step);

        }

    }

    public boolean recordWeight(Long date_recorded, Float weight, Boolean sync) {
        SQLiteDatabase db = this.getWritableDatabase(Constants.KEY);
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_DATE_RECORDED, date_recorded);
        contentValues.put(COLUMN_WEIGHT, weight);
        contentValues.put(COLUMN_IS_SYNCED, sync);

        db.insert(TABLE_WEIGHT_MONITOR, null, contentValues);
        db.close();
        Log.d(Constants.TAG + "@Database", "Insert Data Success");
        return true;
    }

    public boolean setOrUpdateTargetWeight(Long date_recorded, Float weight, Boolean sync) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATE_RECORDED, date_recorded);
        contentValues.put(COLUMN_WEIGHT, weight);
        contentValues.put(COLUMN_IS_SYNCED, sync);

        SQLiteDatabase db = this.getWritableDatabase(Constants.KEY);
        String query = "SELECT * FROM " + TABLE_TARGET_WEIGHT + " ORDER BY id DESC LIMIT 1";
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            System.out.println("++++++++++++++New Exist");
        db.insert(TABLE_TARGET_WEIGHT, null, contentValues);
        db.close();
        Log.d(Constants.TAG + "@Database", "Insert Data Success");
        return true;
        }
        else{
            db.update(TABLE_TARGET_WEIGHT, contentValues, "id=1",null);
            System.out.println("++++++++++++++Exist");
            db.close();
            return  false;
        }

    }

    public WeightMonitoringModel getTargetWeight(){
        SQLiteDatabase db = this.getReadableDatabase(Constants.KEY);
        String query = "SELECT * FROM " + TABLE_TARGET_WEIGHT + " ORDER BY id DESC LIMIT 1";
        Cursor c = db.rawQuery(query, null);
        WeightMonitoringModel weightMonitoringModel = null; // Initialize to null

        if (c != null && c.moveToFirst()) { // Check if Cursor is not null and has data
            weightMonitoringModel = new WeightMonitoringModel();
            weightMonitoringModel.setDateRecorded(c.getLong(1));
            weightMonitoringModel.setWeight(c.getFloat(2));
            c.close();
        }


        return weightMonitoringModel;

    }

    public ArrayList<WeightMonitoringModel> getWeightMonitoringRecord() {
        ArrayList<WeightMonitoringModel> weightMonitoringModel = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase(Constants.KEY);
        String sql = "SELECT * FROM " + TABLE_WEIGHT_MONITOR + ";";
        Cursor c = db.rawQuery(sql, null);

        // moving our cursor to first position.
        if (c.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                weightMonitoringModel.add(new WeightMonitoringModel(
                        c.getLong(1),
                        c.getFloat(2)
                ));
                Log.d(Constants.TAG + "WeightCursor", "" + c.getLong(1));
                Log.d(Constants.TAG + "WeightCursor", "" + c.getFloat(2));

            } while (c.moveToNext());
        }
        c.close();

        return weightMonitoringModel;
    }

    public WeightMonitoringModel getLatestWeightReading() {
        SQLiteDatabase db = this.getReadableDatabase(Constants.KEY);
        String query = "SELECT * FROM " + TABLE_WEIGHT_MONITOR + " ORDER BY id DESC LIMIT 1";
        Cursor c = db.rawQuery(query, null);
        WeightMonitoringModel weightMonitoringModel = null; // Initialize to null

        if (c != null && c.moveToFirst()) { // Check if Cursor is not null and has data
            weightMonitoringModel = new WeightMonitoringModel();
            weightMonitoringModel.setDateRecorded(c.getLong(1));
            weightMonitoringModel.setWeight(c.getFloat(2));
            c.close();
        }

        return weightMonitoringModel;
    }
}
