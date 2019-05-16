package com.example.akin.bilekpartner;

public class StepCounter {
    private float xavg;
    private float yavg;
    private float zavg;
    float xval[];
    float yval[];
    float zval[];
    float xaccl[];
    float yaccl[];
    float zaccl[];
    private int steps=0,flag=0;
    private float threshhold=80.0f;
    private int veriSayisi;
    int acc=0;
    public StepCounter(int n){
        veriSayisi = n;
        xval = new float[n];
        yval = new float[n];
        zval = new float[n];
        xaccl = new float[n];
        yaccl = new float[n];
        zaccl = new float[n];

    }
    public StepCounter(){
        veriSayisi = 15; // DEFAULT buydu.
        xval = new float[15];
        yval = new float[15];
        zval = new float[15];
        xaccl = new float[15];
        yaccl = new float[15];
        zaccl = new float[15];
    }
    public int getPedo(float xVeri[],float yVeri[],float zVeri[]) {

        float totvect[] = new float[veriSayisi]; //15lik hepsi
        // GELEN VERI KESIN 15LIK VERI OLMALI VE DUZGUN VERLI OLMALIDIR #
        // gelen VERI KESINLIK 30LUK VERİYSE GELICEK 15 DOGRU SECIMELI
        //BENCE 2 ARRAY Olmali
        float totave[] = new float[veriSayisi];
        xaccl = xVeri;
        yaccl = yVeri;
        zaccl = zVeri;
        for (int i = 0; i < veriSayisi; i++) {

            totvect[i] = (float) Math.sqrt(((xaccl[i] - xavg) * (xaccl[i] - xavg)) + ((yaccl[i] - yavg) * (yaccl[i] - yavg)) + ((zval[i] - zavg) * (zval[i] - zavg)));
            totave[i] = (totvect[i] + totvect[i - 1]) / 2;

            //cal steps
            if (totave[i] > threshhold && flag == 0) {
                steps = steps + 1;
                flag = 1;
            } else if (totave[i] > threshhold && flag == 1) {
                //do nothing
            }
            if (totave[i] < threshhold && flag == 1) {
                flag = 0;
            }
        }
        // Serial.print("steps=");
        // Serial.println(steps);
        return (steps);
    }


    public void calibrate()
    {
        //.println("Calibrating......"); - TextViewi CALIBRATING TOAST ATSIN # /veya duruma
        float sum=0;
        float sum1=0;
        float sum2=0;
        for (int i=0;i<veriSayisi;i++)
        {
            xval[i]= //float seklinde bluetoothttangelen//float(analogRead(xpin));
                    sum=xval[i]+sum;
        }
        xavg=sum/15.0f;
        //  Serial.println(xavg); X'lerin Ortalamasini Print ET Text
        for (int j=0;j<15;j++)
        {
            xval[j]=//float(analogRead(xpin));
                    sum1=xval[j]+sum1;
        }
        yavg=sum1/15.0f;
        // Serial.println(yavg); Y'lerin Ortalamasını Print ET TEXT
        for (int i=0;i<15;i++)
        {
            zval[i]= //float(analogRead(zpin));
                    sum2=zval[i]+sum2;
        }
        zavg=sum2/15.0f;
        // Serial.println(zavg); Z'lerin Ortalamasını Print Et Text
        //.println("Calibration Successful!");
    }
}