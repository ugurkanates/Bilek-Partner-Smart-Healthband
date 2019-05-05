package com.example.akin.bilekpartner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.natasa.progressviews.CircleProgressBar;

public class Weekly extends AppCompatActivity {

    DataBaseHelper myDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weekly);

        myDB=new DataBaseHelper(this);
        bottomNavigate();


        final CircleProgressBar pulse = (CircleProgressBar) findViewById(R.id.fats_progress2);
        final CircleProgressBar temperature = (CircleProgressBar) findViewById(R.id.carbs_progress2);
        final CircleProgressBar movement = (CircleProgressBar) findViewById(R.id.protein_progress2);
        final CircleProgressBar stairs = (CircleProgressBar) findViewById(R.id.step1_progress);
        final CircleProgressBar run = (CircleProgressBar) findViewById(R.id.step2_progress);
        final CircleProgressBar walk = (CircleProgressBar) findViewById(R.id.walk_progress1);

        MobileDataPackage mdp =myDB.getLastData();
        String minfo = myDB.getLastMove();
        int moveCount = myDB.getMInfoCount(minfo);
        int totalMove=myDB.getTotalCount();
        int runCnt=myDB.getMInfoCount("RUN");
        int stairCnt=myDB.getMInfoCount("STAIRS");
        int walkCnt=myDB.getMInfoCount("WALK");

        pulse.setProgress(mdp.wbData.pulse%360);
        pulse.setText(mdp.wbData.pulse + " bmp");


        temperature.setProgress(mdp.wbData.temp%360);
        temperature.setText(String.valueOf(mdp.wbData.temp)+ " °C");


        movement.setProgress(moveCount%360);
        movement.setText(String.valueOf(totalMove) + " adım " );

        stairs.setProgress(stairCnt%360);
        stairs.setText(String.valueOf(stairCnt) + " basamak " );

        run.setProgress(runCnt%360);
        run.setText(String.valueOf(runCnt) + " adım " );

        walk.setProgress(walkCnt%360);
        walk.setText(String.valueOf(walkCnt) + " adım " );

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
                                Intent intent = new Intent(Weekly.this, InstantStateActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.item2://kendisi tekrar baslatiyor
                                Intent intent3 = new Intent(Weekly.this, MainActivity.class);
                                startActivity(intent3);
                                finish();
                                break;

                            case R.id.item3:
                                Intent intent2 = new Intent(Weekly.this, analyze.class);
                                startActivity(intent2);
                                finish();
                                break;
                            case R.id.item4:
                                Intent intent4 = new Intent(Weekly.this, Settings.class);
                                startActivity(intent4);
                                finish();
                                break;
                        }
                        return true;
                    }
                });
    }
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Weekly.this);
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
