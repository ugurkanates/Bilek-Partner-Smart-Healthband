package com.example.akin.bilekpartner;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.natasa.progressviews.CircleProgressBar;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.akin.bilekpartner.SplashActivity.run;
import static com.example.akin.bilekpartner.SplashActivity.sitting;
import static com.example.akin.bilekpartner.SplashActivity.stair;
import static com.example.akin.bilekpartner.SplashActivity.stand;
import static com.example.akin.bilekpartner.SplashActivity.totaldata;
import static com.example.akin.bilekpartner.SplashActivity.walk;

/**
 * Created by AKIN Ç on 15.04.2019.
 * Update by Hasna on 4.05.2019
 */

public class MainActivity  extends AppCompatActivity{

    private ServerGrafik.ClientThread clientThread;

    Button button;
    ArrayList<String> titleArray = new ArrayList<String>();
    ArrayList<String> textArray = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        bottomNavigate();
        titleArray.add("Saglik Uyarisi");
        titleArray.add("Guvenlik Uyarisi");
        titleArray.add("Saglik Tavsiyesi");
        titleArray.add("Saglikli Yasam Icın");
        titleArray.add("Beslenme Onerisi");

        textArray.add("1 saat boyunca hareket etmediniz,sağlıklı kalmak için lütfen birlikte hemen hareket edelim"
        );
        textArray.add("Düne göre 3 KM daha az yürüdünüz :( , haydi biraz yürüyelim"
        );
        textArray.add("Hey hey biraz sakin olalım , kalp atışınız beklenen değerlerin üzerinde küçük bir mola ?"
        );
        textArray.add("Sağlıklı yaşayabilmek için günde en az 2 litre su tüketmelisiniz "
        );
        textArray.add("Uyku düzenine dikkat etmelisiniz "
        );
        textArray.add("Stres kan basıncını yükselterek sakin insanlara göre kalp hastalıklarına yakalanma oranını 3 kat artıran bir faktör"

        );
        textArray.add("Kan basıncınızı gözlemlemeyi sürdürün. Çünkü bu en büyük risk faktörüdür. Kalp krizlerinin yüzde 40'ına 125/85 ve 140/90 aralığındaki tansiyon neden olur"

        );


        final CircleProgressBar adim = (CircleProgressBar) findViewById(R.id.fats_progress);
        final CircleProgressBar kosu = (CircleProgressBar) findViewById(R.id.carbs_progress);
        final CircleProgressBar merdiven = (CircleProgressBar) findViewById(R.id.carbs_progress2);
        final CircleProgressBar toplamhareket = (CircleProgressBar) findViewById(R.id.fats_progress4);

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

                int runtotal = run.monday+run.tuesday+run.wednesday+run.friday+run.thursday+run.sunday+run.saturday;
                int walktotal= walk.monday+walk.tuesday+walk.wednesday+walk.friday+walk.thursday+walk.sunday+walk.saturday;
                int sittotal = sitting.monday+sitting.tuesday+sitting.wednesday+sitting.friday+sitting.thursday+sitting.sunday+sitting.saturday;
                int stantotal= stand.monday+stand.tuesday+stand.wednesday+stand.friday+stand.thursday+stand.sunday+stand.saturday;
                int stairtotal= stair.monday+stair.tuesday+stair.wednesday+stair.friday+stair.thursday+stair.sunday+stair.saturday;



                adim.setProgress((float) (walktotal%100));
                adim.setText(walktotal + " adim");


                kosu.setProgress((float) (runtotal%100));
                kosu.setText(runtotal+ " adim");


                merdiven.setProgress((float) (stairtotal%100));
                merdiven.setText(stairtotal + " basamak " );

                toplamhareket.setProgress((float) ((walktotal+runtotal+stairtotal)%100-(sittotal+stantotal)%100));
                toplamhareket.setText(walktotal+runtotal+stairtotal + " hareket " );

                handler.postDelayed(this,500); // set time here to refresh data
            }
        });

                                test();

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
    public void test(){
        walk.monday =0;
        walk.tuesday = 20;
        walk.wednesday = 53;
        walk.thursday = 0;
        walk.friday = 44;
        walk.saturday =1;
        walk.sunday = 4;

        stair.monday = 0;
        stair.tuesday = 29;
        stair.wednesday = 0;
        stair.thursday = 17;
        stair.friday = 1;
        stair.saturday = 2;
        stair.sunday = 3;

        sitting.monday = 24;
        sitting.tuesday = 60;
        sitting.wednesday = 73;
        sitting.thursday = 0;
        sitting.friday = 1;
        sitting.saturday = 16;
        sitting.sunday = 15;

        run.monday = 0;
        run.tuesday = 54;
        run.wednesday = 1;
        run.thursday = 2;
        run.friday = 8 ;
        run.saturday = 53;
        run.sunday = 15;

        stand.monday = 0 ;
        stand.tuesday = 35;
        stand.wednesday = 15;
        stand.thursday = 268;
        stand.friday = 5;
        stand.saturday = 18;
        stand.sunday = 2;

        totaldata.monday = 25;
        totaldata.tuesday = 16328;
        totaldata.wednesday = 5617;
        totaldata.thursday = 4812;
        totaldata.friday = 5859;
        totaldata.saturday = 7192;
        totaldata.sunday = 3384;
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

    public void connectServer(){ }
}