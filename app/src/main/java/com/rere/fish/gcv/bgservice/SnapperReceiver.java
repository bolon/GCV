package com.rere.fish.gcv.bgservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Android dev on 6/6/17.
 */

public class SnapperReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receiver", "Waking up service here...");
        context.startService(new Intent(context, CBWatcherService.class));
    }
}
