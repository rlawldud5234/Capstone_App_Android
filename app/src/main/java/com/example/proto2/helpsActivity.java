package com.example.proto2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class helpsActivity extends AppCompatActivity {
    Button pg, os, ai, htu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helps);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("도움말");

        pg = findViewById(R.id.permissionGuide);
        pg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(helpsActivity.this, PermissionGuideActivity.class);
                startActivity(i);
            }
        });
        os = findViewById(R.id.opensource);
        ai = findViewById(R.id.appInfo);
        htu = findViewById(R.id.howToUse);
    }
}
