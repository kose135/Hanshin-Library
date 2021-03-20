package com.example.hanshinlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hanshinlibrary.Model.Profile;
import com.example.hanshinlibrary.Model.Seat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.StringTokenizer;

public class ReadingRoom1Activity extends AppCompatActivity {
    private ImageButton btnBack;
    private ImageView ivExclamation;
    private ImageButton btnScan;
    private ImageButton btnRefresh;

    private IntentIntegrator qrScan;
    private getSeat getSeat;

    private HashMap<Integer, Seat> seatHash = new HashMap<Integer, Seat>();
    private String roomName = "일반열람실1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readingroom1);

        getSeatCall();

        //카메라로 가기위한 인텐트 생성
        qrScan = new IntentIntegrator(this);
        //카메라를 세로로 고정하기 위한 코드
        qrScan.setOrientationLocked(false);

        ivExclamation = (ImageView) findViewById(R.id.ivExclamation);
        ivExclamation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnScan = (ImageButton) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.setPrompt("좌석의 QR코드를 스캔 해주세요");
                qrScan.initiateScan();
            }
        });

        btnRefresh = (ImageButton) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSeatCall();
            }
        });
    }

    // .excute는 객체가 생성되고 한번만 실행이 가능하기때문에 새로고침할때마다 계속 생성해줘야한다.
    public void getSeatCall() {
        getSeat = new getSeat();
        getSeat.execute(PHPCommon.phpAddress("getSeatData"), roomName);
    }

    public void sitting(View view) {

    }
    // 스캔한 정보를 가지고 있는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult QRScanresult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // QRScanresult가 스캔한 정보를 가지고 있음
        if (QRScanresult != null) {
            if (QRScanresult.getContents() == null) {
                Toast.makeText(this, "취소", Toast.LENGTH_SHORT).show();
            } else {
//                String[] SeatData = QRScanresult.getContents().split(" ");
//                int changeSeatId = getResources().getIdentifier(SeatData[0] + "_" + SeatData[1], "id", getPackageName());
//                isAbleSeat(changeSeatId);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class getSeat extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Button seatButton;
            StringTokenizer st = new StringTokenizer(result, ",");

            seatHash.clear();
            while (st.hasMoreTokens()) {
                String[] seatData = st.nextToken().split(" ");

                int seatId = getResources().getIdentifier(seatData[0] + "_" + seatData[1], "id", getPackageName());
                seatHash.put(seatId, new Seat(seatData[0], seatData[1], seatData[2], seatData[3], seatData[4]));

                seatButton = (Button) findViewById(seatId);
                if (seatData[2].equals("비어있음")) {
                    seatButton.setBackgroundColor(Color.BLUE);
                } else if (seatData[2].equals("사용중")) {
                    seatButton.setBackgroundColor(Color.GRAY);
                } else if (seatData[2].equals("예약중")) {
                    seatButton.setBackgroundColor(Color.YELLOW);
                } else {
                    seatButton.setBackgroundColor(Color.RED);
                }
            }

            Log.d("getSeat : ", "response - " + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String room_name = params[1];

            String postParameters = "&room_name=" + room_name;
            return PHPCommon.sendPHP(serverURL, postParameters);
        }
    }
}
