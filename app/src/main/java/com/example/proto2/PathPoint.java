package com.example.proto2;

import com.skt.Tmap.TMapPoint;

public class PathPoint {
    private TMapPoint path_latlng;
    private String descript;

    public TMapPoint getpath_latlng() {
        return path_latlng;
    }
    public void setpath_latlng(TMapPoint path_latlng) {
        this.path_latlng = path_latlng;
    }

    public String getDescript() { return descript; }

    public void setDescript(String descript) { this.descript = descript; }

    public PathPoint(TMapPoint path_latlng, String descript) {
        this.path_latlng = path_latlng;
        this.descript = descript;
    }
}
