package com.example.akin.bilekpartner;
/*
*  Update by Hasna on 4.05.2019
 */
public class MobileDataPackage {

    String date; // size of DATE_BUFFER_SIZE 30 olmali
    WristBandDataPackage wbData= new WristBandDataPackage();

    @Override
    public String toString() {
        return
                "\ndate='" + date  +
                  wbData.toString();
    }
}