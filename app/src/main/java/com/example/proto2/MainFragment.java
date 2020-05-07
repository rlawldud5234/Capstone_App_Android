package com.example.proto2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
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
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainFragment extends Fragment implements TMapGpsManager.onLocationChangedCallback {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String bcheck;
    String desc_str;

    private ArrayList<Dictionary> poiNameArr;
    private ArrayList<PathPoint> pointArr;
    private Adapter mAdapter;
    Dictionary data;
    PathPoint pPonit;

    private Activity activity;

    private String title;
    private int page;
    int count = 1;

    private TMapData tData;
    private TMapView tmapView;
    private TMapPoint tPoint, currentpoint, endpoint, testpoint1, testpoint2, nextPoint;
    private TMapMarkerItem tItem;
    private TMapCircle tCircle;

    private Button buttonSearch;
    private ImageButton searchBtn;
    private TextView speechTextView;
    private EditText searchBar;

    LocationManager lm;
    SpeechRecognizer sRecognizer;

    Intent i;
    String key = "";
    double latitude, longitude;
    float dist;

    DBHelper helper;

    TextToSpeech tts;
    static TimerTask tt, tl;
    final Timer timer = new Timer();
    final Timer tl_timer = new Timer();

    final Handler handler = new Handler(){
        public void handleMessage(Message message){
            Log.d("----", "검색 결과 핸들러");
            mAdapter.notifyDataSetChanged();
        }
    };

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        String p1 = param1;
        String p2 = param2;
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, p1);
        args.putString(ARG_PARAM2, p2);
        fragment.setArguments(args);

        return fragment;
    }

    public TimerTask timerTask(){
        TimerTask tempTask = new TimerTask() {
            @Override
            public void run() {
                changeDescript();
            }
        };
        return tempTask;
    }

    public TimerTask tl_timertask(){
        TimerTask tl_tempTask = new TimerTask() {
            @Override
            public void run() {
                distTest();
            }
        };
        return tl_tempTask;
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

        if(getArguments() != null){
            bcheck = getArguments().getString(ARG_PARAM2);
        }

        //음성 인식 intent 설정
        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);   //활동 시작
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");   //음성 번역 언어
        sRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());    //새 SpeechRecognizer를 만드는 팩토리 메서드
        sRecognizer.setRecognitionListener(listener);   //리스너 설정

        helper = new DBHelper(getContext(), "MapData.db", null, 1);
        if(Boolean.valueOf(bcheck)){   //처음실행했을 때 데이터 삽입
            helper.insert();
        }
    }

    //목적지 선택 확인창
    public void selectConfirm(int pos){
        final int index = pos;
        speakTTS(poiNameArr.get(index).getPOI_name()+"을 선택했습니다.");

        new AlertDialog.Builder(this.getContext()).setTitle("목적지 선택").setMessage(poiNameArr.get(index).getPOI_name()+" 선택")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                endpoint = poiNameArr.get(index).getPOI_latlng();
                GetDirections();
            }
        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                speakTTS("목적지 선택을 취소했습니다.");
            }
        }).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setGps();

        //TTS 설정
        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.KOREA);
            }
        });

        //TMAP 호출
        LinearLayout linearLayoutTmap = view.findViewById(R.id.TMapLayout);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recycler_poi);
        rv.addItemDecoration(new DividerItemDecoration(view.getContext(), 1));

        tData = new TMapData();

        tmapView = new TMapView(activity);
        tmapView.setSKTMapApiKey("l7xxb116f439ac904ca683fb4a533412e093");
        linearLayoutTmap.addView(tmapView);

        tmapView.setIconVisibility(true);
        tmapView.setTrackingMode(true);
        tmapView.setSightVisible(true);
        tmapView.setCompassMode(true);
        tmapView.bringMarkerToFront(tItem);
        tmapView.setZoomLevel(18);

        buttonSearch = view.findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dest = searchBar.getText().toString();
                searchDestination(dest);
            }
        });

        searchBtn = view.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakDestination();
            }
        });

        searchBar = view.findViewById(R.id.search_bar);

        poiNameArr = new ArrayList<>();
        pointArr = new ArrayList<>();

        mAdapter = new Adapter(poiNameArr);
        mAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectConfirm(position);
            }
        });
        rv.setAdapter(mAdapter);

        speechTextView = (TextView) view.findViewById(R.id.speechView);

        return view;
    }

    //TTS 말하기
    public void speakTTS(String sentence) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String utteranceId=this.hashCode() + "";
            tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        } else {
            HashMap<String, String> hashmap = new HashMap<>();
            hashmap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
            tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, hashmap);
        }
    }

    //현재 위치 리스너
    private LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                tmapView.setLocationPoint(longitude, latitude);
                tmapView.setCenterPoint(longitude, latitude,true);
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
        lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mLocationListener);    //통신문제?
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
        }
    }

    //장소 검색
    protected void searchDestination(String dest) {
//        String dest = searchBar.getText().toString();
        Log.d("----debug----", "검색어: "+dest);
        speakTTS(dest+"을 검색했습니다");


        if(poiNameArr.size() > 0) {
            poiNameArr.clear();
        }

//        명칭검색
//        tData.findAroundNamePOI(currentpoint, dest, 1, 10, new TMapData.FindAroundNamePOIListenerCallback() {
//            @Override
//            public void onFindAroundNamePOI(ArrayList<TMapPOIItem> poiItem) {   //지도 위치 또는 현재 위치 기준으로 검색
//                try {
//                    for (int i = 0; i < poiItem.size(); i++) {
//                        TMapPOIItem item = poiItem.get(i);
//                        String str_addr = item.getPOIAddress();
//                        String str_name = item.getPOIName();
//                        TMapPoint poi_point = item.getPOIPoint();
//                        data = new Dictionary(str_name, poi_point);
//                        poiNameArr.add(data);
//
//                        Log.d("----debug----", "\n이름: " + str_name + "\n주소: " + str_addr + "\n좌표: " + poi_point.toString());
//                    }
//                    new Thread() {
//                        public void run() {
//                            Message msg = handler.obtainMessage();
//                            handler.sendMessage(msg);
//                        }
//                    }.start();
//
//                } catch (NullPointerException e){
//                    speechTextView.setText("다시 검색해주세요.");
//                }
//            }
//        });


//        주소검색(단점: 서울기준)
        tData.findAddressPOI(dest, new TMapData.FindAddressPOIListenerCallback() {
            @Override
            public void onFindAddressPOI(ArrayList<TMapPOIItem> arrayList) {
                try {
                    for (int i = 0; i < arrayList.size(); i++) {
                        TMapPOIItem item = arrayList.get(i);
                        String str_addr = item.getPOIAddress();
                        String str_name = item.getPOIName();
                        TMapPoint poi_point = item.getPOIPoint();
                        data = new Dictionary(str_name, poi_point);
                        poiNameArr.add(data);

                        Log.d("----debug----", "\n이름: " + str_name + "\n주소: " + str_addr + "\n좌표: " + poi_point.toString());
                    }
                    new Thread() {
                        public void run() {
                            Message msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                            helper.insertUP(String.valueOf(latitude),String.valueOf(longitude));
                        }
                    }.start();

                } catch (NullPointerException e){
                    speechTextView.setText("다시 검색해주세요.");
                }
            }
        });
    }

    //경로 데이터 얻기
    public void GetDirections() {
//        final ArrayList<String> descArr = new ArrayList<String>();
        String uri = "https://api2.sktelecom.com/tmap/routes/pedestrian?appKey=l7xxb116f439ac904ca683fb4a533412e093&startX="+currentpoint.getLongitude()+"&startY="+currentpoint.getLatitude()+
                "&endX="+endpoint.getLongitude()+"&endY="+endpoint.getLatitude() +"&reqCoordType=WGS84GEO&resCoordType=WGS84GEO&format=json&startName=start&endName=end";
        Log.d("----debug----", "URI: "+uri);
        pointArr.clear();

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
//                                descArr.add(nodeListPlacemarkItem.item(7).getTextContent());    //길 안내 정보 배열로 만듦
                                desc_str = nodeListPlacemarkItem.item(7).getTextContent();
                                Log.d("----", nodeListPlacemarkItem.item(7).getTextContent());
                            }
                        }

                        if(nodeListPlacemarkItem.item(j).getNodeName().equals("Point")) {
                            String pointLatLng = nodeListPlacemarkItem.item(j).getTextContent().trim();
                            String[] latlng = pointLatLng.split(",");
                            TMapPoint pathPoint = new TMapPoint(Double.valueOf(latlng[1]),Double.valueOf(latlng[0]));
                            pPonit = new PathPoint(pathPoint,desc_str);
                            pointArr.add(pPonit);

                            Log.d("path", "----point----: "+pathPoint);
                        }
                    }
                }
                speakTTS(pointArr.get(0).getDescript());
                speechTextView.setText(pointArr.get(0).getDescript());

                tt = timerTask();
                timer.schedule(tt,0,3000);

                tl = tl_timertask();
                tl_timer.schedule(tl, 0, 3000);

                Log.d("----","타이머 실행");
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

    //음성 인식 실행
    public void speakDestination(){
        sRecognizer.startListening(i);
        speechTextView.setText("음성인식을 시작합니다.");
//        speakTTS("음성인식을 시작합니다.");
    }

    //음성 인식 리스너
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            //사용자가 말하기 시작할 준비가 되면 호출된다
            speechTextView.setTextColor(Color.YELLOW);
        }

        @Override
        public void onBeginningOfSpeech() {
            //사용자가 말하기 시작했을 때 호출된다
            speechTextView.setTextColor(Color.RED);
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            //입력받는 소리의 크기를 알려준다
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            //사용자가 말을 시작하고 인식이 된 단어를 buffer에 담는다
        }

        @Override
        public void onEndOfSpeech() {
            //사용자가 말하기를 중지하면 호출된다
            speechTextView.setTextColor(Color.WHITE);
        }

        @Override
        public void onError(int error) {
            //네트워크 또는 인식 오류 발생 시 호출된다
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    speechTextView.setText("오디오 에러");
                    speakTTS("오디오 에러가 발생했습니다.");
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    speechTextView.setText("클라이언트 에러");
                    speakTTS("클라이언트 에러가 발생했습니다.");
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    speechTextView.setText("퍼미션 없음");
                    speakTTS("권한이 없습니다.");
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    speechTextView.setText("네트워크 에러");
                    speakTTS("네트워크 에러가 발생했습니다.");
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    speechTextView.setText("네트워크 시간초과");
                    speakTTS("네트워크 시간이 초과되었습니다.");
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    speechTextView.setText("찾을 수 없음");
                    speakTTS("찾을 수 없습니다");
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    speechTextView.setText("recognizer 바쁨");
                    speakTTS("recognizer 바쁨");
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    speechTextView.setText("서버 에러");
                    speakTTS("서버 에러가 발생했습니다.");
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    speechTextView.setText("말하기 시간초과");
                    speakTTS("말하기 시간을 초과했습니다.");
                    break;
                default:
                    speechTextView.setText("알 수 없는 에러 발생");
                    speakTTS("알 수 없는 에러가 발생했습니다.");
                    break;
            }

        }

        @Override
        public void onResults(Bundle results) {
            //인식 결과가 준비되면 호출된다.
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for(int i = 0; i < matches.size() ; i++){
                String specked = matches.get(i);
                speechTextView.setText(specked);
                //검색 연결
                searchDestination(specked);
            }
        }


        @Override
        public void onPartialResults(Bundle partialResults) {
            //부분 인식 결과를 사용할 수 있을 때 호출된다
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            //향후 이벤트 추가하기 위해 예약된다
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        key = SpeechRecognizer.RESULTS_RECOGNITION;
        speechTextView.setText(key);
        speakTTS(key);
    }

    //거리계산
    public float DistnaceDgree(double lat1, double lng1, double lat2, double lng2) {
        Location point1 = new Location("Point A");
        Location point2 = new Location("Point B");
        point1.setLatitude(lat1);
        point1.setLongitude(lng1);
        point2.setLatitude(lat2);
        point2.setLongitude(lng2);
        float distance = point1.distanceTo(point2);

        return distance;
    }


    //범위 테스트
    public void distTest(){
//        String tl_str = helper.getContact(3);
//        String[] tl_arr = tl_str.split(",");

        String tl_str = helper.getResultTL();
        String bs_str = helper.getResultBS();
        String[] tl_arr = tl_str.split(",");
        String[] bs_arr = bs_str.split(",");

        float min = 5;

        for(int i = 0;i<tl_arr.length;i+=2 ){
            testpoint1 = new TMapPoint(Double.valueOf(tl_arr[i]), Double.valueOf(tl_arr[i+1]));

            TMapCircle tlCircle = new TMapCircle();
            tlCircle.setCenterPoint(testpoint1);
            tlCircle.setRadius(3);
            tlCircle.setAreaColor(Color.RED);
            tlCircle.setAreaAlpha(100);
            tmapView.addTMapCircle("mcircle"+i, tlCircle);

            float tl_dist =  DistnaceDgree(testpoint1.getLatitude(), testpoint1.getLongitude(), currentpoint.getLatitude(), currentpoint.getLongitude());
            if(min>tl_dist){
                min = tl_dist;
                speakTTS("근처에 횡단보도가 있습니다.");
                Log.d("----","타이머 종료");
                tl.cancel();
            }
        }

        for(int j = 0;j<bs_arr.length;j+=2 ){
            testpoint2 = new TMapPoint(Double.valueOf(bs_arr[j]), Double.valueOf(bs_arr[j+1]));

            TMapCircle tlCircle = new TMapCircle();
            tlCircle.setCenterPoint(testpoint2);
            tlCircle.setRadius(3);
            tlCircle.setAreaColor(Color.YELLOW);
            tlCircle.setAreaAlpha(100);
            tmapView.addTMapCircle("bscircle"+j, tlCircle);

            float tl_dist =  DistnaceDgree(testpoint2.getLatitude(), testpoint2.getLongitude(), currentpoint.getLatitude(), currentpoint.getLongitude());
            if(min>tl_dist){
                min = tl_dist;
                speakTTS("근처에 버스정류장이 있습니다.");
                Log.d("----","타이머 종료");
                tl.cancel();
            }
        }

//        testpoint = new TMapPoint(Double.valueOf(tl_arr[0]), Double.valueOf(tl_arr[1]));

//        TMapCircle tlCircle = new TMapCircle();
//        tlCircle.setCenterPoint(testpoint);
//        tlCircle.setRadius(3);
//        tlCircle.setAreaColor(Color.RED);
//        tlCircle.setAreaAlpha(100);
//        tmapView.addTMapCircle("tl_circle", tlCircle);

//        float tl_dist = DistnaceDgree(testpoint.getLatitude(), testpoint.getLongitude(), currentpoint.getLatitude(), currentpoint.getLongitude());
//
//        if(tl_dist<=3){
//            speakTTS("근처에 횡단보도가 있습니다.");
//            Log.d("----","타이머 종료");
//            tl.cancel();
//        }
    }

    final Handler handler2 = new Handler(){
        public void handleMessage(Message message){
            nextPoint = new TMapPoint(pointArr.get(count).getpath_latlng().getLatitude(),pointArr.get(count).getpath_latlng().getLongitude());
            tCircle = new TMapCircle();
            tCircle.setCenterPoint(nextPoint);
            tCircle.setRadius(5);
            tCircle.setAreaColor(Color.GRAY);
            tCircle.setAreaAlpha(100);
            tmapView.addTMapCircle("circle", tCircle);
            dist = DistnaceDgree(nextPoint.getLatitude(), nextPoint.getLongitude(), currentpoint.getLatitude(), currentpoint.getLongitude());

            if(dist<=15){
                speechTextView.setText(pointArr.get(count).getDescript());
                speakTTS(pointArr.get(count).getDescript());
                count++;
            }

            if(count == pointArr.size()){
                tt.cancel();
                Log.d("----","타이머 종료");
                speakTTS("목적지에 도착했습니다.");
                tmapView.removeAllTMapCircle();
                count = 1;
            }
        }
    };

    //안내문 바뀌는거
    public void changeDescript(){
        new Thread(){
            public void run(){
                Message msg = handler2.obtainMessage();
                handler2.sendMessage(msg);
            }
        }.start();
    }

    @Override
    public void onLocationChange(Location location) {
        tmapView.setLocationPoint(location.getLongitude(), location.getLatitude());
    }
}
