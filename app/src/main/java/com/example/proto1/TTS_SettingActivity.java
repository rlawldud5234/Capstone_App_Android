package com.example.proto1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import presenter.TTS_SettingActivityPresenter;

public class TTS_SettingActivity extends AppCompatActivity {
    private ArrayAdapter<String> mAdapterLanguage;
    private ArrayAdapter<String> mAdapterStyle;
    private Spinner mSpinnerLanguage;
    private Spinner mSpinnerStyle;

    private EditText mEditText;
    private TextView mTextViewGender;
    private TextView mTextViewSampleRate;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeakRate;
    private Button mButtonSpeak;
    private Button mButtonRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts__setting);
        buildViews();

        mSeekBarPitch.setProgress(2000);
        mSeekBarSpeakRate.setProgress(75);
    }

    private void buildViews() {
        mEditText = findViewById(R.id.ettIdText);
        mSpinnerLanguage = findViewById(R.id.snrIdLanguage);
        mSpinnerStyle = findViewById(R.id.snrIdStyle);
        mTextViewGender = findViewById(R.id.tvwIdGender);
        mSeekBarPitch = findViewById(R.id.sbrIdPitch);
        mSeekBarSpeakRate = findViewById(R.id.sbrIdSpeakRate);
        mButtonSpeak = findViewById(R.id.btnIdSpeak);
        mButtonRefresh = findViewById(R.id.btnIdRefresh);

        mSeekBarPitch.setMax(4000);
        mSeekBarPitch.setProgress(2000);

        mSeekBarSpeakRate.setMax(375);
        mSeekBarSpeakRate.setProgress(75);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
