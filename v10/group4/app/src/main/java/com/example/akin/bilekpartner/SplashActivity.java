package com.example.akin.bilekpartner;

/**
 * Created by AKIN Ç on 28.04.2019.
 */

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.ServerSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import me.aflak.bluetooth.Bluetooth;

import static app.akexorcist.bluetotohspp.library.BluetoothState.REQUEST_ENABLE_BT;

public class SplashActivity extends AppCompatActivity {

    static Bluetooth b;
    static weekData walk = new weekData();
    static weekData stair = new weekData();
    static weekData sitting = new weekData();
    static weekData run = new weekData();
    static weekData stand = new weekData();
    static weekData totaldata = new weekData();
    protected Typeface mTfRegular;
    TextView app_name;
    DataBaseHelper db=new DataBaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.happy);
        app_name = (TextView) findViewById(R.id.app_name);
        mTfRegular = Typeface.createFromAsset(getAssets(), "logo_regular.ttf");
        app_name.setTypeface(mTfRegular);
        app_name.setTextColor(Color.parseColor("#000000"));
        app_name.startAnimation(AnimationUtils.loadAnimation(SplashActivity.this,R.anim.right_in));

        b = new Bluetooth(this);
        b.enableBluetooth();
        int pos = 0;
        b.connectToDevice(b.getPairedDevices().get(pos));
        Button bt=findViewById(R.id.strt);
        bt.setTextColor(Color.parseColor("#ffffff"));
        bt.setBackgroundColor(Color.parseColor("#9400D3"));
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                        String date = df.format(Calendar.getInstance().getTime());
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                }, 5);
            }
        });

    }
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
        alertDialog.setTitle("Uygulamadan Çık?");
        alertDialog.setMessage("Çıkmak istediğinize emin misiniz?");
        alertDialog.setPositiveButton("Evet",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialog.setNegativeButton("Hayır",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();

    }
}