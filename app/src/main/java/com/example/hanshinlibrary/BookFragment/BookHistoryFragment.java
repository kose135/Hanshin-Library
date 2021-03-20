package com.example.hanshinlibrary.BookFragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hanshinlibrary.Model.BookHistory;
import com.example.hanshinlibrary.Model.BookLoan;
import com.example.hanshinlibrary.Model.Profile;
import com.example.hanshinlibrary.PHPCommon;
import com.example.hanshinlibrary.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class BookHistoryFragment extends Fragment {
    private SwipeRefreshLayout bookHistorySwipeRefreshLayout;
    private RecyclerView bookHistoryRecyclerView;

    private bookHistoryRecyclerViewAdapter bookHistoryRecyclerViewAdapter;

    private ArrayList<BookHistory> bookHistorylist = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_history_book, container, false);

        bookHistoryRecyclerViewAdapter = new bookHistoryRecyclerViewAdapter();

        bookHistoryRecyclerView = (RecyclerView) view.findViewById(R.id.bookHistoryRecyclerView);
        bookHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bookHistoryRecyclerView.setAdapter(bookHistoryRecyclerViewAdapter);

        bookHistorySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.bookHistorySwipeRefreshLayout);
        bookHistorySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bookHistoryRecyclerViewAdapter.getBookHistoryCall();
                bookHistorySwipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private class bookHistoryRecyclerViewAdapter extends RecyclerView.Adapter {

        public void getBookHistoryCall() {
            bookHistorylist.clear();
            getBookHistory task = new getBookHistory();
            task.execute(PHPCommon.phpAddress("getBookHistory"), Profile.student_ID);
        }

        public bookHistoryRecyclerViewAdapter() {
            getBookHistoryCall();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_history, parent, false);
            BookHistoryViewHolder holder = new BookHistoryViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((BookHistoryViewHolder) holder).tvTitle.setText(bookHistorylist.get(position).getTitle());
            ((BookHistoryViewHolder) holder).tvAuthor.setText(": " + bookHistorylist.get(position).getAuthor());
            ((BookHistoryViewHolder) holder).tvID.setText(bookHistorylist.get(position).getId());
            ((BookHistoryViewHolder) holder).tvPublisher.setText(": " + bookHistorylist.get(position).getPublisher());
            ((BookHistoryViewHolder) holder).tvLoanDate.setText(": " + bookHistorylist.get(position).getLoanDate());

            if (bookHistorylist.get(position).getState().equals("반납완료")) {
                ((BookHistoryViewHolder) holder).tvState.setText(bookHistorylist.get(position).getState());
                ((BookHistoryViewHolder) holder).tvState.setTextColor(Color.parseColor("#808080"));
            } else {
                ((BookHistoryViewHolder) holder).tvState.setText(bookHistorylist.get(position).getState());
            }

            if (bookHistorylist.get(position).getReturnDate().equals("없음")) {
                ((BookHistoryViewHolder) holder).layoutReturn.setVisibility(View.GONE);
                try {
                    if (getRetuenDate(bookHistorylist.get(position).getLoanDate()).compareTo(getToday()) < 0) {
                        ((BookHistoryViewHolder) holder).tvState.setText("연체중");
                        ((BookHistoryViewHolder) holder).tvState.setTextColor(Color.parseColor("#FF9B00"));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {
                ((BookHistoryViewHolder) holder).tvReturnDate.setText(": " + bookHistorylist.get(position).getReturnDate());
            }
        }

        @Override
        public int getItemCount() {
            return bookHistorylist.size();
        }

        public String getRetuenDate(String loanDate) throws ParseException {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
            Date date = sdf.parse(loanDate);
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 7);

            return sdf.format(calendar.getTime()).toString();
        }

        public String getToday() {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
            return sdf.format(calendar.getTime());
        }

        public class BookHistoryViewHolder extends RecyclerView.ViewHolder {
            public TextView tvTitle;
            public TextView tvAuthor;
            public TextView tvID;
            public TextView tvPublisher;
            public TextView tvState;
            public TextView tvLoanDate;
            public LinearLayout layoutReturn;
            public TextView tvReturnDate;

            public BookHistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                tvAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
                tvID = (TextView) itemView.findViewById(R.id.tvID);
                tvPublisher = (TextView) itemView.findViewById(R.id.tvPublisher);
                tvState = (TextView) itemView.findViewById(R.id.tvState);
                tvLoanDate = (TextView) itemView.findViewById(R.id.tvLoanDate);
                layoutReturn = (LinearLayout) itemView.findViewById(R.id.layoutReturn);
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

                    BookHistory BookHistory = new BookHistory(line[0], line[1], line[2], line[3], line[4], line[5], line[6]);
                    bookHistorylist.add(BookHistory);
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
