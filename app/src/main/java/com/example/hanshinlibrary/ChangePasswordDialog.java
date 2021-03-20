package com.example.hanshinlibrary;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.hanshinlibrary.Model.Profile;

import static android.content.Context.MODE_PRIVATE;

public class ChangePasswordDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private EditText etNowPassword;
    private EditText etChangePassword;
    private EditText etReconfirmPassword;
    private Button btnCancel;
    private Button btnChange;

    private String nowPassword;
    private String changePassword;
    private String reconfirmPassword;

    public ChangePasswordDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_changedialog);

        etNowPassword = (EditText) findViewById(R.id.etNowPassword);
        etChangePassword = (EditText) findViewById(R.id.etChangePassword);
        etReconfirmPassword = (EditText) findViewById(R.id.etReconfirmPassword);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnChange = (Button) findViewById(R.id.btnChange);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowPassword = etNowPassword.getText().toString();
                changePassword = etChangePassword.getText().toString();
                reconfirmPassword = etReconfirmPassword.getText().toString();

                if(nowPassword.equals("") || changePassword.equals("") || reconfirmPassword.equals("")) {
                    Toast.makeText(mContext, "모든 항목을 작성해 주시길 바랍니다.", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                else if (!nowPassword.equals(Profile.password)) {
                    Toast.makeText(mContext, "현재 비밀번호가 다릅니다.\n다시 입력하시길 바랍니다.", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                else if (!changePassword.equals(reconfirmPassword)) {
                    Toast.makeText(mContext, "변경할 비밀번호가 다릅니다.\n다시 입력하시길 바랍니다.", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                else {
                    updateProfileName updateProfileName = new updateProfileName();
                    updateProfileName.execute(PHPCommon.phpAddress("updateProfilePassword"), changePassword, Profile.student_ID);
                    dismiss();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    private class updateProfileName extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            System.out.println("결과 : " + result);

            if (result.equals("Success")) {
                Toast.makeText(mContext, "비밀번호 변경에 성공했습니다.", Toast.LENGTH_SHORT).show();
                Profile.password = changePassword;

                SharedPreferences pref = mContext.getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("ID", Profile.student_ID);
                editor.putString("PW", Profile.password);
                editor.commit();
            } else if (result.equals("Failure")) {
                Toast.makeText(mContext, "비밀번호 변경에 실패 했습니다.\n다시 시도해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "비밀번호 변경에 오류가 발생했습니다.\n다시 시도해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String) params[0];
            String password = (String) params[1];
            String student_ID = (String) params[2];

            String postParameters = "&password=" + password + "&student_ID=" + student_ID;
            return PHPCommon.sendPHP(serverURL, postParameters);
        }
    }
}
