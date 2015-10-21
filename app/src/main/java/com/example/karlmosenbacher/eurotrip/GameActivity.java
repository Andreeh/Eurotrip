package com.example.karlmosenbacher.eurotrip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "BroadcastService - Game";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Button btn_play;
    private TextView textViewTimer, questionView, currentPointView;
    private static final int amountToMoveRight = 500, amountToMoveDown = -625;
    private GameActivity mainActivity;
    private EuroTripMainActivity parentActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getSupportActionBar();
        setUpMapIfNeeded();
        mainActivity = this;
        textViewTimer = (TextView)findViewById(R.id.timer);
        questionView = (TextView)findViewById(R.id.current_question);
        currentPointView = (TextView)findViewById(R.id.current_points);
        questionView.setMovementMethod(new ScrollingMovementMethod());

//        final RelativeLayout mainContainer = (RelativeLayout)findViewById(R.id.top_layout);

        Button submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_play = (Button)findViewById(R.id.play);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
 /*               int buttonHeight = btn_play.getHeight();
                System.out.println("buttonheight " + buttonHeight);
                int parentHeight = mainContainer.getHeight();
                System.out.println("parentheight " + parentHeight);
                int buttonWidth = btn_play.getWidth();
                System.out.println("buttonwidth " + buttonWidth);
                int parentWidth = mainContainer.getWidth();

                System.out.println("parentwidth " + parentWidth);


                System.out.println("btn get left " + btn_play.getLeft());
                System.out.println("btn get right " + btn_play.getRight());
                System.out.println("container get right " + mainContainer.getRight());
                //Check if button is in corner
                boolean isInCorner = btn_play.getLeft() > parentWidth/2;
                System.out.println("is in corner " + isInCorner);

                final int fromX = isInCorner?(parentWidth/2)-(buttonWidth/2):0;
                final int fromY = isInCorner?(parentHeight/2)-(buttonHeight/2):0;
                final int toX = isInCorner?0:(parentWidth/2)-(buttonWidth/2)-12;
                final int toY = isInCorner?0:(buttonHeight*2)-(parentHeight/2);*/


                Animation animation = new TranslateAnimation(0, amountToMoveRight, 0, amountToMoveDown);
/*                Animation animation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, fromX, TranslateAnimation.ABSOLUTE, toX,
                        TranslateAnimation.ABSOLUTE, fromY, TranslateAnimation.ABSOLUTE, toY);*/
                animation.setDuration(750);
//                animation.setFillAfter(true);
/*                animation.setAnimationListener(new TranslateAnimation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
*//*                        RelativeLayout top_layout = (RelativeLayout) findViewById(R.id.top_layout);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) top_layout.getLayoutParams();
                        params.topMargin += amountToMoveDown;
                        params.rightMargin += amountToMoveRight;
                        btn_play.setLayoutParams(params);*//*
                        System.out.println("btn get right" + btn_play.getRight());
                        btn_play.setVisibility(View.GONE);

                    }
                });*/
                btn_play.startAnimation(animation);
                btn_play.setVisibility(View.GONE);
                startService(new Intent(mainActivity, TimerService.class));
                Log.i(TAG, "Started TimerService");

            }
        });
    }



    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Update gui with new question etc.
            Log.i(TAG, "Received Broadcast");
            if (intent.getExtras() != null) {
                long millisUntilFinished = intent.getLongExtra("countdown",0);
                long min = (millisUntilFinished/ 1000) / 60; // convert millis to minutes
                long sec = (millisUntilFinished / 1000) % 60; // convert millis to seconds
                updateTimer(min, sec); // update textview

            }
        }


    };

    private void updateTimer(long min, long sec) {
        String minutes = String.valueOf(min);
        String seconds = String.valueOf(sec);
        if (sec < 10) {
            seconds = "0" + seconds;
        }
        String timeLeft = minutes + ":" + seconds;
        Log.i(TAG, "Time Left " + timeLeft);
        switch(timeLeft) {
            case "14:59":
                currentPointView.setText("På 10 poäng");
                questionView.setText("Vi lämnar religiös ost och åker norrut mot stor frukt. Liberal holländare har en historisk prägel i vår femstjärniga jorvik.");
                break;
            case "14:00":
                currentPointView.setText("På 8 poäng");
                questionView.setText("Den maskulina huvudbonadens teaterscen ligger på den breda vägen. Reuterswärds revolver fredar Moons högkvarter.");
                break;
        }
        textViewTimer.setText(timeLeft);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.TIMER_BR));
        Log.i(TAG, "Registered broadcast receiver");
        setUpMapIfNeeded();
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        Log.i(TAG, "Unregistered broadcast receiver - onPause");
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(broadcastReceiver);
            Log.i(TAG, "Unregistered broadcast receiver - onStop");
        } catch (Exception e) {
            Log.i(TAG, "onStop "+e);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(mainActivity, TimerService.class));
        Log.i(TAG, "Stopped service");
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to exit current trip?")
                .setNegativeButton("NO", null)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create().show();
    }
}
