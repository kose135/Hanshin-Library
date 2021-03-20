package com.example.hanshinlibrary;

import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class LibraryGuideActivity extends TabActivity {

    private ImageButton btnback1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_guide);

        btnback1 = (ImageButton) findViewById(R.id.btnBack1);



        TabHost tabHost = getTabHost();

        TabHost.TabSpec tabOpen = tabHost.newTabSpec("TAB1").setIndicator("개관시간");
        tabOpen.setContent(R.id.tab1);
        tabHost.addTab(tabOpen);

        TabHost.TabSpec tabFloor = tabHost.newTabSpec("TAB2").setIndicator("층별 안내");
        tabFloor.setContent(R.id.tab2);
        tabHost.addTab(tabFloor);


        TabHost.TabSpec tabBook = tabHost.newTabSpec("TAB3").setIndicator("대출/반납");
        tabBook.setContent(R.id.tab3);
        tabHost.addTab(tabBook);

        TabHost.TabSpec tabEct = tabHost.newTabSpec("ETC").setIndicator("기타사항");
        tabEct.setContent(R.id.tabEtc);
        tabHost.addTab(tabEct);

        tabHost.setCurrentTab(0);


        btnback1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });








    }

}