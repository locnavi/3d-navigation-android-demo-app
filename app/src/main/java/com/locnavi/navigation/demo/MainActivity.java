package com.locnavi.navigation.demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.locnavi.websdk.LocNaviWebSDK;
import com.locnavi.websdk.speech.VoiceManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO = 1;
    private static final int RESULT_SPEECH = 2;
    private EditText etMapId, etAppKey, etUserId, etPOI;
    private TextView tvLog;
    protected static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0x01;

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        etMapId = (EditText) findViewById(R.id.et_mapId);
        etAppKey = (EditText) findViewById(R.id.et_appKey);
        etUserId = (EditText) findViewById(R.id.et_userId);
        etPOI = (EditText) findViewById(R.id.et_poi);
        tvLog = (TextView) findViewById(R.id.tv_log);
        etMapId.setText(com.locnavi.navigation.demo.Constants.mapId);
        etAppKey.setText(com.locnavi.navigation.demo.Constants.appKey);
        etUserId.setText(com.locnavi.navigation.demo.Constants.userId);
        etPOI.setText(com.locnavi.navigation.demo.Constants.poi);
        findViewById(R.id.btn_list).setOnClickListener(view -> {
            String userId = etUserId.getText().toString();
            String poi = etPOI.getText().toString();

            LocNaviWebSDK.setUserId(userId);
            LocNaviWebSDK.openMapList(this);
        });
        findViewById(R.id.btn_map).setOnClickListener(view -> {
            String mapId = etMapId.getText().toString();
            String userId = etUserId.getText().toString();
            String poi = etPOI.getText().toString();

            LocNaviWebSDK.setUserId(userId);
            LocNaviWebSDK.openMap(this, mapId, poi);
        });
        findViewById(R.id.btn_speech).setOnClickListener(view -> {
            Log.d(TAG, "start speech");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
                Log.d(TAG, "申请权限");
            } else {
                Log.d(TAG, "已经授权");
                startListerning();
            }
        });
    }

    private void startListerning() {
        //开启语音识别功能
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //设置模式，这里设置成自由模式
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //提示语音开始文字
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.locnavi_speech_start);
        try {
            startActivityForResult(intent, RESULT_SPEECH);
        }catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getApplicationContext(), R.string.locnavi_speech_service_unavailable, Toast.LENGTH_SHORT);
            t.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //同意授权
                    startListerning();
                }
            }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d(TAG, text.get(0));
                    tvLog.setText(text.get(0));
                }
            }
            break;
        }
    }
}