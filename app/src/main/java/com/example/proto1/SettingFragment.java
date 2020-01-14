package com.example.proto1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingFragment extends Fragment {
    Button TTSBtn;
    Button accBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        TTSBtn = (Button) view.findViewById(R.id.set_TTS);
        accBtn = (Button) view.findViewById(R.id.set_account);

        TTSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TTS_SettingActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
