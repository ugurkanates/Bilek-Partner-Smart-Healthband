package com.example.akin.bilekpartner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AKIN Ç on 28.04.2019.
 * Update by Hasna on 4.05.2019
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME_USER = "USER";
    private static final String TABLE_NAME_GOAL = "GOAL";
    private static final String TABLE_NAME_SERVERDATA= "SERVERDATA";
    private static final String TABLE_NAME_LOG= "LOGS";

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
            sqLiteDatabase.execSQL("CREATE TABLE SERVERDATA(" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "DATE TEXT," +
                    "TEP FLOAT," +
                    "PULSE FLOAT," +
                    "PX FLOAT ," +
                    "PY FLOAT ," +
                    "PZ FLOAT ," +
                    "TINFO TEXT ," +
                    "PINFO TEXT ," +
                    "MINFO TEXT );"
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SERVERDATA);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_GOAL);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LOG);
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
    /*inserting MobileDataPackage class objesini SERVERDATA tablosuna */
    public boolean insert_serverdata(MobileDataPackage data  ) {

        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("DATE", data.date);
        contentValues.put("TEP", data.wbData.temp);
        contentValues.put("PULSE", data.wbData.pulse);
        contentValues.put("PX", data.wbData.pX);
        contentValues.put("PY", data.wbData.pY);
        contentValues.put("PZ", data.wbData.pZ);

        if (data.wbData.temp<0)
            contentValues.put("TINFO","FREEZE");
        else if (data.wbData.temp>=0 && data.wbData.temp<15)
            contentValues.put("TINFO","COLD");
        else if (data.wbData.temp>=15 && data.wbData.temp<=30)
            contentValues.put("TINFO","NORMAL");
        else if ( data.wbData.temp>30)
            contentValues.put("TINFO","HOT");
        else
            contentValues.put("TINFO","FAIL");

        if (data.wbData.pulse>0 && data.wbData.pulse<60)
            contentValues.put("PINFO","SLOW");
        else if (data.wbData.pulse>=60 && data.wbData.pulse<=100)
            contentValues.put("PINFO","NORMAL");
        else if ( data.wbData.pulse>100)
            contentValues.put("PINFO","FAST");
        else
            contentValues.put("PINFO","FAIL");

        if (data.wbData.pY>0 && data.wbData.pY<100)
            contentValues.put("MINFO","WALK");
        else if (data.wbData.pY>=100 && data.wbData.pY<=200)
            contentValues.put("MINFO","STAIRS");
        else if ( data.wbData.pY>200)
            contentValues.put("MINFO","RUN");
        else
            contentValues.put("MINFO","FAIL");

        long l = db1.insert(TABLE_NAME_SERVERDATA, null, contentValues);

        if (l != -1) {
            return true;
        } else {
            return false;
        }
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


    /*tabloya girilen son veriyi MobileDataPackage classi tipinde cikti olarak verir*/
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
    /*
    * son yapilan hareket yani cihaz icin suanki hareket bilgisinin tabloya eklenmis son bilgiyi cekerek onun
    * infosunu cikti olarak verir ornegi "RUN' ciktisi en son hareketinin kosu oldugunu bize soyler*/
    public String getLastMove() {
        String countQuery = "SELECT  * FROM  SERVERDATA  WHERE ID = (SELECT MAX(ID)  FROM SERVERDATA ) " ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        String mionfo = cursor.getString(9);
        cursor.close();
        return mionfo;
    }
    /*
    * parametre olarak verilen hareket turundeb tabloda kac tane satir oldugu sayisini dondurur
    * ornegi "WALK" denilirse kac adim yurundugu bilgisini verir getlastmove fonksiyenunun ciktisi ile kullanilir*/
    public int getMInfoCount( String cInfo) {
        String countQuery = "SELECT  * FROM SERVERDATA WHERE MINFO = '" + cInfo +"'" ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    /*
    * SERVERDATA tablosundaki toblam satir sayisini dondurur*/
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
