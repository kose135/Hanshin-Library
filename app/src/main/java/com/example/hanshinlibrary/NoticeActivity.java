package com.example.hanshinlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class NoticeActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private TextView tvNum;
    private TextView tvDate;
    private TextView tvTitle;
    private TextView tvContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        tvNum = (TextView) findViewById(R.id.tvNum);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvContents = (TextView) findViewById(R.id.tvContents);

        Intent getIntent = getIntent();

        tvNum.setText("글번호 : " + getIntent.getStringExtra("num"));
        tvDate.setText("작성일 : " + getIntent.getStringExtra("date"));
        tvTitle.setText(getIntent.getStringExtra("title"));
        String contents = getIntent.getStringExtra("contents");
        tvContents.setText(contents.replace("</br>", "\n"));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
