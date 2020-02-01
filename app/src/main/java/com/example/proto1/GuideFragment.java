package com.example.proto1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.proto1.Directionhelpers.FetchURL;
import com.example.proto1.Directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
import java.util.HashMap;

public class GuideFragment extends Fragment {
    private String app_key;
    private TMapData tData;
    private TMapView tmapView;
    private TMapPoint tPoint;
    private TMapMarkerItem tItem;

    Bitmap bitmap, wayPoint, rightBtn;
    private EditText searchBar;
    private Button searchBtn, directionBtn, TTSBtn;

    private String start_geo, end_geo;
    private TMapPoint currentpoint, startpoint, endpoint;
    private TextView descView;

    TextToSpeech tts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);
        setGps();

        //TMAP 호출
        LinearLayout linearLayoutTmap = (LinearLayout)view.findViewById(R.id.TMapView);
        app_key = "l7xxb116f439ac904ca683fb4a533412e093";

        tData = new TMapData();

        tmapView = new TMapView(getActivity());
        tmapView.setSKTMapApiKey(app_key);
        linearLayoutTmap.addView(tmapView);

        tmapView.setIconVisibility(true);
        tmapView.bringMarkerToFront(tItem);

        searchBar = view.findViewById(R.id.editDest);
        searchBtn = view.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDestination();
            }
        });

        directionBtn = view.findViewById(R.id.pathBtn);
        directionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directions();
            }
        });

        descView = view.findViewById(R.id.descriptionView);

        TTSBtn = view.findViewById(R.id.TTSbutton);
        TTSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakDescription();
            }
        });


        bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.poi_dot);
        wayPoint = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_circle);
        rightBtn = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.tick);

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
                start_geo = "&startX="+longitude+"&startY="+latitude;
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

    //장소 검색
    protected void searchDestination() {
        final ArrayList<TMapPoint> pointList = new ArrayList<TMapPoint>();        //검색 마커


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dest = searchBar.getText().toString();

                tData.findAroundNamePOI(currentpoint, dest, new TMapData.FindAroundNamePOIListenerCallback() {
                    @Override
                    public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {   //지도 위치 또는 현재 위치 기준으로 검색
                        for(int i = 0; i < poiItem.size(); i++) {
                            TMapPOIItem  item = poiItem.get(i);
                            String str_addr = item.getPOIAddress();
                            String str_name = item.getPOIName();
                            TMapPoint poi_point = item.getPOIPoint();
                            Log.d("----debug----", "\n이름: "+str_name+"\n주소: "+str_addr+"\n좌표: "+poi_point.toString());
                            pointList.add(poi_point);

                            TMapMarkerItem poiMarker = new TMapMarkerItem();
                            poiMarker.setTMapPoint(poi_point);
                            poiMarker.setVisible(tItem.VISIBLE);
                            poiMarker.setIcon(bitmap);
                            poiMarker.setName(str_name);
                            poiMarker.setPosition(0.5f,1.0f);
                            poiMarker.setCanShowCallout(true);
                            poiMarker.setCalloutTitle(str_name);
                            poiMarker.setCalloutRightButtonImage(rightBtn);
                            tmapView.addMarkerItem("poi"+i, poiMarker);
                        }
                    }
                });

                //풍선뷰 클릭 시 호출되는 이벤트 리스너 등록함수
                tmapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
                    @Override
                    public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                        endpoint = tMapMarkerItem.getTMapPoint();
                        Log.d("LL", "----"+endpoint);
                    }
                });
            }
        });
    }

    //보행자 경로 데이터 검색
    protected void directions() {
        final ArrayList<String> descArr = new ArrayList<String>();

        end_geo = "&endX="+endpoint.getLongitude()+"&endY="+endpoint.getLatitude();

        String uri = "https://api2.sktelecom.com/tmap/routes/pedestrian?appKey="+app_key+start_geo+end_geo+"&reqCoordType=WGS84GEO&resCoordType=WGS84GEO&format=json&startName=start&endName=end";
        Log.d("----debug----", "URI: "+uri);

        //보행자 경로 얻는 함수
        tData.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, currentpoint, endpoint, new TMapData.FindPathDataAllListenerCallback(){
            @Override
            public void onFindPathDataAll(Document document) {
                Element root = document.getDocumentElement();
                NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");    //출발점, 안내점, 경유지, 도착점 도로정보 노드
                double latitude, longitude;     //위도, 경도

                //실질적으로 따는 부분
                for( int i=0; i<nodeListPlacemark.getLength(); i++ ) {
                    NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes(); //placemark 하위 노드
                    for( int j=0; j<nodeListPlacemarkItem.getLength(); j++ ) {
                        if(nodeListPlacemarkItem.item(j).getNodeName().equals("tmap:nodeType")) {   //하위 노드 중에서 이름이 tmap:nodeType 인지 판별
                            if(nodeListPlacemarkItem.item(j).getTextContent().equals("POINT")) {    //tmap:nodeType의 값이 POINT인지 판별
                                descArr.add(nodeListPlacemarkItem.item(7).getTextContent());    //길 안내 정보 배열로 만듦
                            }
                        }

                        //위도,경도 값 받기
                        if(nodeListPlacemarkItem.item(j).getNodeName().equals("Point")) {
                            String pointLatLng = nodeListPlacemarkItem.item(j).getTextContent().trim();
                            String[] ll = pointLatLng.split(",");
                            latitude = Double.valueOf(ll[1]);
                            longitude = Double.valueOf(ll[0]);
                            Log.d("path", "----point----: "+pointLatLng);

                            TMapPoint path_point = new TMapPoint(latitude, longitude);

                            TMapMarkerItem pathMarker = new TMapMarkerItem();
                            pathMarker.setTMapPoint(path_point);
                            pathMarker.setVisible(tItem.VISIBLE);
                            pathMarker.setIcon(wayPoint);
                            pathMarker.setPosition(0.5f,1.0f);
                            tmapView.addMarkerItem("path"+i, pathMarker);
                        }
                    }
                }
                //길 안내 문자열 배열로 저장
                String[] strData = descArr.toArray(new String[descArr.size()]);

                for(int i = 0; i < descArr.size(); i++) {
                    Log.d("path", "----path1----: "+descArr.get(i));
                }

                //텍스트뷰에 길 안내 설정
                descView.setText(strData[0]);
            }
        });

        //길찾기 폴리라인
        tData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, currentpoint, endpoint, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                polyLine.setLineColor(Color.RED);
                polyLine.setLineWidth(2);
                tmapView.addTMapPath(polyLine);
            }
        });
    }

    //말하기 버튼
    protected void speakDescription() {
        String description = descView.getText().toString();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String utteranceId=this.hashCode() + "";
            tts.speak(description, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        } else {
            HashMap<String, String> hashmap = new HashMap<>();
            hashmap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
            tts.speak(description, TextToSpeech.QUEUE_FLUSH, hashmap);
        }
    }
}
