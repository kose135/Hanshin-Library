package com.example.hanshinlibrary.BookFragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hanshinlibrary.Model.Book;
import com.example.hanshinlibrary.Model.BookHistory;
import com.example.hanshinlibrary.Model.BookLoan;
import com.example.hanshinlibrary.Model.Profile;
import com.example.hanshinlibrary.PHPCommon;
import com.example.hanshinlibrary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class BookLoanFragment extends Fragment {
    private SwipeRefreshLayout bookLoanSwipeRefreshLayout;
    private RecyclerView bookLoanRecyclerView;
    private bookLoanRecyclerViewAdapter bookLoanRecyclerViewAdapter;

    private ArrayList<BookLoan> bookLoanlist = new ArrayList<>();
    private String TAG = "도서 대출";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_loan_book, container, false);

        bookLoanRecyclerViewAdapter = new bookLoanRecyclerViewAdapter();

        bookLoanRecyclerView = (RecyclerView) view.findViewById(R.id.bookLoanRecyclerView);
        bookLoanRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bookLoanRecyclerView.setAdapter(bookLoanRecyclerViewAdapter);

        bookLoanSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.bookLoanSwipeRefreshLayout);
        bookLoanSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bookLoanRecyclerViewAdapter.getBookHistoryCall();
                bookLoanSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private class bookLoanRecyclerViewAdapter extends RecyclerView.Adapter {

        public void getBookHistoryCall() {
            bookLoanlist.clear();
            getBookHistory task = new getBookHistory();
            task.execute(PHPCommon.phpAddress("getBookHistory"), Profile.student_ID);
        }

        public bookLoanRecyclerViewAdapter() {
            getBookHistoryCall();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_loan, parent, false);
            BookLoanViewHolder holder = new BookLoanViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((BookLoanViewHolder) holder).tvTitle.setText(bookLoanlist.get(position).getTitle());
            ((BookLoanViewHolder) holder).tvAuthor.setText(": " + bookLoanlist.get(position).getAuthor());
            ((BookLoanViewHolder) holder).tvID.setText(bookLoanlist.get(position).getId());
            ((BookLoanViewHolder) holder).tvPublisher.setText(": " + bookLoanlist.get(position).getPublisher());
            ((BookLoanViewHolder) holder).tvState.setText(bookLoanlist.get(position).getState());
            ((BookLoanViewHolder) holder).tvLoanDate.setText(": " + bookLoanlist.get(position).getLoanDate());
            if (getToday().compareTo(bookLoanlist.get(position).getReturnDate()) > 0) {
                ((BookLoanViewHolder) holder).tvState.setText("연체중");
                ((BookLoanViewHolder) holder).tvState.setTextColor(Color.parseColor("#FF9B00"));
            }
            ((BookLoanViewHolder) holder).tvReturnDate.setText(": " + bookLoanlist.get(position).getReturnDate());
        }

        @Override
        public int getItemCount() {
            return bookLoanlist.size();
        }

        public String getRetuenDate(String loanDate) throws ParseException {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
            System.out.println("loanDate : " + loanDate);
            Date date = sdf.parse(loanDate);
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 7);

            return sdf.format(calendar.getTime());
        }

        public String getToday() {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
            return sdf.format(calendar.getTime());
        }

        public class BookLoanViewHolder extends RecyclerView.ViewHolder {
            public TextView tvTitle;
            public TextView tvAuthor;
            public TextView tvID;
            public TextView tvPublisher;
            public TextView tvState;
            public TextView tvLoanDate;
            public TextView tvReturnDate;

            public BookLoanViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                tvAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
                tvID = (TextView) itemView.findViewById(R.id.tvID);
                tvPublisher = (TextView) itemView.findViewById(R.id.tvPublisher);
                tvState = (TextView) itemView.findViewById(R.id.tvState);
                tvLoanDate = (TextView) itemView.findViewById(R.id.tvLoanDate);
                tvReturnDate = (TextView) itemView.findViewById(R.id.tvReturnDate);
            }
        }

        private class getBookHistory extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                System.out.println("도서 대출 내역 : " + result);
                StringTokenizer st = new StringTokenizer(result, "-");

                while (st.hasMoreTokens()) {
                    String[] line = st.nextToken().split(", ");

                    BookLoan bookLoan = null;
                    try {
                        bookLoan = new BookLoan(line[0], line[1], line[2], line[3], line[4], line[5], getRetuenDate(line[5]));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (bookLoan.getState().equals("반납완료")) {
                        continue;
                    }
                    bookLoanlist.add(bookLoan);
                }

                notifyDataSetChanged();
            }

            @Override
            protected String doInBackground(String... params) {
                String serverURL = params[0];
                String student_ID = params[1];

                String postParameters = "&student_ID=" + student_ID;
                return PHPCommon.sendPHP(serverURL, postParameters);
            }
        }
    }


}
