package com.example.akin.bilekpartner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AKIN Ç on 15.04.2019.
 */

public class CreateAccount extends AppCompatActivity {
    DataBaseHelper db;
    EditText name;
    EditText number;
    EditText age;
    EditText weight;
    EditText height;
    EditText email;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        name=findViewById(R.id.nameInput);
        number=findViewById(R.id.phoneInput);
        age=findViewById(R.id.ageInput);
        weight=findViewById(R.id.weightInput);
        height=findViewById(R.id.heightInput);
        email=findViewById(R.id.email1);
        password=findViewById(R.id.password1);
        Button logingA=findViewById(R.id.loginAgain);
        logingA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateAccount.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        final Button completed = findViewById(R.id.nextbutton);
        db=new DataBaseHelper(this);
        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e1=name.getText().toString();
                String c1=number.getText().toString();
                String c2=age.getText().toString();
                String c3=weight.getText().toString();
                String c4=height.getText().toString();
                String e6= email.getText().toString();
                String e7=password.getText().toString();
                if(e1.equals("")||c1.equals("")||c2.equals("")||c3.equals("")||c4.equals("")||e6.equals("")||e7.equals("")){
                    Toast.makeText(getApplicationContext(),"Tüm Alanları Doldurunuz.",Toast.LENGTH_SHORT).show();
                }
                if(c1.length()<11) {
                    Toast.makeText(getApplicationContext(), "Geçerli Bir Numara Giriniz.", Toast.LENGTH_SHORT).show();
                }
               else{
                    int e3=Integer.valueOf(age.getText().toString());
                    int e4=Integer.valueOf(c3);
                    boolean ch=db.checkEmail(e6);
                    int e5=Integer.valueOf(c4);
                    if(e3<=0||e4<=0||e5<=0){
                        Toast.makeText(getApplicationContext(), "Yanlış Bilgi Formatı.", Toast.LENGTH_SHORT).show();
                    }
                    else if(c1.indexOf("0")!=0 || c1.indexOf("5")!=1){
                        Toast.makeText(getApplicationContext(), "Yanlış Telefon Numarası Formatı.", Toast.LENGTH_SHORT).show();
                    }
                    else if(ch==true){
                        boolean insert=db.insert(e1,c1,e3,e4,e5,e6,e7);
                        if(insert==true){
                            Toast.makeText(getApplicationContext(),"Kayıt Başarılı.",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateAccount.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Farklı Bir Email Giriniz",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        if(e6.toLowerCase().contains("@gtu.edu.tr"))
                            Toast.makeText(getApplicationContext(),"Farklı Bir Email Giriniz",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(),"@gtu.edu.tr uzantılı email giriniz",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateAccount.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
