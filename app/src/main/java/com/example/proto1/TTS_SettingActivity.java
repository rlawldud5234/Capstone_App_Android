package com.example.proto1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TTS_SettingActivity extends AppCompatActivity {
    TextToSpeech tts;

    private EditText mEditText;
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

        //TTS 객체 생성
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
           @Override
           public void onInit(int status) {
               if(status == TextToSpeech.SUCCESS) {
                   tts.setLanguage(Locale.KOREA);    //따로 언어 선택 안 해주면 기기 시스템 설정대로 출력
                   int result = tts.setLanguage(Locale.JAPANESE);
                   if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                       Toast.makeText(getApplicationContext(), "일본어 미지원", Toast.LENGTH_LONG).show();
                       Intent install = new Intent();
                       install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                       startActivity(install);
                   }
               } else {
                   Toast.makeText(getApplicationContext(), "설치 실패", Toast.LENGTH_LONG).show();
               }
           }
        });

        //말하기 버튼 클릭리스너
        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLang();
                String text = mEditText.getText().toString();

                // 안드로이드 버전이 롤리팝 이상일 때와 이하일 때 실행 방식 분리
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
    protected void onStop() {
        super.onStop();
        if(tts != null) {
            tts.stop();     //음성출력 중단
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tts != null) {
            tts.shutdown();     //TTS 엔진 리소스 해제
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

        lang_list.check(R.id.select_ko);
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
        lang_list.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.select_ko:
                        korean.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                tts.setLanguage(Locale.KOREAN);
                                Toast.makeText(getApplicationContext(), "한국어 선택", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    case R.id.select_ja:
                        japanese.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                tts.setLanguage(Locale.JAPANESE);
                                Toast.makeText(getApplicationContext(), "일본어 선택", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;

                    case R.id.select_en:
                        english.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                tts.setLanguage(Locale.ENGLISH);
                                Toast.makeText(getApplicationContext(), "영어 선택", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
