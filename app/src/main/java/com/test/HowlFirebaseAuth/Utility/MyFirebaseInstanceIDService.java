package com.test.HowlFirebaseAuth.Utility;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by admin on 2017/10/03.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{

    private static final String TAG = "MyFirebaseIDService";

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);


        FirebaseMessaging.getInstance().subscribeToTopic("all");


        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

}
