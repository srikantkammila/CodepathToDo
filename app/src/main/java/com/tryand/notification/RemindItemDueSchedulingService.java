package com.tryand.notification;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.tryand.R;
import com.tryand.codepathtodo.MainActivity;

import java.util.Random;

/**
 * Created by skammila on 12/10/15.
 */
public class RemindItemDueSchedulingService extends IntentService {
    public RemindItemDueSchedulingService() {
        super("RemindItemDueSchedulingService");
    }

    // An ID used to post the notification.
    public int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    @Override
    protected void onHandleIntent(Intent intent) {

        sendNotification(intent.getStringExtra("note_text"));
        // Release the wake lock provided by the BroadcastReceiver.
        RemindItemDueReceiver.completeWakefulIntent(intent);
    }

    // Post a notification indicating whether a doodle was found.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("DoNote")
                        .setContentText(msg)
                        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        NOTIFICATION_ID = new Random().nextInt();
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
