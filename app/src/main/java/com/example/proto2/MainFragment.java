package com.example.proto2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;


public class MainFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<Dictionary> poiNameArr;
    private Adapter mAdapter;
    Dictionary data;

    private Activity activity;

    private String title;
    private int page;

    private TMapData tData;
    private TMapView tmapView;
    private TMapPoint tPoint, currentpoint, endpoint;
    private TMapMarkerItem tItem;

    private EditText searchBar;
    private Button searchBtn;
    private TextView descTextview;

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setGps();

        //TMAP 호출
        LinearLayout linearLayoutTmap = view.findViewById(R.id.TMapLayout);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recycler_poi);
        rv.addItemDecoration(new DividerItemDecoration(view.getContext(), 1));

        tData = new TMapData();

        tmapView = new TMapView(activity);
        tmapView.setSKTMapApiKey("l7xxb116f439ac904ca683fb4a533412e093");
        linearLayoutTmap.addView(tmapView);

        tmapView.setIconVisibility(true);
        tmapView.bringMarkerToFront(tItem);

        searchBar = view.findViewById(R.id.editDest);
        searchBtn = view.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDestination();
                mAdapter.notifyDataSetChanged();
            }
        });

        poiNameArr = new ArrayList<>();
        mAdapter = new Adapter(poiNameArr);
        mAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                endpoint = poiNameArr.get(position).getPOI_latlng();
                Log.d("----TAG", String.valueOf(endpoint));
                GetDirections();
            }
        });
        rv.setAdapter(mAdapter);

        descTextview = (TextView) view.findViewById(R.id.descView);

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
        final LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mLocationListener);
    }

    //장소 검색
    protected void searchDestination() {
//        final ArrayList<TMapPoint> pointList = new ArrayList<TMapPoint>();        //검색 마커
//        pointList.clear();

        String dest = searchBar.getText().toString();

        tData.findAroundNamePOI(currentpoint, dest, 2, 10, new TMapData.FindAroundNamePOIListenerCallback() {
            @Override
            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {   //지도 위치 또는 현재 위치 기준으로 검색
                for(int i = 0; i < poiItem.size(); i++) {
                    TMapPOIItem item = poiItem.get(i);
                    String str_addr = item.getPOIAddress();
                    String str_name = item.getPOIName();
                    TMapPoint poi_point = item.getPOIPoint();
//                    pointList.add(poi_point);
                    data = new Dictionary(str_name, poi_point);
                    poiNameArr.add(data);

                    Log.d("----debug----", "\n이름: "+str_name+"\n주소: "+str_addr+"\n좌표: "+poi_point.toString());
                }
            }
        });
    }

    public void GetDirections() {
        final ArrayList<String> descArr = new ArrayList<String>();
        descArr.clear();

        tData.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, currentpoint, endpoint, new TMapData.FindPathDataAllListenerCallback() {
            @Override
            public void onFindPathDataAll(Document document) {
                Element root = document.getDocumentElement();
                NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");
                double lat, lng;

                for(int i = 0; i < nodeListPlacemark.getLength(); i++) {
                    NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                    for(int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {
                        if(nodeListPlacemarkItem.item(j).getNodeName().equals("tmap:nodeType")) {
                            if(nodeListPlacemarkItem.item(j).getTextContent().equals("POINT")) {    //tmap:nodeType의 값이 POINT인지 판별
                                descArr.add(nodeListPlacemarkItem.item(7).getTextContent());    //길 안내 정보 배열로 만듦
                            }
                        }
                    }
                }
                descTextview.setText(descArr.get(0));
            }

        });

        tData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, currentpoint, endpoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                polyLine.setLineColor(Color.RED);
                polyLine.setLineWidth(2);
                tmapView.addTMapPath(polyLine);
            }
        });
    }
}
