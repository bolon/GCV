package com.rere.fish.gcv.bgservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.rere.fish.gcv.MainActivity;
import com.rere.fish.gcv.R;

import timber.log.Timber;

/**
 * Created by Android dev on 6/6/17.
 */

public class CBWatcherService extends Service {
    static final String IG_REGEX = "instagram.com/p/";

    private ClipboardManager.OnPrimaryClipChangedListener listener = () -> initCheckingCopiedText();

    private void initCheckingCopiedText() {
        ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        try {
            String s = cb.getPrimaryClip().getItemAt(0).getText().toString();
            buildNotification(s);
        } catch (NullPointerException ex) {
            Timber.e("Type not handled");
        }
    }

    public void buildNotification(String s) {
        int id = (int) System.currentTimeMillis();

        Intent i = MainActivity.createIntentNotif(this, s, id);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                MainActivity.REQUEST_CODE_NOTIF, i, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification n = new Notification.Builder(this).setSmallIcon(
                R.mipmap.ic_launcher).setContentTitle("Instagram link detected").setContentText(
                "Want to process this link?").addAction(0, "PROCEED", pendingIntent).setAutoCancel(
                true).build();

        if (!s.contains(IG_REGEX)) return;

        NotificationManager notifMgr = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        // id to update the notification later on.
        notifMgr.notify(id, n);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        Timber.i("Service starting here...");

        ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cb.addPrimaryClipChangedListener(listener);

        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
