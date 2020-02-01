package com.example.proto1.Directionhelpers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchURL extends AsyncTask<String, Void, String> {
    Context mContext;
    String directionMode = "walking";

    public FetchURL(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... strings) {
        String data = "";
        directionMode = strings[1];

        try {
            data = downloadUrl(strings[0]);
            Log.d("log", "백그라운드 태스크 데이터"+data.toString());
        } catch(Exception e) {
            Log.d("백그라운드 테스트", e.toString());
        }
        return data;
    }

    protected void onPostExcute(String s) {
        super.onPostExecute(s);
        PointsParser parserTask = new PointsParser(mContext, directionMode);
        parserTask.execute(s);
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data  = "";
        InputStream inStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            inStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            StringBuffer sBuffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                sBuffer.append(line);
            }
            data = sBuffer.toString();
            Log.d("log", "다운로드 URL: "+data.toString());
            reader.close();
        } catch (Exception e) {
            Log.d("log", "예외 다운로딩 URL: "+e.toString());
        } finally {
            inStream.close();
            urlConnection.disconnect();
        }

        return data;
    }
}
