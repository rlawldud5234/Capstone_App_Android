package com.example.proto2;

import com.skt.Tmap.TMapPoint;

public class Dictionary {
    private String POI_name;
    private TMapPoint POI_latlng;

    public String getPOI_name() {
        return POI_name;
    }

    public void setPOI_name(String POI_name) {
        this.POI_name = POI_name;
    }

    public TMapPoint getPOI_latlng() {
        return POI_latlng;
    }

    public void setPOI_latlng(TMapPoint POI_latlng) {
        this.POI_latlng = POI_latlng;
    }

    public Dictionary(String POI_name, TMapPoint POI_latlng) {
        this.POI_name = POI_name;
        this.POI_latlng = POI_latlng;
    }
}
