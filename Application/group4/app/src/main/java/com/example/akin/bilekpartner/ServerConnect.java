package com.example.akin.bilekpartner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.Date;
import android.provider.Settings.Secure;

import static com.example.akin.bilekpartner.SplashActivity.run;
import static com.example.akin.bilekpartner.SplashActivity.sitting;
import static com.example.akin.bilekpartner.SplashActivity.stair;
import static com.example.akin.bilekpartner.SplashActivity.stand;
import static com.example.akin.bilekpartner.SplashActivity.totaldata;
import static com.example.akin.bilekpartner.SplashActivity.walk;
import static java.lang.Thread.sleep;

public class ServerConnect extends AppCompatActivity {

    public static final int SERVERPORT = 1379;
    private String android_id = "tesr";
            //Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);

    public static final String SERVER_IP = "89.107.226.204";
    private ClientThread clientThread;
    private Thread thread;
    private LinearLayout msgList;
    private Handler handler;
    private int clientTextColor;
    private EditText edMessage;
    private Button connectBtn,disconnectBtn,MBtn, FLBtn,sendBtn;


    Button pickBeginDate, pickEndDate;
    DataBaseHelper myDB;
    Context context = this;
    String beginDate, endDate, ud_request, FL_request, M_request;

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
        pickBeginDate = (Button) findViewById(R.id.begin_date_btn);
        pickEndDate = (Button) findViewById(R.id.end_date_btn);

        connectBtn = (Button)findViewById(R.id.connect_server);
        disconnectBtn = (Button)findViewById(R.id.disconnect_server);
        MBtn=(Button)findViewById(R.id.send_M);
        FLBtn=(Button)findViewById(R.id.sendFL);
        sendBtn=(Button)findViewById(R.id.send_data);

      /*  msgList.removeAllViews();
        clientThread = new ClientThread();
        thread = new Thread(clientThread);
        thread.start();
        showMessage("Servera Bağlanıyor...", clientTextColor);


        String clientMessage = "M tesr".toString().trim();
        showMessage(clientMessage, Color.BLUE);
        if (null != clientThread) {
            clientThread.sendMessage(clientMessage);
        }*/

