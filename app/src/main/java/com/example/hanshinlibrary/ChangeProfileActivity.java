package com.example.hanshinlibrary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanshinlibrary.Model.Profile;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChangeProfileActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private CircleImageView ivProfile;
    private TextView tvName;
    private TextView tvStudentID;
    private TextView tvEmail;
    private TextView tvMajor;

    private ImageButton ibChangeName;
    private ImageButton ibChangeEmail;
    private LinearLayout llChangePassword;

    private String name;
    private String email;

    private AlertDialog.Builder builder;

    private int PICK_FROM_ALBUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);


        ivProfile = (CircleImageView) findViewById(R.id.ivProfile);
        tvName = (TextView) findViewById(R.id.tvName);
        tvStudentID = (TextView) findViewById(R.id.tvStudentID);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvMajor = (TextView) findViewById(R.id.tvMajor);

        SharedPreferences pref = getSharedPreferences("ProfileImage", MODE_PRIVATE);
        String image = pref.getString("image", "");
        if (!image.equals("") || image != null)
            ivProfile.setImageBitmap(StringToBitMap(image));
        else
            ivProfile.setImageResource(R.drawable.person_icon);


        ibChangeName = (ImageButton) findViewById(R.id.ibChangeName);
        ibChangeEmail = (ImageButton) findViewById(R.id.ibChangeEmail);
        llChangePassword = (LinearLayout) findViewById(R.id.llChangePassword);

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

        tvName.setText(Profile.name);
        tvStudentID.setText(Profile.student_ID);
        tvEmail.setText(Profile.email);
        tvMajor.setText(Profile.major);

        builder = new AlertDialog.Builder(this);

        ibChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText etName = new EditText(getApplicationContext());

                builder.setTitle("이름 변경");
                builder.setMessage("현재 이름 : " + Profile.name);
                builder.setView(etName);
                builder.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name = etName.getText().toString();
                        if (name.equals("") || name == null) {
                            Toast.makeText(getApplicationContext(), "변경하실 이름을 작성해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            updateProfileName task = new updateProfileName();
                            task.execute(PHPCommon.phpAddress("updateProfileName"), name, Profile.student_ID);
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        ibChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText etEmail = new EditText(getApplicationContext());

                builder.setTitle("이메일 변경");
                builder.setMessage("현재 이메일 : " + Profile.email);
                builder.setView(etEmail);
                builder.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        email = etEmail.getText().toString();
                        if (email.equals("") || email == null) {
                            Toast.makeText(getApplicationContext(), "변경하실 이메일 주소를 작성해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            updateProfileEmail task = new updateProfileEmail();
                            task.execute(PHPCommon.phpAddress("updateProfileEmail"), email, Profile.student_ID);
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        llChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordDialog ChangePasswordDialog = new ChangePasswordDialog(ChangeProfileActivity.this);
                ChangePasswordDialog.setCancelable(true);
                ChangePasswordDialog.show();
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

    // Uri를 Bitmap으로 변환
    private Bitmap BitmapToUri(Uri uri) {
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Bitmap을 String으로 변환
    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String image = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
        String resultString = "";
        try {
            resultString = URLEncoder.encode(image, "euckr");
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        return resultString;
    }

    //String을 Bitmap으로 변환
    public Bitmap StringToBitMap(String imageString) {
        try {
            String urlDecoder = "";
            urlDecoder = URLDecoder.decode(imageString, "EUC-KR");
            byte[] decodedBytes = Base64.decode(urlDecoder, 0);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        return null;
    }

    //String의 값을 압축
    private String compress(String string) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(gzipOutputStream);
        bufferedOutputStream.write(string.getBytes());
        bufferedOutputStream.close();
        byteArrayOutputStream.close();

        return new String(byteArrayOutputStream.toByteArray());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            //data.getData (Uri) -> Btimap -> String -> Profile.imageString에 값을 넣음
            //Profile 테이블에 image 값을 넣기 위해서는 String 값으로 변환해한다.
            // *** 문제점 Profile.imageString의 값이 매우 크고 길다 (아무 이미지를 선택한 결과 2~5만 길이가 나옴) ***
            ivProfile.setImageURI(data.getData());

            String imageString = BitMapToString(BitmapToUri(data.getData()));

            SharedPreferences pref = getSharedPreferences("ProfileImage", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("image", imageString);
            editor.commit();

        } else {
            Toast.makeText(getApplicationContext(), "사진 선택을 취소하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private class updateProfileName extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("Success")) {
                Toast.makeText(getApplicationContext(), "이름 변경에 성공했습니다..", Toast.LENGTH_SHORT).show();
                Profile.name = name;
                tvName.setText(Profile.name);
            } else if (result.equals("Failure")) {
                Toast.makeText(getApplicationContext(), "이름 변경에 실패 했습니다.\n다시 시도해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "이름 변경에 오류가 발생했습니다.\n다시 시도해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String) params[0];
            String name = (String) params[1];
            String student_ID = (String) params[2];

            String postParameters = "&name=" + name + "&student_ID=" + student_ID;
            return PHPCommon.sendPHP(serverURL, postParameters);
        }
    }

    private class updateProfileEmail extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("Success")) {
                Toast.makeText(getApplicationContext(), "이메일 변경에 성공했습니다..", Toast.LENGTH_SHORT).show();
                Profile.email = email;
                tvEmail.setText(Profile.email);
            } else if (result.equals("Failure")) {
                Toast.makeText(getApplicationContext(), "이메일 변경에 실패 했습니다.\n다시 시도해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "이메일 변경에 오류가 발생했습니다.\n다시 시도해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String) params[0];
            String email = (String) params[1];
            String student_ID = (String) params[2];

            String postParameters = "&email=" + email + "&student_ID=" + student_ID;
            return PHPCommon.sendPHP(serverURL, postParameters);
        }
    }
}
