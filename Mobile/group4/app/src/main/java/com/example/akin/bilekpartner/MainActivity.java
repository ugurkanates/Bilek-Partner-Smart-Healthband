package com.example.akin.bilekpartner;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
 */

public class MainActivity  extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        bottomNavigate();

        final CircleProgressBar pulse = (CircleProgressBar) findViewById(R.id.fats_progress);
        pulse.setProgress((100 * (100) / 150));
        pulse.setText(String.valueOf((100 * (100) / 150)));

        final CircleProgressBar temperature = (CircleProgressBar) findViewById(R.id.carbs_progress);
        temperature.setProgress((100 * (100) / 290));
        temperature.setText(String.valueOf((100 * (100) / 290)));

        final CircleProgressBar act = (CircleProgressBar) findViewById(R.id.protein_progress);
        act.setProgress((100 * (100) / 270));
        act.setText(String.valueOf((100 * (100) / 270))+" Adım");
    }



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
                            case R.id.item2:
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
