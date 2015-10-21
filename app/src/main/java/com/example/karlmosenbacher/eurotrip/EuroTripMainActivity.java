package com.example.karlmosenbacher.eurotrip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class EuroTripMainActivity extends AppCompatActivity {
    EuroTripMainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_euro_trip_main);
        mainActivity = this;

        ImageButton start = (ImageButton)findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, GameActivity.class);
                startActivity(intent);
            }
        });
    }










}
