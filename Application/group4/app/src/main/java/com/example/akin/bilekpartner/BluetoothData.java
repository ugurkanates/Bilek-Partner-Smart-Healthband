package com.example.akin.bilekpartner;

/**
 * Created by AKIN Ç on 17.05.2019.
 */

public class BluetoothData {
    double acX;
    double acY;
    double acZ;
    double gX;
    double gY;
    double gZ;
    double heartRate;
    double temp;
    int batter;
    double deviceTemp;

    public BluetoothData() {
        acX=0;
        acY=0;
        acZ=0;
        gX=0;
        gY=0;
        gZ=0;
        heartRate=0;
        temp=0;
        batter=0;
        deviceTemp=0;
    }

    public double getAcX() {
        return acX;
    }

    public void setAcX(double acX) {
        this.acX = acX;
    }

    public double getAcY() {
        return acY;
    }

    public void setAcY(double acY) {
        this.acY = acY;
    }

    public double getAcZ() {
        return acZ;
    }

    public void setAcZ(double acZ) {
        this.acZ = acZ;
    }

    public double getgX() {
        return gX;
    }

    public void setgX(double gX) {
        this.gX = gX;
    }

    public double getgY() {
        return gY;
    }

    public void setgY(double gY) {
        this.gY = gY;
    }

    public double getgZ() {
        return gZ;
    }

    public void setgZ(double gZ) {
        this.gZ = gZ;
    }

    public double getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(double heartRate) {
        this.heartRate = heartRate;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getBatter() {
        return batter;
    }

    public void setBatter(int batter) {
        this.batter = batter;
    }

    public double getDeviceTemp() {
        return deviceTemp;
    }

    public void setDeviceTemp(double deviceTemp) {
        this.deviceTemp = deviceTemp;
    }
}