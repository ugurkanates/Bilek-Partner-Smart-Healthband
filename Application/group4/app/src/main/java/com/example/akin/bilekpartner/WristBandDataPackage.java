package com.example.akin.bilekpartner;
/*
 * Update by Hasna on 4.05.2019
 */
public class WristBandDataPackage {

    float temp;
    float pulse;
    float pX;
    float pY;
    float pZ;

    @Override
    public String toString() {
        return
                "\ntemp=" + temp +
                "\n pulse=" + pulse +
                "\n pX=" + pX +
                "\n pY=" + pY +
                "\n pZ=" + pZ ;
    }
}