package com.example.proto1.Directionhelpers;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    TaskLoadedCallback taskLoadedCallback;
    String directionMode = "driving";

    public PointsParser(Context mContext, String directionMode) {
        this.taskLoadedCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
        JSONObject jsonobject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jsonobject = new JSONObject(jsonData[0]);
            Log.d("log", jsonData[0].toString());
            DataParser parser = new DataParser();
            Log.d("data", parser.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    protected void onPostExcute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;

        for(int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = result.get(i);

            for (int j = 0; j<path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng positions = new LatLng(lat, lng);
                points.add(positions);
            }

            lineOptions.addAll(points);
            if(directionMode.equalsIgnoreCase("walking")) {
                lineOptions.width(10);
                lineOptions.color(Color.CYAN);
            } else {
                lineOptions.width(20);
                lineOptions.color(Color.MAGENTA);
            }
            Log.d("decoded", "라인 옵션 디코딩");
        }

        if(lineOptions != null) {
            taskLoadedCallback.onTaskDone(lineOptions);
        } else {
            Log.d("log", "폴리라인 없음??");
        }
    }
}
