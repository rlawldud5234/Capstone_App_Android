package com.example.proto2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class prevActivity extends AppCompatActivity {
    SharedPreferences pref;
    Button navBtn, recogBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prev);
        navBtn = findViewById(R.id.nav_button);
        recogBtn = findViewById(R.id.recog_button);

        //최초 실행 여부 판단
        pref = getSharedPreferences("IsFirst" , Activity.MODE_PRIVATE);

        final boolean isFirst = pref.getBoolean("isFirst", true);
        if(isFirst == true){ //최초 실행시 할 일
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isFirst", false);
            editor.commit();

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1);
                Log.d("----", "퍼미션 요청");
            }

//            navBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(prevActivity.this, MainActivity.class);
//                    i.putExtra("FirstCheck", isFirst);
//                    startActivity(i);
//                    finish();
//                }
//            });
        }

        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(prevActivity.this, MainActivity.class);
                i.putExtra("button", "nav");
                i.putExtra("FirstCheck", isFirst);
                startActivity(i);
            }
        });

        recogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(prevActivity.this, MainActivity.class);
                i.putExtra("button", "object");
                i.putExtra("FirstCheck", isFirst);
                startActivity(i);
            }
        });
    }
}
