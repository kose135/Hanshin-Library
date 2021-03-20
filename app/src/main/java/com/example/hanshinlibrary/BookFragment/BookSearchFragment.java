package com.example.hanshinlibrary.BookFragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hanshinlibrary.Model.Book;
import com.example.hanshinlibrary.PHPCommon;
import com.example.hanshinlibrary.R;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class BookSearchFragment extends Fragment {
    private Spinner searchTypeSpinner;
    private EditText etSearch;
    private ImageButton btnSearch;
    private SwipeRefreshLayout BookSearchSwipeRefreshLayout;
    private RecyclerView bookSearchRecyclerView;
    private BookRecyclerViewAdapter bookRecyclerViewAdapter;

    private String searchType;
    private ArrayList<Book> bookList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search_book, container, false);

        etSearch = (EditText) view.findViewById(R.id.etSearch);

        bookRecyclerViewAdapter = new BookRecyclerViewAdapter();

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        bookSearchRecyclerView = (RecyclerView) view.findViewById(R.id.bookSearchRecyclerView);
        bookSearchRecyclerView.setLayoutManager(mLayoutManager);
        bookSearchRecyclerView.setAdapter(bookRecyclerViewAdapter);

        searchTypeSpinner = (Spinner) view.findViewById(R.id.searchTypeSpinner);
        searchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        searchType = "title";
                        break;
                    case 1:
                        searchType = "author";
                        break;
                    case 2:
                        searchType = "publisher";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSearch = (ImageButton) view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookRecyclerViewAdapter.getBookCall(searchType, etSearch.getText().toString());
            }
        });

        BookSearchSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.BookSearchSwipeRefreshLayout);
        BookSearchSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bookRecyclerViewAdapter.getBookCall(searchType, etSearch.getText().toString());
                BookSearchSwipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    private class BookRecyclerViewAdapter extends RecyclerView.Adapter {

        public void getBookCall(String type, String searchText) {
            bookList.clear();
            getBook getBook = new getBook();
            getBook.execute(PHPCommon.phpAddress("searchBook"), type, searchText);
        }

        public BookRecyclerViewAdapter() {
            getBookCall("", "");
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
            BookViewHolder holder = new BookViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((BookViewHolder) holder).tvTitle.setText(bookList.get(position).getTitle());
            ((BookViewHolder) holder).tvAuthor.setText(bookList.get(position).getAuthor());
            ((BookViewHolder) holder).tvID.setText(bookList.get(position).getId());
            ((BookViewHolder) holder).tvPublisher.setText(bookList.get(position).getPublisher());

            ((BookViewHolder) holder).tvState.setText(bookList.get(position).getState());
            if (bookList.get(position).getState().equals("대출가능")) {
                ((BookViewHolder) holder).tvState.setTextColor(Color.BLUE);
            } else if (bookList.get(position).getState().equals("대출중")) {
                ((BookViewHolder) holder).tvState.setTextColor(Color.GRAY);
            } else {
                ((BookViewHolder) holder).tvState.setTextColor(Color.RED);
            }
        }

        @Override
        public int getItemCount() {
            return bookList.size();
        }

        public class BookViewHolder extends RecyclerView.ViewHolder {
            public TextView tvTitle;
            public TextView tvAuthor;
            public TextView tvID;
            public TextView tvPublisher;
            public TextView tvState;

            public BookViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                tvAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
                tvID = (TextView) itemView.findViewById(R.id.tvID);
                tvPublisher = (TextView) itemView.findViewById(R.id.tvPublisher);
                tvState = (TextView) itemView.findViewById(R.id.tvState);
            }
        }

        public class getBook extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                System.out.println("도서 검색 : " + result);

                StringTokenizer st = new StringTokenizer(result, "-");
                while (st.hasMoreTokens()) {
                    String[] line = st.nextToken().split(", ");
                    Book book = new Book(line[0], line[1], line[2], line[3], line[4]);
                    bookList.add(book);
                }
                notifyDataSetChanged();
            }

            @Override
            protected String doInBackground(String... params) {
                String serverURL = params[0];
                String attribute = params[1];
                String searchText = params[2];

                String postParameters = "attribute=" + attribute + " &searchText=" + searchText;
                return PHPCommon.sendPHP(serverURL, postParameters);
            }
        }
    }
}


