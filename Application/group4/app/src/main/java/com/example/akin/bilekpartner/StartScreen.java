package com.example.akin.bilekpartner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import me.aflak.bluetooth.Bluetooth;

/**
 * Created by AKIN Ç on 28.04.2019.
 */

public class StartScreen extends AppCompatActivity {
    static int packet=0;
    private Bluetooth b;
    private int SLEEP_TIMER = 3;
    static String arr=new String();
    DataBaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen2);
        db=new DataBaseHelper(this);
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        db.insert_log("\n********************************************\n|Uygulama Açıldı "+date+"|\n");
        b = new Bluetooth(this);
        b.enableBluetooth();
        db.insert_log("|Bluetooth Açıldı "+date+"|\n");
        LogoLauncher logoLauncher = new LogoLauncher();
        logoLauncher.start();
    }

    private class LogoLauncher extends Thread {
        public void run() {
            try {
                sleep(1000 * SLEEP_TIMER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
           // log.writeLog("Splash Screen Activity Açıldı\n");
            Intent intent = new Intent(StartScreen.this, SplashActivity.class);
            startActivity(intent);
            StartScreen.this.finish();
        }
    }
}
