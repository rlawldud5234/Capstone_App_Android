package com.example.proto1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private String[] ListFav;
    private ListView listview;
    private ArrayAdapter adapter;

    private MapView mapview = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        mapview = (MapView) view.findViewById(R.id.mapView);
        mapview.getMapAsync(this);

        ListFav = getResources().getStringArray(R.array.favorite_item);
        listview = (ListView) view.findViewById(R.id.favorite_list);
        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, ListFav);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(mapview != null)
        {
            mapview.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng TEST = new LatLng(35.896125, 128.620099);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(TEST);
        markerOptions.title("테스트용 위치");
        markerOptions.snippet("나중에 수정");
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(TEST));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
    }
}