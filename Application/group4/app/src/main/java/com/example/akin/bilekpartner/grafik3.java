package com.example.akin.bilekpartner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import static com.example.akin.bilekpartner.SplashActivity.run;

public class grafik3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafik3);

        BarChart barChart = (BarChart) findViewById(R.id.barchart4);

        int total=run.monday+run.tuesday+run.wednesday+run.thursday+run.friday+run.saturday+run.sunday;

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(run.monday, 0));
        entries.add(new BarEntry(run.tuesday, 1));
        entries.add(new BarEntry(run.wednesday, 2));
        entries.add(new BarEntry(run.thursday, 3));
        entries.add(new BarEntry(run.friday, 4));
        entries.add(new BarEntry(run.saturday, 5));
        entries.add(new BarEntry(run.sunday, 6));
        entries.add(new BarEntry(total, 7));


        BarDataSet bardataset = new BarDataSet(entries, "Haftalık Koşu Özeti");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Pazartesi");
        labels.add("Salı");
        labels.add("Çarsamba");
        labels.add("Perşembe");
        labels.add("Cuma");
        labels.add("Cumartesi");
        labels.add("Pazar");
        labels.add("Toplam");

        BarData data = new BarData( labels, bardataset);
        barChart.setData(data); // set the data and list of lables into chart
        bardataset.setValueTextSize(30f);
        barChart.setDescription("Bilek-Partner");  // set the description

        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);

        barChart.animateY(5000);


    }
    /*bottom navigate kismi bu ekrana eklenemiyor uzerinde animasyon calistigindan dolai bende geri tusuna basilinca aminactivityi actiriyorum*/
    public void onBackPressed() {
        Intent intent4 = new Intent(grafik3.this, ServerGrafik.class);
        startActivity(intent4);
        finish();

    }
}
