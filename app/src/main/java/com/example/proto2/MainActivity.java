package com.example.proto2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    ViewPager vpPager;
    static boolean checkFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());

        vpPager.setAdapter(adapterViewPager);

        Intent intent = getIntent();
        checkFirst = intent.getExtras().getBoolean("FirstCheck");

        if(intent.getExtras().getString("button").equals("recog")){
            vpPager.setCurrentItem(1);
        }

        if(intent.getExtras().getString("button").equals("nav")){
            vpPager.setCurrentItem(2);
        }
    }



    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 4;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return LeftFragment.newInstance("0", "Page");   //메뉴
                case 1:
                    return ObjectRecognitionFragment.newInstance("2", "page");  //사물인식
                case 2:
                    return MainFragment.newInstance("1", String.valueOf(checkFirst));   //길찾기
                case 3:
                    return RightFragment.newInstance("3", "Page");  //메뉴였던 것
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
