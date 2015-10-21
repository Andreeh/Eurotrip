package com.example.karlmosenbacher.eurotrip;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Andree on 2015-10-21.
 */
public class TimerService extends Service {
    private static final String TAG = "BroadcastService";
    public static final String TIMER_BR = "com.example.karlmosenbacher.eurotrip.timer_br";
    Intent broadcastIntent = new Intent(TIMER_BR);
    CountDownTimer countDownTimer = null;
    static final int startTime = 900000;

    @Override
    public void onCreate() {
        super.onCreate();
        countDownTimer = new CountDownTimer(startTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                Log.i(TAG, "Seconds remaining: " + millisUntilFinished / 1000);
                broadcastIntent.putExtra("countdown", millisUntilFinished);
                sendBroadcast(broadcastIntent);
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
            }
        };
        countDownTimer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        countDownTimer.cancel();
        Log.i(TAG, "Timer Cancelled");
        super.onDestroy();
    }
}
