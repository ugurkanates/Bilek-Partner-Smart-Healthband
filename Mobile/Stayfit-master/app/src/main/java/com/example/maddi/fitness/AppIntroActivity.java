package com.example.maddi.fitness;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by maddi on 4/23/2016.
 */
public class AppIntroActivity extends AppIntro {


    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {
        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Göz Alıcı Grafikler", "Günlük İstatikler", R.drawable.appintro1, getResources().getColor(R.color.appintro1)));
        addSlide(AppIntroFragment.newInstance("", "Başla", R.drawable.appintro4, getResources().getColor(R.color.appintro4)));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#F44336"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed() {
        // Do something when users tap on Skip button.
        Intent i = new Intent(AppIntroActivity.this, EnterInfoActivity.class);
        startActivity(i);
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        Intent i = new Intent(AppIntroActivity.this, EnterInfoActivity.class);
        startActivity(i);

        Toast.makeText(getApplicationContext(), "Tamamlandı", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

}