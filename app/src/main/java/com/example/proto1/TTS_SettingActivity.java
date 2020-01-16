package com.example.proto1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import java.util.HashMap;
import java.util.Locale;

public class TTS_SettingActivity extends AppCompatActivity {
    TextToSpeech tts;

    private EditText mEditText;
//    private SeekBar mSeekBarPitch;
//    private SeekBar mSeekBarSpeakRate;
    private Button mButtonSpeak;
    private Button mButtonRefresh;
    private RadioGroup lang_list;
    private RadioButton korean;
    private RadioButton japanese;
    private RadioButton english;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts_setting);
        buildViews();

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
           @Override
           public void onInit(int status) {
               if(status != TextToSpeech.ERROR) {
                   tts.setLanguage(Locale.ENGLISH);
               }
           }
        });

        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                setSpeack();
                changeLang();
                String text = mEditText.getText().toString();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String utteranceId=this.hashCode() + "";
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                } else {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
                }
            }
        });

        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lang_list.check(R.id.select_en);
                mEditText.getText().clear();
//                mSeekBarPitch.setProgress(1);
//                mSeekBarSpeakRate.setProgress(1);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    private void buildViews() {
        mEditText = findViewById(R.id.ettIdText);
//        mSeekBarPitch = findViewById(R.id.sbrIdPitch);
//        mSeekBarSpeakRate = findViewById(R.id.sbrIdSpeakRate);
        mButtonSpeak = findViewById(R.id.btnIdSpeak);
        mButtonRefresh = findViewById(R.id.btnIdRefresh);

        lang_list = findViewById(R.id.select_lang);
        korean = findViewById(R.id.select_ko);
        japanese = findViewById(R.id.select_ja);
        english = findViewById(R.id.select_en);

//        mSeekBarPitch.setMax(10);
//        mSeekBarPitch.setProgress(1);
//
//        mSeekBarSpeakRate.setMax(5);
//        mSeekBarSpeakRate.setProgress(1);

        lang_list.check(R.id.select_en);
    }

//    private void setSpeack() {
//        float pitch = (float)mSeekBarPitch.getProgress();
//        if (pitch < 0.1) {
//            pitch = 0.1f;
//        }
//        float speed = (float)mSeekBarSpeakRate.getProgress();
//        if (speed < 0.1) {
//            speed = 0.1f;
//        }
//
//        tts.setPitch(pitch);
//        tts.setSpeechRate(speed);
//    }

    private void changeLang() {
        switch (lang_list.getCheckedRadioButtonId()) {
            case R.id.select_ko:
                korean.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tts.setLanguage(Locale.KOREA);
                    }
                });
                break;

            case R.id.select_ja:
                japanese.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tts.setLanguage(Locale.JAPAN);
                    }
                });
                break;

            case R.id.select_en:
                english.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tts.setLanguage(Locale.US);
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
