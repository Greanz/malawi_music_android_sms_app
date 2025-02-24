package com.malawi_music.malawi_music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by OWEN KALUNGWE on 11/11/2019.
 */
public class restart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, NotificationIntentService.class));
        context.startService(new Intent(context, incoming.class));
    }
}