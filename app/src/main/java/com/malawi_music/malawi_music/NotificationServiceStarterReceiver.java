package com.malawi_music.malawi_music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by OWEN KALUNGWE on 04/09/2020.
 */

public final class NotificationServiceStarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationEventReceiver.setupAlarm(context);
        //Log.i(context.getClass().getSimpleName(), "Hello 2");
    }
}
