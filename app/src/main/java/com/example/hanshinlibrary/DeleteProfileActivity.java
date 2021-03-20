package com.example.hanshinlibrary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hanshinlibrary.Model.Profile;

import de.hdodenhof.circleimageview.CircleImageView;

public class DeleteProfileActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private CircleImageView ivProfile;
    private TextView tvName;
    private TextView tvStudentID;
    private TextView tvEmail;
    private TextView tvMajor;

    private Button ibDelete;
    private LinearLayout llChangePassword;

    private String delete;

    private AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        ivProfile = (CircleImageView) findViewById(R.id.ivProfile);
        tvName = (TextView) findViewById(R.id.tvName);
        tvStudentID = (TextView) findViewById(R.id.tvStudentID);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvMajor = (TextView) findViewById(R.id.tvMajor);


        ibDelete = (Button) findViewById(R.id.deletebtn);


        tvName.setText(Profile.name);
        tvStudentID.setText(Profile.student_ID);
        tvEmail.setText(Profile.email);
        tvMajor.setText(Profile.major);

        builder = new AlertDialog.Builder(this);

        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText delete1 = new EditText(getApplicationContext());

                builder.setTitle("회원 탈퇴");
                builder.setMessage("회원탈퇴를 하면 저장되어있던 \n모든 정보가 사라지게 됩니다. \n탈퇴하시겠습니까?");
                builder.setPositiveButton("회원 탈퇴", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete = delete1.getText().toString();

                        DeleteProfileActivity.deleteProfile task = new DeleteProfileActivity.deleteProfile();
                        task.execute(PHPCommon.phpAddress("delete"), delete, Profile.student_ID);

                        startActivity(new Intent(DeleteProfileActivity.this, LoginActivity.class));

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

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private class deleteProfile extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("Success")) {
                Toast.makeText(getApplicationContext(), "회원 탈퇴에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                /////넣기/////
                //???????????????????????
                //////////
            } else if (result.equals("Failure")) {
                Toast.makeText(getApplicationContext(), "회원 탈퇴에 실패 했습니다.\n다시 시도해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "회원 탈퇴에 오류가 발생했습니다.\n다시 시도해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
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

}




