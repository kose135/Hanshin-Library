package com.example.hanshinlibrary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hanshinlibrary.Model.Notice;
import com.example.hanshinlibrary.Model.Suggestion;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SuggestionListActivity extends AppCompatActivity {
    private SwipeRefreshLayout suggestionListSwipeRefreshLayout;
    private RecyclerView suggestionListRecyclerView;
    private suggestionRecyclerViewAdapter suggestionRecyclerViewAdapter;

    private ArrayList<Suggestion> suggestionList = new ArrayList<>();
    private Button btnRegister;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_list);

        suggestionRecyclerViewAdapter = new suggestionRecyclerViewAdapter();

        suggestionListRecyclerView = (RecyclerView) findViewById(R.id.suggestionListRecyclerView);
        suggestionListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        suggestionListRecyclerView.setAdapter(suggestionRecyclerViewAdapter);

        suggestionListSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.suggestionListSwipeRefreshLayout);
        suggestionListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                suggestionRecyclerViewAdapter.getSuggestionCall();
                suggestionListSwipeRefreshLayout.setRefreshing(false);
            }
        });


        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SuggestionWriteActivity.class);
                startActivityForResult(intent, 1);
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

    private class suggestionRecyclerViewAdapter extends RecyclerView.Adapter {
        public void getSuggestionCall() {
            suggestionList.clear();
            getSuggestion task = new getSuggestion();
            task.execute(PHPCommon.phpAddress("getSuggestion"));
        }

        public suggestionRecyclerViewAdapter() {
            getSuggestionCall();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
            SuggestionViewHolder holder = new SuggestionViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((SuggestionViewHolder) holder).tvTitle.setText(suggestionList.get(position).getTitle());
            ((SuggestionViewHolder) holder).tvType.setText(suggestionList.get(position).getType());
            ((SuggestionViewHolder) holder).tvContents.setText(suggestionList.get(position).getContents());
            ((SuggestionViewHolder) holder).tvDate.setText(suggestionList.get(position).getDate());
            ((SuggestionViewHolder) holder).tvWriter.setText(suggestionList.get(position).getWriter());
        }

        @Override
        public int getItemCount() {
            return suggestionList.size();
        }

        private class SuggestionViewHolder extends RecyclerView.ViewHolder {
            public TextView tvTitle;
            public TextView tvType;
            public TextView tvContents;
            public TextView tvDate;
            public TextView tvWriter;

            public SuggestionViewHolder(View view) {
                super(view);
                tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                tvType = (TextView) view.findViewById(R.id.tvType);
                tvContents = (TextView) view.findViewById(R.id.tvContents);
                tvDate = (TextView) view.findViewById(R.id.tvDate);
                tvWriter = (TextView) view.findViewById(R.id.tvWriter);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent NoticeActivity = new Intent(getApplicationContext(), SuggestionActivity.class);
                        NoticeActivity.putExtra("num", suggestionList.get(getAdapterPosition()).getNum());
                        NoticeActivity.putExtra("type", suggestionList.get(getAdapterPosition()).getType());
                        NoticeActivity.putExtra("title", suggestionList.get(getAdapterPosition()).getTitle());
                        NoticeActivity.putExtra("writer", suggestionList.get(getAdapterPosition()).getWriter());
                        NoticeActivity.putExtra("date", suggestionList.get(getAdapterPosition()).getDate());
                        NoticeActivity.putExtra("contents", suggestionList.get(getAdapterPosition()).getContents());

                        startActivity(NoticeActivity);
                    }
                });
            }
        }

        private class getSuggestion extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                StringTokenizer st = new StringTokenizer(result, "-");

                System.out.println("건의사항 출력 : " + result);
                while (st.hasMoreTokens()) {
                    String[] suggestionLine = st.nextToken().split(", ");
                    Suggestion suggestion = new Suggestion(suggestionLine[0], suggestionLine[1], suggestionLine[2], suggestionLine[3], suggestionLine[4], suggestionLine[5].replace("</br>", "\n"));
                    suggestionList.add(suggestion);
                }
                notifyDataSetChanged();
            }

            @Override
            protected String doInBackground(String... params) {
                String serverURL = params[0];

                String postParameters = "";
                return PHPCommon.sendPHP(serverURL, postParameters);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            suggestionRecyclerViewAdapter.getSuggestionCall();
        }
    }
}