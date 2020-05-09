package com.example.proto2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class MainActivity extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    FragmentManager fm;
    FragmentTransaction trans;
    ViewPager vpPager;
    static String value;
    static boolean checkFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        trans = fm.beginTransaction();
        vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());

        vpPager.setAdapter(adapterViewPager);

        Intent intent = getIntent();
        checkFirst = intent.getExtras().getBoolean("FirstCheck");

        if(intent.getExtras().getString("button").equals("recog")){
            value = "object";
            vpPager.setCurrentItem(1);
        }

        if(intent.getExtras().getString("button").equals("object")){
            value = "object";
            vpPager.setCurrentItem(1);
        }

        if(intent.getExtras().getString("button").equals("color")){
            value = "color";
            vpPager.setCurrentItem(1);
        }

        if(intent.getExtras().getString("button").equals("text")){
            value = "text";
            vpPager.setCurrentItem(1);

        }

        if(intent.getExtras().getString("button").equals("light")){
            value = "light";
            vpPager.setCurrentItem(1);
        }

       if(intent.getExtras().getString("button").equals("nav")){
           value = "nav";
            vpPager.setCurrentItem(2);
        }
    }



    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {     //메뉴 - 인식 - 길 찾기
            switch (position) {
                case 0:
                    return LeftFragment.newInstance("0", "Page");   //메뉴
                case 1:
                    Log.e("-------------------", value);

                    if(value.equals("object")) {
                        return ObjectRecognitionFragment.newInstance("1", "page");
                    } else if(value.equals("color")) {
                        return ColorRecognitionFragment.newInstance("1", "page");
                    } else if(value.equals("text")) {
                        return CharacterRecognitionFragment.newInstance("1", "page");
                    } else if(value.equals("light")){
                        return BrightnessFragment.newInstance("1", "page");
                    } else {
                        return ObjectRecognitionFragment.newInstance("1", "page");  //인식
                    }
                case 2:
                    return MainFragment.newInstance("2", String.valueOf(checkFirst));   //길찾기
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }
}
