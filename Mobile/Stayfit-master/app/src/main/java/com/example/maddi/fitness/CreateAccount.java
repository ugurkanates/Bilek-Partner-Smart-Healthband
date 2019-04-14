package com.example.maddi.fitness;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by AKIN Ç on 14.04.2019.
 */

public class CreateAccount extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_basicinfo);

        Button finish =findViewById(R.id.nextbutton);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( CreateAccount.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}