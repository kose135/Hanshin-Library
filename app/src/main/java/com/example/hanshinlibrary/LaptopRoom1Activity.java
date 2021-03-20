package com.example.hanshinlibrary;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.hanshinlibrary.Model.Profile;
import com.example.hanshinlibrary.Model.Seat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

public class LaptopRoom1Activity extends AppCompatActivity implements LocationListener {
    private ImageButton btnBack;
    private ImageView ivExclamation;
    private ImageButton btnScan;
    private ImageButton btnRefresh;
    private FloatingActionButton btnMap;
    private Button btn;

    private IntentIntegrator qrScan;
    private ReadingRoomDialog ReadingRoomDialog;
    private getSeat getSeat;
    private updateSeat updateSeat;

    private HashMap<Integer, Seat> seatHash = new HashMap<Integer, Seat>();
    private String roomName = "노트북열람실1";
    private AlertDialog.Builder seatBuilder;

    private LocationManager locationManager;

    private Double hsLibraryLatitude = 37.1940466;
    private Double hsLibraryLongitude = 127.023961;
    private int MIN_DISTANCE = 100;
    private int time = 10;
    private int gpsAlramID = 1;
    private boolean alram = false;
    float oldXvalue;
    float oldYvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laptoproom1);
        //좌석 상태를 불러오기
        getSeatCall();

        System.out.println("좌석 " + Profile.sit);
        if (!Profile.sit.isEmpty()) {
            Profile.sit = "";
        }

        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeNotification(gpsAlramID);
            }
        });

        //카메라로 가기위한 인텐트 생성
        qrScan = new IntentIntegrator(this);
        //카메라를 세로로 고정하기 위한 코드
        qrScan.setOrientationLocked(false);

        ivExclamation = (ImageView) findViewById(R.id.ivExclamation);
        ivExclamation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadingRoomDialog = new ReadingRoomDialog(LaptopRoom1Activity.this);
                ReadingRoomDialog.setCancelable(true);
                ReadingRoomDialog.show();
            }
        });

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnScan = (ImageButton) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.setPrompt("좌석의 QR코드를 스캔 해주세요");
                qrScan.initiateScan();
            }
        });

        btnRefresh = (ImageButton) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                removeNotification(gpsAlramID);
                getSeatCall();
            }
        });

        btnMap = (FloatingActionButton) findViewById(R.id.btnMap);
        btnMap.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    v.setX(event.getRawX() - oldXvalue);
                    v.setY(event.getRawY() - (oldYvalue + v.getHeight()));
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Toast.makeText(getApplicationContext(), "지도화면으로 전환 하실려면 버튼을 오래 눌러주세요", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        btnMap.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                return false;
            }
        });


    }



    // .excute는 객체가 생성되고 한번만 실행이 가능하기때문에 새로고침할때마다 계속 생성후에 .excute를 해줘야한다.
    public void getSeatCall() {
        getSeat = new getSeat();
        getSeat.execute(PHPCommon.phpAddress("getSeatData"), roomName);
    }

    // .excute는 객체가 생성되고 한번만 실행이 가능하기때문에 새로고침할때마다 계속 생성후에 .excute를 해줘야한다.
    public void getUpdateSeatCall(String roomName, String num, String state, String date, String id) {
        updateSeat = new updateSeat();
        updateSeat.execute(PHPCommon.phpAddress("updateSeat"), roomName, num, state, date, id);
    }

    // 좌석 버튼을 눌럿을때 생기는 경우의 수들
    public void sitting(final View view) {
        getSeatCall();
        seatBuilder = new AlertDialog.Builder(LaptopRoom1Activity.this);
        final Seat seat = seatHash.get(view.getId());

        //1. 비어있음 상태 (예약 가능으로 상태변경 가능)
        if (seat.getState().equals("비어있음")) {
            // 1-1 자신이 이미 다른 좌석을 예약한 경우
            if ((Profile.sit.contains("예약중") || Profile.sit.contains("사용중")) /* && !Profile.sit.isEmpty() */) {
                String reservedSeat[] = Profile.sit.split(",");
                seatBuilder.setTitle("이미 지정된 좌석이 있습니다.");
                seatBuilder.setMessage(reservedSeat[1] + "에 지정 되어있습니다.");
                seatBuilder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
            }
            // 1-2 자신이 처음으로 좌석을 예약할 경우
            else {
                seatBuilder.setTitle("예약하시겠습니까?");
                seatBuilder.setMessage(seat.getRoomName() + " " + seat.getNum() + "\n15분이내에 예약한 좌석으로 착석 안할 경우에는 예약이 취소 됩니다.");
                seatBuilder.setPositiveButton("예약",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Calendar calendar = Calendar.getInstance();
                                // 1000(milli) - 5초 원래대로면 15분으로 해야하나 테스트로 시간을 5초 후에
                                calendar.setTimeInMillis(System.currentTimeMillis() + 10000);
                                Date currentDateTime = calendar.getTime();
                                String time = new SimpleDateFormat("MM월dd일hh시mm분ss초", Locale.getDefault()).format(currentDateTime);

                                getUpdateSeatCall(seat.getRoomName(), seat.getNum(), "예약중", time, Profile.student_ID);

                                Toast.makeText(getApplicationContext(), time + " 이전까지 \n착석해 주시길 바랍니다.", Toast.LENGTH_SHORT).show();

                                //  Preference에 설정한 값 저장
                                // SharedPreferences.Editor 은 어플 종료해도 알람 시간을 다시 불러 올 수 있도록하는 1회성으로 저장할 수 있음
                                SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
                                editor.putLong("NotifyTime", (long) calendar.getTimeInMillis());
                                editor.apply();

                                String mySeat = "예약중, " + seat.getRoomName() + " " + seat.getNum();
                                Profile.sit = mySeat;

                                SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
                                SharedPreferences.Editor profileEditor = pref.edit();
                                profileEditor.putString("sit", mySeat);
                                profileEditor.commit();

                                setNotification(view.getId(), seat.getRoomName(), seat.getNum(), calendar.getTimeInMillis());

                                getSeatCall();
                            }
                        });
                seatBuilder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getSeatCall();
                            }
                        });
            }
            seatBuilder.show();
        }
        //2. 사용중 상태 (사용자의 ID의 따라 상태 변경)
        else if (seat.getState().equals("사용중")) {
            //2-1 사용중인 좌석에 등록된 학생 아이디와 자신의 아이디가 같으면 예약좌석 취소
            if (seat.getStudent_ID().equals(Profile.student_ID)) {
                seatBuilder.setTitle("현재 사용중인 좌석을 비우시겠습니까?");
                seatBuilder.setMessage("비우신다면 '예'를 눌러주세요.");
                seatBuilder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getUpdateSeatCall(seat.getRoomName(), seat.getNum(), "비어있음", "없음", "없음");
                                getSeatCall();
                                removeNotification(gpsAlramID);
                                btnMap.setVisibility(View.GONE);
                                Profile.sit = "";
                            }
                        });
                seatBuilder.setNegativeButton("아니요",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                seatBuilder.show();
            }
            //2-2 사용중인 좌석에 등록된 학생 아이디와 자신의 아이디가 다르면 다른좌석 이용 안내
            else {
                seatBuilder.setTitle("현재 좌석은 다른사람이 사용중입니다.");
                seatBuilder.setMessage("다른좌석을 이용해주시길 바랍니다.");
                seatBuilder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                seatBuilder.show();
            }
        }
        //3. 예약중 상태 (사용자의 ID의 따라 상태 변경)
        else if (seat.getState().equals("예약중")) {
            //3-1 예약중인 좌석에 등록된 학생 아이디와 자신의 아이디가 같으면 예약좌석 취소
            if (seat.getStudent_ID().equals(Profile.student_ID)) {
                seatBuilder.setTitle("현재 예약 좌석을 취소하시겠습니까?");
                seatBuilder.setMessage("예약 취소를 원하신다면 '예'를 눌러주세요.");
                seatBuilder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getUpdateSeatCall(seat.getRoomName(), seat.getNum(), "비어있음", "없음", "없음");
                                getSeatCall();
                                onPauseNotification(view.getId());
                                Profile.sit = "";
                            }
                        });
                seatBuilder.setNegativeButton("아니요",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                seatBuilder.show();
            }
            //3-2 예약중인 좌석에 등록된 학생 아이디와 자신의 아이디가 다르면 다른좌석 이용 안내
            else {
                seatBuilder.setTitle("현재 좌석은 다른사람이 예약중입니다.");
                seatBuilder.setMessage("다른좌석을 이용해주시길 바랍니다.");
                seatBuilder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                seatBuilder.show();
            }
        }
        //4. 그 외의 상태 (사용 불가능한 좌석)
        else {
            seatBuilder.setTitle("현재 좌석은 이용할 수 없습니다.");
            seatBuilder.setMessage("다른좌석을 이용해주시길 바랍니다.");
            seatBuilder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            seatBuilder.show();
        }
    }

    //좌석을 스캔햇을 경우의 생기는 경우
    public void isAbleSeat(final int seatID) {
        seatBuilder = new AlertDialog.Builder(LaptopRoom1Activity.this);

        //seatID가 seatHash 안에 있으면 현재 열람실의 QR코드이므로 좌석의 상태 변경 가능
        if (seatHash.containsKey(seatID)) {
            final Seat seat = seatHash.get(seatID);
            //1. 비어있는 상태 (사용중으로 상태변경 가능)
            if (seat.getState().equals("비어있음")) {
                if ((Profile.sit.contains("예약중") && !seat.getStudent_ID().equals(Profile.student_ID)) || (Profile.sit.contains("사용중") && !seat.getStudent_ID().equals(Profile.student_ID))) {
                    String reservedSeat[] = Profile.sit.split(", ");
                    seatBuilder.setTitle("이미 지정된 좌석이 있습니다.");
                    seatBuilder.setMessage(reservedSeat[1] + "에 지정 되어있습니다.");
                    seatBuilder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                } else {
                    seatBuilder.setTitle("현재좌석을 이용하시겠습니까?");
                    seatBuilder.setMessage("이용하신다면 '예'를 눌러주세요.");
                    seatBuilder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Date currentDateTime = Calendar.getInstance().getTime();
                                    String time = new SimpleDateFormat("MM월dd일hh시mm분", Locale.getDefault()).format(currentDateTime);
                                    getUpdateSeatCall(seat.getRoomName(), seat.getNum(), "사용중", time, Profile.student_ID);
                                    getSeatCall();

                                    String mySeat = "사용중, " + seat.getRoomName() + " " + seat.getNum();
                                    Profile.sit = mySeat;

                                    // GPS 권한 체크
                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        return;
                                    }
                                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                        System.out.println("실패");
                                    } else {
                                        System.out.println("성공");
                                        Toast.makeText(getApplicationContext(), "도서관으로부터 " + MIN_DISTANCE + "m 벗어나면 사용중인 좌석이 취소됩니다.", Toast.LENGTH_SHORT).show();
                                        btnMap.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                    seatBuilder.setNegativeButton("아니요",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                }
                seatBuilder.show();
            }
            //2. 사용중인 상태
            else if (seat.getState().equals("사용중")) {
                //2-1. 좌석에 등록된 ID와 사용자의 ID가 일치하면 비어있음 상태로 변경 가능
                if (seat.getStudent_ID().equals(Profile.student_ID)) {
                    seatBuilder.setTitle("현재 좌석을 비우시겠습니까?");
                    seatBuilder.setMessage("비우신다면 '예'를 눌러주세요.");
                    seatBuilder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    getUpdateSeatCall(seat.getRoomName(), seat.getNum(), "비어있음", "없음", "없음");
                                    getSeatCall();
                                    removeNotification(gpsAlramID);
                                    Profile.sit = "";
                                    btnMap.setVisibility(View.GONE);
                                }
                            });
                    seatBuilder.setNegativeButton("아니요",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    seatBuilder.show();
                }
                //2-1. 좌석에 등록된 ID와 사용자의 ID가 다르면 다른좌석으로 안내
                else {
                    seatBuilder.setTitle("현재 좌석은 사용중입니다.");
                    seatBuilder.setMessage("다른좌석을 이용해주시길 바랍니다.");
                    seatBuilder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    seatBuilder.show();
                }
            }
            //3. 예약중 상태 (사용자의 ID의 따라 상태 변경)
            else if (seat.getState().equals("예약중")) {
                //3-1 예약중인 좌석에 등록된 학생 아이디와 자신의 아이디가 같으면 사용중으로 상태 변경
                if (seat.getStudent_ID().equals(Profile.student_ID)) {
                    seatBuilder.setTitle("현재좌석을 이용하시겠습니까?");
                    seatBuilder.setMessage("이용하신다면 '예'를 눌러주세요.");
                    seatBuilder.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Date currentDateTime = Calendar.getInstance().getTime();
                                    String time = new SimpleDateFormat("MM월dd일hh시mm분", Locale.getDefault()).format(currentDateTime);
                                    getUpdateSeatCall(seat.getRoomName(), seat.getNum(), "사용중", time, Profile.student_ID);
                                    getSeatCall();

                                    onPauseNotification(seatID);

                                    String mySeat = "사용중, " + seat.getRoomName() + " " + seat.getNum();
                                    Profile.sit = mySeat;

                                    // GPS 권한 체크
                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        return;
                                    }
                                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                        System.out.println("실패");
                                    } else {
                                        Toast.makeText(getApplicationContext(), "도서관으로부터 " + MIN_DISTANCE + "m 벗어나면 사용중인 좌석이 취소됩니다.", Toast.LENGTH_SHORT).show();
                                        btnMap.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                    seatBuilder.setNegativeButton("아니요",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    seatBuilder.show();
                }
                //3-2 예약중인 좌석에 등록된 학생 아이디와 자신의 아이디가 다르면 다른좌석 이용 안내
                else {
                    seatBuilder.setTitle("현재 좌석은 다른사람이 예약중입니다.");
                    seatBuilder.setMessage("다른좌석을 이용해주시길 바랍니다.");
                    seatBuilder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    seatBuilder.show();
                }
            }
            //4. 그 외의 상태 (사용 불가능한 좌석)
            else {
                seatBuilder.setTitle("현재 좌석은 이용할 수 없습니다.");
                seatBuilder.setMessage("다른좌석을 이용해주시길 바랍니다.");
                seatBuilder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                seatBuilder.show();
            }
        }
        // seatID가 seatHash 안에 없으면 다른 열람실의 QR코드 혹은 잘못된 값
        else {
            Toast.makeText(LaptopRoom1Activity.this, "잘못된 QR코드입니다. 다시 스캔하시오.", Toast.LENGTH_LONG).show();
        }
    }

    void setNotification(int id, String room_name, String num, long time) {
        //       System.out.println("time : " + time);
        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);

        Intent alarmIntent = new Intent(this, ReservationAlarmReceiver.class);
        /*송신*/
        alarmIntent.putExtra("id", id);
        alarmIntent.putExtra("room_name", room_name);
        alarmIntent.putExtra("num", num);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 알람 설정 세팅
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
        // 부팅 후 실행되는 리시버 사용가능하게 설정
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    // 스캔한 정보를 가지고 있는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult QRScanresult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // QRScanresult가 스캔한 정보를 가지고 있음
        if (QRScanresult != null) {
            if (QRScanresult.getContents() == null) {
                Toast.makeText(this, "취소", Toast.LENGTH_SHORT).show();
            } else {
                String[] SeatData = QRScanresult.getContents().split(" ");
                int changeSeatId = getResources().getIdentifier(SeatData[0] + "_" + SeatData[1], "id", getPackageName());
                isAbleSeat(changeSeatId);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class getSeat extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Button seatButton;
            StringTokenizer st = new StringTokenizer(result, ",");

            seatHash.clear();
            while (st.hasMoreTokens()) {
                String[] seatData = st.nextToken().split(" ");

                int seatId = getResources().getIdentifier(seatData[0] + "_" + seatData[1], "id", getPackageName());
                seatHash.put(seatId, new Seat(seatData[0], seatData[1], seatData[2], seatData[3], seatData[4]));

                seatButton = (Button) findViewById(seatId);
                if (seatData[2].equals("비어있음")) {
                    seatButton.setBackgroundColor(Color.BLUE);
                } else if (seatData[2].equals("사용중")) {
                    seatButton.setBackgroundColor(Color.GRAY);
                } else if (seatData[2].equals("예약중")) {
                    seatButton.setBackgroundColor(Color.YELLOW);
                } else {
                    seatButton.setBackgroundColor(Color.RED);
                }
            }

            Log.d("getSeat : ", "response - " + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String room_name = params[1];

            String postParameters = "&room_name=" + room_name;
            return PHPCommon.sendPHP(serverURL, postParameters);
        }
    }

    public class updateSeat extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("updateSeatData : ", "POST response  - " + result);
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

    void createNotification(int id, String sit) {
        //조건 충족시 바로 알람
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("지정된 좌석이 취소됩니다.");
        builder.setContentText(time + "초 안에 도서관으로 복귀 안하면 지정한 좌석이 취소됩니다.");
        builder.setColor(Color.BLUE);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(id, builder.build());

        // 일정 시간 뒤에 알림
        Intent alarmIntent = new Intent(this, GPSAlarmReceiver.class);
        /*송신*/
        alarmIntent.putExtra("id", id + 1);
        alarmIntent.putExtra("sit", sit);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id + 1, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 알람 설정 세팅
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time * 1000, pendingIntent);
        }

    }

    public void removeNotification(int gpsAlramID) {
        // Notification 제거
        //NotificationManagerCompat.from(this).cancel(gpsAlramID);

        Intent alarmIntent = new Intent(this, GPSAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, gpsAlramID + 1, alarmIntent, 0);
        AlarmManager cancleAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        cancleAlarmManager.cancel(pendingIntent);
        locationManager.removeUpdates(this);
    }

    public void onPauseNotification(int id) {
        Intent alarmIntent = new Intent(this, ReservationAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, alarmIntent, 0);
        AlarmManager cancleAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        cancleAlarmManager.cancel(pendingIntent);
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        System.out.println("GPS 시작");
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = 0.0;
        double longitude = 0.0;
        double distance;

        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            distance = getDistance(latitude, longitude);

            System.out.println("거리 " + distance + "m 입니다.");

            if (distance > MIN_DISTANCE && !alram) {
                alram = true;
                Toast.makeText(getApplicationContext(), "경삼관으로부터 거리 " + MIN_DISTANCE + "m 이상입니다.", Toast.LENGTH_SHORT).show();
                createNotification(gpsAlramID, Profile.sit);
            } else if (distance < MIN_DISTANCE && alram) {
                alram = false;
                Toast.makeText(getApplicationContext(), "경삼관으로부터 거리 " + MIN_DISTANCE + "m 이하입니다.", Toast.LENGTH_SHORT).show();
                removeNotification(gpsAlramID);
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getApplicationContext(), "현재 위치를 파악할 수 없는 장소 입니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }
    }

    public Double getDistance(double latitude, double longitude) {
        double distance;

        Location location = new Location("Current location");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        Location Library = new Location("Hanshin Library");
        Library.setLatitude(hsLibraryLatitude);
        Library.setLongitude(hsLibraryLongitude);

        distance = location.distanceTo(Library);

        return distance;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null)
            locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }
}
