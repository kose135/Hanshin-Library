package com.example.hanshinlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.hanshinlibrary.HomeFragment.BookTabFragment;
import com.example.hanshinlibrary.HomeFragment.HomeFragment;
import com.example.hanshinlibrary.HomeFragment.ReadingRoomListFragment;
import com.example.hanshinlibrary.HomeFragment.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeTabActivity extends AppCompatActivity {
    private long backKeyPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hometab);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.HomeTabActivity_frameLayout, new HomeFragment()).commitAllowingStateLoss();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.Home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.HomeTabActivity_frameLayout, new HomeFragment()).commitAllowingStateLoss();
                        return true;

                    case R.id.Book:
                        getSupportFragmentManager().beginTransaction().replace(R.id.HomeTabActivity_frameLayout, new BookTabFragment()).commitAllowingStateLoss();
                        return true;

                    case R.id.ReadingRoom:
                        getSupportFragmentManager().beginTransaction().replace(R.id.HomeTabActivity_frameLayout, new ReadingRoomListFragment()).commitAllowingStateLoss();
                        return true;

                    case R.id.setting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.HomeTabActivity_frameLayout, new SettingFragment()).commitAllowingStateLoss();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        // 기존 뒤로가기 버튼의 기능을 막기위해 주석처리 또는 삭제
        // super.onBackPressed();

        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 1.5초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 1.5초가 지났으면 Toast Show
        if (System.currentTimeMillis() > backKeyPressedTime + 1500) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
        // 현재 표시된 Toast 취소
        if (System.currentTimeMillis() <= backKeyPressedTime + 1500) {
            finish();
        }
    }
}
