package com.example.hanshinlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hanshinlibrary.Model.Notice;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class FacilityNoticeAllListActivity extends AppCompatActivity {
    private SwipeRefreshLayout facilityNoticeListSwipeRefreshLayout;
    private RecyclerView facilityNoticeListRecyclerViewNotice;
    private ImageButton btnBack;

    private academicnoticeListRecyclerViewAdapter academicnoticeListRecyclerViewAdapter;

    private ArrayList<Notice> noticeList = new ArrayList<>();
    private String type = "시설공지";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_notice_all_list);

        academicnoticeListRecyclerViewAdapter = new academicnoticeListRecyclerViewAdapter();

        facilityNoticeListRecyclerViewNotice = (RecyclerView) findViewById(R.id.facilityNoticeListRecyclerViewNotice);
        facilityNoticeListRecyclerViewNotice.setLayoutManager(new LinearLayoutManager(this));
        facilityNoticeListRecyclerViewNotice.setAdapter(academicnoticeListRecyclerViewAdapter);

        facilityNoticeListSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.facilityNoticeListSwipeRefreshLayout);
        facilityNoticeListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                academicnoticeListRecyclerViewAdapter.getNoticeCall();
                facilityNoticeListSwipeRefreshLayout.setRefreshing(false);
            }
        });

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class academicnoticeListRecyclerViewAdapter extends RecyclerView.Adapter {
        public void getNoticeCall() {
            noticeList.clear();
            getNotice task = new getNotice();
            task.execute(PHPCommon.phpAddress("getNotice"), type);
        }

        public academicnoticeListRecyclerViewAdapter() {
            getNoticeCall();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
            NoticeViewHolder holder = new NoticeViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((NoticeViewHolder) holder).tvTitle.setText(noticeList.get(position).getTitle());
            ((NoticeViewHolder) holder).tvContents.setText(noticeList.get(position).getContents());
            ((NoticeViewHolder) holder).tvDate.setText(noticeList.get(position).getDate());
            ((NoticeViewHolder) holder).tvType.setText(noticeList.get(position).getType());
        }

        @Override
        public int getItemCount() {
            return noticeList.size();
        }

        private class NoticeViewHolder extends RecyclerView.ViewHolder {
            public TextView tvTitle;
            public TextView tvContents;
            public TextView tvDate;
            public TextView tvType;

            public NoticeViewHolder(View view) {
                super(view);
                tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                tvContents = (TextView) view.findViewById(R.id.tvContents);
                tvDate = (TextView) view.findViewById(R.id.tvDate);
                tvType = (TextView) view.findViewById(R.id.tvType);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent FacilityNoticeActivity = new Intent(getApplicationContext(), FacilityNoticeActivity.class);
                        FacilityNoticeActivity.putExtra("num", noticeList.get(getAdapterPosition()).getNum());
                        FacilityNoticeActivity.putExtra("title", noticeList.get(getAdapterPosition()).getTitle());
                        FacilityNoticeActivity.putExtra("date", noticeList.get(getAdapterPosition()).getDate());
                        FacilityNoticeActivity.putExtra("contents", noticeList.get(getAdapterPosition()).getContents());

                        startActivity(FacilityNoticeActivity);
                    }
                });
            }
        }
        private class getNotice extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                StringTokenizer st = new StringTokenizer(result, "$");

                System.out.println("시설공지 출력 : " + result);
                while (st.hasMoreTokens()) {
                    String[] noticeLine = st.nextToken().split("!@# ");
                    Notice notice = new Notice(noticeLine[0], noticeLine[1], noticeLine[2], noticeLine[3], noticeLine[4].replace("</br>", "\n"));
                    noticeList.add(notice);
                }
                notifyDataSetChanged();
            }

            @Override
            protected String doInBackground(String... params) {
                String serverURL = params[0];

                String postParameters = "type=" + type;
                return PHPCommon.sendPHP(serverURL, postParameters);
            }
        }
    }
}
