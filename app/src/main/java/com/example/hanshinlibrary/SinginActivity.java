package com.example.hanshinlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.hanshinlibrary.Model.Profile;

public class SinginActivity extends AppCompatActivity {
    private ArrayAdapter majorAdapter;
    private Spinner majorSpinner;
    private String name;
    private String ID;
    private String PW;
    private String email;
    private String gender;
    private String major;
    private boolean validate = false;

    private EditText etName;
    private EditText etID;
    private EditText etPassword;
    private EditText etEmail;
    private Button btnValidate;
    private Button btnSignin;

    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singin);
        builder = new AlertDialog.Builder(this);

        majorSpinner = (Spinner) findViewById(R.id.majorSpinner);
        majorAdapter = ArrayAdapter.createFromResource(this, R.array.major, android.R.layout.simple_spinner_dropdown_item);
        majorSpinner.setAdapter(majorAdapter);
        majorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    major = majorSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etName = (EditText) findViewById(R.id.etName);
        etID = (EditText) findViewById(R.id.etID);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etEmail = (EditText) findViewById(R.id.etEmail);

        RadioGroup genderGroup = (RadioGroup) findViewById(R.id.genderGroup);
        int genderGroup_ID = genderGroup.getCheckedRadioButtonId();
        gender = ((RadioButton) findViewById(genderGroup_ID)).getText().toString();

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton genderButton = (RadioButton) findViewById(checkedId);
                gender = genderButton.getText().toString();
            }
        });

        btnValidate = (Button) findViewById(R.id.btnValidate);
        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID = etID.getText().toString();

                if (ID.equals("")) {
                    builder.setTitle("학번을 입력하시길 바랍니다.");
                    builder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {}
                            });
                    builder.show();
                } else {
                    validateID search = new validateID();
                    search.execute(PHPCommon.phpAddress("validate"), ID);
                }

            }
        });

        btnSignin = (Button) findViewById(R.id.btnSignin);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString();
                ID = etID.getText().toString();
                PW = etPassword.getText().toString();
                email = etEmail.getText().toString();

                if (!validate) {
                    builder.setTitle("학번 중복확인이 안되었습니다.");
                    builder.setMessage("학번 중복확인을 해주시길 바랍니다.");
                    builder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.show();
                } else {
                    if (ID.equals("") || PW.equals("") || major.equals("") || email.equals("")) {
                        builder.setTitle("빈칸이 있습니다.");
                        builder.setMessage("모든 항목을 작성해주시길 바랍니다.");
                        builder.setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.show();
                    } else {
                        insertProfile insert = new insertProfile();
                        insert.execute(PHPCommon.phpAddress("insertProfile"),name, ID, PW, email, major, gender);
                    }
                }

            }
        });
    }

    private class validateID extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            System.out.println("중복확인 후: " + result);

            if (result.equals("Possible")) {
                builder.setTitle("사용가능한 학번입니다.");
                builder.setMessage("");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
                validate = true;
            } else {
                builder.setTitle("이미 존재하는 학번입니다.");
                builder.setMessage("새로운 학번을 입력하시길 바랍니다.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
                validate = false;
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

    private class insertProfile extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result.equals("Success")) {
                builder.setTitle("정상적으로 회원가입이 되었습니다.");
                builder.setMessage("한신 도서관에 오실것을 환영합니다.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("ID", ID);
                                editor.putString("PW", PW);
                                editor.commit();

                                new Profile(name, ID, PW, email, major, gender);
                                startActivity(new Intent(getApplicationContext(), HomeTabActivity.class));
                                finish();
                            }
                        });
                builder.show();
            }
            else{
                startActivity(new Intent(getApplicationContext(), SinginActivity.class));
                finish();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String) params[0];
            String name = (String) params[1];
            String student_ID = (String) params[2];
            String password = (String) params[3];
            String email = (String) params[4];
            String major = (String) params[5];
            String gender = (String) params[6];

            String postParameters = "&name=" + name + "&student_ID=" + student_ID + "&password=" + password + "&email=" + email + "&major=" + major + "&gender=" + gender;
            return PHPCommon.sendPHP(serverURL, postParameters);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}
