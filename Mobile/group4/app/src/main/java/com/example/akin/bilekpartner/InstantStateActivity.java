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
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.natasa.progressviews.CircleProgressBar;

import java.util.Random;

/**
 * Created by AKIN Ç on 15.04.2019.
 * Update by Hasna on 4.05.2019
 */

public class InstantStateActivity extends AppCompatActivity {

    DataBaseHelper myDB;
    String []arr={"RUN","STAIRS","WALK","FAIL"};
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_state);
        bottomNavigate();
    }

    public void bottomNavigate(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item1://kendisini tekrar baslatsin
                                Intent intent3 = new Intent(InstantStateActivity.this, InstantStateActivity.class);
                                startActivity(intent3);
                                finish();
                                break;
                            case R.id.item2:
                                Intent intent = new Intent(InstantStateActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.item3:
                                Intent intent4 = new Intent(InstantStateActivity.this, analyze.class);
                                startActivity(intent4);
                                finish();
                                break;
                            case R.id.item4:
                                Intent intent5 = new Intent(InstantStateActivity.this, Settings.class);
                                startActivity(intent5);
                                finish();
                                break;
                        }
                        return true;
                    }
                });
    }
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(InstantStateActivity.this);
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
