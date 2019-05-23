package com.example.akin.bilekpartner;


import java.lang.Object;
import java.lang.Math;

public class Mlogic{

    enum UserState{
        STANDING, WALKING, RUNNING, ON_STAIRS
    };

    public static class Odata{
        double ax;
        double ay;
        double az;
        double gx;
        double gy;
        double gz;

        Odata(){
            ax = 0.0;
            ay = 0.0;
            az = 0.0;
            gx = 0.0;
            gy = 0.0;
            gz = 0.0;
        }
    }

    public class allOData{
        Odata aData;
        double po;
        boolean isGyroDown;
        boolean isaxNeg;
        boolean isayNeg;
        boolean isazNeg;
        boolean isMoving;
        UserState lastState;

        allOData(){
            aData = new Odata();
            po = 0.0;
            isGyroDown = false;
            isaxNeg = false;
            isayNeg = false;
            isazNeg = false;
            isMoving = false;
            lastState = UserState.STANDING;
        }
    }

    allOData[] lastAllData;
    allOData lastOdata;

    Mlogic(){
        lastAllData = new allOData[2];
        lastAllData[0] = new allOData();
        lastAllData[1] = new allOData();
        lastOdata = new allOData();
    }

    public void init(double acx[],double acy[],double acz[],double gcx[],double gcy[], double gcz[]){
        for(int i=0;i<2;i++){
            lastAllData[i].aData.ax=acx[i];
            lastAllData[i].aData.ay=acy[i];
            lastAllData[i].aData.az=acz[i];
            lastAllData[i].aData.gx=gcx[i];
            lastAllData[i].aData.gy=gcy[i];
            lastAllData[i].aData.gz=gcz[i];
        }
    }

