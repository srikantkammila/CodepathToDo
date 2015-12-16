package com.tryand.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by skammila on 12/10/15.
 */
public class RemindItemDueBootReceiver extends BroadcastReceiver {
    RemindItemDueReceiver alarm = new RemindItemDueReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
//            alarm.setAlarm(context);
        }
    }
}
//END_INCLUDE(autostart)