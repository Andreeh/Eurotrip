package com.example.karlmosenbacher.eurotrip;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class EuroTripMainActivity extends AppCompatActivity {
    EuroTripMainActivity mainActivity;
    public int currentImage = 0;
    Timer timer;
    TimerTask task;
    ImageView imageView;
    private Button start_btn, score_btn, settings_btn;

    // Array containging all immages for sliding train
    int[] IMAGE_IDS = {R.drawable.pic1, R.drawable.pic2, R.drawable.pic3,
            R.drawable.pic4, R.drawable.pic5, R.drawable.pic6, R.drawable.pic7, R.drawable.pic8, R.drawable.pic9, R.drawable.pic10, R.drawable.pic11};

//    Resources res = getResources();
//    int[] IMAGE_IDS = res.getIntArray(R.array.pic_id);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_euro_trip_main);
        mainActivity = this;
        final Handler mHandler = new Handler();


        // Start game button
        start_btn = (Button) findViewById(R.id.start);
        start_btn.setOnClickListener(new ButtonClick());

        // Start button
        score_btn = (Button) findViewById(R.id.topScore);
        score_btn.setOnClickListener(new ButtonClick());

        // Settings button
        settings_btn = (Button) findViewById(R.id.settings);
        settings_btn.setOnClickListener(new ButtonClick());

        // Create runnable for posting
        final Runnable mUpdateResults = new Runnable() {
            public void run() {

                AnimateandSlideShow();
            }
        };

        // Delay for every picture
        int delay = 100;

        // Repeat every 0,5 second
        int period = 200;

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {

                mHandler.post(mUpdateResults);
            }

        }, delay, period);
    }

 /*   public void onClick(View v) {

        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }*/

    private void AnimateandSlideShow() {

        imageView = (ImageView) findViewById(R.id.lower_image);
        imageView.setImageResource(IMAGE_IDS[currentImage % IMAGE_IDS.length]);
        currentImage++;
    }

    // Inner class that handles button clicks
    public class ButtonClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (v == start_btn) {
                Intent intent = new Intent(mainActivity, GameActivity.class);
                startActivity(intent);
            }

            if (v == score_btn) {
                Intent intent = new Intent(mainActivity, TopScoreActivity.class);
                startActivity(intent);
            }

            if (v == settings_btn) {
                System.out.println("SETTINGS");
            }

        }
    }
}
