package com.cropimage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.jhengweipan.Guandisignonehundred.R;

/**
 * Created by HYXEN20141227 on 2016/10/18.
 */
public class MyApi {
    private static final int NOTIFICATION_ID = 0;
    private static  NotificationManager notificationManger;
    private static  Notification notification;
    private static  Intent intent;
    public static void  setNotification(Context context,Class<?> cls,int icon,String title,String text,int raw){
        notificationManger = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        intent = new Intent();
        intent.setClass(context,cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification = new Notification.Builder(context)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + raw))
                .setContentIntent(pendingIntent)
                .build(); // available from API level 11 and onwards
        notification.flags = Notification.FLAG_AUTO_CANCEL;
    }
    public  static  void  getNotification(){
        notificationManger.notify(0, notification);
    }


}
