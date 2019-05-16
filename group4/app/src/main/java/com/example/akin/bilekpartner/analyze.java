package com.example.akin.bilekpartner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class analyze extends AppCompatActivity {
    DataBaseHelper myDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        myDB=new DataBaseHelper(this);

        BarChart barChart = (BarChart) findViewById(R.id.barchart);

        MobileDataPackage mdp =myDB.getLastData();
        int totalMove=myDB.getTotalCount();
        int runCnt=myDB.getMInfoCount("RUN");
        int stairCnt=myDB.getMInfoCount("STAIRS");
        int walkCnt=myDB.getMInfoCount("WALK");

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(mdp.wbData.pulse, 0));
        entries.add(new BarEntry(mdp.wbData.temp, 1));
        entries.add(new BarEntry(walkCnt, 2));
        entries.add(new BarEntry(runCnt, 3));
        entries.add(new BarEntry(stairCnt, 4));
        entries.add(new BarEntry(totalMove, 5));


        BarDataSet bardataset = new BarDataSet(entries, "Haftalık Veri Özeti");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Nabız");
        labels.add("Sıcaklık");
        labels.add("Yürüme");
        labels.add("Koşma");
        labels.add("Merdiven");
        labels.add("Toplam Hareket");


        BarData data = new BarData(labels, bardataset);
        barChart.setData(data); // set the data and list of lables into chart

        barChart.setDescription("BilekPartner");  // set the description

        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);

        barChart.animateY(5000);


    }
/*bottom navigate kismi bu ekrana eklenemiyor uzerinde animasyon calistigindan dolai bende geri tusuna basilinca aminactivityi actiriyorum*/
    public void onBackPressed() {
        Intent intent4 = new Intent(analyze.this, MainActivity.class);
        startActivity(intent4);
        finish();

    }
}
