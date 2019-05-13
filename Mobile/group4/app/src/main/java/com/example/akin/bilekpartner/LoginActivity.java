package com.example.akin.bilekpartner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

public class LoginActivity extends AppCompatActivity {
    Button btn_login, btn_logout,other_app;
    EditText editTextEmail;
    EditText editTextPassword;
    DataBaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        Button createAccount = findViewById(R.id.link_signup);
        editTextEmail = (EditText) findViewById(R.id.input_email1);
        editTextPassword = (EditText) findViewById(R.id.input_password);
        db=new DataBaseHelper(this);
        Button login = findViewById(R.id.btn_login);
        other_app=findViewById(R.id.new_app);
        other_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.rockcode.har.demo");
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=editTextEmail.getText().toString();
                String password=editTextPassword.getText().toString();
                if(email.equals("") || password.equals("")){
                    Toast.makeText(getApplicationContext(),"Email ve Şifre boş bırakılmaz",Toast.LENGTH_SHORT).show();
                }
                if(isEmailValid(email)){
                    boolean chck=db.checkEmailPassword(email,password);
                    if(chck==true){
                        Toast.makeText(getApplicationContext(),"Giriş Başarılı",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Email veya Şifre hatalı.",Toast.LENGTH_SHORT).show();
                   }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Email Format Hatası",Toast.LENGTH_SHORT).show();
                }
            }
        });
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CreateAccount.class);
                startActivity(intent);
                finish();
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
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
