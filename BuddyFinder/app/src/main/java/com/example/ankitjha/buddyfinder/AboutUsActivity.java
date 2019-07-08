package com.example.ankitjha.buddyfinder;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AboutUsActivity extends AppCompatActivity {


    ActionBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        bar=getSupportActionBar();
        bar.setTitle("About Us");

    }
}
