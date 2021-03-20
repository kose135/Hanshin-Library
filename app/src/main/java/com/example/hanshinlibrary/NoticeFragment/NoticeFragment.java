package com.example.hanshinlibrary.NoticeFragment;

import android.content.Intent;
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

import com.example.hanshinlibrary.Model.Notice;
import com.example.hanshinlibrary.NoticeActivity;
import com.example.hanshinlibrary.NoticeAllListActivity;
import com.example.hanshinlibrary.PHPCommon;
import com.example.hanshinlibrary.R;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class NoticeFragment extends Fragment {
    private LinearLayout layoutMore;
    private RecyclerView noticeRecyclerView;
    private SwipeRefreshLayout noticeSwipeRefreshLayout;
    private noticeRecyclerViewAdapter noticeRecyclerViewAdapter;

    private ArrayList<Notice> noticeList = new ArrayList<>();
    private String type = "공지사항";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_notice, container, false);

        layoutMore = (LinearLayout) view.findViewById(R.id.layoutMore);
        layoutMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NoticeAllListActivity.class));
            }
        });

        noticeRecyclerViewAdapter = new noticeRecyclerViewAdapter();

        noticeRecyclerView = (RecyclerView) view.findViewById(R.id.noticeRecyclerView);
        noticeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        noticeRecyclerView.setAdapter(noticeRecyclerViewAdapter);

        noticeSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.noticeSwipeRefreshLayout);
        noticeSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                noticeRecyclerViewAdapter.getNoticeCall();
                noticeSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private class noticeRecyclerViewAdapter extends RecyclerView.Adapter {

        public void getNoticeCall(){
            noticeList.clear();
            getNotice task = new getNotice();
            task.execute(PHPCommon.phpAddress("getNoticeLast10"), type);
        }

        public noticeRecyclerViewAdapter() {
            getNoticeCall();
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_oneline_notice, parent, false);
            NoticeViewHolder holder = new NoticeViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((NoticeViewHolder) holder).tvTitle.setText(noticeList.get(position).getTitle());
            ((NoticeViewHolder) holder).tvDate.setText(noticeList.get(position).getDate());
        }

        @Override
        public int getItemCount() {
            return noticeList.size();
        }

        private class NoticeViewHolder extends RecyclerView.ViewHolder {
            public TextView tvTitle;
            public TextView tvDate;

            public NoticeViewHolder(View view) {
                super(view);
                tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                tvDate = (TextView) view.findViewById(R.id.tvDate);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent NoticeActivity = new Intent(getActivity(), NoticeActivity.class);
                        NoticeActivity.putExtra("num", noticeList.get(getAdapterPosition()).getNum());
                        NoticeActivity.putExtra("title", noticeList.get(getAdapterPosition()).getTitle());
                        NoticeActivity.putExtra("date", noticeList.get(getAdapterPosition()).getDate());
                        NoticeActivity.putExtra("contents", noticeList.get(getAdapterPosition()).getContents());

                        startActivity(NoticeActivity);
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

                System.out.println("공지사항 출력 : " + result);
                StringTokenizer st = new StringTokenizer(result, "$");

                while (st.hasMoreTokens()) {
                    String[] noticeLine = st.nextToken().split("!@# ");
                    System.out.println(noticeLine[0] + "-" + noticeLine[1] + "-" + noticeLine[2] + "-" + noticeLine[3] + "-" + noticeLine[4]);
                    Notice notice = new Notice(noticeLine[0], noticeLine[1], noticeLine[2], noticeLine[3], noticeLine[4]);
                    noticeList.add(notice);
                }
                notifyDataSetChanged();
            }

            @Override
            protected String doInBackground(String... params) {
                String serverURL = params[0];
                String type = params[1];

                String postParameters = "type=" + type;
                return PHPCommon.sendPHP(serverURL, postParameters);
            }
        }
    }

}
