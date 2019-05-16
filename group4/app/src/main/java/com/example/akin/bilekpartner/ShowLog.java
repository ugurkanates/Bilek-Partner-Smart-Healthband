package com.example.akin.bilekpartner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by AKIN Ç on 16.05.2019.
 */

public class ShowLog extends AppCompatActivity{
    private TextView text;
    private ScrollView scrollView;
    DataBaseHelper db;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        db=new DataBaseHelper(this);
        ArrayList<String> arrayList=new ArrayList<>();
        arrayList=db.getAllLogs();
        text = (TextView) findViewById(R.id.text);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText(arrayList.toString());


    }
    public void onBackPressed() {
        Intent intent = new Intent(ShowLog.this, Settings.class);
        startActivity(intent);
        finish();

    }
}
