package com.example.proto2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;

public class DBHelper extends SQLiteOpenHelper {
    //관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name,SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    //DB 새로 생성 시 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        //새로운 테이블 생성
        db.execSQL("CREATE TABLE TrafficLight(id INTEGER PRIMARY KEY AUTOINCREMENT, trafficlight_lat TEXT, trafficlight_lng TEXT);");
        db.execSQL("CREATE TABLE BusStop(id INTEGER PRIMARY KEY AUTOINCREMENT, busstop_lat TEXT, busstop_lng TEXT);");
        db.execSQL("CREATE TABLE UserPath(id INTEGER PRIMARY KEY AUTOINCREMENT, user_lat TEXT, user_lng TEXT, time DATETIME DEFAULT (datetime('now','localtime')));");
    }

    //DB 업그레이드 버전 변경
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(){
        SQLiteDatabase db = getWritableDatabase();
//        String tl_lat, String tl_lng
//        db.execSQL("INSERT INTO TrafficLight('trafficlight_lat','trafficlight_lng') VALUES('" + tl_lat + "', '" + tl_lng + "');");

        db.execSQL("INSERT INTO TrafficLight('trafficlight_lat','trafficlight_lng') VALUES('35.89513', '128.62362');");
        db.execSQL("INSERT INTO TrafficLight('trafficlight_lat','trafficlight_lng') VALUES('35.89511', '128.623684');");
        db.execSQL("INSERT INTO TrafficLight('trafficlight_lat','trafficlight_lng') VALUES('35.895', '128.62426');");
        db.execSQL("INSERT INTO TrafficLight('trafficlight_lat','trafficlight_lng') VALUES('35.8951', '128.6243');");
        db.execSQL("INSERT INTO TrafficLight('trafficlight_lat','trafficlight_lng') VALUES('35.895084', '128.62426');");

        db.execSQL("INSERT INTO TrafficLight('trafficlight_lat','trafficlight_lng') VALUES('35.894187', '128.619485');");
        db.execSQL("INSERT INTO TrafficLight('trafficlight_lat','trafficlight_lng') VALUES('35.894101', '128.619228');");
        db.execSQL("INSERT INTO TrafficLight('trafficlight_lat','trafficlight_lng') VALUES('35.894262', '128.619005');");
        db.execSQL("INSERT INTO TrafficLight('trafficlight_lat','trafficlight_lng') VALUES('35.894309', '128.618920');");


//        db.execSQL("INSERT INTO BusStop('busstop_lat','busstop_lng') VALUES('35.8944791', '128.6238956');");
//        db.execSQL("INSERT INTO BusStop('busstop_lat','busstop_lng') VALUES('35.8929168', '128.6214253');");
        db.execSQL("INSERT INTO BusStop('busstop_lat','busstop_lng') VALUES('35.893447', '128.619989');");
        db.execSQL("INSERT INTO BusStop('busstop_lat','busstop_lng') VALUES('35.8936551', '128.6197469');");
        db.execSQL("INSERT INTO BusStop('busstop_lat','busstop_lng') VALUES('35.8949593', '128.6186385');");
        db.execSQL("INSERT INTO BusStop('busstop_lat','busstop_lng') VALUES('35.894948', '128.618090');");
        db.execSQL("INSERT INTO BusStop('busstop_lat','busstop_lng') VALUES('35.894433', '128.623899');");
        db.execSQL("INSERT INTO BusStop('busstop_lat','busstop_lng') VALUES('35.894074', '128.623826');");
        db.execSQL("INSERT INTO BusStop('busstop_lat','busstop_lng') VALUES('35.892937', '128.621346');");
        db.close();
    }

    public long insertUP(String lat, String lng){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("user_lat", lat);
        values.put("user_lng", lng);

        return db.insert("UserPath",null,values);

    }

    //전부
    public String getResultTL(){
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        Cursor cursor = db.rawQuery("SELECT * FROM TrafficLight", null);
        while(cursor.moveToNext()){
            result += cursor.getString(cursor.getColumnIndex("trafficlight_lat"))+","+cursor.getString(cursor.getColumnIndex("trafficlight_lng"))+",";
        }
        return result;
    }

    public String getResultBS(){
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        Cursor cursor = db.rawQuery("SELECT * FROM BusStop", null);
        while(cursor.moveToNext()){
            result += cursor.getString(cursor.getColumnIndex("busstop_lat"))+","+cursor.getString(cursor.getColumnIndex("busstop_lng"))+",";
        }
        return result;
    }

    //하나만
    public String getContactTL(int id){
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        Cursor cursor = db.rawQuery("SELECT * FROM TrafficLight", null);
        cursor.moveToPosition(id);
        result += cursor.getString(cursor.getColumnIndex("trafficlight_lat"))+","+cursor.getString(cursor.getColumnIndex("trafficlight_lng"));

        return result;
    }
}
