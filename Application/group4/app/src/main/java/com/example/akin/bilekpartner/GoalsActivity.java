package com.example.akin.bilekpartner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by AKIN Ç on 6.05.2019.
 */

public class GoalsActivity  extends AppCompatActivity{
    DataBaseHelper db;
    EditText walk;
    EditText run;
    EditText weight;
    TextView alldata;
    Button save;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        walk=findViewById(R.id.walk1);
        run=findViewById(R.id.run);
        weight=findViewById(R.id.weight1);
        alldata=findViewById(R.id.allgoals);
        save=findViewById(R.id.save);
        db=new DataBaseHelper(this);
        int arr[]=new int[3];
        boolean b=db.getGoals(arr);

        if(b==true){
            alldata.setText("Yürüme: m"+Integer.toString(arr[0])+"\n\nKoşu: m"+Integer.toString(arr[1])+"\n\nKilo: kg"+Integer.toString(arr[2]));
        }



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e1=walk.getText().toString();
                String e2=run.getText().toString();
                String e3=weight.getText().toString();
                if(e1.equals("")||e2.equals("")||e3.equals("")){
                    Toast.makeText(getApplicationContext(),"Tüm Alanları Doldurunuz.", Toast.LENGTH_SHORT).show();
                }
                else{
                    int e4=Integer.valueOf(e1);
                    int e5=Integer.valueOf(e2);
                    int e6=Integer.valueOf(e3);
                    if(e4<=0||e5<=0||e6<=0){
                        Toast.makeText(getApplicationContext(), "Yanlış Bilgi Formatı.", Toast.LENGTH_SHORT).show();
                    }
                    boolean insert=db.insert_goal(e4,e5,e6);
                    if(insert==true){
                        Toast.makeText(getApplicationContext(),"Kaydedildi.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(GoalsActivity.this, Settings.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Bilgileri Kontrol Ediniz",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
    public void onBackPressed() {
        Intent intent = new Intent(GoalsActivity.this, Settings.class);
        startActivity(intent);
        finish();

    }
}
