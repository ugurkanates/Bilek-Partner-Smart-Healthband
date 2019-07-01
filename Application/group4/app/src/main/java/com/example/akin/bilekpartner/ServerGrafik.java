package com.example.akin.bilekpartner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.example.akin.bilekpartner.SplashActivity.run;
import static com.example.akin.bilekpartner.SplashActivity.sitting;
import static com.example.akin.bilekpartner.SplashActivity.stair;
import static com.example.akin.bilekpartner.SplashActivity.stand;
import static com.example.akin.bilekpartner.SplashActivity.totaldata;
import static com.example.akin.bilekpartner.SplashActivity.walk;

public class ServerGrafik extends AppCompatActivity {

    public static final int SERVERPORT = 1379;
    private String android_id = "tesr";
    public static final String SERVER_IP = "89.107.226.204";
    private ClientThread clientThread;
    private Thread thread;
    private Handler handler;
    private LinearLayout msgList;

    private int clientTextColor;
    private EditText edMessage;
    private Button b1,b2,b6,b7;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_grafik);

        setTitle("Android");
        clientTextColor = ContextCompat.getColor(this, R.color.green);
        handler = new Handler();
        msgList = findViewById(R.id.msgList);
        edMessage = findViewById(R.id.edMessage);
        b1=(Button)findViewById(R.id.b1);
        b2=(Button)findViewById(R.id.b2);
        b6=(Button)findViewById(R.id.b5);
        b7=(Button)findViewById(R.id.b7);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientThread = new ClientThread();
                thread = new Thread(clientThread);
                thread.start();
                showMessage("Servera Bağlanıyor...", clientTextColor);
                //Toast.makeText(getApplicationContext(),"Connection server...",Toast.LENGTH_SHORT).show();
            }
        });

        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread.interrupted();
                String message = "Server Bağlantısı Kapandı.";
                showMessage(message, Color.RED);
                //Toast.makeText(getApplicationContext(),"Closed connection",Toast.LENGTH_SHORT).show();


            }

        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clientMessage = "M tesr".toString().trim();
                Toast.makeText(getApplicationContext(),"Sended request",Toast.LENGTH_SHORT).show();
                if (null != clientThread) {
                    clientThread.sendMessage(clientMessage);
                }

                String clientMessage2 = "RE ".toString().trim();
                showMessage("Request Data", Color.BLUE);
                //Toast.makeText(getApplicationContext(),clientMessage2,Toast.LENGTH_SHORT).show();
                if (null != clientThread) {
                    clientThread.sendMessage(clientMessage2);
                }
            }
        });

        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServerGrafik.this, grafikall.class);
                startActivity(intent);
                finish();
            }
        });

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

  /*  public void showMessage(final String message, final int color) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                msgList.addView(textView(message, color));
            }
        });*/


    /*! servera baglanildi mesajini sanirim kazara sildim baglanmis oldugunu goremeyebilirsniz*/
    class ClientThread implements Runnable {

        private Socket socket;
        private BufferedReader input;
        String message = " ";
        String message2 = " ";
        String message3 = " ";
        String message4 = " ";
        String message5 = " ";
        String message6 = " ";
        String errormesssage = " ";

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);

                if (!Thread.currentThread().isInterrupted()) {

                    this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        if (this.input!= null && null != message ) {
                            message = input.readLine().toString();
                            message2 = input.readLine().toString();
                            message3 = input.readLine().toString();
                            message4 = input.readLine().toString();
                            message5 = input.readLine().toString();
                            message6 = input.readLine().toString();
                            showMessage(message, Color.RED);
                            showMessage(message2, Color.RED);
                            showMessage(message3, Color.RED);
                            showMessage(message4, Color.RED);
                            showMessage(message5, Color.RED);
                            showMessage(message6, Color.RED);

                        if (null == message || "Kesildi".contentEquals(message)) {
                            Thread.interrupted();
                            message = "Server Bağlantısı Kapandı.";
                            showMessage(message, Color.RED);
                            //break;
                        }else {

                          //  try {
                                String[] values = message.split(",");
                                String[] values2 = message2.split(",");
                                String[] values3 = message3.split(",");
                                String[] values4 = message4.split(",");
                                String[] values5 = message5.split(",");
                                String[] values6 = message6.split(",");

                                walk.setMonday( Integer.parseInt(values[0]));
                                walk.setTuesday(Integer.parseInt(values[1]));
                                walk.setWednesday(Integer.parseInt(values[2]));
                                walk.setThursday(Integer.parseInt(values[3]));
                                walk.setFriday(Integer.parseInt(values[4]));
                                walk.setSaturday(Integer.parseInt(values[5]));
                                walk.setSunday(Integer.parseInt(values[6]));

                                stair.setMonday( Integer.parseInt(values2[0]));
                                stair.setTuesday(Integer.parseInt(values2[1]));
                                stair.setWednesday( Integer.parseInt(values2[2]));
                                stair.setThursday( Integer.parseInt(values2[3]));
                                stair.setFriday( Integer.parseInt(values2[4]));
                                stair.setSaturday(Integer.parseInt(values2[5]));
                                stair.setSunday(Integer.parseInt(values2[6]));

                                sitting.setMonday( Integer.parseInt(values3[0]));
                                sitting.setTuesday(Integer.parseInt(values3[1]));
                                sitting.setWednesday(Integer.parseInt(values3[2]));
                                sitting.setThursday(Integer.parseInt(values3[3]));
                                sitting.setFriday(Integer.parseInt(values3[4]));
                                sitting.setSaturday(Integer.parseInt(values3[5]));
                                sitting.setSunday(Integer.parseInt(values3[6]));

                                run.setMonday( Integer.parseInt(values4[0]));
                                run.setTuesday( Integer.parseInt(values4[1]));
                                run.setWednesday(Integer.parseInt(values4[2]));
                                run.setThursday(Integer.parseInt(values4[3]));
                                run.setFriday(Integer.parseInt(values4[4]));
                                run.setFriday(Integer.parseInt(values4[5]));
                                run.setSunday(Integer.parseInt(values4[6]));

                                stand.setMonday(Integer.parseInt(values5[0]));
                                stand.setTuesday(Integer.parseInt(values5[1]));
                                stand.setWednesday( Integer.parseInt(values5[2]));
                                stand.setThursday(Integer.parseInt(values5[3]));
                                stand.setFriday(Integer.parseInt(values5[4]));
                                stand.setSaturday(Integer.parseInt(values5[5]));
                                stand.setSunday(Integer.parseInt(values5[6]));

                                totaldata.setMonday(Integer.parseInt(values6[0]));
                                totaldata.setTuesday( Integer.parseInt(values6[1]));
                                totaldata.setWednesday(Integer.parseInt(values6[2]));
                                totaldata.setThursday(Integer.parseInt(values6[3]));
                                totaldata.setFriday(Integer.parseInt(values6[4]));
                                totaldata.setSaturday( Integer.parseInt(values6[5]));
                                totaldata.setSunday(Integer.parseInt(values6[6]));

                            /*}catch (Exception e){
                               showMessage("finished "+message, Color.BLUE);
                            }*/
                        }
                    }
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
                        long time = System.currentTimeMillis();
                        while ((null == socket || socket.getOutputStream() == null) && System.currentTimeMillis() - time < 5000);
                       // showMessage("basarili", Color.RED);
                        PrintWriter out = new PrintWriter(new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())),
                                true);
                        out.println(message);
                        //out.flush();

                    } catch (Exception e) {
                       // showMessage("Wifi kapali", Color.RED);
                      // Toast.makeText(getApplicationContext(),"Wifi kapali",Toast.LENGTH_SHORT).show();
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
    public void showMessage(final String message, final int color) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                msgList.addView(textView(message, color));
            }
        });
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
        Intent intent2 = new Intent(ServerGrafik.this, Settings.class);
        startActivity(intent2);
        finish();

    }
}
