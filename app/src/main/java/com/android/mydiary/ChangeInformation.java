package com.android.mydiary;

import static com.android.mydiary.MainActivity.helper;
import static com.android.mydiary.MenuActivity.ID;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangeInformation extends AppCompatActivity {
    EditText oldPassword;
    EditText newPassword;
    Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);

        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        changePasswordButton = findViewById(R.id.changePasswordButton);



        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old = oldPassword.getText().toString();
                String ne = newPassword.getText().toString();
                if(old.equals(helper.getUser(ID).getPassword())) {
                    helper.UpdateUserPassword(ID, ne);
                    Intent i = new Intent();
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}