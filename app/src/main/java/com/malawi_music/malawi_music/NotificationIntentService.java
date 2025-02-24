package com.malawi_music.malawi_music;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
//import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
//import android.support.v7.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by OWEN KALUNGWE on 04/09/2020.
 */

public class NotificationIntentService extends IntentService {

    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";

    NotificationManager m_notificationManager;

    int count=0;

    public static boolean isSyncing=false;

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
            if (ACTION_DELETE.equals(action)) {
                processDeleteNotification(intent);
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
        //Log.d(getClass().getSimpleName(), "onHandleIntent");
    }

    private void processStartNotification() {
        try {
            if (isMyServiceRunning(incoming.class)) {
                getApplicationContext().startService(new Intent(getApplicationContext(), incoming.class));
            }
        }catch (Exception e){

        }
        // Task
        String n = helper.valueOf(count++);
        n = "Synced: "+n+" SMS(s)";
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
            try{
                cursor.close();
            }catch (Exception e){
                Log.i("MYSQL",e.toString());
            }
            return api;
        }
        else{
            api[0] = null;   // url
            api[1] = null;  // key
            api[2] = null; // password
        }
        try{
            cursor.close();
        }catch (Exception e){
            Log.i("MYSQL",e.toString());
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
            try{
                obj.close();
            }catch (Exception e){
                Log.i("MYSQL",e.toString());
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

    private static int getRequestCode() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(900000);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void addNotification(String num) {
        try {
/*
            PendingIntent pendingIntent = PendingIntent.getActivity(this, getRequestCode()
                    sms.this,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                            R.mipmap.ic_launcher))
                    .setContentTitle("f") // use your own title
                    .setContentText("f")    // use your own message
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setBadgeIconType(Notification.BADGE_ICON_SMALL);


            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert notificationManager != null;
                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            assert notificationManager != null;
            notificationManager.notify(getRequestCode(), notificationBuilder.build());
            */


            Intent notificationIntent;
            notificationIntent = new Intent(this, sms.class);
            PendingIntent pi = PendingIntent.getActivity(NotificationIntentService.this, 0, notificationIntent,  0);
            Notification notification;
            notification = new NotificationCompat.Builder(NotificationIntentService.this)
                    .setTicker("Malawi Music")
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.FLAG_NO_CLEAR)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ii32)
                    .setContentTitle("Sync Service")
                    .setContentText(num)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setVibrate(new long[]{0L})
                    .setOnlyAlertOnce(true)
                    .build();
            NotificationManager notificationManager2 = (NotificationManager) NotificationIntentService.this.getSystemService(
                    Service.NOTIFICATION_SERVICE
            );
            notificationManager2.notify(100, notification);

/*
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

                // Configure the notification channel.
                notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                    .setVibrate(new long[]{0, 100, 100, 100, 100, 100})
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Content Title")
                    .setContentText("Content Text");

            notificationManager.notify(NOTIFICATION_ID, builder.build());
            */
        } catch (Exception n){
            Log.i("NOTIFICATION",n.toString());
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
/*
    public void sendNotification(String messageBody) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ii32)
                        .setColor(Color.parseColor("#5878f2"))
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true);

        Intent notificationIntent = new Intent(this, sms.class);
        notificationIntent.putExtra("message",messageBody);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        if(Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            builder.setChannelId(createNotificationChannel());
        }

        Notification notification = builder.build();
        notificationManager.notify(211, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannel(){
        String channelId = "demo";
        String channelName = "My demo";
        NotificationChannel mChannel = new NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_NONE);
        mChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        }
        return channelId;
    }*/
}
