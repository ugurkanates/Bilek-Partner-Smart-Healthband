package com.example.akin.bilekpartner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.natasa.progressviews.CircleProgressBar;

/**
 * Created by AKIN Ç on 15.04.2019.
 */

public class InstantStateActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_state);

        bottomNavigate();

        final CircleProgressBar act = (CircleProgressBar) findViewById(R.id.fats_progress1);
        act.setProgress((100 * (100) / 270));
        act.setText("Merdiven Çıkılıyor...\n"+"3 basamak");
        act.setTextColor(Color.BLACK);

    }
    public void bottomNavigate(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item1:
                                break;
                            case R.id.item2:
                                Intent intent = new Intent(InstantStateActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.item3:
                                Intent intent2 = new Intent(InstantStateActivity.this, Settings.class);
                                startActivity(intent2);
                                finish();
                                break;
                        }
                        return true;
                    }
                });
    }
}
