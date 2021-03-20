package com.example.hanshinlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hanshinlibrary.Model.Profile;
import com.example.hanshinlibrary.Model.Suggestion;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

public class SuggestionWriteActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private Spinner typeSpinner;
    private Button btnRegister;
    private EditText etTitle;
    private EditText etContents;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_write);

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etContents = (EditText) findViewById(R.id.etContents);


        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = typeSpinner.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String contents = etContents.getText().toString().replace("\n", "</br>");
                if (title.equals("") || contents.equals("")) {
                    Toast.makeText(getApplicationContext(), "모든 항목을 작성해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    Date currentDateTime = calendar.getTime();
                    String date = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(currentDateTime);

                    insertSuggestion task = new insertSuggestion();
                    task.execute(PHPCommon.phpAddress("insertSuggestion"), type, title, date, Profile.name, contents);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class insertSuggestion extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("Success")) {
                Toast.makeText(getApplicationContext(), "건의사항 글이 정상적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, getIntent());
            } else if (result.equals("Failure")) {
                Toast.makeText(getApplicationContext(), "건의사항 글을 등록하는데에 실패했습니다.\n다시 작성해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "네트워크에 오류가 발생했습니다.\n다시 작성해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
            }

            finish();
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String type = params[1];
            String title = params[2];
            String date = params[3];
            String writer = params[4];
            String contents = params[5];

            String postParameters = "type=" + type + "&title=" + title + "&date=" + date + "&writer=" + writer + "&contents=" + contents;
            return PHPCommon.sendPHP(serverURL, postParameters);
        }
    }
}