    public UserState whatIsUserDoing(double acx[],double acy[],double acz[],double gcx[],double gcy[], double gcz[]){
        allOData[] mahData = new allOData[4];
        allOData newAve = new allOData();

        for(int i=0; i<4; ++i){
            mahData[i]=new allOData();
            if(i<2){
                mahData[i].aData.ax = lastAllData[i].aData.ax;
                mahData[i].aData.ay = lastAllData[i].aData.ay;
                mahData[i].aData.az = lastAllData[i].aData.az;
                mahData[i].aData.gx = lastAllData[i].aData.gx;
                mahData[i].aData.gy = lastAllData[i].aData.gy;
                mahData[i].aData.gz = lastAllData[i].aData.gz;
                mahData[i].po = lastAllData[i].po;
                mahData[i].isGyroDown = lastAllData[i].isGyroDown;
                mahData[i].isaxNeg = lastAllData[i].isaxNeg;
                mahData[i].isayNeg = lastAllData[i].isayNeg;
                mahData[i].isazNeg = lastAllData[i].isazNeg;
                mahData[i].isMoving = lastAllData[i].isMoving;
                mahData[i].lastState = lastAllData[i].lastState;
            }
            else{
                mahData[i].aData.ax = acx[i-2];
                mahData[i].aData.ay = acy[i-2];
                mahData[i].aData.az = acz[i-2];
                mahData[i].aData.gx = gcx[i-2];
                mahData[i].aData.gy = gcy[i-2];
                mahData[i].aData.gz = gcz[i-2];
                mahData[i].po = java.lang.Math.sqrt(java.lang.Math.pow(mahData[i-2].aData.ax, 2.0) + java.lang.Math.pow(mahData[i-2].aData.ay, 2.0));
                if (java.lang.Math.abs(mahData[i-2].aData.gx) - java.lang.Math.abs(mahData[i-1].aData.gx) < 0) {
                    mahData[i].isGyroDown = true;
                    mahData[i].isMoving = true;
                }
                if (java.lang.Math.abs(mahData[i-2].aData.gx) - java.lang.Math.abs(mahData[i-1].aData.gx) > 0)
                    mahData[i].isMoving = true;
                if(mahData[i].aData.ax < 0)
                    mahData[i].isaxNeg = true;
                if(mahData[i].aData.ay < 0)
                    mahData[i].isayNeg = true;
                if(mahData[i].aData.az < 0)
                    mahData[i].isazNeg = true;
                mahData[i].lastState = UserState.STANDING;
            }
        }

        for(int q=0; q<4; ++q){
            if(mahData[q].aData.ax < 0)
                newAve.isaxNeg = true;
            if(mahData[q].aData.ay < 0)
                newAve.isayNeg = true;
            if(mahData[q].aData.az < 0)
                newAve.isazNeg = true;
            if(q>0) {
                if (java.lang.Math.abs(mahData[q].aData.gx) - java.lang.Math.abs(mahData[q - 1].aData.gx) < 0) {
                    newAve.isGyroDown = true;
                    newAve.isMoving = true;
                }
                if (java.lang.Math.abs(mahData[q].aData.gx) - java.lang.Math.abs(mahData[q - 1].aData.gx) > 0)
                    newAve.isMoving = true;
            }
            newAve.aData.ax += mahData[q].aData.ax;
            newAve.aData.ay += mahData[q].aData.ay;
            newAve.aData.az += mahData[q].aData.az;
            newAve.aData.gx += mahData[q].aData.gx;
            newAve.aData.gy += mahData[q].aData.gy;
            newAve.aData.gz += mahData[q].aData.gz;
        }

        newAve.aData.ax /= 4.0;
        newAve.aData.ay /= 4.0;
        newAve.aData.az /= 4.0;
        newAve.aData.gx /= 4.0;
        newAve.aData.gy /= 4.0;
        newAve.aData.gz /= 4.0;
        newAve.po = java.lang.Math.sqrt(java.lang.Math.pow(newAve.aData.ax, 2.0) + java.lang.Math.pow(newAve.aData.ay, 2.0));

        if(newAve.isMoving){
            int negs = 0;
            for(int k=0; k<4; ++k){
                if(mahData[k].isaxNeg)
                    ++negs;
            }
            if(newAve.isaxNeg)
                ++negs;
            if(lastOdata.lastState != UserState.RUNNING && negs > 1){
                newAve.lastState = UserState.ON_STAIRS;
            }
            else if(lastOdata.lastState == UserState.STANDING){
                if(newAve.po > 1.1 || 0.85 > newAve.po){
                    newAve.lastState = UserState.WALKING;
                }
                else{
                    newAve.lastState = UserState.STANDING;
                }
            }
            else if(lastOdata.lastState == UserState.WALKING || lastOdata.lastState == UserState.ON_STAIRS){
                if(newAve.po > 1.3 || 0.75 > newAve.po){
                    newAve.lastState = UserState.RUNNING;
                }
                else if(newAve.po > 1.1 || 0.85 > newAve.po){
                    newAve.lastState = UserState.WALKING;
                }
                else{
                    newAve.lastState = UserState.STANDING;
                }
            }
            else if(lastOdata.lastState == UserState.RUNNING){
                if(newAve.po > 1.3 || 0.75 > newAve.po){
                    newAve.lastState = UserState.RUNNING;
                }
                else{
                    newAve.lastState = UserState.WALKING;
                }
            }
        }
        else{
            newAve.lastState = UserState.STANDING;
        }

        for(int i=0; i<2; ++i){
            lastAllData[i].aData.ax = mahData[i+2].aData.ax;
            lastAllData[i].aData.ay = mahData[i+2].aData.ay;
            lastAllData[i].aData.az = mahData[i+2].aData.az;
            lastAllData[i].aData.gx = mahData[i+2].aData.gx;
            lastAllData[i].aData.gy = mahData[i+2].aData.gy;
            lastAllData[i].aData.gz = mahData[i+2].aData.gz;
            lastAllData[i].po = mahData[i+2].po;
            lastAllData[i].isGyroDown = mahData[i+2].isGyroDown;
            lastAllData[i].isaxNeg = mahData[i+2].isaxNeg;
            lastAllData[i].isayNeg = mahData[i+2].isayNeg;
            lastAllData[i].isazNeg = mahData[i+2].isazNeg;
            lastAllData[i].isMoving = mahData[i+2].isMoving;
            lastAllData[i].lastState = mahData[i+2].lastState;
        }
        lastOdata.aData.ax = newAve.aData.ax;
        lastOdata.aData.ay = newAve.aData.ay;
        lastOdata.aData.az = newAve.aData.az;
        lastOdata.aData.gx = newAve.aData.gx;
        lastOdata.aData.gy = newAve.aData.gy;
        lastOdata.aData.gz = newAve.aData.gz;
        lastOdata.po = newAve.po;
        lastOdata.isGyroDown = newAve.isGyroDown;
        lastOdata.isaxNeg = newAve.isaxNeg;
        lastOdata.isayNeg = newAve.isayNeg;
        lastOdata.isazNeg = newAve.isazNeg;
        lastOdata.isMoving = newAve.isMoving;
        lastOdata.lastState = newAve.lastState;
        return newAve.lastState;
    }
}