package com.example.akin.bilekpartner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class grafikall extends AppCompatActivity {
    private Button b1,b2,b3,b4,b5,b6,b7,b8;

    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafikall);

        b1=(Button)findViewById(R.id.b1);
        b2=(Button)findViewById(R.id.b2);
        b3=(Button)findViewById(R.id.b3);
        b4=(Button)findViewById(R.id.b4);
        b5=(Button)findViewById(R.id.b25);
        b6=(Button)findViewById(R.id.b5);
        b7=(Button)findViewById(R.id.b7);
        b8=(Button)findViewById(R.id.b8);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(grafikall.this, grafik.class);
                startActivity(intent);
                finish();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(grafikall.this, grafik2.class);
                startActivity(intent);
                finish();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(grafikall.this, grafik6.class);
                startActivity(intent);
                finish();
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(grafikall.this, grafik3.class);
                startActivity(intent);
                finish();
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(grafikall.this, grafik5.class);
                startActivity(intent);
                finish();
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(grafikall.this, grafik6.class);
                startActivity(intent);
                finish();
            }
        });
        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(grafikall.this, ServerGrafik.class);
                startActivity(intent);
                finish();
            }
        });
        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(grafikall.this, stepgrafik.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void onBackPressed() {
        Intent intent2 = new Intent(grafikall.this, ServerGrafik.class);
        startActivity(intent2);
        finish();

    }
}
