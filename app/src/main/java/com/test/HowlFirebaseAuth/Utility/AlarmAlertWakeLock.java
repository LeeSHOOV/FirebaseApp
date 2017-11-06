package com.test.HowlFirebaseAuth.Utility;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by admin on 2017/10/03.
 */
public class AlarmAlertWakeLock {
    private static final String TAG = "Sample_3";
    private static PowerManager.WakeLock sCpuWakeLock;

    static void acquireCpuWakeLock(Context context) {
        Log.d(TAG, "Acquiring cpu wake lock");
        if (sCpuWakeLock != null) {
            return;
        }
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE );
        PowerManager.WakeLock sCpuWakeLock = pm.newWakeLock( PowerManager.SCREEN_DIM_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG" );
        sCpuWakeLock.acquire(3000);
    }

    static void releaseCpuLock() {
        Log.d(TAG, "Releasing cpu wake lock");

        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}