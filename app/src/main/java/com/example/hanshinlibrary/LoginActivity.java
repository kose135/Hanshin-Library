package com.example.hanshinlibrary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hanshinlibrary.Model.Profile;

public class LoginActivity extends AppCompatActivity {

    private EditText etID;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignin;

    private String ID;
    private String PW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        etID = (EditText) findViewById(R.id.etID);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignin = (Button) findViewById(R.id.btnSignin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ID = etID.getText().toString();
                PW = etPassword.getText().toString();
                searchID searchID = new searchID();
                searchID.execute(PHPCommon.phpAddress("searchProfile"), ID);
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SinginActivity.class));
                finish();
            }
        });
    }

    public class searchID extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("결과 : " + result);

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

            if (result.equals("Failure")) {
                builder.setTitle("로그인에 실패했습니다.");
                builder.setMessage("회원가입 혹은 다시 입력하시길 바랍니다.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
            } else {
                String[] profile = result.split(" ");
                String name = profile[0];
                String id = profile[1];
                String password = profile[2];
                String email = profile[3];
                String major = profile[4];
                String gender = profile[5];

                if (ID.equals("관리자") && PW.equals(password)) {
//                    Intent adminMenuActivity = new Intent(LoginActivity.this, AdminMenuActivity.class);
//                    startActivity(adminMenuActivity);
//
//                    Login log = new Login(student_ID, password, email, major, gender);
//
//                    editor.putString("student_ID", id);
//                    editor.putString("password", password);
//
//                    editor.commit();
                } else if (PW.equals(password)) {
                    new Profile(name, id, password, email, major, gender);

                    Intent HomeTabActivity = new Intent(LoginActivity.this, HomeTabActivity.class);
                    startActivity(HomeTabActivity);

                    SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("ID", ID);
                    editor.putString("PW", PW);

                    editor.commit();
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
}
