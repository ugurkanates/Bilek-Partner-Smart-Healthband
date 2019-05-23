package com.example.akin.bilekpartner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.akin.bilekpartner.Mlogic.Odata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AKIN Ç on 28.04.2019.
 * Update by Hasna on 4.05.2019
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME_USER = "USER";
    private static final String TABLE_NAME_GOAL = "GOAL";
    private static final String TABLE_NAME_LOG= "LOGS";
    private  static final String TABLE_MAIN_LOGIC="MAINLOGIC";
    private static final String QUERY_GETMAX = "SELECT  * FROM  MAINLOGIC  WHERE ID = (SELECT MAX(ID)  FROM MAINLOGIC ) " ;
    private static final String QUERY_DAY_AVG_PULSE = "SELECT  AVG( PULSE ), DATE FROM MAINLOGIC WHERE DATE BETWEEN datetime('now', 'start of day') AND datetime('now', 'localtime');";
    private static final String QUERY_WEEK_AVG_PULSE = "SELECT  AVG( PULSE ), DATE FROM MAINLOGIC WHERE DATE BETWEEN datetime('now', '-6 days') AND datetime('now', 'localtime');";
    private static final String QUERY_MONTH_AVG_PULSE = "SELECT  AVG( PULSE ), DATE FROM MAINLOGIC WHERE DATE BETWEEN datetime('now', 'start of month') AND datetime('now', 'localtime');";
    private static final String QUERY_DAY_AVG_TEMP = "SELECT  AVG( TEP ), DATE FROM MAINLOGIC WHERE DATE BETWEEN datetime('now', 'start of day') AND datetime('now', 'localtime');";
    private static final String QUERY_WEEK_AVG_TEMP = "SELECT  AVG( TEP ), DATE FROM MAINLOGIC WHERE DATE BETWEEN datetime('now', '-6 days') AND datetime('now', 'localtime');";
    private static final String QUERY_MONTH_AVG_TEMP = "SELECT  AVG( TEP ), DATE FROM MAINLOGIC WHERE DATE BETWEEN datetime('now', 'start of month') AND datetime('now', 'localtime');";

    public DataBaseHelper(Context context){
        super(context,"Login.db",null,1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL("Create table user(" +
                    "name  text ," +
                    "number text," +
                    "age int," +
                    "weight int," +
                    " height int," +
                    "email text," +
                    "password text,"+"emername text,"+"emernum text)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            sqLiteDatabase.execSQL("Create table GOAL(" +
                    "walk  int ," +
                    "run int," +
                    "weight int,"+"id int)");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            sqLiteDatabase.execSQL("CREATE TABLE MAINLOGIC(" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "DATE TEXT," +
                    "AX DOUBLE," +
                    "AY DOUBLE," +
                    "AZ DOUBLE ," +
                    "GX DOUBLE ," +
                    "GY DOUBLE ," +
                    "GZ DOUBLE ," +
                    "PULSE DOUBLE ," +
                    "TEP DOUBLE ," +
                    "BAT DOUBLE ," +
                    "TEP2 DOUBLE );"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            sqLiteDatabase.execSQL("Create table LOG(" +
                    "log text)");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_GOAL);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LOG);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MAIN_LOGIC);
    }
    public boolean insert_goal(int walk,int run,int weight){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put("walk",walk);
        cv.put("run",run);
        cv.put("weight",weight);
        cv.put("id",1);
        long insertData =db.insert("GOAL",null,cv);
        if(insertData==-1)
            return false;
        return true;
    }
    //insert logs;
    public boolean insert_log(String log){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put("log",log);
        long insertData =db.insert("LOG",null,cv);
        if(insertData==-1)
            return false;
        return true;
    }
   public boolean insert(String name,String number,int age,int weight,int height,String email,String password,String emername,String emernum){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put("name",name);
        cv.put("number",number);
        cv.put("age",age);
        cv.put("weight",weight);
        cv.put("height",height);
        cv.put("email",email);
        cv.put("password",password);
        cv.put("emername",emername);
        cv.put("emernum",emernum);
        long insertData =db.insert("user",null,cv);
        if(insertData==-1)
            return false;
        return true;
    }


    public boolean insert_mainlogic(socketdata a ) {

        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("DATE", a.date);
        contentValues.put("AX", a.odata.ax);
        contentValues.put("AY", a.odata.ay);
        contentValues.put("AZ", a.odata.az);
        contentValues.put("GX", a.odata.gx);
        contentValues.put("GY", a.odata.gy);
        contentValues.put("GZ", a.odata.gz);
        contentValues.put("PULSE", a.pulse);
        contentValues.put("TEP", a.tep);
        contentValues.put("BAT", a.bat);
        contentValues.put("TEP2", a.tep2);

        long l = db1.insert(TABLE_MAIN_LOGIC, null, contentValues);

        if (l != -1) {
            return true;
        } else {
            return false;
        }
    }
    public ArrayList<Odata> mainLogicServerArr() {

        ArrayList<Odata> list = new ArrayList<Odata>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MAIN_LOGIC;

        SQLiteDatabase db = this.getReadableDatabase();
        try {

            Cursor cursor = db.rawQuery(selectQuery, null);
            try {

                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        Odata obj;
                        obj = new Odata();
                        obj.ax= Double.valueOf(cursor.getString(2));
                        obj.ay= Double.valueOf(cursor.getString(3));
                        obj.az= Double.valueOf(cursor.getString(4));
                        obj.gx= Double.valueOf(cursor.getString(5));
                        obj.gy= Double.valueOf(cursor.getString(6));
                        obj.gz= Double.valueOf(cursor.getString(7));
                        list.add(obj);
                    } while (cursor.moveToNext());
                }

            } finally {
                try { cursor.close(); } catch (Exception ignore) {}
            }

        } finally {
            try { db.close(); } catch (Exception ignore) {}
        }

        return list;
    }




    public Boolean checkEmailPassword(String email,String password){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cr=db.rawQuery("Select * from user where email=? and password=?",new String[] {email,password});
        if(cr.getCount()>0) return true;
        return false;
    }
    public boolean checkEmail(String email){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cr=db.rawQuery("Select * from user where email=?",new String[] {email});
        if(cr.getCount()>0) return false;
        return true;
    }
    public boolean getGoals(int arr[]){
        SQLiteDatabase db=getReadableDatabase();
        Cursor cr=db.rawQuery("Select * from GOAL where id=1",null);
        if(cr!= null && cr.moveToFirst()) {
            arr[0]=cr.getInt(0);
            arr[1]=cr.getInt(1);
            arr[2]=cr.getInt(2);
            cr.close();
            return true;
        }
        return false;
    }
    public socketdata getLastData() {
        String countQuery = "SELECT  * FROM  MAINLOGIC  WHERE ID = (SELECT MAX(ID)  FROM MAINLOGIC ) " ;
        SQLiteDatabase db = this.getReadableDatabase();
        socketdata mdp = new socketdata();
        Cursor cursor = null;
        cursor = db.rawQuery(countQuery, null);
        if( cursor != null && cursor.moveToFirst() ) {
            mdp.pulse = cursor.getDouble(8);
            mdp.tep = cursor.getDouble(9);
            mdp.bat = cursor.getDouble(10);
            mdp.tep2 = cursor.getDouble(11);
            cursor.close();
        }
        return mdp;
    }


