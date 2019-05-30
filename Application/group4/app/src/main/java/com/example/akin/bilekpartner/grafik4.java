package com.example.akin.bilekpartner;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.example.akin.bilekpartner.SplashActivity.run;
import static com.example.akin.bilekpartner.SplashActivity.sitting;
import static com.example.akin.bilekpartner.SplashActivity.stair;
import static com.example.akin.bilekpartner.SplashActivity.stand;
import static com.example.akin.bilekpartner.SplashActivity.totaldata;
import static com.example.akin.bilekpartner.SplashActivity.walk;

public class grafik4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafik4);
        PieChart pieChart= (PieChart) findViewById(R.id.piechart);
        ArrayList NoOfEmp = new ArrayList();

        int runtotal = run.monday+run.tuesday+run.wednesday+run.friday+run.thursday+run.sunday+run.saturday;
        int walktotal= walk.monday+walk.tuesday+walk.wednesday+walk.friday+walk.thursday+walk.sunday+walk.saturday;
        int sittotal = sitting.monday+sitting.tuesday+sitting.wednesday+sitting.friday+sitting.thursday+sitting.sunday+sitting.saturday;
        int stantotal= stand.monday+stand.tuesday+stand.wednesday+stand.friday+stand.thursday+stand.sunday+stand.saturday;
        int stairtotal= stair.monday+stair.tuesday+stair.wednesday+stair.friday+stair.thursday+stair.sunday+stair.saturday;

        NoOfEmp.add(new Entry(walktotal, 0));
        NoOfEmp.add(new Entry(stairtotal, 1));
        NoOfEmp.add(new Entry(sittotal, 2));
        NoOfEmp.add(new Entry(runtotal, 3));
        NoOfEmp.add(new Entry(stantotal, 4));

        PieDataSet dataSet = new PieDataSet(NoOfEmp, "Toplam hareket");

        ArrayList year = new ArrayList();

        year.add("Yürüme");
        year.add("Merdiven");
        year.add("Dinlenme");
        year.add("Koşu");
        year.add("Durma");


        PieData data = new PieData(year, dataSet);
        pieChart.setData(data);
        pieChart.setCenterTextColor(Color.BLACK);

        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setCenterTextSize(20f);
        pieChart.setCenterText("Bilek-Partner");
        data.setValueTextSize(25f);

        pieChart.animateXY(5000, 5000);
    }
    /*bottom navigate kismi bu ekrana eklenemiyor uzerinde animasyon calistigindan dolai bende geri tusuna basilinca aminactivityi actiriyorum*/
    public void onBackPressed() {
        Intent intent4 = new Intent(grafik4.this, ServerGrafik.class);
        startActivity(intent4);
        finish();

    }
}
