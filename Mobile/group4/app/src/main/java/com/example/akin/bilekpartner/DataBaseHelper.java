package com.example.akin.bilekpartner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by AKIN Ç on 28.04.2019.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    public DataBaseHelper(Context context){
        super(context,"Login.db",null,1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("Create table user(name  text ,number text,age int,weight int, height int,email text,password text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists user");
    }
    public boolean insert(String name,String number,int age,int weight,int height,String email,String password){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put("name",name);
        cv.put("number",number);
        cv.put("age",age);
        cv.put("weight",weight);
        cv.put("height",height);
        cv.put("email",email);
        cv.put("password",password);
        long insertData =db.insert("user",null,cv);
        if(insertData==-1)
            return false;
        return true;
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
}