/*tabloya girilen son veriyi MobileDataPackage classi tipinde cikti olarak verir*//*

    public MobileDataPackage getLastData() {
        String countQuery = "SELECT  * FROM  SERVERDATA  WHERE ID = (SELECT MAX(ID)  FROM SERVERDATA ) " ;
        SQLiteDatabase db = this.getReadableDatabase();
        MobileDataPackage mdp = new MobileDataPackage();
        Cursor cursor = null;
        cursor = db.rawQuery(countQuery, null);
        if( cursor != null && cursor.moveToFirst() ) {
            mdp.date = cursor.getString(1);
            mdp.wbData.temp = cursor.getFloat(2);
            mdp.wbData.pulse = cursor.getFloat(3);
            mdp.wbData.pX = cursor.getFloat(4);
            mdp.wbData.pY = cursor.getFloat(5);
            mdp.wbData.pZ = cursor.getFloat(6);
            cursor.close();
        }
        return mdp;
    }
    */
/*
     * son yapilan hareket yani cihaz icin suanki hareket bilgisinin tabloya eklenmis son bilgiyi cekerek onun
     * infosunu cikti olarak verir ornegi "RUN' ciktisi en son hareketinin kosu oldugunu bize soyler*//*

    public String getLastMove() {
        String countQuery = "SELECT  * FROM  SERVERDATA  WHERE ID = (SELECT MAX(ID)  FROM SERVERDATA ) " ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        String mionfo = cursor.getString(9);
        cursor.close();
        return mionfo;
    }
    */
