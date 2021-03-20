package com.example.hanshinlibrary;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanshinlibrary.Model.Profile;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SuggestionsReadingRoomActivity extends AppCompatActivity {
    private IntentIntegrator qrScan;

    private EditText etContents;
    private TextView textReadingRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions_readingroom);

        textReadingRoom = (TextView) findViewById(R.id.textReadingRoomName);
        etContents = (EditText) findViewById(R.id.etContents);

        //카메라로 가기위한 인텐트 생성
        qrScan = new IntentIntegrator(this);
        //카메라를 세로로 고정하기 위한 코드
        qrScan.setOrientationLocked(false);
        qrScan.setPrompt("열람실 좌석 QR코드를 스캔 해주세요");
        qrScan.initiateScan();

        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button btnRegistration = (Button) findViewById(R.id.btnRegistration);
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

                insertSuggestion insertSuggestion = new insertSuggestion();
                insertSuggestion.execute(PHPCommon.phpAddress("insertSuggestion"), "열람실", textReadingRoom.getText().toString(), date, Profile.student_ID, etContents.getText().toString());

            }
        });

    }

    // 스캔한 정보를 가지고 있는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult QRScanresult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // QRScanresult가 스캔한 정보를 가지고 있음
        if (QRScanresult != null) {
            if (QRScanresult.getContents() == null) {
                Toast.makeText(this, "열람실 좌석 스캔을 취소했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String result = QRScanresult.getContents();
                if(result.contains("열람실")){
                    textReadingRoom.setText(result);
                }
                else{
                    Toast.makeText(getApplicationContext(), "열람실 QR코드가 아닙니다.\n다시 스캔 하시길 바랍니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class insertSuggestion extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Toast.makeText(getApplicationContext(), "정상적으로 건의사항 글 등록이 되었습니다.", Toast.LENGTH_SHORT).show();
            finish();

            Log.d("insertSuggestion : ", "response - " + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String type = params[1];
            String title = params[2];
            String date = params[3];
            String writer =  params[4];
            String contents = params[5];

            String postParameters = "&type=" + type + "&title=" + title + "&date=" + date + "&writer=" + writer + "&contents=" + contents;
            return PHPCommon.sendPHP(serverURL, postParameters);
        }
    }
}