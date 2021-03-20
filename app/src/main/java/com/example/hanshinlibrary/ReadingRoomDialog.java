package com.example.hanshinlibrary;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

public class ReadingRoomDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private Button btnCancel;
    private Button btnScan;

    public ReadingRoomDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_readingroom);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SuggestionsReadingRoom = new Intent(getContext(), SuggestionsReadingRoomActivity.class);
                getContext().startActivity(SuggestionsReadingRoom);
                dismiss();
            }
        });

    }

    @Override
    public void onClick(View v) {

    }
}
