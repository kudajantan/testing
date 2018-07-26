package com.seseorang.demos.notif;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.seseorang.demos.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import login.LoginSessionDemos;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage.getData().size()>0){

            LoginSessionDemos loginSessionDemos   = new LoginSessionDemos(getApplicationContext());

            HashMap<String,String> hma  = loginSessionDemos.getUserDetailDemos();

            if(loginSessionDemos.isLoggedInDemos()) {
                Bitmap gambar = getBitmapfromUrl(remoteMessage.getData().get("image"));
                String click_action = remoteMessage.getData().get("click_action");
                String pesan = remoteMessage.getData().get("message");
                String title = remoteMessage.getData().get("title");
                String id_data = remoteMessage.getData().get("id_defect");
                int id_user  = Integer.parseInt(hma.get(LoginSessionDemos.ID_USER_DEMOS));
                String pwd      = hma.get(LoginSessionDemos.PWD_DEMOS);
                sendNotification(pesan, title, click_action, gambar, id_data, id_user, pwd);
            } else {
                Log.d("DeMOS", "Please Login First");
            }
        } else {
            Log.d("DeMOS", "KOSONG");
        }

    }


    private void sendNotification(String messageBody, String title, String click_action, Bitmap gambar, String id_data, int id_user, String pwd) {
        Intent intent = new Intent(click_action);
        intent.putExtra("id_data", id_data);
        intent.putExtra("id_user", String.valueOf(id_user));
        intent.putExtra("pwd", pwd);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int uniqueInt = (int) (System.currentTimeMillis() & 0xff);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), uniqueInt, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"notif_defect_demos");
        notificationBuilder.setSmallIcon(R.drawable.logo_kc)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo_kc))
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setContentTitle(title)
                .setContentText(Html.fromHtml(messageBody))
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(gambar))
                .setLights(Color.RED, 3000, 3000)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(0, notificationBuilder.build());
    }


    /*
     *To get a Bitmap image from the URL received
     * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

}
