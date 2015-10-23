package com.example.karlmosenbacher.eurotrip;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "BroadcastService - Game";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Button btn_play, submit;
    private TextView textViewTimer, questionView, currentPointView;
    private static final int amountToMoveRight = 500, amountToMoveDown = -625;
    private GameActivity gameActivity;
    private Firebase mFirebase;
    private Trip currentTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getSupportActionBar();
        setUpMapIfNeeded();
        gameActivity = this;

        setUpFirebase();



        textViewTimer = (TextView)findViewById(R.id.timer);
        questionView = (TextView)findViewById(R.id.current_question);
        currentPointView = (TextView)findViewById(R.id.current_points);
        questionView.setMovementMethod(new ScrollingMovementMethod());

        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // compare input answer to correct answer
                EditText input = (EditText)findViewById(R.id.answer);
                String answer = input.getText().toString().toUpperCase();
                String correct_answer = currentTrip.getEndcity().toUpperCase().toString();
                if (answer.equals(correct_answer)) {
                    try {
                        questionView.setText("Correct answer!");
                        unregisterReceiver(broadcastReceiver);
                        stopService(new Intent(gameActivity, TimerService.class));
                    } catch (Exception e) {
                        Log.i(TAG, "onClick " + e);
                    }
                } else {
                    questionView.setText("Wrong answer");

                }
                submit.setEnabled(false);
                input.setText("");
                input.setEnabled(false);
            }
        });

        btn_play = (Button)findViewById(R.id.play);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { new Thread(new Runnable() {
                public void run() {
                    btn_play.post(new Runnable() {
                        public void run() {
                            Animation animation = new TranslateAnimation(0, amountToMoveRight, 0, amountToMoveDown);
                            animation.setDuration(750);
                            btn_play.startAnimation(animation);
                            btn_play.setVisibility(View.GONE);
                        }
                    });
                    getTrip();
                    Log.i(TAG, "Started TimerService");
                }
            }).start();

            }
        });


    }

    //gets the second trip in the list in firebase db.
    private void getTrip() {

        mFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("There are " + dataSnapshot.child("Questions").getChildrenCount() + " trips");
                DataSnapshot postSnapshot = dataSnapshot.child("Questions").child("4");
                currentTrip = postSnapshot.getValue(Trip.class);
                System.out.println("ASDASD" + currentTrip.getZoom_coords());
                startService(new Intent(gameActivity, TimerService.class));

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }


        });
    }

    private void setUpFirebase() {
        Firebase.setAndroidContext(gameActivity);
        mFirebase = new Firebase("https://blistering-torch-8544.firebaseio.com");

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
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(0,0)));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Update gui with new question etc.
//            Log.i(TAG, "Received Broadcast");
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
        switch(timeLeft) {
            case "14:59":
                currentPointView.setText("På 10 poäng");
                questionView.setText(currentTrip.getQ10P());
                break;
            case "14:00":
                currentPointView.setText("På 8 poäng");
                questionView.setText(currentTrip.getQ8P());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentTrip.getZoom_coords(), 3));
                break;
            case "13:00":
                currentPointView.setText("På 6 poäng");
                questionView.setText(currentTrip.getQ6P());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentTrip.getZoom_coords(), 4));
                break;
            case "12:00":
                currentPointView.setText("På 4 poäng");
                questionView.setText(currentTrip.getQ4P());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentTrip.getZoom_coords(), 5));
                break;
            case "11:00":
                currentPointView.setText("På 2 poäng");
                questionView.setText(currentTrip.getQ2P());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentTrip.getZoom_coords(), 8));
                break;
            case "10:00":
                currentPointView.setText("-");
                questionView.setText("Tiden är ute!");
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
        try {
            unregisterReceiver(broadcastReceiver);
            Log.i(TAG, "Unregistered broadcast receiver - onPause");
        } catch (Exception e) {
            Log.i(TAG, "onPause " + e);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(broadcastReceiver);
            Log.i(TAG, "Unregistered broadcast receiver - onStop");
        } catch (Exception e) {
            Log.i(TAG, "onStop "+ e);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(gameActivity, TimerService.class));
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
