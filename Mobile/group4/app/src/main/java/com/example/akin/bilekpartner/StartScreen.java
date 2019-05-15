package com.example.akin.bilekpartner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import me.aflak.bluetooth.Bluetooth;

/**
 * Created by AKIN Ç on 28.04.2019.
 */

public class StartScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        startActivity(new Intent(StartScreen.this, SplashActivity.class));
        finish();

    }
}
