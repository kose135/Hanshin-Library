package com.example.hanshinlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hanshinlibrary.Model.Profile;

import java.net.URI;
import java.net.URLDecoder;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView ivProfile;
    private TextView tvName;
    private TextView tvStudentID;
    private TextView tvMajor;
    private ImageView ivStudentQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ivProfile = (CircleImageView) findViewById(R.id.ivProfile);
        tvName = (TextView) findViewById(R.id.tvName);
        tvStudentID = (TextView) findViewById(R.id.tvStudentID);
        tvMajor = (TextView) findViewById(R.id.tvMajor);
        ivStudentQR = (ImageView) findViewById(R.id.ivStudentQR);

        SharedPreferences pref = getSharedPreferences("ProfileImage", MODE_PRIVATE);
        String image =  pref.getString("image", "");
        if(!image.equals("") || image != null)
            ivProfile.setImageBitmap(StringToBitMap(image));
        else
            ivProfile.setImageResource(R.drawable.person_icon);


        tvName.setText(Profile.name);
        tvStudentID.setText(Profile.student_ID);
        tvMajor.setText(Profile.major);

        createQR(Profile.student_ID);
        ivStudentQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

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

    public void createQR(String studnet_ID) {
        String inputValue = studnet_ID.trim();
        if (inputValue.length() > 0) {
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            QREncoding qrgEncoder = new QREncoding(inputValue, null, "TEXT_TYPE", smallerDimension);
            qrgEncoder.setColorCode(Color.WHITE);
            qrgEncoder.setColorBackground(Color.parseColor("#2C1A58"));
            try {
                Bitmap bitmap = qrgEncoder.getBitmap();
                ivStudentQR.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

        }
    }
}