        /*String clientMessage2 = "FL".toString().trim();
        showMessage(clientMessage2, Color.BLUE);
        if (null != clientThread) {
            clientThread.sendMessage(clientMessage2);
        }*/

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgList.removeAllViews();
                clientThread = new ClientThread();
                thread = new Thread(clientThread);
                thread.start();
                showMessage("Servera Bağlanıyor...", clientTextColor);
            }
        });

        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread.interrupted();
                String message = "Server Bağlantısı Kapandı.";
                showMessage(message, Color.RED);

            }

        });
        MBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clientMessage = "M tesr".toString().trim();
                showMessage("Kullanici adiyla baglanti isteginde bulunuldu. ", Color.BLUE);
                if (null != clientThread) {
                    clientThread.sendMessage(clientMessage);
                }
            }
        });
        FLBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clientMessage = "RE".toString().trim();
                showMessage(clientMessage, Color.BLUE);
                if (null != clientThread) {
                    clientThread.sendMessage(clientMessage);
                }
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clientMessage = edMessage.getText().toString().trim();

                showMessage(clientMessage, Color.BLUE);
                if (null != clientThread) {
                    clientThread.sendMessage(clientMessage);
                }
            }
        });

        pickBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Şimdiki zaman bilgilerini alıyoruz. güncel yıl, güncel ay, güncel gün.
                final Calendar takvim = Calendar.getInstance();
                int yil = takvim.get(Calendar.YEAR);
                int ay = takvim.get(Calendar.MONTH);
                int gun = takvim.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // ay değeri 0 dan başladığı için (Ocak=0, Şubat=1,..,Aralık=11)
                                // değeri 1 artırarak gösteriyoruz.
                                month += 1;
                                // year, month ve dayOfMonth değerleri seçilen tarihin değerleridir.
                                // Edittextte bu değerleri gösteriyoruz.
                                //etTarih.setText(dayOfMonth + "/" + month + "/" + year);
                                if (month<10)
                                    beginDate=year+"-0"+month+"-"+dayOfMonth+" 00:00:00";
                                if (dayOfMonth<10)
                                    beginDate=year+"-0"+month+"-0"+dayOfMonth+" 00:00:00";
                                if (month<10 && dayOfMonth<10)
                                    beginDate=year+"-0"+month+"-0"+dayOfMonth+" 00:00:00";

                            }
                        }, yil, ay, gun);
                // datepicker açıldığında set edilecek değerleri buraya yazıyoruz.
                // şimdiki zamanı göstermesi için yukarda tanmladığımz değşkenleri kullanyoruz.

                // dialog penceresinin button bilgilerini ayarlıyoruz ve ekranda gösteriyoruz.
                dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Seç", dpd);
                dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", dpd);
                dpd.show();

            }
        });

        pickEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Şimdiki zaman bilgilerini alıyoruz. güncel yıl, güncel ay, güncel gün.
                final Calendar takvim = Calendar.getInstance();
                int yil = takvim.get(Calendar.YEAR);
                int ay = takvim.get(Calendar.MONTH);
                int gun = takvim.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // ay değeri 0 dan başladığı için (Ocak=0, Şubat=1,..,Aralık=11)
                                // değeri 1 artırarak gösteriyoruz.
                                month += 1;
                                // year, month ve dayOfMonth değerleri seçilen tarihin değerleridir.
                                // Edittextte bu değerleri gösteriyoruz.
                                //etTarih.setText(dayOfMonth + "/" + month + "/" + year);
                                if (month<10)
                                    endDate=year+"-0"+month+"-"+dayOfMonth+" 00:00:00";
                                if (dayOfMonth<10)
                                    endDate=year+"-0"+month+"-0"+dayOfMonth+" 00:00:00";
                                if (month<10 && dayOfMonth<10)
                                    endDate=year+"-0"+month+"-0"+dayOfMonth+" 00:00:00";
                            }
                        }, yil, ay, gun);
                // datepicker açıldığında set edilecek değerleri buraya yazıyoruz.
                // şimdiki zamanı göstermesi için yukarda tanmladığımz değşkenleri kullanyoruz.

                // dialog penceresinin button bilgilerini ayarlıyoruz ve ekranda gösteriyoruz.
                dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Seç", dpd);
                dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", dpd);
                dpd.show();
                ud_request= "UD " + beginDate + " " + endDate;
                String clientMessage = ud_request;
                showMessage(clientMessage, Color.BLUE);
                if (null != clientThread) {
                    clientThread.sendMessage(clientMessage);
                }



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

    public void showMessage(final String message, final int color) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                msgList.addView(textView(message, color));
            }
        });
    }


    /*! servera baglanildi mesajini sanirim kazara sildim baglanmis oldugunu goremeyebilirsniz*/
    class ClientThread implements Runnable {

        private  Socket socket;
        private BufferedReader input;
        String message = " ";
        public int success=0; // basarili gelen veri sayisi
        /*not her veri isteginde random olarak 3 -4 veya2 veri gelmekte sebebi arastirilmali bacada androidte bir buffer mi var ?
         * Turn back buttonu olmali
         * Bazen ani olarak uygulamada cokmeler yasaniyor sebebi blunmali */

        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);

                while (!Thread.currentThread().isInterrupted()) {

                    this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (this.input!= null && null != message ) {
                        message = input.readLine().toString();


                        if (null == message || "Kesildi".contentEquals(message)) {
                            Thread.interrupted();
                            message = "Server Bağlantısı Kapandı.";
                            showMessage(message, Color.RED);
                            break;
                        }else {

                            try {//gelen string input parse edilip sqlite' a kaydedilmeye calisilir
                                String[] values = message.split(",");


                                showMessage("\nData :"+ (success++) +" "+ message.toString(), clientTextColor);

                            }catch (Exception e){

                                showMessage("error inserting db"+message, clientTextColor);
                            }

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
                        long time = System.currentTimeMillis();
                        while ((null == socket || socket.getOutputStream() == null) && System.currentTimeMillis() - time < 5000);
                        showMessage("basarili", Color.RED);
                        PrintWriter out = new PrintWriter(new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())),
                                true);
                        out.println(message);
                        out.flush();

                    } catch (Exception e) {
                        showMessage("Wifi kapali", Color.RED);
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