package com.example.akin.bilekpartner;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import me.aflak.bluetooth.Bluetooth;

public class Chat extends AppCompatActivity implements Bluetooth.CommunicationCallback {
    private String name;
    private Bluetooth b;
    private TextView text;
    private ScrollView scrollView;
    private boolean registered=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        b = new Bluetooth(this);
        b.enableBluetooth();

        b.setCommunicationCallback(this);
        text = (TextView)findViewById(R.id.text);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        text.setMovementMethod(new ScrollingMovementMethod());
        int pos = getIntent().getExtras().getInt("pos");
        name = b.getPairedDevices().get(pos).getName();

        Display("Bağlanıyor...");
        b.connectToDevice(b.getPairedDevices().get(pos));


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
            }
        });
    }

    @Override
    public void onConnect(BluetoothDevice device) {
        Display("Connected to "+device.getName()+" - "+device.getAddress());
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Override
    public void onDisconnect(BluetoothDevice device, String message) {
        Display("Bağlantı Koptu!");
        Display("Tekrar Bağlanılıyor...");
        b.connectToDevice(device);
    }

    @Override
    public void onMessage(String message) {
        Display(name+": "+message);
        String[] parts = message.split("_");
        //gelen data x,y,z,pulse,temp
        String[] data= {parts[1],parts[2],parts[3],parts[4],parts[5]};
    }

    @Override
    public void onError(String message) {
        Display("Hata: "+message);


    }

    @Override
    public void onConnectError(final BluetoothDevice device, String message) {
        Display("Hata: "+"Time Out");
        Display("3 saniye içinde tekrar deneyin.");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.connectToDevice(device);
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
        Intent intent4 = new Intent(Chat.this, Settings.class);
        startActivity(intent4);
        finish();
    }
}