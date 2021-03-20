package com.example.hanshinlibrary.HomeFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.hanshinlibrary.Model.Profile;
import com.example.hanshinlibrary.NoticeFragment.AcademicNoticeFragment;
import com.example.hanshinlibrary.NoticeFragment.FacilityNoticeFragment;
import com.example.hanshinlibrary.NoticeFragment.NoticeFragment;
import com.example.hanshinlibrary.ProfileActivity;
import com.example.hanshinlibrary.QREncoding;
import com.example.hanshinlibrary.R;
import com.google.android.material.tabs.TabLayout;

import static android.content.Context.WINDOW_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;


public class HomeFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager tabViewPager;
    private PagerAdapter tabPagerAdapter;
    private ImageButton ibStudentQR;
    private TextView tvName;
    private TextView tvStudentID;
    private TextView tvMajor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvName = (TextView) view.findViewById(R.id.tvName);
        tvName.setText(Profile.name);

        tvStudentID = (TextView) view.findViewById(R.id.tvStudentID);
        tvStudentID.setText(Profile.student_ID);

        tvMajor = (TextView) view.findViewById(R.id.tvMajor);
        tvMajor.setText(Profile.major);

        ibStudentQR = (ImageButton) view.findViewById(R.id.ibStudentQR);
        createQR(Profile.student_ID);
        ibStudentQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });


        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabPagerAdapter = new ContentsPagerAdapter(getFragmentManager(), tabLayout.getTabCount());

        tabViewPager = (ViewPager) view.findViewById(R.id.tabViewPager);
        tabViewPager.setAdapter(tabPagerAdapter);
        tabViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabViewPager.setCurrentItem(tab.getPosition());
                tabPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    public class ContentsPagerAdapter extends FragmentStatePagerAdapter {
        private int mPageCount;

        private NoticeFragment NoticeFragment;
        private AcademicNoticeFragment AcademicNoticeFragment;
        private FacilityNoticeFragment FacilityNoticeFragment;

        public ContentsPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            this.mPageCount = behavior;

            NoticeFragment = new NoticeFragment();
            AcademicNoticeFragment = new AcademicNoticeFragment();
            FacilityNoticeFragment = new FacilityNoticeFragment();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return NoticeFragment;

                case 1:
                    return AcademicNoticeFragment;

                case 2:
                    return FacilityNoticeFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mPageCount;
        }
    }

    public void createQR(String student_ID) {
        String inputValue = student_ID.trim();
        if (inputValue.length() > 0) {
            WindowManager manager = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            QREncoding qrgEncoder = new QREncoding(inputValue, null, "TEXT_TYPE", smallerDimension);
            qrgEncoder.setColorCode(Color.WHITE);
            qrgEncoder.setColorBackground(Color.parseColor("#2C1A58"));
            try {
                Bitmap bitmap = qrgEncoder.getBitmap();
                ibStudentQR.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

        }
    }
}
