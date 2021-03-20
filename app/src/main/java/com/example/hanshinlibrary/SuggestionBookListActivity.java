package com.example.hanshinlibrary;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hanshinlibrary.Model.SuggestionBook;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SuggestionBookListActivity extends AppCompatActivity {

    private SwipeRefreshLayout suggestionBookListSwipeRefreshLayout;
    private RecyclerView suggestionBookListRecyclerView;
    public suggestionBookRecyclerViewAdapter suggestionBookRecyclerViewAdapter;


    private ArrayList<SuggestionBook> suggestionBookList = new ArrayList<>();
    private Button btnRegister1;
    private ImageButton btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_book_list);

        suggestionBookRecyclerViewAdapter = new suggestionBookRecyclerViewAdapter();

        suggestionBookListRecyclerView = (RecyclerView)findViewById(R.id.suggestionBookListRecyclerView);
        suggestionBookListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        suggestionBookListRecyclerView.setAdapter(suggestionBookRecyclerViewAdapter);

        suggestionBookListSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.suggestionBookListSwipeRefreshLayout);
        suggestionBookListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                suggestionBookRecyclerViewAdapter.getSuggestionBookCall();
                suggestionBookListSwipeRefreshLayout.setRefreshing(false);
            }
        });

        /////////////////////////////////////////////////////////////////////////////////////////

        btnRegister1 = (Button)findViewById(R.id.btnRegister1);
        btnRegister1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), SuggestionBookWriteActivity.class);
                startActivityForResult(intent1, 1);
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////////

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    private class suggestionBookRecyclerViewAdapter extends RecyclerView.Adapter {
        public void getSuggestionBookCall() {
            suggestionBookList.clear();
            getSuggestionBook task = new getSuggestionBook();
            task.execute(PHPCommon.phpAddress("getSuggestionBook"));
        }

        public suggestionBookRecyclerViewAdapter() {
            getSuggestionBookCall();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestionbook, parent, false);
            SuggestionBookViewHolder holder = new SuggestionBookViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((SuggestionBookViewHolder) holder).tvTitle1.setText(suggestionBookList.get(position).getTitle1());
            ((SuggestionBookViewHolder) holder).tvType1.setText(suggestionBookList.get(position).getType1());
            ((SuggestionBookViewHolder) holder).tvContents1.setText(suggestionBookList.get(position).getContents1());
            ((SuggestionBookViewHolder) holder).tvDate1.setText(suggestionBookList.get(position).getDate1());
            ((SuggestionBookViewHolder) holder).tvWriter1.setText(suggestionBookList.get(position).getWriter1());
        }

        @Override
        public int getItemCount() {
            return suggestionBookList.size();
        }


        /////////////////////////////////////////////////////////////////////////////////////


        private class SuggestionBookViewHolder extends RecyclerView.ViewHolder {

            private TextView tvTitle1;
            private TextView tvType1;
            private TextView tvContents1;
            private TextView tvDate1;
            private TextView tvWriter1;

            public SuggestionBookViewHolder(View view) {
                super(view);
                tvTitle1 = (TextView) view.findViewById(R.id.tvTitle1);
                tvType1 = (TextView) view.findViewById(R.id.tvType1);
                tvContents1 = (TextView) view.findViewById(R.id.tvContents1);
                tvDate1 = (TextView) view.findViewById(R.id.tvDate1);
                tvWriter1 = (TextView) view.findViewById(R.id.tvWriter1);


                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent NoticeActivity = new Intent(getApplicationContext(), SuggestionBookActivity.class);
                        NoticeActivity.putExtra("num1", suggestionBookList.get(getAdapterPosition()).getNum1());
                        NoticeActivity.putExtra("type1", suggestionBookList.get(getAdapterPosition()).getType1());
                        NoticeActivity.putExtra("title1", suggestionBookList.get(getAdapterPosition()).getTitle1());
                        NoticeActivity.putExtra("writer1", suggestionBookList.get(getAdapterPosition()).getWriter1());
                        NoticeActivity.putExtra("date1", suggestionBookList.get(getAdapterPosition()).getDate1());
                        NoticeActivity.putExtra("contents1", suggestionBookList.get(getAdapterPosition()).getContents1());

                        startActivity(NoticeActivity);
                    }
                });
            }
        }


        ////////////////////////////////////////////////////////////////////////////////////////////////////

        private class getSuggestionBook extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result){
                super.onPostExecute(result);
                StringTokenizer st = new StringTokenizer(result, "-");

                System.out.println("희망 도서 신청 출력 : " + result);
                while(st.hasMoreTokens()){
                    String[] suggestionBookLine = st.nextToken().split(", ");
                    SuggestionBook suggestionBook = new SuggestionBook(suggestionBookLine[0], suggestionBookLine[1], suggestionBookLine[2], suggestionBookLine[3], suggestionBookLine[4], suggestionBookLine[5].replace("</br>", "\n") );
                    suggestionBookList.add(suggestionBook);
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

    //////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            suggestionBookRecyclerViewAdapter.getSuggestionBookCall();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
}