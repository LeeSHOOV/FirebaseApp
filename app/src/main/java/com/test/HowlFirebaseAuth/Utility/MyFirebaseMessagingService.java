package com.test.HowlFirebaseAuth.Utility;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.test.HowlFirebaseAuth.Activity.HomeActivity;
import com.test.HowlFirebaseAuth.R;

import java.util.Map;

/**
 * Created by admin on 2017/10/03.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();


        Map<String, String> data = remoteMessage.getData();

        String name = data.get("name");
        String title = data.get("title");

        showNotification(remoteMessage, name, title);

    }

    public void showNotification(RemoteMessage remoteMessage, String name, String title){

        AlarmAlertWakeLock.acquireCpuWakeLock(getApplicationContext());

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //클릭할 때까지 액티비티 실행을 보류하고 있는 PendingIntent 객체 생성
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //사운드 기능 추가
        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(name);
        notificationBuilder.setContentText(title/*remoteMessage.getNotification().getBody()*/);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setSound(soundUri);
        notificationBuilder.setVibrate(new long[] {100,200,300,400}); // pattern의 첫번째 파라미터는 wait시간, 두번째는 진동시간(단위 ms)
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(0, notificationBuilder.build());

/*        //NotificationManage 생성
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //NotificationManager가 알림(Notification)을 표시, id는 알림구분용
        notificationManager.notify(0, notificationBuilder.build());*/

    }

    private void sendNotification(){

    }

    @Override
    public void onMessageSent(String msgID) {
        Log.d("TAG", "onMessageSent: " + msgID );
    }

    @Override
    public void onSendError(String msgID, Exception e) {
        Log.d("TAG", "onSendError: " + e.getMessage() );
    }
}


