package com.example.proto1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Info {
    EditText idText, passText;
    String strId, strPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idText = (EditText) findViewById(R.id.et_id);
        passText = (EditText) findViewById(R.id.et_pass);

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BaseActivity.class);
                startActivity(intent);
            }
        });

        Button btnRegister = (Button) findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void login(View v) {
        if(database != null) {
            Cursor cursor = database.rawQuery("SELECT name, num, pass FROM " + tableName, null);
            int count = cursor.getCount();

            for(int i=0; i<count; i++){
                cursor.moveToNext();
                Cid = cursor.getString(0);
                Cpass = cursor.getString(1);
                Cname = cursor.getString(2);
                Cage = cursor.getString(3);
            }
            strId = idText.getText().toString();
            strPass = passText.getText().toString();
            if(strId.equals(Cid) && strPass.equals(Cpass)) {
                Intent main = new Intent(getApplication(), MainActivity.class);
                main.putExtra("splash", "splash");
                startActivity(main);
                Toast.makeText(getApplicationContext(), Cname + "님 환영합니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "정확한 정보를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        }
    }

    public void member(View view){
        Intent member = new Intent(getApplication(), RegisterActivity.class);
        member.putExtra("splash", "splash");
        startActivity(member);
    }
}
