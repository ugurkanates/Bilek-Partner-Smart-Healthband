package com.example.akin.bilekpartner;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.natasa.progressviews.CircleProgressBar;

/**
 * Created by AKIN Ç on 15.04.2019.
 * Update by Hasna on 4.05.2019
 */

public class MainActivity  extends AppCompatActivity{

    DataBaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        myDB=new DataBaseHelper(this);
        bottomNavigate();
        MobileDataPackage mdp = new MobileDataPackage();
        mdp.date = "S2019 05 05 03:16:18";
        mdp.wbData.temp = Float.parseFloat("36.0");
        mdp.wbData.pulse = Float.parseFloat("85.0");
        mdp.wbData.pX = Float.parseFloat("120");
        mdp.wbData.pY = Float.parseFloat("130");
        mdp.wbData.pZ = Float.parseFloat("140");
        myDB.insert_serverdata(mdp);

        final CircleProgressBar pulse = (CircleProgressBar) findViewById(R.id.fats_progress);
        final CircleProgressBar temperature = (CircleProgressBar) findViewById(R.id.carbs_progress);
        final CircleProgressBar movement = (CircleProgressBar) findViewById(R.id.protein_progress);


        /*burda surekli bu islemi yapmasini istedim
        islem dedigim databaseden bilgileri alip processbarlari guncellemesi
        ! calismiyor olabilir test edemedim serverdan surekli bilgi aldiginda bakilabilir*/

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
                                Intent intent2 = new Intent(MainActivity.this, Settings.class);
                                startActivity(intent2);
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
}
