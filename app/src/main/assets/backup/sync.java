package com.malawi_music.malawi_music;

import android.annotation.TargetApi;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;

import android.support.v7.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by OWEN KALUNGWE on 16/07/2018.
 */

public class sync extends Service {

    NotificationManager m_notificationManager;

    int count=0;

    public static boolean isSyncing=false;

    @Override
    public void onCreate() {
        super.onCreate();
        Intent myService = new Intent(getApplicationContext(), restart.class);
        sendBroadcast(myService);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        // Task
                        String n = helper.valueOf(count++);
                        n = "Synced: "+n+" SMS(s)";
                        if(!isMyServiceRunning(incoming.class)){
                            Intent myService = new Intent(getApplicationContext(), incoming.class);
                            startService(myService);
                        }
                        sql mysql = new sql();
                        SQLiteDatabase db = connect(getApplicationContext());
                        String[] api = getApi(mysql);
                        if(api==null){
                            addNotification("Enable api");
                        }
                        else{
                            try {
                                String password = api[2];
                                String url = api[0];
                                String key = api[1];
                                String messages = getMessages(mysql);
                                if (messages != null) {
                                    if (!isSyncing) {
                                        isSyncing = true;
                                        new volley(
                                                url,
                                                key,
                                                password,
                                                messages,
                                                getApplicationContext(),
                                                mysql,
                                                db
                                        );
                                    }
                                }
                                addNotification("Syncing.....");
                            }catch (Exception e){
                                addNotification("API temporary error");
                            }
                        }
                        //String update = "UPDATE messages SET sms_seen='2'";
                        //sql.SQLInsert(update,db);
                    }
                }, 0, 50000);
            }
        }).start();

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent myService = new Intent(getApplicationContext(), restart.class);
        sendBroadcast(myService);
    }

    @Override
    public void onDestroy() {
        Intent myService = new Intent(getApplicationContext(), restart.class);
        sendBroadcast(myService);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void addNotification(String num) {
        try {
            Intent notificationIntent;
            notificationIntent = new Intent(this, sms.class);
            PendingIntent pi = PendingIntent.getActivity(sync.this, 0, notificationIntent,  0);
            Notification notification;
            notification = new NotificationCompat.Builder(sync.this)
                    .setTicker("Malawi Music")
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.FLAG_NO_CLEAR)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ii32)
                    .setContentTitle("Sync Service Server")
                    .setContentText(num)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setVibrate(new long[]{0L})
                    .setOnlyAlertOnce(true)
                    .build();
            NotificationManager notificationManager2 = (NotificationManager) sync.this.getSystemService(Service.NOTIFICATION_SERVICE);
            notificationManager2.notify(100, notification);
        } catch (Exception n){

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private String[] getApi(sql sql){
        String q = "SELECT * FROM api ORDER BY id DESC LIMIT 1";
        Cursor cursor = sql.SQLSelect(q);
        String[] api = new String[3];
        if(sql.SQLHasRows(cursor)){
            while (cursor.moveToNext()){
                api[0] = cursor.getString(1);   // url
                api[1] = cursor.getString(2);  // key
                api[2] = cursor.getString(3); // password
            }
            return api;
        }
        else{
            api[0] = null;   // url
            api[1] = null;  // key
            api[2] = null; // password
        }
        return null;
    }

    public String getMessages(sql sql){
        String query = "SELECT * FROM messages WHERE sms_status = '1' ORDER BY id DESC";
        String ID, Message, From, Time, Status, SyncTime, Seen, Color;
        Cursor obj = sql.SQLSelect(query);
        JSONObject jObj;
        JSONArray jsonArray = new JSONArray();
        if(sql.SQLHasRows(obj)){
            while (obj.moveToNext()) {
                ID          = obj.getString(0);
                Message     = obj.getString(1);
                From        = obj.getString(2);
                Time        = obj.getString(3);
                Status      = obj.getString(4);
                SyncTime    = obj.getString(5);
                jObj = new JSONObject();
                try {
                    jObj.put("Id", ID);
                    jObj.put("msg", Message);
                    jObj.put("From", From);
                    jObj.put("Time", Time);
                    jObj.put("Status", Status);
                    jObj.put("SyncTime", SyncTime);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                jsonArray.put(jObj);
            }
            return jsonArray.toString();
        }
        return null;
    }

    private SQLiteDatabase connect(Context context){
        if(sql.db == null){
            sql.db = context.openOrCreateDatabase(sql.DATABASE, context.MODE_PRIVATE, null);
        }
        return sql.db;
    }
}