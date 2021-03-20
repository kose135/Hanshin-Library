package com.example.hanshinlibrary.HomeFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hanshinlibrary.ChangeProfileActivity;
import com.example.hanshinlibrary.DeleteProfileActivity;
import com.example.hanshinlibrary.LibraryGuideActivity;
import com.example.hanshinlibrary.LoginActivity;
import com.example.hanshinlibrary.R;
import com.example.hanshinlibrary.SuggestionBookListActivity;
import com.example.hanshinlibrary.SuggestionListActivity;

public class SettingFragment extends Fragment {
    private ImageView ivChrome;
    private ImageView ivHanshinApp;
    private ImageView ivEnrolmentApp;
    private Button btnSuggestion;
    private Button btnLogout;
    private Button btnWithdrawal;
    private Button btnChangeProfile;
    private Button btnRequestBook;
    private Button btnLibraryGuide;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_setting, container, false);

        ivChrome = (ImageView) view.findViewById(R.id.ivChrome);
        ivChrome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.hs.ac.kr/sites/kor/index.do")));
            }
        });

        ivHanshinApp = (ImageView) view.findViewById(R.id.ivHanshinApp);
        ivHanshinApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getActivity().getPackageManager();
                String packageName = "kr.ac.hanshin.push";
                try {
                    pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                    startActivity(pm.getLaunchIntentForPackage(packageName));
                } catch (PackageManager.NameNotFoundException e) {
                    // 해당 패키지 앱이 설치되어있지 않을 경우 NameNotFoundException 예외가 발생하면서 학교 공지사항으로 가서 설치 하는 링크를 걸어둠
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.hs.ac.kr/kor/4953/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGa29yJTJGMjQlMkYxMDY5OTQlMkZhcnRjbFZpZXcuZG8lM0ZwYWdlJTNENCUyNnNyY2hDb2x1bW4lM0QlMjZzcmNoV3JkJTNEJTI2YmJzQ2xTZXElM0QlMjZiYnNPcGVuV3JkU2VxJTNEJTI2cmdzQmduZGVTdHIlM0QlMjZyZ3NFbmRkZVN0ciUzRCUyNmlzVmlld01pbmUlM0RmYWxzZSUyNnBhc3N3b3JkJTNEJTI2")));
                }

            }
        });

        ivEnrolmentApp = (ImageView) view.findViewById(R.id.ivEnrolmentApp);
        ivEnrolmentApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = getActivity().getPackageManager();
                String packageName = "kr.co.swit.hsuv";
                try {
                    pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                    startActivity(pm.getLaunchIntentForPackage(packageName));

                } catch (PackageManager.NameNotFoundException e) {
                    // 해당 패키지 앱이 설치되어있지 않을 경우 NameNotFoundException 예외가 발생하면서 구글 플레이로 이동.
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                }
            }
        });

        btnSuggestion = (Button) view.findViewById(R.id.btnSuggestion);
        btnSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SuggestionListActivity.class));
            }
        });

        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        btnWithdrawal = (Button) view.findViewById(R.id.btnWithdrawal);
        btnWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DeleteProfileActivity.class));
            }
        });

        btnChangeProfile = (Button) view.findViewById(R.id.btnChangeProfile);
        btnChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangeProfileActivity.class));
            }
        });

        btnRequestBook = (Button) view.findViewById(R.id.btnRequestBook);
        btnRequestBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SuggestionBookListActivity.class));
            }
        });

        btnLibraryGuide = (Button) view.findViewById(R.id.btnLibraryGuide);
        btnLibraryGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LibraryGuideActivity.class));
            }
        });

        return view;
    }

    public void logout() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
        SharedPreferences pref = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ID", "");
        editor.putString("PW", "");
        editor.commit();
        getActivity().finish();

        Toast.makeText(getActivity(), "정상적으로 로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
    }
}

