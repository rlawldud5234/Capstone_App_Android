package com.example.proto1;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.Location;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;

import java.util.ArrayList;
import java.util.List;


public class GuideFragment extends Fragment {
    Location destination;
    NaviOptions options;
    List<Location> viaList = new ArrayList<Location>();
    KakaoNaviParams.Builder builder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        destination = Location.newBuilder("카카오 판교 오피스", 127.10821222694533, 37.40205604363057).build();
        options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84).setVehicleType(VehicleType.FIRST).setRpOption(RpOption.SHORTEST).build();
        viaList.add(Location.newBuilder("서서울호수공원", 126.8322289016308, 37.528495607451205).build());
        builder = KakaoNaviParams.newBuilder(destination).setNaviOptions(options).setViaList(viaList);

        KakaoNaviParams params = builder.build();

        final KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(Location.newBuilder("카카오 판교오피스", 127.10821222694533, 37.40205604363057).build()).setNaviOptions(NaviOptions.newBuilder().setCoordType(CoordType.WGS84).setStartX(126.5).setStartY(35.2).build()).setViaList(viaList);

        KakaoNaviService.getInstance().navigate(getActivity(), builder.build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);
        return view;
    }

}
