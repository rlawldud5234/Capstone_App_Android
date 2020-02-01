package com.example.proto1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

public class HomeFragment extends Fragment {
    private String[] ListFav;
    private ListView listview;
    private ArrayAdapter adapter;

    private String app_key;
    private TMapData tData;
    private TMapView tmapView;
    private TMapPoint tPoint;
    private TMapMarkerItem tItem;

    Button Btnloc;

    private TMapPoint currentpoint;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGps();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button addBtn = (Button) view.findViewById(R.id.add_favorite);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFavoriteActivity.class);
                startActivity(intent);
            }
        });

        ListFav = getResources().getStringArray(R.array.favorite_item);
        listview = (ListView) view.findViewById(R.id.favorite_list);
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, ListFav);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Btnloc = (Button)view.findViewById(R.id.share_loc);


        //TMAP 호출
        LinearLayout linearLayoutTmap = (LinearLayout)view.findViewById(R.id.TMapView);
        app_key = "l7xxb116f439ac904ca683fb4a533412e093";

        tData = new TMapData();

        tmapView = new TMapView(getActivity());
        tmapView.setSKTMapApiKey(app_key);linearLayoutTmap.addView(tmapView);

        tmapView.setIconVisibility(true);
        tmapView.bringMarkerToFront(tItem);

        return view;
    }

    //현재 위치
    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                tmapView.setLocationPoint(longitude, latitude);
                tmapView.setCenterPoint(longitude, latitude);

                currentpoint = new TMapPoint(latitude, longitude);
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    //현재위치 받기
    public void setGps() {
        final LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}