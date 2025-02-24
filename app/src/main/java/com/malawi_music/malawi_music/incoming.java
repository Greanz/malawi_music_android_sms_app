package com.malawi_music.malawi_music;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by OWEN KALUNGWE on 06/04/2017.
 */
public class incoming extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {
            sql mysql = new sql();
            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                boolean canInsert=true;

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message   = currentMessage.getDisplayMessageBody();

                    Boolean filter[] = getFilters(senderNum,mysql);
                    if(filter[0]==true){
                        canInsert = false; // it has filters - sender should be validated
                        if(filter[1]==true) canInsert=true; // sender is trusted
                    }
                    if(canInsert) {
                        String query = "INSERT INTO messages(sms_text,sms_from,sms_time,sms_status,sms_seen) VALUES" +
                                "('" + mysql.strEscape(message) + "'," +
                                "'" + mysql.strEscape(senderNum) + "'," +
                                "'" + mysql.strEscape(sql.date_dmy_time) + "','1','1');";
                        if (mysql.SQLInsert(query, sql.db)) {
                            if(helper.getPreference("SMS_VIBRATOR",context).equals("on")){
                                try {
                                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                    v.vibrate(500);
                                }catch (Exception v){
                                    //Log("Vibrator",v.toString());
                                }
                            }
                            try{
                                Intent myService = new Intent(context, restart.class);
                                context.sendBroadcast(myService);
                            }
                            catch (Exception v){

                            }
                            toast(context, "SMS received");
                        }
                        else toast(context, "SMS Not Saved");
                    }
                    else{
                        // Message not accepted
                        toast(context, "SMS From untrusted sender");
                    }
                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
        }
    }

    /*Response*/
    public void toast(Context context, String str){
        if(helper.getPreference("SMS_NOTIFIER",context).equals("on"))
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    /*
        Connecting to database
    */
    public SQLiteDatabase connect(Context context){
        if(sql.db == null){
            sql.db = context.openOrCreateDatabase(sql.DATABASE, context.MODE_PRIVATE, null);
        }
        return sql.db;
    }

    public Boolean[] getFilters(String From, sql sql){
        String query = "SELECT filter FROM filters";
        Cursor cursor = sql.SQLSelect(query);
        Boolean boolz[] = new Boolean[2];
        boolz[0]=false;// has no filters
        boolz[1]=false;
        if(sql.SQLHasRows(cursor)){
            boolz[0]=true;// has filters - has to filter
            while (cursor.moveToNext()){
                if(cursor.getString(0).equals(From.toLowerCase())){
                    boolz[1]=true;//the sender is trusted
                    break;
                }
            }
        }
        try{
            cursor.close();
        }catch (Exception e){
            Log.i("MYSQL",e.toString());
        }
        return boolz;
    }
}