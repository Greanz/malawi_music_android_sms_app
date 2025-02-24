package com.malawi_music.malawi_music;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class sms extends AppCompatActivity {

    public static String URL = "file:///android_asset/www/index.html";
    WebView browser;
    public static LinearLayout ll=null;
    Handler mHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        sql mysql = new sql();
        connect(this);
        try {
            mysql.CreateTables();
        }
        catch (Exception e){
            mysql.Log("Unable to create database","No databases created");
        }

        ll = (LinearLayout) findViewById(R.id.linearLayout);
        mHandle = new Handler();

        //browser = (WebView) findViewById(R.id.web);
        //browse(URL,browser,savedInstanceState);
        //browser = (WebView) findViewById(R.id.web);
        startApp(savedInstanceState);

        try{
            NotificationEventReceiver.setupAlarm(getApplicationContext());
        }catch (Exception e){

        }

        if(helper.getPreference("SMS_VIBRATOR",this).equals("")){
            helper.savePreference("SMS_VIBRATOR","on",this);
            helper.savePreference("SYNC_VIBRATOR","on",this);
            helper.savePreference("SMS_NOTIFIER","off",this);
            helper.savePreference("SYNC_ERROR","on",this);
        }
    }

    protected void startApp(Bundle savedInstanceState){
        browser = (WebView) findViewById(R.id.web);
        browse(URL,browser,savedInstanceState);
    }

    public SQLiteDatabase connect(Context context){
        if(sql.db == null){
            sql.db = context.openOrCreateDatabase(sql.DATABASE, context.MODE_PRIVATE, null);
        }
        return sql.db;
    }

    private void browse(String URL, final WebView web,Bundle b){
        try {
            //  Permission check
            if (!hasPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS})) {
                // Permission ask
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 111);

                return;
            }
            //  Permission check
            if (!hasPermissions(this, new String[]{Manifest.permission.READ_SMS})) {
                // Permission ask
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 111);

                return;
            }
        }catch (Exception p){

        }

        getWindow().setFeatureInt(2, -1);
        final Activity MyActivity = this;
        web.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                }
            }
        });

        web.setWebViewClient(new WebViewClient());
        web.getSettings().setJavaScriptEnabled(true);
        web.addJavascriptInterface(new WebAppInterface(this), "Android");
        web.getSettings().setDatabaseEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        web.getSettings().setAllowFileAccessFromFileURLs(true);
        web.getSettings().setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            web.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            web.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        if (b == null){;
            web.loadUrl(URL);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (browser.canGoBack()) {
                        browser.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState ){
        super.onSaveInstanceState(outState);
        browser.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        browser.restoreState(savedInstanceState);
    }

    public class WebAppInterface{
        Context mContext;
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void Vibrate(){
            try {
                Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);
            }catch (Exception v){
                //Log("Vibrator",v.toString());
            }
        }

        @JavascriptInterface
        public String getMessages(String seen){
            sql mysql = new sql();
            try {
                connect(mContext);
                return mysql.getMessages(seen, mContext, mysql.db);
            }catch (Exception e){
                return null;
            }
        }

        @JavascriptInterface
        public void HideFlash(){
            mHandle.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        browser.setVisibility(View.VISIBLE);
                        ll.setVisibility(View.GONE);
                    }catch (Exception vb){
                    }
                }
            });
        }

        @JavascriptInterface
        public String Delete(String Id){
            sql mysql = new sql();
            try {
                connect(mContext);
                if(mysql.SQLDelete("DELETE FROM messages WHERE id='"+Id+"'", mysql.db)){
                    Vibrate();
                    return "Message Deleted";
                }
                else{
                    return "Unable to delete";
                }
            }catch (Exception e){
                return "Error while deleting";
            }
        }

        @JavascriptInterface
        public String getInbox() {
            Uri inboxURI  = Uri.parse("content://sms/inbox");
            ContentResolver cr = getContentResolver();
            Cursor c = cr.query(inboxURI,null,null,null,null);
            String Message, From, Time, Status, SyncTime, Seen;
            JSONObject jObj;
            JSONArray jsonArray = new JSONArray();
            int count=0;
            if(c.getCount()>0) {
                while (c.moveToNext()) {
                    Message = c.getString(c.getColumnIndexOrThrow("body")).toString();
                    From = c.getString(c.getColumnIndexOrThrow("address")).toString();
                    Time = helper.setDateFromTimeStamp(c.getString(c.getColumnIndexOrThrow("date")).toString());
                    Status = "0";
                    SyncTime = "0";
                    Seen = "0";
                    jObj = new JSONObject();
                    try {
                        jObj.put("ID", helper.randomId(7));
                        jObj.put("msg", Message);
                        jObj.put("From", From);
                        jObj.put("Time", Time);
                        jObj.put("Status", Status);
                        jObj.put("SyncTime", SyncTime);
                        jObj.put("Seen", Seen);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    jsonArray.put(jObj);
                    if(count++==31)break;
                }
                c.close();
               return jsonArray.toString();
            }
            c.close();
            return null;
        }

        @JavascriptInterface
        public String importSMS(String message,String senderNum){
            sql mysql = new sql();
            try {
                connect(mContext);
                String query = "INSERT INTO messages(sms_text,sms_from,sms_time,sms_status,sms_seen) VALUES" +
                        "('" + mysql.strEscape(message) + "'," +
                        "'" + mysql.strEscape(senderNum) + "'," +
                        "'" + mysql.strEscape(sql.date_dmy_time) + "','3','1');";
                if (mysql.SQLInsert(query, sql.db)) {
                    Vibrate();
                    return "Message Saved";
                }
                return "Error, Importing";
            }catch (Exception e){
                return "Exception, Error importing";
            }
        }

        @JavascriptInterface
        public String addFilter(String name){
            sql mysql = new sql();
            name = name.toLowerCase();
            try {
                connect(mContext);
                Cursor check = mysql.SQLSelect("SELECT * FROM filters WHERE filter = '"+name+"'");
                if(mysql.SQLHasRows(check)){
                    return "Filter already exists";
                }
                String query = "INSERT INTO filters(filter) VALUES" +
                        "('" + mysql.strEscape(name) + "');";
                if (mysql.SQLInsert(query, sql.db)) {
                    Vibrate();
                    return "Filter saved successfully";
                }
                return "Unable to save filter";
            }catch (Exception e){
                return "Exception, Filter not saved";
            }
        }

        @JavascriptInterface
        public String getFilters() {
            sql mysql = new sql();
            try {
                SQLiteDatabase d = connect(mContext);
                String query = "SELECT * FROM filters ORDER BY filter ASC";
                Cursor filters = mysql.SQLSelect(query);
                if(mysql.SQLHasRows(filters)){
                    JSONObject jObj;
                    JSONArray jsonArray = new JSONArray();
                    String ID,Name,Color;
                    while (filters.moveToNext()){
                        ID          = filters.getString(0);
                        Name        = filters.getString(1);
                        Color       = mysql.getColor(Name.toLowerCase(),d);
                        jObj = new JSONObject();
                        try {
                            jObj.put("Id", ID);
                            jObj.put("Name", Name);
                            jObj.put("Color", Color);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        jsonArray.put(jObj);
                    }
                    return jsonArray.toString();
                }
                return null;
            }catch (Exception e){
                return null;
            }
        }

        @JavascriptInterface
        public String DeleteFilter(String Id){
            sql mysql = new sql();
            try {
                connect(mContext);
                if(mysql.SQLDelete("DELETE FROM filters WHERE id='"+Id+"'", mysql.db)){
                    Vibrate();
                    return "Filter Deleted";
                }
                else{
                    return "Unable to delete";
                }
            }catch (Exception e){
                return "Error while deleting";
            }
        }

        @JavascriptInterface
        public String addAPI(String u, String k, String p){
            sql mysql = new sql();
            try {
                connect(mContext);
                mysql.SQLDelete("DELETE FROM api", mysql.db);
                String query = "INSERT INTO api(api_url,api_password,api_key)"+
                        "VALUES('"+u+"','"+k+"','"+p+"');";
                if (mysql.SQLInsert(query, sql.db)) {
                    Vibrate();
                    return "API successfully saved";
                }
                return "Unable to save API";
            }catch (Exception e){
                return "Exception, API not saved";
            }
        }

        @JavascriptInterface
        public String setColor(String color, String name){
            sql mysql = new sql();
            try {
                connect(mContext);
                mysql.SQLDelete("DELETE FROM colors WHERE name = '"+name+"' ", mysql.db);
                String query = "INSERT INTO colors(name,color)"+
                        "VALUES('"+name+"','"+color+"');";
                if (mysql.SQLInsert(query, sql.db)) {
                    Vibrate();
                    return "Color applied";
                }
                return "Unable to apply color";
            }catch (Exception e){
                return "Exception, unable to add color";
            }
        }

        @JavascriptInterface
        public String getAPI() {
            sql mysql = new sql();
            try {
                connect(mContext);
                String query = "SELECT * FROM api ORDER BY id DESC";
                Cursor api = mysql.SQLSelect(query);
                if(mysql.SQLHasRows(api)){
                    JSONObject jObj;
                    JSONArray jsonArray = new JSONArray();
                    while (api.moveToNext()){
                        jObj = new JSONObject();
                        try {
                            jObj.put("url", api.getString(1));
                            jObj.put("key", api.getString(2));
                            jObj.put("password", api.getString(3));
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        jsonArray.put(jObj);
                    }
                    return jsonArray.toString();
                }
                return null;
            }catch (Exception e){
                return null;
            }
        }

        @JavascriptInterface
        public String initiateSync(String Id) {
            sql mysql = new sql();
            try {
                mysql.SQLInsert("UPDATE messages SET sms_status='1' WHERE id = '"+Id+"' ",connect(mContext));
                return "Sent to que";
            }catch (Exception e){
                return "Unable to que";
            }
        }

        @JavascriptInterface
        public String getStatus(String Id) {
            sql mysql = new sql();
            try {
                connect(mContext);
                String query = "SELECT sms_status FROM messages WHERE id = '"+Id+"'";
                Cursor status = mysql.SQLSelect(query);
                if(mysql.SQLHasRows(status)){
                    while (status.moveToNext()){
                        return status.getString(0);
                    }
                }
                return null;
            }catch (Exception e){
                return null;
            }
        }

        @JavascriptInterface
        public String getPreference(String myPreference){
            //SMS_VIBRATOR SMS_NOTIFIER SYNC_ERROR SYNC_VIBRATOR
            return helper.getPreference(myPreference,mContext);
        }

        @JavascriptInterface
        public String setPreference(String myPreference,String option){
            //SMS_VIBRATOR SMS_NOTIFIER SYNC_ERROR SYNC_VIBRATOR
            Boolean i = helper.savePreference(myPreference,option,mContext);
            if(i){
                Vibrate();
                return getPreference(myPreference);
            }
            else{
                return "off";
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //ll.setVisibility(View.GONE);
                startApp(null);
            }
        }
    }
}