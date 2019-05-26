package com.example.akin.bilekpartner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import android.text.method.ScrollingMovementMethod;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Queue;
import java.util.Random;

import me.aflak.bluetooth.Bluetooth;

public class Chat extends AppCompatActivity implements Bluetooth.CommunicationCallback {
    BluetoothData bt;
    private String name;
    private TextView text;
    private ScrollView scrollView;
    private boolean registered = false;
    ImageButton img;
    TextView tv;
    TextView tv2;
    TextView t3;
    TextView t4;
    DataBaseHelper db;
    double pulse = 70;
    StepCounter sp;
    static int flag = 0;
    static int flag2=0;
    double []acx;
    double []acy;
    double []acz;
    double []gcx;
    double []gcy;
    double []gcz;
    static int i=0;
    static int counter=0;
    static int step=0;
    enum UserState{
        SITTING, STANDING, WALKING, RUNNING, ON_STAIRS
    };

    static Mlogic.ret instant_state = new Mlogic.ret(Mlogic.UserState.RUNNING, 1.1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        text = (TextView) findViewById(R.id.text);
        t3 = findViewById(R.id.cihaz);
        tv2 = findViewById(R.id.nabiz);
        tv = findViewById(R.id.temperature);
        t4=findViewById(R.id.battery);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        img = findViewById(R.id.state);
        acx=new double[2];
        acy=new double[2];
        acz=new double[2];
        gcx=new double[2];
        gcy=new double[2];
        gcz=new double[2];
        text.setMovementMethod(new ScrollingMovementMethod());
        sp = new StepCounter();
        SplashActivity.b.setCommunicationCallback(this);
        db = new DataBaseHelper(this);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        bt = new BluetoothData();
        registered = true;

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
                if(counter==2){
                    counter=0;
                    if(instant_state.lastState== Mlogic.UserState.STANDING)
                        img.setBackgroundResource(R.drawable.standing2);
                    else if(instant_state.lastState== Mlogic.UserState.WALKING) {
                        img.setBackgroundResource(R.drawable.walking);
                        step+=instant_state.step;
                        t4.setText(String.valueOf(step));
                    }
                    else if(instant_state.lastState== Mlogic.UserState.RUNNING) {
                        img.setBackgroundResource(R.drawable.running);
                        step+=instant_state.step;
                        t4.setText(String.valueOf(step));
                    }
                    else if(instant_state.lastState==Mlogic.UserState.ON_STAIRS) {
                        img.setBackgroundResource(R.drawable.stairs);
                        step += instant_state.step;
                        t4.setText(String.valueOf(step));
                    }
                    else if(instant_state.lastState==Mlogic.UserState.SITTING) {
                        img.setBackgroundResource(R.drawable.sitting);
                    }
                }
                if(StartScreen.packet==10){
                    int a1=(int)bt.temp;
                    int b1=(int)bt.deviceTemp;
                    pulse+=bt.heartRate;
                    bt.heartRate=0;
                    if(flag==0) {
                        pulse = pulse / 10.0;
                        flag = 1;
                    }
                    else
                        pulse=pulse/30.0;
                    int a=(int)pulse;
                    tv2.setText("Nabız:\n"+String.valueOf(a));
                    tv.setText("Vücut Isısı:\n"+String.valueOf(a1)+" °C");
                    t3.setText("Cihaz ısısı:\n"+String.valueOf(b1)+" °C");
                }
                if(StartScreen.packet==29){
                    StartScreen.packet=0;
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
        if(message.length()>0&&message.substring(0,1).equals("B")&&message.substring(1,2).equals("_")) {
            String [] part=message.split("_");
            bt.acX=Double.parseDouble(part[1]);
            bt.acY=Double.parseDouble(part[2]);
            bt.acZ=Double.parseDouble(part[3]);
            bt.gX=Double.parseDouble(part[4]);
            bt.gY=Double.parseDouble(part[5]);
            bt.gZ=Double.parseDouble(part[6]);
            bt.heartRate+=Double.parseDouble(part[7]);
            bt.temp=Double.parseDouble(part[8]);
            bt.batter=Integer.parseInt(part[9]);
            bt.deviceTemp=Double.parseDouble(part[10]);
            if(i<2) {
                acx[i] = bt.acX;
                acy[i] = bt.acY;
                acz[i] = bt.acZ;
                gcx[i] = bt.gX;
                gcy[i] = bt.gY;
                gcz[i] = bt.gZ;
                i++;
                counter++;
            }
            if(i==2){
                if(flag2==0) {
                    LoginActivity.mlogic.init(acx, acy, acz, gcx, gcy, gcz);
                    flag2 = 1;
                }
                else
                    instant_state = LoginActivity.mlogic.whatIsUserDoing(acx, acy, acz, gcx, gcy, gcz);
                i=0;
            }
            StartScreen.packet++;
            Display(message);

        }
        if((StartScreen.packet)==29) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            db.insert_log("|" + StartScreen.packet + " paket okundu " + date + "|\n");
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
