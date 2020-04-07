package com.example.proto2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class prevActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prev);
        //최초 실행 여부 판단
        SharedPreferences pref = getSharedPreferences("IsFirst" , Activity.MODE_PRIVATE);

        Button btn = findViewById(R.id.button6);

        boolean isFirst = pref.getBoolean("isFirst", true);
        if(isFirst == true){ //최초 실행시 할 일
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", false);
            editor.commit();

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.RECORD_AUDIO}, 1);
                Log.d("----", "퍼미션 요청");
            }

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(prevActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        } else {
            Intent i = new Intent(prevActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}
