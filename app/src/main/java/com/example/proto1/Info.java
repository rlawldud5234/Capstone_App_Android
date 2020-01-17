package com.example.proto1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Info extends AppCompatActivity {
    SQLiteDatabase database;
    CustomerDatabaseHelper databaseHelper;
    String tableName = "PRODUCT";
    String databaseName = "memberJoin";
    String Cid;
    String Cpass;
    String Cname;
    String Cage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            if(database == null) {
                databaseHelper = new CustomerDatabaseHelper(getApplicationContext(), databaseName, null, 1);
                database = databaseHelper.getWritableDatabase();
                Toast.makeText(getApplication(), "DB : "+databaseName+"이 생성되었습니다.", Toast.LENGTH_SHORT).show();
            }else if(database != null) {
                Toast.makeText(getApplication(), "이미 디비 열렸음", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        try{
            if(database != null){
                database.execSQL("CREATE TABLE if not exists "+tableName+"("+"_id integer PRIMARY KEY autoincrement,"+"id text,"+"pass text,"+"name text,"+"age text"+")" );
                Toast.makeText(getApplication(), "Table : "+tableName+"이 생성되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    class CustomerDatabaseHelper extends SQLiteOpenHelper{
        CustomerDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onOpen(SQLiteDatabase database){
            super.onOpen(database);
        }

        @Override
        public void onCreate(SQLiteDatabase database){

        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){

        }
    }

}
