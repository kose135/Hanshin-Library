package com.example.hanshinlibrary;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.hanshinlibrary.HomeFragment.ReadingRoomListFragment;
import com.example.hanshinlibrary.Model.Profile;

import static android.content.Context.MODE_PRIVATE;

public class ReservationAlarmReceiver extends BroadcastReceiver {
    public static int id;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        id = intent.getIntExtra("id", 0);
        String room_name = intent.getStringExtra("room_name");
        String num = intent.getStringExtra("num");

//        System.out.println("알람 리시버 수신 " + id + " " + room_name + " " + num);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, SplashActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");

        //OREO API 26 이상에서는 채널 필요
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남

            String channelName = "도서관 열람실 알람";
            String description = room_name + " " + num;
            int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel("default", channelName, importance);
            channel.setDescription(description);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }
        } else
            builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("{Time to watch some cool stuff!}")
                .setContentTitle("예약한 좌석 시간이 지났습니다.")
                .setContentText(room_name + " " + num + "좌석으로 지정한 예약이 취소됩니다.")
                .setContentInfo("INFO")
                .setContentIntent(pendingIntent);

        Profile.sit = "";

        if (notificationManager != null) {
            // 노티피케이션 동작시킴
            notificationManager.notify(id, builder.build());

            UpdateSeatData updateSeat = new UpdateSeatData();
            updateSeat.execute(PHPCommon.phpAddress("updateSeat"), room_name, num, "비어있음", "없음", "없음");
        }
    }

    public class UpdateSeatData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("respon - ", "POST response  - " + result);

            SharedPreferences.Editor profileEditor =  context.getSharedPreferences("Login", MODE_PRIVATE).edit();
            profileEditor.putString("sit", "");
            profileEditor.commit();

            Profile.sit = "";
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String) params[0];
            String room_name = (String) params[1];
            String num = (String) params[2];
            String state = (String) params[3];
            String date = (String) params[4];
            String student_ID = (String) params[5];

            String postParameters = "&room_name=" + room_name + "&num=" + num + "&state=" + state + "&date=" + date + "&student_ID=" + student_ID;
            return PHPCommon.sendPHP(serverURL, postParameters);
        }
    }
}

