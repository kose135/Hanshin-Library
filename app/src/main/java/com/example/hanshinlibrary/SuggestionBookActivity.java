package com.example.hanshinlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SuggestionBookActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvNum1;
    private TextView tvDate1;
    private TextView tvTitle1;
    private TextView tvContents1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_book);

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        tvNum1 = (TextView) findViewById(R.id.tvNum1);
        tvTitle1 = (TextView) findViewById(R.id.tvTitle1);
        tvDate1 = (TextView) findViewById(R.id.tvDate1);
        tvContents1 = (TextView) findViewById(R.id.tvContents1);

        Intent getIntent = getIntent();

        tvNum1.setText("글번호 : " + getIntent.getStringExtra("num1"));
        tvDate1.setText("작성일 : " + getIntent.getStringExtra("date1") + "  |  작성자 : " + getIntent.getStringExtra("writer1"));
        tvTitle1.setText(getIntent.getStringExtra("title1"));

        String contents1 = getIntent.getStringExtra("contents1");
        tvContents1.setText(contents1.replace("</br>", "\n"));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}