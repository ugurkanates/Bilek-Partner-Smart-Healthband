package com.example.akin.bilekpartner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Created by AKIN Ç on 15.04.2019.
 */

public class Settings extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        bottomNavigate();
    }
    public void bottomNavigate(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item1:
                                Intent intent = new Intent(Settings.this, InstantStateActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.item2:
                                Intent intent2 = new Intent(Settings.this, MainActivity.class);
                                startActivity(intent2);
                                finish();
                                break;
                            case R.id.item3:
                                break;
                        }
                        return true;
                    }
                });
    }
}
