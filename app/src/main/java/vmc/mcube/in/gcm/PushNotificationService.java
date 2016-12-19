package vmc.mcube.in.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import vmc.mcube.in.R;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.activity.MainActivity;
import vmc.mcube.in.utils.Utils;


public class PushNotificationService extends GcmListenerService {

    private NotificationManager mNotificationManager;
    public static int NOTIFICATION_ID = 1;
    private String TAG = "GCMPRO";
    private String url;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String code = data.getString("code");
        //sendStickyNotification(message);
        if(code!=null && code.equals("205")){
            //sendNotification(message);
            Utils.logoutByGCM(getApplication(),message);
            updateMyActivity(getApplicationContext(),message);
        }

    }
    static void updateMyActivity(Context context, String message) {

        Intent intent = new Intent("unique_name");

        //put whatever data you want to send, if any
        intent.putExtra("message", message);

        //send broadcast
        context.sendBroadcast(intent);
    }
//    private void sendNotification(String msg) {
//        Intent resultIntent = new Intent(this, MainActivity.class);
//        TaskStackBuilder TSB = TaskStackBuilder.create(this);
//        TSB.addParentStack(MainActivity.class);
//        TSB.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                TSB.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
//        //Create notification object and set the content.
//        NotificationCompat.Builder nb = new NotificationCompat.Builder(this);
//        nb.setSmallIcon(R.drawable.ic_backup);
//        nb.setContentTitle("Set your title");
//        nb.setContentText("Set Content text");
//        nb.setTicker("Set Ticker text");
//        nb.addAction(R.drawable.ic_share_24dp, "Share", resultPendingIntent);
//        //  nb.setContent(new RemoteViews(new Tex))
//        nb.setContentText(msg);
//
//        //get the bitmap to show in notification bar
//        // Bitmap bitmap_image = BitmapFactory.decodeResource(this.getResources(), R.drawable.drawerr);
//        // Bitmap bitmap_image = getBitmapFromURL("http://images.landscapingnetwork.com/pictures/images/500x500Max/front-yard-landscaping_15/front-yard-hillside-banyon-tree-design-studio_1018.jpg");
//        Bitmap bitmap_image = getBitmapFromURL(url);
//        NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle().bigPicture(bitmap_image);
//        s.setSummaryText("Summary text appears on expanding the notification");
//        nb.setStyle(s);
//
////        Intent resultIntent = new Intent(this, MainActivity.class);
////        TaskStackBuilder TSB = TaskStackBuilder.create(this);
////        TSB.addParentStack(MainActivity.class);
//
//        // Adds the Intent that starts the Activity to the top of the stack
//
//
//        nb.setContentIntent(resultPendingIntent);
//        nb.setAutoCancel(true);
//        NotificationManager mNotificationManager =
//                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        // mId allows you to update the notification later on.
//        mNotificationManager.notify(11221, nb.build());
//
//
//    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendNotification1(String message) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.mcube)
                .setContentTitle("MCube Alert")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setLights(Color.RED, 1, 1)
                .setColor(getApplicationContext().getResources().getColor(R.color.accent))
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendStickyNotification(String message) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.mcube)
                .setContentTitle("title")
                .setAutoCancel(false)
                .setOngoing(true)
                .setSound(defaultSoundUri)
                .setContentText(message)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }




    private void sendNotification(String msg) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder TSB = TaskStackBuilder.create(this);
        TSB.addParentStack(MainActivity.class);
        TSB.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                TSB.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.largeicon);
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this);
        nb.setContentText(msg);
        nb.setContentTitle("MCube");
        nb.setTicker(msg);
        nb.setSmallIcon(R.drawable.smallicon);
        nb.setLargeIcon(bm);
        nb.setColor(ContextCompat.getColor(getApplicationContext(), R.color.accent));
        // nb.addAction(R.drawable.ic_share_24dp, "Share", resultPendingIntent);
        //  nb.setContent(new RemoteViews(new Tex))
        nb.setContentText(msg);
        NotificationCompat.BigTextStyle s = new NotificationCompat.BigTextStyle();
        s.setBigContentTitle("MCube");
        s.bigText(msg);
        nb.setStyle(s);
        nb.setContentIntent(resultPendingIntent);
        nb.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, nb.build());


    }
}
