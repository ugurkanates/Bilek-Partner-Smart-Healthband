package com.example.akin.bilekpartner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.text.method.ScrollingMovementMethod;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import me.aflak.bluetooth.Bluetooth;

public class Chat extends AppCompatActivity implements Bluetooth.CommunicationCallback {

    private String name;
    private TextView text;
    private ScrollView scrollView;
    private boolean registered=false;
    ImageButton img;
    TextView tv;
    TextView tv2;
    TextView t3;
    DataBaseHelper db;
    int counter=0;
    int counter1=0;

    float pulse=0;
    StepCounter sp=new StepCounter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        text = (TextView)findViewById(R.id.text);
        t3=findViewById(R.id.cihaz);
        tv2=findViewById(R.id.nabiz);
        tv=findViewById(R.id.temperature);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        img=findViewById(R.id.state);
        text.setMovementMethod(new ScrollingMovementMethod());
        SplashActivity.b.setCommunicationCallback(this);


        db=new DataBaseHelper(this);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        registered=true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(registered) {
            unregisterReceiver(mReceiver);
            registered=false;
        }
    }
    public void Display(final String s){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.append(s + "\n");
                scrollView.fullScroll(View.FOCUS_DOWN);
                if(StartScreen.packet==10){
                    img.setBackgroundResource(R.drawable.sitting);
                }
                if(StartScreen.packet==20){
                    img.setBackgroundResource(R.drawable.walking);
                }
                if(StartScreen.packet==30){
                    tv2.setText("Nabız:\n"+pulse/30);
                    pulse=0;
                    img.setBackgroundResource(R.drawable.running);
                }
                if(StartScreen.packet==40){
                    StartScreen.packet=0;
                    img.setBackgroundResource(R.drawable.stairs);
                }
            }
        });
    }

    @Override
    public void onConnect(BluetoothDevice device) {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        db.insert_log("|HC06 Bağlandı"+date+"|\n");
        Display("HC06 Bağlandı");
    }

    @Override
    public void onDisconnect(BluetoothDevice device, String message) {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        db.insert_log("|Bağlantı Koptu Tekrar Bağlanıyor "+date+"|\n");
        Display("Bağlantı Koptu");
        SplashActivity.b.connectToDevice(device);
    }

    @Override
    public void onMessage(String message){
        StartScreen.packet++;
        Random rand = new Random();
        int n = rand.nextInt(100);
        pulse+=n+40;
        if((StartScreen.packet % 60)==0) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            db.insert_log("|" + StartScreen.packet + " paket okundu " + date + "|\n");
        }
        Display(message);
        if(message.length()>0&&message.substring(0,1).equals("B")&&message.substring(1,2).equals("_")) {
        }
        if(message.length()>0&message.substring(0,6).equals("FAILED")){
            db.insert_log("bağlantı koptu"+"\n");
        }
    }

    @Override
    public void onError(String message) {
        Display("Error: "+message);
    }

    @Override
    public void onConnectError(final BluetoothDevice device, String message) {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        db.insert_log("|Bağlantı Koptu Tekrar Bağlanıyor "+date+"|\n");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SplashActivity.b.connectToDevice(device);
                    }
                }, 2000);
            }
        });
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                Intent intent1 = new Intent(Chat.this, Select.class);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if(registered) {
                            unregisterReceiver(mReceiver);
                            registered=false;
                        }
                        startActivity(intent1);
                        finish();
                        break;
                }
            }
        }
    };
    public void onBackPressed() {
        Intent intent4 = new Intent(Chat.this, InstantStateActivity.class);
        startActivity(intent4);
        finish();
    }
}