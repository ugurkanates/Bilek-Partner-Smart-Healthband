package com.example.akin.bilekpartner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerConnect extends AppCompatActivity implements View.OnClickListener {

    public static final int SERVERPORT = 1379;

    public static final String SERVER_IP = "88.228.222.183";
    private ClientThread clientThread;
    private Thread thread;
    private LinearLayout msgList;
    private Handler handler;
    private int clientTextColor;
    private EditText edMessage;
    DataBaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_connection);
        myDB=new DataBaseHelper(this);
        setTitle("Android");
        clientTextColor = ContextCompat.getColor(this, R.color.green);
        handler = new Handler();
        msgList = findViewById(R.id.msgList);
        edMessage = findViewById(R.id.edMessage);
    }

    public TextView textView(String message, int color) {
        if (null == message || message.trim().isEmpty()) {
            message = "<Bos Mesaj>";
        }
        TextView tv = new TextView(this);
        tv.setTextColor(color);
        tv.setText(message + " [" + getTime() + "]");
        tv.setTextSize(20);
        tv.setPadding(0, 5, 0, 0);
        return tv;
    }

    public void showMessage(final String message, final int color) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                msgList.addView(textView(message, color));
            }
        });
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.connect_server) {
            msgList.removeAllViews();
            clientThread = new ClientThread();
            thread = new Thread(clientThread);
            thread.start();
            showMessage("Servera Bağlanıyor...", clientTextColor);
            return;
        }

        if (view.getId() == R.id.send_data) {
            String clientMessage = edMessage.getText().toString().trim();
            showMessage(clientMessage, Color.BLUE);
            if (null != clientThread) {
                clientThread.sendMessage(clientMessage);
            }
        }
        if(view.getId() == R.id.disconnect_server){
            Thread.interrupted();
            String message = "Server Bağlantısı Kapandı.";
            showMessage(message, Color.RED);
        }
    }
    /*! servera baglanildi mesajini sanirim kazara sildim baglanmis oldugunu goremeyebilirsniz*/
    class ClientThread implements Runnable {

        private Socket socket;
        private BufferedReader input;
        public int success=0; // basarili gelen veri sayisi
    /*not her veri isteginde random olarak 3 -4 veya2 veri gelmekte sebebi arastirilmali bacada androidte bir buffer mi var ?
    * Turn back buttonu olmali
    * Bazen ani olarak uygulamada cokmeler yasaniyor sebebi blunmali */

        @Override
        public void run() {


            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);

                while (!Thread.currentThread().isInterrupted()) {

                    this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String message = input.readLine().toString();

                    if (null == message || "Kesildi".contentEquals(message)) {
                        Thread.interrupted();
                        message = "Server Bağlantısı Kapandı.";
                        showMessage(message, Color.RED);
                        break;
                    }else {

                        try {//gelen string input parse edilip sqlite' a kaydedilmeye calisilir
                            String[] values = message.split(",");
                            MobileDataPackage mdp = new MobileDataPackage();
                            mdp.date = values[0];
                            mdp.wbData.temp = Float.parseFloat(values[1]);
                            mdp.wbData.pulse = Float.parseFloat(values[2]);
                            mdp.wbData.pX = Float.parseFloat(values[3]);
                            mdp.wbData.pY = Float.parseFloat(values[4]);
                            mdp.wbData.pZ = Float.parseFloat(values[5]);
                            myDB.insert_serverdata(mdp);
                            showMessage("\ngelen veri :"+ (success++) + mdp.toString(), clientTextColor);
                        }catch (Exception e){

                            showMessage(message, clientTextColor);
                        }

                    }

                    //showMessage("Server: " + message, clientTextColor);
                }

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

        void sendMessage(final String message) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (null != socket) {
                            PrintWriter out = new PrintWriter(new BufferedWriter(
                                    new OutputStreamWriter(socket.getOutputStream())),
                                    true);
                            out.println(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != clientThread) {
            clientThread.sendMessage("Server Bağlantısı Kapandı");
            clientThread = null;
        }
    }
    public void onBackPressed() {
        Intent intent2 = new Intent(ServerConnect.this, Settings.class);
        startActivity(intent2);
        finish();

    }
}