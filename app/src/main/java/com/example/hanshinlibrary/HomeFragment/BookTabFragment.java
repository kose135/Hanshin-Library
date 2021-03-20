package com.example.hanshinlibrary.HomeFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hanshinlibrary.BookFragment.BookHistoryFragment;
import com.example.hanshinlibrary.BookFragment.BookLoanFragment;
import com.example.hanshinlibrary.BookFragment.BookSearchFragment;
import com.example.hanshinlibrary.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BookTabFragment extends Fragment {
    private BookSearchFragment BookSearchFragment = new BookSearchFragment();
    private BookLoanFragment BookLoanFragment = new BookLoanFragment();
    private BookHistoryFragment BookHistoryFragment = new BookHistoryFragment();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_book_tab, container, false);

        BottomNavigationView bottomPostView = (BottomNavigationView) view.findViewById(R.id.bottomBookView);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.bookFragment_frameLayout, BookSearchFragment).commitAllowingStateLoss();

        bottomPostView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.BookSearch:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.bookFragment_frameLayout, BookSearchFragment).commitAllowingStateLoss();
                        return true;

                    case R.id.BookLoan:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.bookFragment_frameLayout, BookLoanFragment).commitAllowingStateLoss();
                        return true;

                    case R.id.BookHistory:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.bookFragment_frameLayout, BookHistoryFragment).commitAllowingStateLoss();
                        return true;
                }
                return false;
            }
        });

        return view;
    }
}
