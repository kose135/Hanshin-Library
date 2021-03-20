package com.example.hanshinlibrary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hanshinlibrary.Model.Profile;

public class SplashActivity extends AppCompatActivity {
    private Boolean caps;
    private String ID;
    private String PW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        caps = false;

        System.out.println("스플래쉬 시작");

        if (caps) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("현재 점검중입니다.").setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.create().show();
        } else {
            SharedPreferences profileSharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
            ID = profileSharedPreferences.getString("ID", "");
            PW = profileSharedPreferences.getString("PW", "");
            Profile.sit = profileSharedPreferences.getString("sit", "");

            if (ID.equals("") && PW.equals("")) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                searchID searchID = new searchID();
                searchID.execute(PHPCommon.phpAddress("searchProfile"), ID);
            }
        }
    }

    public class searchID extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("Failure")) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            } else {
                System.out.println("로그 : " + result);
                String[] profile = result.split(" ");
                String name = profile[0];
                String id = profile[1];
                String password = profile[2];
                String email = profile[3];
                String major = profile[4];
                String gender = profile[5];

                if (ID.equals(id) && PW.equals(password)) {
                    new Profile(name, ID, PW, email, major, gender);
                    startActivity(new Intent(getApplicationContext(), HomeTabActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    Toast.makeText(getApplicationContext(), "네트워크 오류", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String) params[0];
            String student_ID = (String) params[1];

            String postParameters = "&student_ID=" + student_ID;
            return PHPCommon.sendPHP(serverURL, postParameters);
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }
}
