package com.cropimage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.login.widget.LoginButton;
import com.jhengweipan.Guandisignonehundred.R;
import com.jhengweipan.MyAPI.VersionChecker;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

    public static  void loadImage(final String path,
                                  final ImageView imageView, final Activity activity){

        new Thread(){

            @Override
            public void run() {

                try {
                    URL imageUrl = new URL(path);
                    HttpURLConnection httpCon =
                            (HttpURLConnection) imageUrl.openConnection();
                    InputStream imageStr =  httpCon.getInputStream();
                    final Bitmap bitmap =  BitmapFactory.decodeStream(imageStr);

                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            imageView.setImageBitmap(bitmap);
                        }
                    });


                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    Log.e("Howard", "MalformedURLException:" + e);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.e("Howard", "IOException:"+e);
                }



            }


        }.start();

    }
    public static  void configVersionCheck(final  Context context) {

//        if (!GtApi.checkNetwork(IndexActivity.this)) return;

        VersionChecker.checkOnce(context, new VersionChecker.DoneAdapter() {

            @Override
            public void onHasNewVersion() {
                new AlertDialog.Builder(context)
                        .setTitle("已有最新版本!")
                        .setMessage("目前有最新版本上架,請盡快更新")
                        .setNegativeButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                context.startActivity(VersionChecker.openMartketIntent());
                                dialog.dismiss();
                            }
                        })
                        .show();
            }


        });

    }
}
