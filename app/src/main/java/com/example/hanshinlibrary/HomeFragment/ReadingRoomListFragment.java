package com.example.hanshinlibrary.HomeFragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hanshinlibrary.LaptopRoom1Activity;
import com.example.hanshinlibrary.Model.Profile;
import com.example.hanshinlibrary.Model.ReadingRoom;
import com.example.hanshinlibrary.PHPCommon;
import com.example.hanshinlibrary.R;
import com.example.hanshinlibrary.ReadingRoom1Activity;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class ReadingRoomListFragment extends Fragment {
    private SwipeRefreshLayout readingroomSwipeRefreshLayout;
    private RecyclerView readingroomRecyclerView;

    private ArrayList<ReadingRoom> readingroomList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_readingroom_list, container, false);


        readingroomSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.readingroomSwipeRefreshLayout);
        readingroomSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readingroomRecyclerView.setAdapter(new ReadingRoomRecyclerViewAdapter());
                readingroomSwipeRefreshLayout.setRefreshing(false);
            }
        });

        readingroomRecyclerView = (RecyclerView) view.findViewById(R.id.readingroomRecyclerView);
        readingroomRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        readingroomRecyclerView.setAdapter(new ReadingRoomRecyclerViewAdapter());

        return view;
    }

    private class ReadingRoomRecyclerViewAdapter extends RecyclerView.Adapter {

        public ReadingRoomRecyclerViewAdapter() {
            getReadingRoom getReadingRoom = new getReadingRoom();
            getReadingRoom.execute(PHPCommon.phpAddress("getReadingRoom"), "");
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_readingroom, parent, false);
            ReadingRoomViewHolder holder = new ReadingRoomViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((ReadingRoomViewHolder) holder).tvName.setText(readingroomList.get(position).getName());
            ((ReadingRoomViewHolder) holder).tvTotal.setText("전체 : "  + readingroomList.get(position).getTotal());
            ((ReadingRoomViewHolder) holder).tvUsing.setText("사용중 : "  + readingroomList.get(position).getUsing());
            ((ReadingRoomViewHolder) holder).tvReservation.setText("예약중 : "  + readingroomList.get(position).getReservation());
            ((ReadingRoomViewHolder) holder).tvResidual.setText("잔여 : "  + readingroomList.get(position).getResidual());
        }

        @Override
        public int getItemCount() {
            return readingroomList.size();
        }

        private class ReadingRoomViewHolder extends  RecyclerView.ViewHolder{
            public TextView tvName;
            public TextView tvTotal;
            public TextView tvUsing;
            public TextView tvReservation;
            public TextView tvResidual;

            public ReadingRoomViewHolder(@NonNull final View itemView) {
                super(itemView);
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvTotal = (TextView) itemView.findViewById(R.id.tvTotal);
                tvUsing = (TextView) itemView.findViewById(R.id.tvUsing);
                tvReservation = (TextView) itemView.findViewById(R.id.tvReservation);
                tvResidual = (TextView) itemView.findViewById(R.id.tvResidual);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (readingroomList.get(getAdapterPosition()).getName()) {
                            case "노트북열람실1":
                                startActivity(new Intent(getActivity(), LaptopRoom1Activity.class));
                                break;
                            case "일반열람실1":
                                startActivity(new Intent(getActivity(), ReadingRoom1Activity.class));
                                break;
                            default:
                                Toast.makeText(getActivity(), "선택한 열람실은 사용할 수 없습니다.\n다른 열람실을 이용하시길 바랍니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        private class getReadingRoom extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                readingroomList.clear();

                StringTokenizer st = new StringTokenizer(result, ",");
                while (st.hasMoreTokens()) {
                    String[] line = st.nextToken().split(" ");
                    readingroomList.add(new ReadingRoom(line[0], line[1], line[2], line[3], line[4]));
                }
                notifyDataSetChanged();
            }

            @Override
            protected String doInBackground(String... params) {
                String serverURL = params[0];
                String postParameters = params[1];

                return PHPCommon.sendPHP(serverURL, postParameters);
            }
        }
    }
}
