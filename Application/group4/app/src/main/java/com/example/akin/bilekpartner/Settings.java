package com.example.akin.bilekpartner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class Settings extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        bottomNavigate();

        Button server=findViewById(R.id.serv);
        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, ServerGrafik.class);
                startActivity(intent);
                finish();
            }
        });
        Button bt=findViewById(R.id.feedbackText);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, GoalsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button bt1=findViewById(R.id.logs);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, ShowLog.class);
                startActivity(intent);
                finish();
            }
        });
        Button exit=findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Settings.this);
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
        });
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
                                finish();
                                break;
                            case R.id.item2:
                                Intent intent2 = new Intent(Settings.this, MainActivity.class);
                                startActivity(intent2);
                                finish();
                                break;
                            case R.id.item4:
                                Intent intent4 = new Intent(Settings.this, Settings.class);
                                startActivity(intent4);
                                finish();
                                break;
                        }
                        return true;
                    }
                });
    }
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Settings.this);
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
