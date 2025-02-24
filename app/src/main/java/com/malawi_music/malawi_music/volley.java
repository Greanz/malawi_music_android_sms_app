package com.malawi_music.malawi_music;

/**
 * Created by OWEN KALUNGWE on 11/11/2019.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by OWEN KALUNGWE on 23/05/2018.
 * Updated on 16-07-2018
 */

public class volley {
    static RequestQueue queue=null;
    public static String TAG = "MY_VOLLEY";
    volley(final String url, final String key, final String password, final String msg,final Context context, final sql sql,
           final SQLiteDatabase db){
        // Instantiate the RequestQueue.
        if(queue==null){
            queue = Volley.newRequestQueue(context);
        }
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.i(TAG, response.toString());
                try{
                    NotificationIntentService.isSyncing=false;;
                }catch (Exception e){

                }
                if(response.contains("received")) {
                    sql.SQLInsert("UPDATE messages SET sms_status='2' WHERE sms_status='1'",db);
                    if(helper.getPreference("SYNC_VIBRATOR",context).equals("on")) {
                        try {
                            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(500);
                        } catch (Exception v) {
                            //Log("Vibrator",v.toString());
                        }
                    }
                }
                else {
                    toast(context,response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    NotificationIntentService.isSyncing=false;;
                }catch (Exception e){

                }
                toast(context,"Unable to connect to "+url);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("message",msg);
                    params.put("password",password);
                    params.put("key",key);
                } catch (Exception e) {
                    toast(context,"Unable to map");
                }
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }};
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        // ends here
        return;
    }

    // response
    public void toast(Context context, String str){
        if(helper.getPreference("SYNC_ERROR",context).equals("on"))
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }
}