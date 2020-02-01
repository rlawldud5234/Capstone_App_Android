package com.example.proto1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {
    private FragmentManager fragmentmanager = getSupportFragmentManager();
    private HomeFragment homefragment = new HomeFragment();
    private GuideFragment guideFragment = new GuideFragment();
    private SettingFragment settingfragment = new SettingFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomBar);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction = fragmentmanager.beginTransaction();
                switch(menuItem.getItemId()) {
                    case R.id.menu_guide: {
                        transaction.replace(R.id.frame_layout, guideFragment).commit();
                        break;
                    }
                    case R.id.menu_home: {
                        transaction.replace(R.id.frame_layout, homefragment).commit();
                        break;
                    }
                    case R.id.menu_setting: {
                        transaction.replace(R.id.frame_layout, settingfragment).commit();
                        break;
                    }
                }
                return true;
            }
        };

        FragmentTransaction trans = fragmentmanager.beginTransaction();
        trans.replace(R.id.frame_layout, homefragment).commitAllowingStateLoss();

        bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);
    }
}