/*
     * parametre olarak verilen hareket turundeb tabloda kac tane satir oldugu sayisini dondurur
     * ornegi "WALK" denilirse kac adim yurundugu bilgisini verir getlastmove fonksiyenunun ciktisi ile kullanilir*//*

    public int getMInfoCount( String cInfo) {
        String countQuery = "SELECT  * FROM SERVERDATA WHERE MINFO = '" + cInfo +"'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    */
/*
     * SERVERDATA tablosundaki toblam satir sayisini dondurur*//*

    public int getTotalCount( ) {
        String countQuery = "SELECT  * FROM SERVERDATA " ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public Float getAvgTemperature( ) {
        String countQuery = "SELECT  AVG( TEP ) FROM SERVERDATA "  ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        String avg = cursor.getString(0);
        cursor.close();
        return Float.valueOf(avg);
    }
    public Float getAvgPulse( ) {
        String countQuery = "SELECT  AVG( PULSE ) FROM SERVERDATA "  ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        String avg = cursor.getString(0);
        cursor.close();
        return Float.valueOf(avg);
    }
    public Float get_Avg_Day_Pulse( ) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(QUERY_DAY_AVG_PULSE, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if(count>0) {
            String avg = cursor.getString(0);
            cursor.close();
            return Float.valueOf(avg);
        }else {
            cursor.close();
            return Float.valueOf(0);
        }
    }
    public Float get_Avg_Week_Pulse( ) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(QUERY_WEEK_AVG_PULSE, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if(count>0) {
            String avg = cursor.getString(0);
            cursor.close();
            return Float.valueOf(avg);
        }else
            return Float.valueOf(0);
    }
    public Float get_Avg_Month_Pulse( ) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(QUERY_MONTH_AVG_PULSE, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if(count>0) {
            String avg = cursor.getString(0);
            cursor.close();
            return Float.valueOf(avg);
        }else
            return Float.valueOf(0);
    }

    public Float get_Avg_Day_Temp( ) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(QUERY_DAY_AVG_TEMP, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if(count>0) {
            String avg = cursor.getString(0);
            cursor.close();
            return Float.valueOf(avg);
        }else
            return Float.valueOf(0);

    }
    public Float get_Avg_Week_Temp( ) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(QUERY_WEEK_AVG_TEMP, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if(count>0) {
            String avg = cursor.getString(0);
            cursor.close();
            return Float.valueOf(avg);
        }else
            return Float.valueOf(0);
    }
    public Float get_Avg_Month_Temp( ) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(QUERY_MONTH_AVG_TEMP, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if(count>0) {
            String avg = cursor.getString(0);
            cursor.close();
            return Float.valueOf(avg);
        }else
            return Float.valueOf(0);
    }

    public int get_Cnt_Day_Walk( ) {
        // String countQuery = "SELECT  * FROM SERVERDATA WHERE MINFO = WALK " ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_DAY_TOTAL_WALK, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public int get_Cnt_Week_Walk( ) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_WEEK_TOTAL_WALK, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public int get_Cnt_Month_Walk( ) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_MONTH_TOTAL_WALK, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int get_Cnt_Day_Run( ) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_DAY_TOTAL_RUN, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public int get_Cnt_Week_Run( ) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_WEEK_TOTAL_RUN, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public int get_Cnt_Month_Run( ) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_MONTH_TOTAL_RUN, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int get_Cnt_Day_Stair( ) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_DAY_TOTAL_STAIR, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public int get_Cnt_Week_Stair( ) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_WEEK_TOTAL_STAIR, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public int get_Cnt_Month_Stair( ) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_MONTH_TOTAL_STAIR, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int get_Cnt_Day_Wait( ) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_DAY_TOTAL_WAIT, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public int get_Cnt_Week_Wait( ) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_WEEK_TOTAL_WAIT, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    public int get_Cnt_Month_Wait( ) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QUERY_MONTH_TOTAL_WAIT, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
*/

    public void deleteLog(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("LOG", null, null);
    }
    public ArrayList<String> getAllLogs() {
        ArrayList<String> movieDetailsList = new ArrayList();
        String selectQuery = "SELECT * FROM LOG";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery(selectQuery, null);

        //if TABLE has rows
        if (cursor.moveToFirst()) {
            do {
                movieDetailsList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        db.close();
        return movieDetailsList;
    }
}