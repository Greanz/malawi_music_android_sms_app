package com.malawi_music.malawi_music;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by OWEN KALUNGWE on 10/11/2019.
 */

public class sql {
    static String DATABASE = "smsXXX1", prepareQuery=null;
    static boolean logErrors =false;
    static SQLiteDatabase db;

    public void Log(String Title, String str){
        Log.i(Title, str);
    }

    static String date_dmy_time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a").format(new Date());

    //Create system database tables
    public boolean CreateTables(){
        for(int i =0; i<DATABASE_TABLES.length; i++){
            try{
                this.db.execSQL(DATABASE_TABLES[i]);
                if(logErrors) Log("DATABASE HELPER ERROR",DATABASE_TABLES[i] + " done");
            }
            catch (SQLiteAbortException e){
                if(logErrors) Log("DATABASE HELPER ERROR",DATABASE_TABLES[i] + " nop");
                return false;
            }
        }
        return true;
    }

    /*
        List of system database to be created and used
    */
    private String[] DATABASE_TABLES = {
            "CREATE TABLE IF NOT EXISTS 'messages' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'sms_text' TEXT, 'sms_from' TEXT" +
                    ", 'sms_time' TEXT, 'sms_status' TEXT, 'sms_sent_time' TEXT, 'sms_seen' TEXT);",
            "CREATE TABLE IF NOT EXISTS 'api'  ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'api_url' TEXT, 'api_password' TEXT" +
                    ", 'api_key' TEXT 'api_notifications_status' TEXT);",
            "CREATE TABLE IF NOT EXISTS 'colors'  ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'name' TEXT,'color' TEXT);",
            "CREATE TABLE IF NOT EXISTS 'filters'  ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'filter' TEXT);"
    };

    // Insert data into database tables
    public Boolean SQLInsert(String Query, SQLiteDatabase con){
        try {
            con.execSQL(Query);
            return true;
        }
        catch (Exception e){
            if(logErrors) {
               Log("MY_ERROR",e.toString());
            }
            return  false;
        }
    }
    // Delete data from database tables
    public Boolean SQLDelete(String Query, SQLiteDatabase con){
        try{
            con.execSQL(Query);
            return true;
        } catch (Exception e){
            this.prepareQuery = e.toString();
            return false;
        }
    }
    // Select data from database tables
    public Cursor SQLSelect(String query) {
        Cursor c;
        try{
            c = this.db.rawQuery(query,null);
            return c;
        } catch (Exception e){
            return null;
        }
    }
    // Check if query return rows
    public Boolean SQLHasRows(Cursor obj){
        if(obj==null) return false;
        try {
            if (obj.getCount() == 0) {
                return false;
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }
    // query str escape string
    public  String strEscape(String str){
        return str.replaceAll("'","\'");
    }

    /*Response*/
    public void toast(Context context, String str){
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }
    
    public String getColor(String name, SQLiteDatabase db){
        String query = "SELECT id,name,color FROM colors WHERE name = '"+name+"' ORDER BY id DESC";
        String Name="",Color="";
        Cursor obj = this.SQLSelect(query);
        JSONObject jObj;
        JSONArray jsonArray = new JSONArray();
        String update = "UPDATE messages SET sms_seen='2'";
        if(this.SQLHasRows(obj)){
            while (obj.moveToNext()) {
                Name = obj.getString(1).toLowerCase();
                Color = obj.getString(2).toLowerCase();
            }
            return Name+','+Color;
        }
        return null;
    }

    public String getMessages(String where,Context c, SQLiteDatabase db){
        if(where.length()==3){
            where = " WHERE sms_seen = '1' ";
        }
        else where="";
        String query = "SELECT * FROM messages "+where+" ORDER BY id DESC";
        String ID, Message, From, Time, Status, SyncTime, Seen, Color;
        Cursor obj = this.SQLSelect(query);
        JSONObject jObj;
        JSONArray jsonArray = new JSONArray();
        String update = "UPDATE messages SET sms_seen='2'";
        if(this.SQLHasRows(obj)){
            while (obj.moveToNext()) {
                ID          = obj.getString(0);
                Message     = obj.getString(1);
                From        = obj.getString(2);
                Time        = obj.getString(3);
                Status      = obj.getString(4);
                SyncTime    = obj.getString(5);
                Seen        = obj.getString(6);
                Color       = getColor(From.toLowerCase(),db);
                jObj = new JSONObject();
                try {
                    jObj.put("Id", ID);
                    jObj.put("msg", Message);
                    jObj.put("From", From);
                    jObj.put("Time", Time);
                    jObj.put("Status", Status);
                    jObj.put("SyncTime", SyncTime);
                    jObj.put("Seen", Seen);
                    jObj.put("Color", Color);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                jsonArray.put(jObj);
            }
            this.SQLInsert(update,db);
            try{
                obj.close();
            }catch (Exception e){
                Log.i("MYSQL",e.toString());
            }
            return jsonArray.toString();
        }
        try{
            obj.close();
        }catch (Exception e){
            Log.i("MYSQL",e.toString());
        }
        return null;
    }
}