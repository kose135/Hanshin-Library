package com.example.hanshinlibrary;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hanshinlibrary.Model.Profile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SuggestionBookWriteActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Spinner typeSpinner1;
    private Button btnRegister1;
    private EditText etTitle1;
    private EditText etContents1;
    private String type1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_book_write);

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        typeSpinner1 = (Spinner) findViewById(R.id.typeSpinner1);
        btnRegister1 = (Button) findViewById(R.id.btnRegister1);
        etTitle1 = (EditText) findViewById(R.id.etTitle1);
        etContents1 = (EditText) findViewById(R.id.etContents1);






        ////////////////////////////////////////////////////////////////////////////////
        typeSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type1 = typeSpinner1.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ////////////////////////////////////////////////////////////////////////////////








        btnRegister1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title1 = etTitle1.getText().toString();
                String contents1 = etContents1.getText().toString().replace("\n", "</br>");

                if(title1.equals(" ") || contents1.equals("")){
                    Toast.makeText(getApplicationContext(), "모든 항목을 작성해주시길 바랍니다. ", Toast.LENGTH_SHORT).show();
                }
                else {
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTimeInMillis(System.currentTimeMillis());
                    Date currentDateTime1 = calendar1.getTime();
                    String date1 = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(currentDateTime1);

                    System.out.println("type1 : " + type1 + " title : " + title1 + " date : " + date1 + " name : " + Profile.name + " contents : " + contents1 );

                    insertSuggestionBook task1 = new insertSuggestionBook();
                    task1.execute(PHPCommon.phpAddress("insertSuggestionBook"), type1, title1, date1, Profile.name, contents1);
                }

            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    }







    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class insertSuggestionBook extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            if(result.equals("Success")){
                Toast.makeText(getApplicationContext(), "희망 도서 신청 글이 정상적으로 등록되었습니다. ", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, getIntent());
            }
            else if (result.equals("Failure")) {
                Toast.makeText(getApplicationContext(), "희망 도서 신청 글을 등록하는데에 실패했습니다.\n다시 작성해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "네트워크에 오류가 발생했습니다.\n다시 작성해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
            }

            finish();
        }

        /////////////////////////////////////////////////////////////////////////

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String type1 = params[1];
            String title1 = params[2];
            String date1 = params[3];
            String writer1 = params[4];
            String contents1 = params[5];

            String postParameters = "type=" + type1 + "&title=" + title1 + "&date=" + date1 + "&writer=" + writer1 + "&contents=" + contents1;

            return PHPCommon.sendPHP(serverURL, postParameters);
        }

        /////////////////////////////////////////////////////////////////////////
    }
}