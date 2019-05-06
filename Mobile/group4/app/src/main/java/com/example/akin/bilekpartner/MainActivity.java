package com.example.akin.bilekpartner;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.natasa.progressviews.CircleProgressBar;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by AKIN Ç on 15.04.2019.
 * Update by Hasna on 4.05.2019
 */

public class MainActivity  extends AppCompatActivity{

    DataBaseHelper myDB;
    Button button;
    ArrayList<String> titleArray = new ArrayList<String>();
    ArrayList<String> textArray = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        myDB=new DataBaseHelper(this);
        bottomNavigate();
        titleArray.add("Saglik Uyarisi");
        titleArray.add("Guvenlik Uyarisi");
        titleArray.add("Saglik Tavsiyesi");
        titleArray.add("Saglikli Yasam Icın");
        titleArray.add("Yeme Icme Onerisi");

        textArray.add("1 saat boyunca hareket etmediniz,sağlıklı kalmak için lütfen birlikte hemen hareket edelim"
        );
        textArray.add("Düne göre 3 KM daha az yürüdünüz :( , haydi biraz yürüyelim"
        );
        textArray.add("Hey hey biraz sakin olalım , kalp atışınız beklenen değerlerin üzerinde küçük bir mola ?"
        );
        textArray.add("Sağlıklı yaşayabilmek için günde en az 2 litre su tüketmelisiniz "
        );
        textArray.add("Uyku düzenine dikkat etmelisiniz bugün düne göre 3 saat az uyudunuz"
        );
        textArray.add("Stres kan basıncını yükselterek sakin insanlara göre kalp hastalıklarına yakalanma oranını 3 kat artıran bir faktör"

        );
        textArray.add("Kan basıncınızı gözlemlemeyi sürdürün. Çünkü bu en büyük risk faktörüdür. Kalp krizlerinin yüzde 40'ına 125/85 ve 140/90 aralığındaki tansiyon neden olur"

        );

        MobileDataPackage mdp = new MobileDataPackage();
        mdp.date = "S2019 05 05 03:16:18";
        mdp.wbData.temp = Float.parseFloat("36.0");
        mdp.wbData.pulse = Float.parseFloat("85.0");
        mdp.wbData.pX = Float.parseFloat("120");
        mdp.wbData.pY = Float.parseFloat("130");
        mdp.wbData.pZ = Float.parseFloat("140");
        myDB.insert_serverdata(mdp);
        final TextView textView=findViewById(R.id.pulses);
        Button bt=findViewById(R.id.ekle);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textView.getText() != null &&Integer.parseInt(String.valueOf(textView.getText()))>100 ||Integer.parseInt(String.valueOf(textView.getText()))<60) {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", "05349881140");
                    smsIntent.putExtra("sms_body", "Nabız değerim: " + textView.getText() + " Yardıma ihtiyacım var");
                    startActivity(smsIntent);
                }
                else if(textView.getText() != null){
                    final CircleProgressBar pulse = (CircleProgressBar) findViewById(R.id.fats_progress);
                    pulse.setProgress(Integer.parseInt(String.valueOf(textView.getText()))%360);
                    pulse.setText(Integer.parseInt(String.valueOf(textView.getText())) + " bmp");
                }
            }
        });

        final CircleProgressBar pulse = (CircleProgressBar) findViewById(R.id.fats_progress);
        final CircleProgressBar temperature = (CircleProgressBar) findViewById(R.id.carbs_progress);
        final CircleProgressBar movement = (CircleProgressBar) findViewById(R.id.protein_progress);

        /*burda surekli bu islemi yapmasini istedim
        islem dedigim databaseden bilgileri alip processbarlari guncellemesi
        ! calismiyor olabilir test edemedim serverdan surekli bilgi aldiginda bakilabilir*/
        final Handler handler2 = new Handler();
        Random rand = new Random();
        final int value = rand.nextInt(5);
        final int value2 = rand.nextInt(7);
        Timer timer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler2.post(new Runnable() {
                    @Override
                    public void run() {
                        // Do whatever you want
                        sendNotification(titleArray.get(value),textArray.get(value2));
                    }
                });
            }
        };
        timer.schedule(timerTask, 1000); // 1000 = 1 second.
        final Handler handler=new Handler();
        handler.post(new Runnable(){
            @Override
            public void run() {
                MobileDataPackage mdp =myDB.getLastData();
                String minfo = myDB.getLastMove();
                int moveCount = myDB.getMInfoCount(minfo);
                int totalMove=myDB.getTotalCount();

                pulse.setProgress(mdp.wbData.pulse%360);
                pulse.setText(mdp.wbData.pulse + " bmp");


                temperature.setProgress(mdp.wbData.temp%360);
                temperature.setText(String.valueOf(mdp.wbData.temp)+ " °C");


                movement.setProgress(moveCount%360);
                movement.setText(String.valueOf(totalMove) + " hareket " );
                handler.postDelayed(this,500); // set time here to refresh data
            }
        });




    }

    /*guncelleme kendi sayfasini acmasini saglladim sayfa refreshlensin diye*/
    public void bottomNavigate(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item1:
                                Intent intent = new Intent(MainActivity.this, InstantStateActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.item2://kendisi tekrar baslatiyor
                                Intent intent3 = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(intent3);
                                finish();
                                break;

                            case R.id.item3:
                                Intent intent2 = new Intent(MainActivity.this, analyze.class);
                                startActivity(intent2);
                                finish();
                                break;
                            case R.id.item4:
                                Intent intent4 = new Intent(MainActivity.this, Settings.class);
                                startActivity(intent4);
                                finish();
                                break;
                        }
                        return true;
                    }
                });
    }
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
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
    public void sendNotification(String title,String text) {

        //Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.appintro3)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(text));


        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // When you issue multiple notifications about the same type of event,
        // it’s best practice for your app to try to update an existing notification
        // with this new information, rather than immediately creating a new notification.
        // If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//
        mNotificationManager.notify(001, mBuilder.build());

    }
}
