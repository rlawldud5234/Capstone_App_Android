package com.example.proto1;

import android.content.Intent;

import android.database.Cursor;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

public class RegisterActivity extends Info {
    EditText userId, userPass, userName, userAge;
    String Tid;
    String Tpass;
    String Tname;
    String Tage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userId = (EditText) findViewById(R.id.et_id);
        userPass = (EditText) findViewById(R.id.et_pass);
        userName = (EditText) findViewById(R.id.et_name);
        userAge = (EditText) findViewById(R.id.et_age);

        Button join = (Button) findViewById(R.id.btn_register);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tid = userId.getText().toString();
                Tpass = userPass.getText().toString();
                Tname = userName.getText().toString();
                Tage = userAge.getText().toString();
                Cursor cursor = database.rawQuery("SELECT name, name, age FROM " + tableName, null);
                int count = cursor.getCount();
                for (int i = 0; i < count; i++) {
                    cursor.moveToNext();
                    Cid = cursor.getString(0);
                    Cpass = cursor.getString(1);
                    Cname = cursor.getString(2);
                    Cage = cursor.getString(3);
                }

                if(Tname.length()<2) {
                    Toast.makeText(getApplicationContext(), "이름을 정확하게 입력해주세요", Toast.LENGTH_SHORT).show();
                }else if (Tpass.length()<6){
                    Toast.makeText(getApplicationContext(), "비밀번호를 6자리 이상 입력하세요.", Toast.LENGTH_SHORT).show();
                }else{
                    try{
                        if(database!=null){
                            database.execSQL("INSERT INTO "+tableName+"(id,pass,name,age) VALUES"+"("+""+Tid+""+","+""+Tpass+""+","+""+Tname+","+""+Tage+""+")");
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    Intent login = new Intent(getApplication(), LoginActivity.class);
                    login.putExtra("splash", "splash");
                    startActivity(login);
                    finish();
                    Toast.makeText(getApplication(), Tname + "님 회원가입을 축하합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
