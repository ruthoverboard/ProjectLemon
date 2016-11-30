package com.example.projectlemon.projectlemon;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Collection;
import java.util.Map;


public class FirebaseMessagesServices extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    static AWSHelper awsHelper = AWSHelper.getInstance();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.i(TAG, "From: " + remoteMessage.getFrom());
        Log.i(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "DATAAAAAA: " + remoteMessage.getData());
        Object[] data = remoteMessage.getData().values().toArray();
        Toast.makeText(this,remoteMessage.toString(),Toast.LENGTH_LONG);

        //Calling method to generate notification
        if (data[data.length-1].equals("Notification")) {
            sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        }else {
            sendLocation(data);
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, MapsActivityPedirRaite.class);
        Toast.makeText(this,messageBody,Toast.LENGTH_LONG);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }


    private void sendLocation(Object[] data){
        //AWSHelper.driver.setLatitude(Double.parseDouble(data[0].toString()));
        //AWSHelper.driver.setLongitude(Double.parseDouble(data[1].toString()));
    }
}
